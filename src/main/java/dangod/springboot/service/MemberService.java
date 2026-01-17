package dangod.springboot.service;

import dangod.springboot.entity.Member;
import dangod.springboot.entity.ApprovalRecord;
import dangod.springboot.repository.MemberRepository;
import dangod.springboot.repository.ApprovalRecordRepository;
import dangod.springboot.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ApprovalRecordRepository approvalRecordRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String MEMBER_CACHE_PREFIX = "member:";
    private static final String EXPIRING_MEMBERS_KEY = "expiring_members";

    public Member createMember(Member member) {
        if (memberRepository.findByPhone(member.getPhone()) != null) {
            throw new BusinessException("该手机号已注册");
        }
        member.setCreateTime(new Date());
        member.setUpdateTime(new Date());
        member.setStatus(Member.MemberStatus.ACTIVE);
        member.setPoints(0);
        return memberRepository.save(member);
    }

    public Member updateMember(Long id, Member member) {
        Member existingMember = memberRepository.findOne(id);
        if (existingMember == null) {
            throw new BusinessException("会员不存在");
        }
        
        if (member.getName() != null) {
            existingMember.setName(member.getName());
        }
        if (member.getGender() != null) {
            existingMember.setGender(member.getGender());
        }
        if (member.getAge() != null) {
            existingMember.setAge(member.getAge());
        }
        if (member.getAvatar() != null) {
            existingMember.setAvatar(member.getAvatar());
        }
        
        existingMember.setUpdateTime(new Date());
        return memberRepository.save(existingMember);
    }

    public Member getMemberById(Long id) {
        String cacheKey = MEMBER_CACHE_PREFIX + id;
        Member member = (Member) redisTemplate.opsForValue().get(cacheKey);
        
        if (member == null) {
            member = memberRepository.findOne(id);
            if (member == null) {
                throw new BusinessException("会员不存在");
            }
            redisTemplate.opsForValue().set(cacheKey, member, 1, TimeUnit.HOURS);
        }
        
        return member;
    }

    public Member getMemberByPhone(String phone) {
        return memberRepository.findByPhone(phone);
    }

    public List<Member> getMembersByStore(Long storeId) {
        return memberRepository.findByStoreId(storeId);
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    @Transactional
    public Member upgradeCard(Long memberId, Member.CardLevel newLevel) {
        Member member = memberRepository.findOne(memberId);
        if (member == null) {
            throw new BusinessException("会员不存在");
        }
        
        if (member.getCardLevel().ordinal() >= newLevel.ordinal()) {
            throw new BusinessException("只能升级到更高级别的卡");
        }
        
        member.setCardLevel(newLevel);
        member.setUpdateTime(new Date());
        return memberRepository.save(member);
    }

    @Transactional
    public Member renewCard(Long memberId, int months) {
        Member member = memberRepository.findOne(memberId);
        if (member == null) {
            throw new BusinessException("会员不存在");
        }
        
        Calendar calendar = Calendar.getInstance();
        if (member.getCardEndTime().after(new Date())) {
            calendar.setTime(member.getCardEndTime());
        } else {
            calendar.setTime(new Date());
        }
        calendar.add(Calendar.MONTH, months);
        
        member.setCardEndTime(calendar.getTime());
        member.setStatus(Member.MemberStatus.ACTIVE);
        member.setUpdateTime(new Date());
        
        return memberRepository.save(member);
    }

    @Transactional
    public ApprovalRecord requestFreezeCard(Long memberId, String reason) {
        Member member = memberRepository.findOne(memberId);
        if (member == null) {
            throw new BusinessException("会员不存在");
        }
        
        if (member.getStatus() == Member.MemberStatus.FROZEN) {
            throw new BusinessException("会员卡已冻结");
        }
        
        if (member.getStatus() == Member.MemberStatus.EXPIRED) {
            throw new BusinessException("会员卡已过期");
        }
        
        ApprovalRecord record = new ApprovalRecord();
        record.setMemberId(memberId);
        record.setType(ApprovalRecord.ApprovalType.FREEZE_CARD);
        record.setReason(reason);
        record.setStatus(ApprovalRecord.ApprovalStatus.PENDING);
        
        member.setStatus(Member.MemberStatus.PENDING_FREEZE);
        member.setUpdateTime(new Date());
        
        approvalRecordRepository.save(record);
        memberRepository.save(member);
        
        return record;
    }

    @Transactional
    public void approveFreezeRequest(Long approvalId, Long approverId, String approverName, String comment) {
        ApprovalRecord record = approvalRecordRepository.findOne(approvalId);
        if (record == null) {
            throw new BusinessException("审批记录不存在");
        }
        
        if (record.getStatus() != ApprovalRecord.ApprovalStatus.PENDING) {
            throw new BusinessException("该申请已处理");
        }
        
        record.setStatus(ApprovalRecord.ApprovalStatus.APPROVED);
        record.setApproverId(approverId);
        record.setApproverName(approverName);
        record.setApprovalComment(comment);
        record.setApprovalTime(new Date());
        
        Member member = memberRepository.findOne(record.getMemberId());
        if (member == null) {
            throw new BusinessException("会员不存在");
        }
        member.setStatus(Member.MemberStatus.FROZEN);
        member.setUpdateTime(new Date());
        
        approvalRecordRepository.save(record);
        memberRepository.save(member);
    }

    @Transactional
    public void rejectFreezeRequest(Long approvalId, Long approverId, String approverName, String comment) {
        ApprovalRecord record = approvalRecordRepository.findOne(approvalId);
        if (record == null) {
            throw new BusinessException("审批记录不存在");
        }
        
        if (record.getStatus() != ApprovalRecord.ApprovalStatus.PENDING) {
            throw new BusinessException("该申请已处理");
        }
        
        record.setStatus(ApprovalRecord.ApprovalStatus.REJECTED);
        record.setApproverId(approverId);
        record.setApproverName(approverName);
        record.setApprovalComment(comment);
        record.setApprovalTime(new Date());
        
        Member member = memberRepository.findOne(record.getMemberId());
        if (member == null) {
            throw new BusinessException("会员不存在");
        }
        member.setStatus(Member.MemberStatus.ACTIVE);
        member.setUpdateTime(new Date());
        
        approvalRecordRepository.save(record);
        memberRepository.save(member);
    }

    @Transactional
    public Member unfreezeCard(Long memberId) {
        Member member = memberRepository.findOne(memberId);
        if (member == null) {
            throw new BusinessException("会员不存在");
        }
        
        if (member.getStatus() != Member.MemberStatus.FROZEN) {
            throw new BusinessException("会员卡未冻结");
        }
        
        member.setStatus(Member.MemberStatus.ACTIVE);
        member.setUpdateTime(new Date());
        
        return memberRepository.save(member);
    }

    public List<Member> getExpiringMembers(int daysBefore) {
        Calendar calendar = Calendar.getInstance();
        Date now = new Date();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_MONTH, daysBefore);
        Date endDate = calendar.getTime();
        
        return memberRepository.findMembersWithCardExpiringBetween(now, endDate);
    }

    public void cacheExpiringMembers() {
        List<Member> expiringMembers = getExpiringMembers(7);
        redisTemplate.opsForValue().set(EXPIRING_MEMBERS_KEY, expiringMembers, 1, TimeUnit.DAYS);
    }

    public List<Member> getCachedExpiringMembers() {
        return (List<Member>) redisTemplate.opsForValue().get(EXPIRING_MEMBERS_KEY);
    }

    @Transactional
    public void updateExpiredMembers() {
        List<Member> expiredMembers = memberRepository.findExpiredMembers(new Date());
        for (Member member : expiredMembers) {
            member.setStatus(Member.MemberStatus.EXPIRED);
            member.setUpdateTime(new Date());
            memberRepository.save(member);
        }
    }

    public List<Member> getMembersByLevel(Member.CardLevel level) {
        return memberRepository.findByCardLevel(level);
    }

    public List<Member> getMembersByStatus(Member.MemberStatus status) {
        return memberRepository.findByStatus(status);
    }

    public void addPoints(Long memberId, Integer points) {
        Member member = memberRepository.findOne(memberId);
        if (member == null) {
            throw new BusinessException("会员不存在");
        }
        member.setPoints(member.getPoints() + points);
        member.setUpdateTime(new Date());
        memberRepository.save(member);
        
        String cacheKey = MEMBER_CACHE_PREFIX + memberId;
        redisTemplate.delete(cacheKey);
    }

    public List<Member> getMembersPendingFreeze() {
        return memberRepository.findMembersPendingFreeze();
    }
}
