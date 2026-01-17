package dangod.springboot.repository;

import dangod.springboot.entity.BodyMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface BodyMeasurementRepository extends JpaRepository<BodyMeasurement, Long> {

    List<BodyMeasurement> findByMemberId(Long memberId);

    @Query("SELECT bm FROM BodyMeasurement bm WHERE bm.memberId = :memberId ORDER BY bm.createTime DESC")
    List<BodyMeasurement> findByMemberIdOrderByCreateTimeDesc(@Param("memberId") Long memberId);

    @Query("SELECT bm FROM BodyMeasurement bm WHERE bm.memberId = :memberId AND bm.createTime BETWEEN :startDate AND :endDate ORDER BY bm.createTime ASC")
    List<BodyMeasurement> findByMemberIdAndDateRange(@Param("memberId") Long memberId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT bm FROM BodyMeasurement bm WHERE bm.hasAbnormalIndicator = true")
    List<BodyMeasurement> findAbnormalMeasurements();

    List<BodyMeasurement> findByCreateTimeAfter(Date date);
}
