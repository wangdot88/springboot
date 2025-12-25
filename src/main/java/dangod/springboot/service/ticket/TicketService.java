package dangod.springboot.service.ticket;

import dangod.springboot.entity.ticket.Ticket;
import dangod.springboot.entity.ticket.TicketEvaluation;
import dangod.springboot.entity.ticket.TicketReply;

import java.util.List;

public interface TicketService {
    Ticket createTicket(Ticket ticket);
    
    Ticket getTicketById(Long id);
    
    List<Ticket> getAllTickets();
    
    List<Ticket> getTicketsByStatus(String status);
    
    List<Ticket> getTicketsByAssigneeId(Long assigneeId);
    
    List<Ticket> getTicketsByCreatorId(Long creatorId);
    
    Ticket updateTicket(Ticket ticket);
    
    Ticket assignTicket(Long ticketId, Long assigneeId);
    
    Ticket transferTicket(Long ticketId, Long newAssigneeId);
    
    Ticket startProcessing(Long ticketId);
    
    Ticket resolveTicket(Long ticketId);
    
    Ticket closeTicket(Long ticketId);
    
    TicketReply addReply(TicketReply reply);
    
    List<TicketReply> getRepliesByTicketId(Long ticketId);
    
    TicketEvaluation addEvaluation(TicketEvaluation evaluation);
    
    TicketEvaluation getEvaluationByTicketId(Long ticketId);
    
    Long countTotalTickets();
    
    Long countTicketsByStatus(String status);
    
    Double averageResolveTime();
}