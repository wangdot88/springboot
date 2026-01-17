package dangod.springboot.repository;

import dangod.springboot.entity.GymBranch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GymBranchRepository extends JpaRepository<GymBranch, Long> {
    List<GymBranch> findByStatus(Integer status);
    List<GymBranch> findByNameContaining(String name);
}
