package dangod.springboot.repository;

import dangod.springboot.entity.Ticket;
import dangod.springboot.entity.TicketReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketReviewRepository extends JpaRepository<TicketReview, Long> {
    
    Optional<TicketReview> findByTicket(Ticket ticket);
    
    boolean existsByTicket(Ticket ticket);
}