package dangod.springboot.repository;

import dangod.springboot.entity.MemberFreezeApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberFreezeApplicationRepository extends JpaRepository<MemberFreezeApplication, Long> {
    List<MemberFreezeApplication> findByMemberId(Long memberId);
    List<MemberFreezeApplication> findByStatus(Integer status);
    List<MemberFreezeApplication> findByMemberIdAndStatus(Long memberId, Integer status);
}
