package dangod.springboot.repository;

import dangod.springboot.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, JpaSpecificationExecutor<Member> {
    Optional<Member> findByMemberNo(String memberNo);
    Optional<Member> findByPhone(String phone);
    boolean existsByPhone(String phone);
}
