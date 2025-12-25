package dangod.springboot.repository;

import dangod.springboot.entity.Ticket;
import dangod.springboot.entity.TicketReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketReplyRepository extends JpaRepository<TicketReply, Long> {
    
    List<TicketReply> findByTicketOrderByCreatedAtAsc(Ticket ticket);
    
    List<TicketReply> findByTicketAndIsInternalFalseOrderByCreatedAtAsc(Ticket ticket);
}