package dangod.springboot.service;

import dangod.springboot.common.BusinessException;
import dangod.springboot.common.Result;
import dangod.springboot.entity.Member;
import dangod.springboot.entity.MemberCardLevel;
import dangod.springboot.entity.MemberFreezeRecord;
import dangod.springboot.repository.MemberCardLevelRepository;
import dangod.springboot.repository.MemberFreezeRecordRepository;
import dangod.springboot.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class MemberService {
    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private MemberCardLevelRepository memberCardLevelRepository;
    
    @Autowired
    private MemberFreezeRecordRepository memberFreezeRecordRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Value("${gym.member.expire-warning-days:7}")
    private Integer expireWarningDays;
    
    @Value("${gym.member.freeze-require-approval:true}")
    private Boolean freezeRequireApproval;
    
    @Transactional
    public Member createMember(Member member) {
        if (memberRepository.findByPhone(member.getPhone()) != null) {
            throw new BusinessException("该手机号已注册会员");
        }
        
        if (member.getCardLevel() == null || member.getCardLevel().getId() == null) {
            throw new BusinessException("必须选择会员等级");
        }
        
        MemberCardLevel cardLevel = memberCardLevelRepository.findOne(member.getCardLevel().getId());
        if (cardLevel == null) {
            throw new BusinessException("会员等级不存在");
        }
        
        member.setCardLevel(cardLevel);
        member.setExpireDate(LocalDateTime.now().plusDays(cardLevel.getDurationDays()));
        
        return memberRepository.save(member);
    }
    
    public Member getMemberById(Long id) {
        return memberRepository.findOne(id);
    }
    
    public Member getMemberByPhone(String phone) {
        return memberRepository.findByPhone(phone);
    }
    
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }
    
    public List<Member> getMembersByStore(Long storeId) {
        return memberRepository.findByStoreId(storeId);
    }
    
    @Transactional
    public Member updateMember(Member member) {
        Member existingMember = memberRepository.findOne(member.getId());
        if (existingMember == null) {
            throw new BusinessException("会员不存在");
        }
        
        if (!existingMember.getPhone().equals(member.getPhone())) {
            if (memberRepository.findByPhone(member.getPhone()) != null) {
                throw new BusinessException("该手机号已被其他会员使用");
            }
        }
        
        existingMember.setName(member.getName());
        existingMember.setPhone(member.getPhone());
        existingMember.setGender(member.getGender());
        existingMember.setIdCard(member.getIdCard());
        existingMember.setAddress(member.getAddress());
        existingMember.setNote(member.getNote());
        existingMember.setUpdateBy(member.getUpdateBy());
        existingMember.setUpdateTime(LocalDateTime.now());
        
        return memberRepository.save(existingMember);
    }
    
    @Transactional
    public Member renewMember(Long memberId, Long newCardLevelId, String operator) {
        Member member = memberRepository.findOne(memberId);
        if (member == null) {
            throw new BusinessException("会员不存在");
        }
        
        MemberCardLevel newCardLevel = memberCardLevelRepository.findOne(newCardLevelId);
        if (newCardLevel == null) {
            throw new BusinessException("会员等级不存在");
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (member.getExpireDate().isBefore(now)) {
            member.setExpireDate(now.plusDays(newCardLevel.getDurationDays()));
        } else {
            member.setExpireDate(member.getExpireDate().plusDays(newCardLevel.getDurationDays()));
        }
        
        member.setCardLevel(newCardLevel);
        member.setStatus(1);
        member.setUpdateBy(operator);
        member.setUpdateTime(now);
        
        int pointsEarned = newCardLevel.getPrice().intValue() * newCardLevel.getPointRate() / 100;
        member.setTotalPoints(member.getTotalPoints() + pointsEarned);
        
        return memberRepository.save(member);
    }
    
    @Transactional
    public void applyFreezeMember(Long memberId, Integer freezeDays, String reason, String operator) {
        Member member = memberRepository.findOne(memberId);
        if (member == null) {
            throw new BusinessException("会员不存在");
        }
        
        if (member.getStatus() != 1) {
            throw new BusinessException("只有正常状态的会员才能申请冻结");
        }
        
        if (!member.getCardLevel().getFreezeAllowed()) {
            throw new BusinessException("该会员等级不支持冻结");
        }
        
        if (freezeDays > member.getCardLevel().getFreezeMaxDays()) {
            throw new BusinessException("冻结天数不能超过" + member.getCardLevel().getFreezeMaxDays() + "天");
        }
        
        MemberFreezeRecord record = new MemberFreezeRecord();
        record.setMember(member);
        record.setFreezeDays(freezeDays);
        record.setReason(reason);
        record.setCreateBy(operator);
        
        if (freezeRequireApproval) {
            record.setStatus(0);
        } else {
            record.setStatus(1);
            record.setFreezeStartDate(LocalDateTime.now());
            record.setFreezeEndDate(LocalDateTime.now().plusDays(freezeDays));
            member.setStatus(2);
            member.setUpdateBy(operator);
            member.setUpdateTime(LocalDateTime.now());
            memberRepository.save(member);
        }
        
        memberFreezeRecordRepository.save(record);
    }
    
    @Transactional
    public void approveFreeze(Long recordId, Integer approveStatus, String opinion, String operator) {
        MemberFreezeRecord record = memberFreezeRecordRepository.findOne(recordId);
        if (record == null) {
            throw new BusinessException("冻结记录不存在");
        }
        
        if (record.getStatus() != 0) {
            throw new BusinessException("该申请已处理");
        }
        
        record.setStatus(approveStatus);
        record.setApprovalOpinion(opinion);
        record.setApprovalBy(operator);
        record.setApprovalTime(LocalDateTime.now());
        
        if (approveStatus == 1) {
            LocalDateTime now = LocalDateTime.now();
            record.setFreezeStartDate(now);
            record.setFreezeEndDate(now.plusDays(record.getFreezeDays()));
            
            Member member = record.getMember();
            member.setStatus(2);
            member.setUpdateBy(operator);
            member.setUpdateTime(now);
            member.setExpireDate(member.getExpireDate().plusDays(record.getFreezeDays()));
            memberRepository.save(member);
        }
        
        memberFreezeRecordRepository.save(record);
    }
    
    @Transactional
    public void unfreezeMember(Long memberId, String operator) {
        Member member = memberRepository.findOne(memberId);
        if (member == null) {
            throw new BusinessException("会员不存在");
        }
        
        if (member.getStatus() != 2) {
            throw new BusinessException("会员未处于冻结状态");
        }
        
        List<MemberFreezeRecord> activeFreezes = memberFreezeRecordRepository.findByMemberIdAndStatus(memberId, 1);
        for (MemberFreezeRecord record : activeFreezes) {
            record.setStatus(3);
            memberFreezeRecordRepository.save(record);
        }
        
        member.setStatus(1);
        member.setUpdateBy(operator);
        member.setUpdateTime(LocalDateTime.now());
        memberRepository.save(member);
    }
    
    public List<MemberFreezeRecord> getPendingFreezeApprovals() {
        return memberFreezeRecordRepository.findByStatus(0);
    }
    
    public List<MemberFreezeRecord> getMemberFreezeHistory(Long memberId) {
        return memberFreezeRecordRepository.findByMemberId(memberId);
    }
    
    public void checkAndSendExpireWarnings() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime warningDate = now.plusDays(expireWarningDays);
        
        List<Member> expiringMembers = memberRepository.findExpiringMembers(now, warningDate);
        
        for (Member member : expiringMembers) {
            try {
                String subject = "会员卡即将到期提醒";
                String content = String.format("尊敬的%s会员，您好！您的会员卡将于%s到期，请及时续费。", 
                    member.getName(), member.getExpireDate().toString().substring(0, 10));
                emailService.sendEmail(member.getPhone(), subject, content);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public List<MemberCardLevel> getAllCardLevels() {
        return memberCardLevelRepository.findAll();
    }
    
    public MemberCardLevel getCardLevelById(Long id) {
        return memberCardLevelRepository.findOne(id);
    }
    
    @Transactional
    public MemberCardLevel createCardLevel(MemberCardLevel cardLevel) {
        if (memberCardLevelRepository.findByLevelCode(cardLevel.getLevelCode()) != null) {
            throw new BusinessException("等级编码已存在");
        }
        return memberCardLevelRepository.save(cardLevel);
    }
    
    @Transactional
    public MemberCardLevel updateCardLevel(MemberCardLevel cardLevel) {
        MemberCardLevel existing = memberCardLevelRepository.findOne(cardLevel.getId());
        if (existing == null) {
            throw new BusinessException("会员等级不存在");
        }
        
        if (!existing.getLevelCode().equals(cardLevel.getLevelCode())) {
            if (memberCardLevelRepository.findByLevelCode(cardLevel.getLevelCode()) != null) {
                throw new BusinessException("等级编码已被使用");
            }
        }
        
        return memberCardLevelRepository.save(cardLevel);
    }
}
