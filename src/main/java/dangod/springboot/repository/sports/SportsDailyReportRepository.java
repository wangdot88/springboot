package dangod.springboot.repository.sports;

import dangod.springboot.entity.sports.SportsDailyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SportsDailyReportRepository extends JpaRepository<SportsDailyReport, Long> {

    List<SportsDailyReport> findByReportDate(LocalDate reportDate);

    List<SportsDailyReport> findByClazzId(Long classId);

    SportsDailyReport findByClazzIdAndReportDate(Long classId, LocalDate reportDate);

    List<SportsDailyReport> findByReportDateBetween(LocalDate startDate, LocalDate endDate);
}
