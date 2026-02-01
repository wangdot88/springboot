package dangod.springboot.repository;

import dangod.springboot.entity.CardFreezeApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CardFreezeApprovalRepository extends JpaRepository<CardFreezeApproval, Long>, JpaSpecificationExecutor<CardFreezeApproval> {
    List<CardFreezeApproval> findByCardIdAndApproveResult(Long cardId, Integer approveResult);
    List<CardFreezeApproval> findByApproveResult(Integer approveResult);
}
