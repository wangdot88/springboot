package dangod.springboot.repository;

import dangod.springboot.model.MemberBenefit;
import dangod.springboot.enums.MemberCardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberBenefitRepository extends JpaRepository<MemberBenefit, Long> {
    
    List<MemberBenefit> findByCardType(MemberCardType cardType);
    
    List<MemberBenefit> findByCardTypeAndIsActive(MemberCardType cardType, Boolean isActive);
    
    List<MemberBenefit> findByIsActive(Boolean isActive);
}