package dangod.springboot.repository;

import dangod.springboot.entity.MemberCardLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberCardLevelRepository extends JpaRepository<MemberCardLevel, Long> {
    MemberCardLevel findByLevelCode(String levelCode);
    MemberCardLevel findByLevelName(String levelName);
}
