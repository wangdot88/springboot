package dangod.springboot.repository;

import dangod.springboot.entity.CourseSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CourseScheduleRepository extends JpaRepository<CourseSchedule, Long> {

    List<CourseSchedule> findByCourseId(Long courseId);

    List<CourseSchedule> findByStoreId(Long storeId);

    List<CourseSchedule> findByStatus(CourseSchedule.ScheduleStatus status);

    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.storeId = :storeId AND cs.startTime BETWEEN :startDate AND :endDate")
    List<CourseSchedule> findSchedulesByStoreAndDateRange(@Param("storeId") Long storeId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.courseId = :courseId AND cs.startTime > :now ORDER BY cs.startTime ASC")
    List<CourseSchedule> findUpcomingSchedules(@Param("courseId") Long courseId, @Param("now") Date now);
}
