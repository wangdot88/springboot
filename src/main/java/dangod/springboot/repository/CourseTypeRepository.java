package dangod.springboot.repository;

import dangod.springboot.entity.CourseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseTypeRepository extends JpaRepository<CourseType, Long> {
    
    Optional<CourseType> findByTypeName(String typeName);
    
    List<CourseType> findByStatus(Integer status);
    
    List<CourseType> findByGymIdAndStatus(Long gymId, Integer status);
}
