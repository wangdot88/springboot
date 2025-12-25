package dangod.springboot.repository.ticket;

import dangod.springboot.entity.ticket.TicketEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketEvaluationRepository extends JpaRepository<TicketEvaluation, Long> {
    TicketEvaluation findByTicketId(Long ticketId);
}