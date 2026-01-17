package dangod.springboot.repository;

import dangod.springboot.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByMemberNo(String memberNo);
    Optional<Member> findByPhone(String phone);
    List<Member> findByLevelCode(String levelCode);
    List<Member> findByStatus(Integer status);

    @Query("SELECT m FROM Member m WHERE m.cardEndDate <= :endDate AND m.cardEndDate >= :startDate")
    List<Member> findMembersWithCardExpiringBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT m FROM Member m WHERE DATEDIFF(m.cardEndDate, CURRENT_DATE) <= :days AND m.status = 1")
    List<Member> findMembersWithCardExpiringWithinDays(@Param("days") int days);

    List<Member> findByBranchId(Long branchId);

    @Query("SELECT m FROM Member m WHERE m.status = 1 AND m.levelCode IN :levelCodes")
    List<Member> findActiveMembersByLevelCodes(@Param("levelCodes") List<String> levelCodes);

    @Query("SELECT COUNT(m) FROM Member m WHERE m.branchId = :branchId")
    Long countByBranchId(@Param("branchId") Long branchId);

    @Query("SELECT COUNT(m) FROM Member m WHERE m.branchId = :branchId AND m.status = :status")
    Long countByBranchIdAndStatus(@Param("branchId") Long branchId, @Param("status") Integer status);

    @Query("SELECT COUNT(m) FROM Member m WHERE m.status = :status")
    Long countByStatus(@Param("status") Integer status);

    @Query("SELECT m FROM Member m WHERE m.id = :id")
    Optional<Member> findById(@Param("id") Long id);
}
