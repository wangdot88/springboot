package dangod.springboot.repository;

import dangod.springboot.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByPhone(String phone);
    
    List<Member> findByStoreId(Long storeId);
    
    List<Member> findByStatus(Integer status);
    
    List<Member> findByCardLevelId(Long cardLevelId);
    
    @Query("SELECT m FROM Member m WHERE m.expireDate <= :expireDate AND m.expireDate >= :startDate AND m.status = 1")
    List<Member> findExpiringMembers(@Param("startDate") LocalDateTime startDate, @Param("expireDate") LocalDateTime expireDate);
    
    @Query("SELECT COUNT(m) FROM Member m WHERE m.storeId = :storeId AND m.registerDate >= :startDate")
    long countNewMembersByStore(@Param("storeId") Long storeId, @Param("startDate") LocalDateTime startDate);
    
    List<Member> findByStoreIdAndStatus(Long storeId, Integer status);
    
    @Query("SELECT m FROM Member m WHERE m.totalPoints - m.usedPoints >= :points ORDER BY (m.totalPoints - m.usedPoints) DESC")
    List<Member> findMembersWithPoints(@Param("points") Integer points);
    
    long countByCreateTimeAfter(LocalDateTime createTime);
    
    @Query("SELECT COUNT(m) FROM Member m WHERE m.expireDate < :expireDate AND m.status = :status")
    long countByExpireDateBeforeAndStatus(@Param("expireDate") LocalDateTime expireDate, @Param("status") Member.Status status);
}
