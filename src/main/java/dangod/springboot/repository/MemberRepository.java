package dangod.springboot.repository;

import dangod.springboot.model.Member;
import dangod.springboot.enums.MemberCardType;
import dangod.springboot.enums.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    
    Optional<Member> findByMemberNumber(String memberNumber);
    
    Optional<Member> findByPhone(String phone);
    
    Optional<Member> findByEmail(String email);
    
    List<Member> findByCardType(MemberCardType cardType);
    
    List<Member> findByStatus(MemberStatus status);
    
    List<Member> findByStoreId(String storeId);
    
    @Query("SELECT m FROM Member m WHERE m.expiryDate BETWEEN :startDate AND :endDate")
    List<Member> findMembersWithExpiryDateBetween(@Param("startDate") LocalDate startDate, 
                                                 @Param("endDate") LocalDate endDate);
    
    @Query("SELECT m FROM Member m WHERE m.expiryDate <= :date AND m.status != :status")
    List<Member> findExpiredMembers(@Param("date") LocalDate date, 
                                   @Param("status") MemberStatus status);
    
    @Query("SELECT m FROM Member m WHERE m.expiryDate BETWEEN :today AND :sevenDaysLater AND m.renewalReminderSent = false")
    List<Member> findMembersExpiringSoonWithoutReminder(@Param("today") LocalDate today, 
                                                        @Param("sevenDaysLater") LocalDate sevenDaysLater);
    
    @Query("SELECT m FROM Member m WHERE m.chainStoreAccess = true AND m.status = :status")
    List<Member> findChainStoreMembers(@Param("status") MemberStatus status);
    
    @Query("SELECT m FROM Member m WHERE m.status = :status AND m.freezeApprovedBy = :managerId")
    List<Member> findFrozenMembersByManager(@Param("status") MemberStatus status, 
                                           @Param("managerId") Long managerId);
    
    @Query("SELECT COUNT(m) FROM Member m WHERE m.cardType = :cardType AND m.status = :status")
    Long countMembersByCardTypeAndStatus(@Param("cardType") MemberCardType cardType, 
                                        @Param("status") MemberStatus status);
    
    @Query("SELECT m.cardType, COUNT(m) FROM Member m WHERE m.status = :status GROUP BY m.cardType")
    List<Object[]> countMembersByCardType(@Param("status") MemberStatus status);
}