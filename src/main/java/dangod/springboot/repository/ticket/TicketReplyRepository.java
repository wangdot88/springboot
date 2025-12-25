package dangod.springboot.repository.ticket;

import dangod.springboot.entity.ticket.TicketReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketReplyRepository extends JpaRepository<TicketReply, Long> {
    List<TicketReply> findByTicketId(Long ticketId);
}