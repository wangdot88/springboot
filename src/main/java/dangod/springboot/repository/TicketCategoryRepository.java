package dangod.springboot.repository;

import dangod.springboot.entity.TicketCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketCategoryRepository extends JpaRepository<TicketCategory, Long> {
    
    List<TicketCategory> findByIsActiveTrue();
    
    List<TicketCategory> findByIsActiveTrueOrderByName();
}