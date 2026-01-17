package dangod.springboot.repository;

import dangod.springboot.entity.CourseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseTypeRepository extends JpaRepository<CourseType, Long> {
    CourseType findByTypeCode(String typeCode);
    
    CourseType findByTypeName(String typeName);
    
    List<CourseType> findByStatus(Integer status);
    
    List<CourseType> findByDifficulty(String difficulty);
    
    List<CourseType> findBySuitableBodyTypeContaining(String bodyType);
    
    List<CourseType> findByBenefitsContaining(String benefit);
    
    @Query("SELECT ct FROM CourseType ct WHERE ct.status = 1 ORDER BY ct.createTime DESC")
    List<CourseType> findActiveTypes();
    
    @Query("SELECT ct FROM CourseType ct WHERE ct.minAge <= :age AND ct.maxAge >= :age AND ct.status = 1")
    List<CourseType> findSuitableByAge(@Param("age") Integer age);
}
