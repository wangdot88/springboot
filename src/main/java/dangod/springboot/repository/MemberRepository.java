package dangod.springboot.repository;

import dangod.springboot.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByPhone(String phone);

    List<Member> findByStoreId(Long storeId);

    @Query("SELECT m FROM Member m WHERE m.cardEndTime BETWEEN :startDate AND :endDate")
    List<Member> findMembersWithCardExpiringBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT m FROM Member m WHERE m.cardEndTime < :date AND m.status = 'ACTIVE'")
    List<Member> findExpiredMembers(@Param("date") Date date);

    @Query("SELECT m FROM Member m WHERE m.status = 'PENDING_FREEZE'")
    List<Member> findMembersPendingFreeze();

    List<Member> findByStatus(Member.MemberStatus status);

    List<Member> findByCardLevel(Member.CardLevel cardLevel);

    @Query("SELECT m FROM Member m WHERE m.cardEndTime < :date AND m.status = 'ACTIVE'")
    List<Member> findByCardExpirationDateBeforeAndStatus(@Param("date") Date date, Member.MemberStatus status);
}
