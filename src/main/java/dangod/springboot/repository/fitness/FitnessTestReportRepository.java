package dangod.springboot.repository.fitness;

import dangod.springboot.entity.fitness.FitnessTestReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FitnessTestReportRepository extends JpaRepository<FitnessTestReport, Long> {

    List<FitnessTestReport> findBySemester(String semester);

    List<FitnessTestReport> findByClazzId(Long classId);

    FitnessTestReport findByClazzIdAndSemester(Long classId, String semester);
}
