package dangod.springboot.repository;

import dangod.springboot.entity.FitnessMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FitnessMetricRepository extends JpaRepository<FitnessMetric, Long> {
    Optional<FitnessMetric> findByMetricCode(String metricCode);
}