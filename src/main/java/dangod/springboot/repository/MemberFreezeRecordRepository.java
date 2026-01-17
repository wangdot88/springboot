package dangod.springboot.repository;

import dangod.springboot.entity.MemberFreezeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberFreezeRecordRepository extends JpaRepository<MemberFreezeRecord, Long> {
    List<MemberFreezeRecord> findByMemberId(Long memberId);
    List<MemberFreezeRecord> findByStatus(Integer status);
    List<MemberFreezeRecord> findByMemberIdAndStatus(Long memberId, Integer status);
}
