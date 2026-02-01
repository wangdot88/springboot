package dangod.springboot.repository;

import dangod.springboot.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation> {
    Optional<Reservation> findByReservationNo(String reservationNo);
    List<Reservation> findByMemberIdAndStatus(Long memberId, Integer status);
    List<Reservation> findByCourseId(Long courseId);
    
    @Query("SELECT r FROM Reservation r WHERE r.memberId = :memberId AND r.status IN (1, 2) AND " +
           "EXISTS (SELECT c FROM Course c WHERE c.id = r.courseId AND " +
           "((c.startTime <= :start AND c.endTime > :start) OR (c.startTime < :end AND c.endTime >= :end) OR " +
           "(c.startTime >= :start AND c.endTime <= :end)))")
    List<Reservation> findConflictingReservations(Long memberId, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.memberId = :memberId AND r.status = 1 AND " +
           "r.reservationTime >= :weekStart AND r.reservationTime < :weekEnd")
    int countReservationsThisWeek(Long memberId, LocalDateTime weekStart, LocalDateTime weekEnd);
    
    @Query("SELECT r FROM Reservation r WHERE r.status = 1 AND EXISTS " +
           "(SELECT c FROM Course c WHERE c.id = r.courseId AND c.startTime BETWEEN :now AND :oneHourLater)")
    List<Reservation> findReservationsNeedingReminder(LocalDateTime now, LocalDateTime oneHourLater);

    Optional<Reservation> findByMemberIdAndCourseId(Long memberId, Long courseId);
}
