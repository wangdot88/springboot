package dangod.springboot.repository;

import dangod.springboot.entity.ApprovalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalRecordRepository extends JpaRepository<ApprovalRecord, Long> {

    List<ApprovalRecord> findByMemberId(Long memberId);

    List<ApprovalRecord> findByStatus(ApprovalRecord.ApprovalStatus status);

    List<ApprovalRecord> findByType(ApprovalRecord.ApprovalType type);

    @Query("SELECT ar FROM ApprovalRecord ar WHERE ar.status = 'PENDING' ORDER BY ar.createTime ASC")
    List<ApprovalRecord> findPendingApprovals();
}
