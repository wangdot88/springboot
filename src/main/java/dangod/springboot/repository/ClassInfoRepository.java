package dangod.springboot.repository;

import dangod.springboot.entity.ClassInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassInfoRepository extends JpaRepository<ClassInfo, Long> {

    ClassInfo findByClassName(String className);

    List<ClassInfo> findByGrade(String grade);

    List<ClassInfo> findByMajor(String major);

    @Query("SELECT c FROM ClassInfo c WHERE c.className LIKE %:keyword% OR c.grade LIKE %:keyword% OR c.major LIKE %:keyword%")
    List<ClassInfo> searchByKeyword(@Param("keyword") String keyword);
}
