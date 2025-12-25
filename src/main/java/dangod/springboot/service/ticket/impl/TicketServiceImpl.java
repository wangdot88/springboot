package dangod.springboot.service.ticket.impl;

import dangod.springboot.entity.ticket.Ticket;
import dangod.springboot.entity.ticket.TicketEvaluation;
import dangod.springboot.entity.ticket.TicketReply;
import dangod.springboot.repository.ticket.TicketEvaluationRepository;
import dangod.springboot.repository.ticket.TicketReplyRepository;
import dangod.springboot.repository.ticket.TicketRepository;
import dangod.springboot.service.ticket.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class TicketServiceImpl implements TicketService {
    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private TicketReplyRepository ticketReplyRepository;
    
    @Autowired
    private TicketEvaluationRepository ticketEvaluationRepository;

    @Override
    public Ticket createTicket(Ticket ticket) {
        ticket.setStatus("待处理");
        ticket.setCreateTime(new Date());
        ticket.setUpdateTime(new Date());
        return ticketRepository.save(ticket);
    }

    @Override
    public Ticket getTicketById(Long id) {
        return ticketRepository.findOne(id);
    }

    @Override
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @Override
    public List<Ticket> getTicketsByStatus(String status) {
        return ticketRepository.findByStatus(status);
    }

    @Override
    public List<Ticket> getTicketsByAssigneeId(Long assigneeId) {
        return ticketRepository.findByAssigneeId(assigneeId);
    }

    @Override
    public List<Ticket> getTicketsByCreatorId(Long creatorId) {
        return ticketRepository.findByCreatorId(creatorId);
    }

    @Override
    public Ticket updateTicket(Ticket ticket) {
        ticket.setUpdateTime(new Date());
        return ticketRepository.save(ticket);
    }

    @Override
    public Ticket assignTicket(Long ticketId, Long assigneeId) {
        Ticket ticket = ticketRepository.findOne(ticketId);
        if (ticket != null) {
            ticket.setAssigneeId(assigneeId);
            ticket.setUpdateTime(new Date());
            return ticketRepository.save(ticket);
        }
        return null;
    }

    @Override
    public Ticket transferTicket(Long ticketId, Long newAssigneeId) {
        Ticket ticket = ticketRepository.findOne(ticketId);
        if (ticket != null) {
            ticket.setAssigneeId(newAssigneeId);
            ticket.setUpdateTime(new Date());
            return ticketRepository.save(ticket);
        }
        return null;
    }

    @Override
    public Ticket startProcessing(Long ticketId) {
        Ticket ticket = ticketRepository.findOne(ticketId);
        if (ticket != null) {
            ticket.setStatus("处理中");
            ticket.setUpdateTime(new Date());
            return ticketRepository.save(ticket);
        }
        return null;
    }

    @Override
    public Ticket resolveTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findOne(ticketId);
        if (ticket != null) {
            ticket.setStatus("已解决");
            ticket.setResolveTime(new Date());
            ticket.setUpdateTime(new Date());
            return ticketRepository.save(ticket);
        }
        return null;
    }

    @Override
    public Ticket closeTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findOne(ticketId);
        if (ticket != null) {
            ticket.setStatus("已关闭");
            ticket.setCloseTime(new Date());
            ticket.setUpdateTime(new Date());
            return ticketRepository.save(ticket);
        }
        return null;
    }

    @Override
    public TicketReply addReply(TicketReply reply) {
        reply.setCreateTime(new Date());
        return ticketReplyRepository.save(reply);
    }

    @Override
    public List<TicketReply> getRepliesByTicketId(Long ticketId) {
        return ticketReplyRepository.findByTicketId(ticketId);
    }

    @Override
    public TicketEvaluation addEvaluation(TicketEvaluation evaluation) {
        evaluation.setEvaluateTime(new Date());
        return ticketEvaluationRepository.save(evaluation);
    }

    @Override
    public TicketEvaluation getEvaluationByTicketId(Long ticketId) {
        return ticketEvaluationRepository.findByTicketId(ticketId);
    }

    @Override
    public Long countTotalTickets() {
        return ticketRepository.countTotalTickets();
    }

    @Override
    public Long countTicketsByStatus(String status) {
        return ticketRepository.countTicketsByStatus(status);
    }

    @Override
    public Double averageResolveTime() {
        return ticketRepository.averageResolveTime();
    }
}