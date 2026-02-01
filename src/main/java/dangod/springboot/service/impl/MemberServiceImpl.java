package dangod.springboot.service.impl;

import dangod.springboot.model.Member;
import dangod.springboot.model.User;
import dangod.springboot.dto.MemberRegistrationDto;
import dangod.springboot.dto.MemberFreezeDto;
import dangod.springboot.enums.MemberCardType;
import dangod.springboot.enums.MemberStatus;
import dangod.springboot.repository.MemberRepository;
import dangod.springboot.repository.UserRepository;
import dangod.springboot.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Override
    public Member registerMember(MemberRegistrationDto registrationDto) {
        // 检查手机号和邮箱是否已存在
        if (memberRepository.findByPhone(registrationDto.getPhone()).isPresent()) {
            throw new RuntimeException("手机号已存在");
        }
        
        if (memberRepository.findByEmail(registrationDto.getEmail()).isPresent()) {
            throw new RuntimeException("邮箱已存在");
        }
        
        // 生成会员号
        String memberNumber = generateMemberNumber();
        
        // 创建会员
        Member member = new Member(
            memberNumber,
            registrationDto.getName(),
            registrationDto.getPhone(),
            registrationDto.getEmail(),
            registrationDto.getCardType(),
            registrationDto.getExpiryDate(),
            registrationDto.getStoreId()
        );
        
        // 设置可选属性
        if (registrationDto.getInitialBalance() != null) {
            member.setBalance(registrationDto.getInitialBalance());
        }
        
        if (registrationDto.getTotalSessions() != null) {
            member.setTotalSessions(registrationDto.getTotalSessions());
        }
        
        if (registrationDto.getChainStoreAccess() != null) {
            member.setChainStoreAccess(registrationDto.getChainStoreAccess());
        }
        
        return memberRepository.save(member);
    }

    @Override
    public Optional<Member> findByMemberNumber(String memberNumber) {
        return memberRepository.findByMemberNumber(memberNumber);
    }

    @Override
    public Optional<Member> findByPhone(String phone) {
        return memberRepository.findByPhone(phone);
    }

    @Override
    public List<Member> findAllMembers() {
        return memberRepository.findAll();
    }

    @Override
    public List<Member> findByCardType(MemberCardType cardType) {
        return memberRepository.findByCardType(cardType);
    }

    @Override
    public List<Member> findByStatus(MemberStatus status) {
        return memberRepository.findByStatus(status);
    }

    @Override
    public List<Member> findByStoreId(String storeId) {
        return memberRepository.findByStoreId(storeId);
    }

    @Override
    public Member updateMember(Long memberId, MemberRegistrationDto updateDto) {
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (!memberOpt.isPresent()) {
            throw new RuntimeException("会员不存在");
        }
        
        Member member = memberOpt.get();
        
        // 检查手机号和邮箱是否与其他会员冲突
        Optional<Member> existingPhone = memberRepository.findByPhone(updateDto.getPhone());
        if (existingPhone.isPresent() && !existingPhone.get().getId().equals(memberId)) {
            throw new RuntimeException("手机号已被其他会员使用");
        }
        
        Optional<Member> existingEmail = memberRepository.findByEmail(updateDto.getEmail());
        if (existingEmail.isPresent() && !existingEmail.get().getId().equals(memberId)) {
            throw new RuntimeException("邮箱已被其他会员使用");
        }
        
        // 更新会员信息
        member.setName(updateDto.getName());
        member.setPhone(updateDto.getPhone());
        member.setEmail(updateDto.getEmail());
        member.setCardType(updateDto.getCardType());
        member.setExpiryDate(updateDto.getExpiryDate());
        member.setStoreId(updateDto.getStoreId());
        
        if (updateDto.getInitialBalance() != null) {
            member.setBalance(updateDto.getInitialBalance());
        }
        
        if (updateDto.getTotalSessions() != null) {
            member.setTotalSessions(updateDto.getTotalSessions());
        }
        
        if (updateDto.getChainStoreAccess() != null) {
            member.setChainStoreAccess(updateDto.getChainStoreAccess());
        }
        
        return memberRepository.save(member);
    }

    @Override
    public Member freezeMember(Long memberId, MemberFreezeDto freezeDto) {
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (!memberOpt.isPresent()) {
            throw new RuntimeException("会员不存在");
        }
        
        Optional<User> managerOpt = userRepository.findById(freezeDto.getApprovedById());
        if (!managerOpt.isPresent()) {
            throw new RuntimeException("审批人不存在");
        }
        
        Member member = memberOpt.get();
        member.setStatus(MemberStatus.FROZEN);
        member.setFreezeReason(freezeDto.getFreezeReason());
        member.setFreezeDate(LocalDate.now());
        member.setFreezeApprovedBy(freezeDto.getApprovedById());
        
        return memberRepository.save(member);
    }

    @Override
    public Member unfreezeMember(Long memberId) {
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (!memberOpt.isPresent()) {
            throw new RuntimeException("会员不存在");
        }
        
        Member member = memberOpt.get();
        member.setStatus(MemberStatus.ACTIVE);
        member.setFreezeReason(null);
        member.setFreezeDate(null);
        member.setFreezeApprovedBy(null);
        
        return memberRepository.save(member);
    }

    @Override
    public List<Member> findMembersExpiringSoon() {
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysLater = today.plusDays(7);
        return memberRepository.findMembersExpiringSoonWithoutReminder(today, sevenDaysLater);
    }

    @Override
    public void sendRenewalReminders() {
        List<Member> expiringMembers = findMembersExpiringSoon();
        for (Member member : expiringMembers) {
            // 这里应该发送短信或邮件提醒
            // 实际项目中可以集成短信服务或邮件服务
            System.out.println("发送续费提醒给会员: " + member.getName() + 
                              ", 手机号: " + member.getPhone() + 
                              ", 到期日: " + member.getExpiryDate());
            
            // 标记已发送提醒
            member.setRenewalReminderSent(true);
            memberRepository.save(member);
        }
    }

    @Override
    public List<Member> findExpiredMembers() {
        return memberRepository.findExpiredMembers(LocalDate.now(), MemberStatus.EXPIRED);
    }

    @Override
    public List<Member> findChainStoreMembers() {
        return memberRepository.findChainStoreMembers(MemberStatus.ACTIVE);
    }

    @Override
    public List<Member> findFrozenMembersByManager(Long managerId) {
        return memberRepository.findFrozenMembersByManager(MemberStatus.FROZEN, managerId);
    }

    @Override
    public void deleteMember(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new RuntimeException("会员不存在");
        }
        memberRepository.deleteById(memberId);
    }

    @Override
    public Member renewMembership(Long memberId, LocalDate newExpiryDate) {
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (!memberOpt.isPresent()) {
            throw new RuntimeException("会员不存在");
        }
        
        Member member = memberOpt.get();
        member.setExpiryDate(newExpiryDate);
        member.setStatus(MemberStatus.ACTIVE);
        member.setRenewalReminderSent(false);
        
        return memberRepository.save(member);
    }

    @Override
    public Member upgradeCardType(Long memberId, MemberCardType newCardType) {
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (!memberOpt.isPresent()) {
            throw new RuntimeException("会员不存在");
        }
        
        Member member = memberOpt.get();
        member.setCardType(newCardType);
        
        return memberRepository.save(member);
    }
    
    private String generateMemberNumber() {
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "M" + datePrefix + randomSuffix;
    }
}