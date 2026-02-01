package dangod.springboot.service;

import dangod.springboot.model.Member;
import dangod.springboot.dto.MemberRegistrationDto;
import dangod.springboot.dto.MemberFreezeDto;
import dangod.springboot.enums.MemberCardType;
import dangod.springboot.enums.MemberStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MemberService {
    
    Member registerMember(MemberRegistrationDto registrationDto);
    
    Optional<Member> findByMemberNumber(String memberNumber);
    
    Optional<Member> findByPhone(String phone);
    
    List<Member> findAllMembers();
    
    List<Member> findByCardType(MemberCardType cardType);
    
    List<Member> findByStatus(MemberStatus status);
    
    List<Member> findByStoreId(String storeId);
    
    Member updateMember(Long memberId, MemberRegistrationDto updateDto);
    
    Member freezeMember(Long memberId, MemberFreezeDto freezeDto);
    
    Member unfreezeMember(Long memberId);
    
    List<Member> findMembersExpiringSoon();
    
    void sendRenewalReminders();
    
    List<Member> findExpiredMembers();
    
    List<Member> findChainStoreMembers();
    
    List<Member> findFrozenMembersByManager(Long managerId);
    
    void deleteMember(Long memberId);
    
    Member renewMembership(Long memberId, LocalDate newExpiryDate);
    
    Member upgradeCardType(Long memberId, MemberCardType newCardType);
}