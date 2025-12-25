package dangod.springboot.repository.ticket;

import dangod.springboot.entity.ticket.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByStatus(String status);
    
    List<Ticket> findByAssigneeId(Long assigneeId);
    
    List<Ticket> findByCreatorId(Long creatorId);
    
    List<Ticket> findByCategory(String category);
    
    List<Ticket> findByPriority(Integer priority);
    
    @Query("SELECT COUNT(t) FROM Ticket t")
    Long countTotalTickets();
    
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.status = :status")
    Long countTicketsByStatus(@Param("status") String status);
    
    @Query("SELECT AVG(TIMESTAMPDIFF(SECOND, t.createTime, t.resolveTime)) FROM Ticket t WHERE t.resolveTime IS NOT NULL")
    Double averageResolveTime();
}