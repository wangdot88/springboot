package dangod.springboot.repository;

import dangod.springboot.entity.CourseBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseBookingRepository extends JpaRepository<CourseBooking, Long> {

    List<CourseBooking> findByMemberId(Long memberId);

    List<CourseBooking> findByScheduleId(Long scheduleId);

    List<CourseBooking> findByStatus(CourseBooking.BookingStatus status);

    @Query("SELECT cb FROM CourseBooking cb WHERE cb.memberId = :memberId AND cb.scheduleId = :scheduleId")
    CourseBooking findByMemberIdAndScheduleId(@Param("memberId") Long memberId, @Param("scheduleId") Long scheduleId);

    @Query("SELECT cb FROM CourseBooking cb WHERE cb.scheduleId = :scheduleId AND cb.status = 'QUEUED' ORDER BY cb.queuePosition ASC")
    List<CourseBooking> findQueuedBookings(@Param("scheduleId") Long scheduleId);

    @Query("SELECT COUNT(cb) FROM CourseBooking cb WHERE cb.scheduleId = :scheduleId AND cb.status = 'CONFIRMED'")
    Integer countConfirmedBookings(@Param("scheduleId") Long scheduleId);

    @Query("SELECT cb FROM CourseBooking cb WHERE cb.memberId = :memberId AND cb.scheduleId IN (SELECT cs.id FROM CourseSchedule cs WHERE cs.startTime > :now)")
    List<CourseBooking> findUpcomingBookings(@Param("memberId") Long memberId, @Param("now") java.util.Date now);
}
