package dangod.springboot.repository;

import dangod.springboot.entity.MemberCourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberCourseEnrollmentRepository extends JpaRepository<MemberCourseEnrollment, Long> {
    List<MemberCourseEnrollment> findByMemberId(Long memberId);
    List<MemberCourseEnrollment> findByScheduleId(Long scheduleId);
    Optional<MemberCourseEnrollment> findByMemberIdAndScheduleId(Long memberId, Long scheduleId);
    List<MemberCourseEnrollment> findByMemberIdAndStatus(Long memberId, Integer status);
    List<MemberCourseEnrollment> findByScheduleIdAndStatus(Long scheduleId, Integer status);

    @Query("SELECT e FROM MemberCourseEnrollment e WHERE e.memberId = :memberId AND e.status = 0 AND e.queuePosition > 0")
    List<MemberCourseEnrollment> findMemberQueueingCourses(@Param("memberId") Long memberId);

    @Query("SELECT e FROM MemberCourseEnrollment e WHERE e.scheduleId = :scheduleId AND e.status = 0 AND e.queuePosition > 0 ORDER BY e.queuePosition ASC")
    List<MemberCourseEnrollment> getCourseQueueList(@Param("scheduleId") Long scheduleId);

    @Query("SELECT COUNT(e) FROM MemberCourseEnrollment e WHERE e.scheduleId = :scheduleId AND e.status = 0 AND e.queuePosition = 0")
    Long countEnrolledMembers(@Param("scheduleId") Long scheduleId);

    @Query("SELECT e FROM MemberCourseEnrollment e WHERE e.memberId = :memberId AND e.status = 0 AND e.queuePosition = 0 AND EXISTS (SELECT cs FROM CourseSchedule cs WHERE cs.id = e.scheduleId AND cs.scheduleDate = :date)")
    List<MemberCourseEnrollment> findMemberCoursesOnDate(@Param("memberId") Long memberId, @Param("date") Date date);

    @Query("SELECT MAX(e.queuePosition) FROM MemberCourseEnrollment e WHERE e.scheduleId = :scheduleId")
    Integer getMaxQueuePosition(@Param("scheduleId") Long scheduleId);

    @Query("SELECT COUNT(e) FROM MemberCourseEnrollment e WHERE e.enrollTime BETWEEN :startDate AND :endDate")
    Long countByEnrollTimeBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT e.memberId, COUNT(e) FROM MemberCourseEnrollment e WHERE e.enrollTime BETWEEN :startDate AND :endDate GROUP BY e.memberId ORDER BY COUNT(e) DESC")
    List<Object[]> countEnrollmentsByMember(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT e FROM MemberCourseEnrollment e WHERE e.id = :id")
    Optional<MemberCourseEnrollment> findById(@Param("id") Long id);
}
