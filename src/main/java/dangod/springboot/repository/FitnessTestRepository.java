package dangod.springboot.repository;

import dangod.springboot.model.FitnessTest;
import dangod.springboot.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FitnessTestRepository extends JpaRepository<FitnessTest, Long> {
    
    List<FitnessTest> findByMember_Id(Long memberId);
    
    @Query("SELECT ft FROM FitnessTest ft WHERE ft.member.id = :memberId ORDER BY ft.testDate DESC")
    List<FitnessTest> findByMemberIdOrderByDateDesc(@Param("memberId") Long memberId);
    
    @Query("SELECT ft FROM FitnessTest ft WHERE ft.testDate BETWEEN :startDate AND :endDate")
    List<FitnessTest> findTestsBetween(@Param("startDate") LocalDate startDate, 
                                        @Param("endDate") LocalDate endDate);
    
    @Query("SELECT ft FROM FitnessTest ft WHERE ft.testDate = :date")
    List<FitnessTest> findTestsByDate(@Param("date") LocalDate date);
    
    @Query("SELECT ft FROM FitnessTest ft WHERE ft.weight < :minWeight OR ft.weight > :maxWeight")
    List<FitnessTest> findAbnormalWeight(@Param("minWeight") Double minWeight, 
                                          @Param("maxWeight") Double maxWeight);
    
    @Query("SELECT ft FROM FitnessTest ft WHERE ft.bodyFatPercentage < :minBfp OR ft.bodyFatPercentage > :maxBfp")
    List<FitnessTest> findAbnormalBodyFat(@Param("minBfp") Double minBfp, 
                                           @Param("maxBfp") Double maxBfp);
    
    @Query("SELECT ft FROM FitnessTest ft WHERE ft.heartRate < :minHr OR ft.heartRate > :maxHr")
    List<FitnessTest> findAbnormalHeartRate(@Param("minHr") Integer minHr, 
                                             @Param("maxHr") Integer maxHr);
    
    @Query("SELECT ft FROM FitnessTest ft WHERE ft.bloodPressureSystolic > :maxSystolic OR ft.bloodPressureDiastolic > :maxDiastolic")
    List<FitnessTest> findAbnormalBloodPressure(@Param("maxSystolic") Integer maxSystolic, 
                                                 @Param("maxDiastolic") Integer maxDiastolic);
    
    @Query("SELECT ft FROM FitnessTest ft WHERE ft.bmi < :minBmi OR ft.bmi > :maxBmi")
    List<FitnessTest> findAbnormalBMI(@Param("minBmi") Double minBmi, 
                                       @Param("maxBmi") Double maxBmi);
    
    @Query("SELECT ft FROM FitnessTest ft WHERE ft.testDate = (SELECT MAX(f.testDate) FROM FitnessTest f WHERE f.member.id = :memberId)")
    FitnessTest findLatestTestForMember(@Param("memberId") Long memberId);
}