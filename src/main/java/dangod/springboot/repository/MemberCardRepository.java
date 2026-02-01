package dangod.springboot.repository;

import dangod.springboot.entity.MemberCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberCardRepository extends JpaRepository<MemberCard, Long>, JpaSpecificationExecutor<MemberCard> {
    List<MemberCard> findByMemberId(Long memberId);
    Optional<MemberCard> findByCardNo(String cardNo);
    
    @Query("SELECT c FROM MemberCard c WHERE c.status = 1 AND c.expireTime BETWEEN :now AND :sevenDaysLater")
    List<MemberCard> findExpiringCards(LocalDateTime now, LocalDateTime sevenDaysLater);
    
    @Query("SELECT COUNT(c) FROM MemberCard c WHERE c.memberId = :memberId AND c.status = 1")
    int countActiveCardsByMemberId(Long memberId);

    Optional<MemberCard> findByMemberIdAndStatus(Long memberId, Integer status);
}
