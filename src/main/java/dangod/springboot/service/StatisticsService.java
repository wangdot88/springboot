package dangod.springboot.service;

import dangod.springboot.model.EquipmentUsage;
import dangod.springboot.dto.EquipmentUsageDto;
import dangod.springboot.dto.RenewalPredictionDto;
import dangod.springboot.dto.CoursePopularityDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface StatisticsService {
    
    EquipmentUsage recordEquipmentUsage(EquipmentUsageDto usageDto);
    
    List<EquipmentUsage> getEquipmentUsagesByEquipment(String equipmentId);
    
    List<EquipmentUsage> getEquipmentUsagesBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    List<EquipmentUsage> getEquipmentUsagesBetweenByStore(LocalDateTime startTime, LocalDateTime endTime, String storeId);
    
    Map<String, Object> getEquipmentUsageStats(LocalDateTime startTime, LocalDateTime endTime);
    
    Map<String, Object> getEquipmentUsageStatsByStore(LocalDateTime startTime, LocalDateTime endTime, String storeId);
    
    Map<String, Object> getSpecificEquipmentUsageStats(String equipmentId, LocalDateTime startTime, LocalDateTime endTime);
    
    List<RenewalPredictionDto> predictMemberRenewals();
    
    List<RenewalPredictionDto> predictMemberRenewalsByStore(String storeId);
    
    List<RenewalPredictionDto> predictMemberRenewalsByRiskLevel(String riskLevel);
    
    List<CoursePopularityDto> getCoursePopularityRanking();
    
    List<CoursePopularityDto> getCoursePopularityRankingByStore(String storeId);
    
    List<CoursePopularityDto> getCoursePopularityRankingByCategory(String category);
    
    List<CoursePopularityDto> getCoursePopularityRankingByTimeRange(LocalDateTime startTime, LocalDateTime endTime);
    
    Map<String, Object> getOverallStatistics();
    
    Map<String, Object> getStoreStatistics(String storeId);
    
    Map<String, Object> getMemberStatistics(Long memberId);
    
    Map<String, Object> getTrainerStatistics(Long trainerId);
    
    Map<String, Object> getCourseStatistics(Long courseId);
    
    void deleteEquipmentUsage(Long usageId);
}