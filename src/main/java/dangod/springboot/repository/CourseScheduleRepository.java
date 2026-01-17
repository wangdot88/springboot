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
    List<CourseSchedule> findByBranchId(Long branchId);
    List<CourseSchedule> findByCourseId(Long courseId);
    List<CourseSchedule> findByScheduleDate(Date scheduleDate);
    List<CourseSchedule> findByBranchIdAndScheduleDate(Long branchId, Date scheduleDate);
    List<CourseSchedule> findByStatus(Integer status);
    List<CourseSchedule> findByBranchIdAndStatus(Long branchId, Integer status);

    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.scheduleDate >= :startDate AND cs.scheduleDate <= :endDate AND cs.branchId = :branchId")
    List<CourseSchedule> findSchedulesInDateRange(@Param("branchId") Long branchId, 
                                                  @Param("startDate") Date startDate, 
                                                  @Param("endDate") Date endDate);

    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.status = 1 AND cs.scheduleDate >= :date")
    List<CourseSchedule> findUpcomingSchedules(@Param("date") Date date);

    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.courseId = :courseId AND cs.scheduleDate = :date AND cs.status = 1")
    List<CourseSchedule> findSchedulesForCourseOnDate(@Param("courseId") Long courseId, @Param("date") Date date);

    @Query("SELECT COUNT(cs) FROM CourseSchedule cs WHERE cs.branchId = :branchId AND cs.scheduleDate BETWEEN :startDate AND :endDate")
    Long countByBranchIdAndScheduleDateBetween(@Param("branchId") Long branchId, 
                                                @Param("startDate") Date startDate, 
                                                @Param("endDate") Date endDate);

    @Query("SELECT COUNT(cs) FROM CourseSchedule cs WHERE cs.scheduleDate = :date")
    Long countByScheduleDate(@Param("date") Date date);

    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.id = :id")
    Optional<CourseSchedule> findById(@Param("id") Long id);
}
