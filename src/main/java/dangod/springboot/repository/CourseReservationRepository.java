package dangod.springboot.repository;

import dangod.springboot.entity.Course;
import dangod.springboot.entity.CourseReservation;
import dangod.springboot.entity.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CourseReservationRepository extends JpaRepository<CourseReservation, Long> {
    @Query("SELECT COUNT(cr) FROM CourseReservation cr WHERE cr.member = :member AND cr.attended = true")
    int countByMemberAndAttendedTrue(@Param("member") Member member);
    
    @Query("SELECT cr FROM CourseReservation cr WHERE cr.member = :member ORDER BY cr.reservationTime DESC")
    List<CourseReservation> findByMemberOrderByReservationTimeDesc(@Param("member") Member member, Pageable pageable);
    
    @Query("SELECT cr.course, COUNT(cr) FROM CourseReservation cr WHERE cr.reservationTime BETWEEN :startDate AND :endDate GROUP BY cr.course ORDER BY COUNT(cr) DESC")
    List<Object[]> countReservationsByCourse(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    long countByReservationTimeAfter(LocalDateTime dateTime);
    
    long countByAttendedTrueAndReservationTimeAfter(LocalDateTime dateTime);
    
    long countByReservationTimeBetween(LocalDateTime start, LocalDateTime end);
    List<CourseReservation> findByMemberId(Long memberId);
    
    List<CourseReservation> findByCourseId(Long courseId);
    
    CourseReservation findByMemberIdAndCourseId(Long memberId, Long courseId);
    
    List<CourseReservation> findByMemberIdAndStatus(Long memberId, Integer status);
    
    List<CourseReservation> findByCourseIdAndStatus(Long courseId, Integer status);
    
    @Query("SELECT cr FROM CourseReservation cr WHERE cr.memberId = :memberId AND cr.status IN (0, 1) AND cr.courseDate >= :now ORDER BY cr.courseDate ASC")
    List<CourseReservation> findUpcomingReservations(@Param("memberId") Long memberId, @Param("now") LocalDateTime now);
    
    @Query("SELECT cr FROM CourseReservation cr WHERE cr.courseId = :courseId AND cr.status = 0 ORDER BY cr.createTime ASC")
    List<CourseReservation> findWaitingListByCourse(@Param("courseId") Long courseId);
    
    @Query("SELECT COUNT(cr) FROM CourseReservation cr WHERE cr.courseId = :courseId AND cr.status = 1")
    long countConfirmedReservations(@Param("courseId") Long courseId);
    
    @Query("SELECT COUNT(cr) FROM CourseReservation cr WHERE cr.courseId = :courseId AND cr.status = 0")
    long countWaitingReservations(@Param("courseId") Long courseId);
    
    @Query("SELECT cr FROM CourseReservation cr WHERE cr.courseId = :courseId AND cr.status = 0 ORDER BY cr.createTime ASC")
    List<CourseReservation> findFirstInWaitingList(@Param("courseId") Long courseId);
    
    @Query("SELECT COUNT(cr) FROM CourseReservation cr WHERE cr.memberId = :memberId AND cr.status = 1 AND cr.courseDate >= :startDate AND cr.courseDate <= :endDate")
    long countMemberReservationsInPeriod(@Param("memberId") Long memberId, 
                                         @Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT cr FROM CourseReservation cr WHERE cr.courseId = :courseId AND cr.status IN (0, 1) ORDER BY cr.createTime ASC")
    List<CourseReservation> findAllReservationsForCourse(@Param("courseId") Long courseId);
}
