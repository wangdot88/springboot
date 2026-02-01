package dangod.springboot.repository;

import dangod.springboot.model.CourseBooking;
import dangod.springboot.model.Course;
import dangod.springboot.model.Member;
import dangod.springboot.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseBookingRepository extends JpaRepository<CourseBooking, Long> {
    
    List<CourseBooking> findByMember_Id(Long memberId);
    
    List<CourseBooking> findByCourse_Id(Long courseId);
    
    List<CourseBooking> findByStatus(BookingStatus status);
    
    @Query("SELECT cb FROM CourseBooking cb WHERE cb.member.id = :memberId AND cb.course.id = :courseId")
    Optional<CourseBooking> findByMemberAndCourse(@Param("memberId") Long memberId, 
                                                  @Param("courseId") Long courseId);
    
    @Query("SELECT cb FROM CourseBooking cb WHERE cb.course.id = :courseId AND cb.status = :status ORDER BY cb.queuePosition")
    List<CourseBooking> findWaitingListByCourse(@Param("courseId") Long courseId, 
                                                @Param("status") BookingStatus status);
    
    @Query("SELECT cb FROM CourseBooking cb WHERE cb.course.id = :courseId AND cb.status = :status ORDER BY cb.queuePosition ASC")
    List<CourseBooking> findNextInWaitingList(@Param("courseId") Long courseId, 
                                              @Param("status") BookingStatus status);
    
    @Query("SELECT COUNT(cb) FROM CourseBooking cb WHERE cb.course.id = :courseId AND cb.status = :status")
    Long countBookingsByCourseAndStatus(@Param("courseId") Long courseId, 
                                        @Param("status") BookingStatus status);
    
    @Query("SELECT cb FROM CourseBooking cb WHERE cb.bookingTime >= :startTime AND cb.bookingTime <= :endTime")
    List<CourseBooking> findBookingsBetween(@Param("startTime") LocalDateTime startTime, 
                                             @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT cb FROM CourseBooking cb WHERE cb.member.id = :memberId AND cb.course.startTime >= :now AND cb.status IN :statuses")
    List<CourseBooking> findUpcomingBookingsForMember(@Param("memberId") Long memberId, 
                                                       @Param("now") LocalDateTime now, 
                                                       @Param("statuses") List<BookingStatus> statuses);
}