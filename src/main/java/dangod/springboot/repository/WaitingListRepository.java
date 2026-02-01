package dangod.springboot.repository;

import dangod.springboot.entity.WaitingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WaitingListRepository extends JpaRepository<WaitingList, Long> {
    
    List<WaitingList> findByCourseIdOrderByCreatedAtAsc(Long courseId);
    
    Optional<WaitingList> findFirstByCourseIdOrderByCreatedAtAsc(Long courseId);
    
    List<WaitingList> findByMemberId(Long memberId);
    
    Optional<WaitingList> findByMemberIdAndCourseId(Long memberId, Long courseId);
    
    @Query("SELECT w FROM WaitingList w WHERE w.status = 0 AND w.courseId = :courseId ORDER BY w.createdAt ASC")
    List<WaitingList> findActiveByCourseIdOrderByCreatedAtAsc(Long courseId);
    
    @Query("SELECT COUNT(w) FROM WaitingList w WHERE w.memberId = :memberId AND w.status = 0")
    int countActiveByMemberId(Long memberId);
}
