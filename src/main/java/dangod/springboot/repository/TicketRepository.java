package dangod.springboot.repository;

import dangod.springboot.entity.Ticket;
import dangod.springboot.entity.User;
import dangod.springboot.entity.Ticket.Status;
import dangod.springboot.entity.Ticket.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    List<Ticket> findByCustomerOrderByCreatedAtDesc(User customer);
    
    List<Ticket> findByAssignedAgentOrderByCreatedAtDesc(User assignedAgent);
    
    List<Ticket> findByStatusOrderByCreatedAtDesc(Status status);
    
    List<Ticket> findByStatusInOrderByCreatedAtDesc(List<Status> statuses);
    
    List<Ticket> findByCustomerAndStatusOrderByCreatedAtDesc(User customer, Status status);
    
    List<Ticket> findByAssignedAgentAndStatusOrderByCreatedAtDesc(User assignedAgent, Status status);
    
    @Query("SELECT t FROM Ticket t WHERE t.status = 'PENDING' AND (t.dueDate IS NULL OR t.dueDate > :currentDate) ORDER BY t.priority DESC, t.createdAt ASC")
    List<Ticket> findPendingTicketsOrderByPriorityAndCreatedAt(@Param("currentDate") Date currentDate);
    
    @Query("SELECT t FROM Ticket t WHERE t.status IN ('PENDING', 'IN_PROGRESS') AND t.dueDate < :currentDate")
    List<Ticket> findOverdueTickets(@Param("currentDate") Date currentDate);
    
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.status = :status")
    Long countByStatus(@Param("status") Status status);
    
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.assignedAgent = :agent")
    Long countByAssignedAgent(@Param("agent") User agent);
    
    @Query("SELECT AVG(t.resolvedAt - t.createdAt) FROM Ticket t WHERE t.resolvedAt IS NOT NULL")
    Double findAverageResolutionTime();
    
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.createdAt BETWEEN :startDate AND :endDate")
    Long countTicketsBetweenDates(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}