package dangod.springboot.controller.ticket;

import dangod.springboot.entity.ticket.Ticket;
import dangod.springboot.entity.ticket.TicketEvaluation;
import dangod.springboot.entity.ticket.TicketReply;
import dangod.springboot.service.ticket.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    @Autowired
    private TicketService ticketService;

    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody Ticket ticket) {
        Ticket createdTicket = ticketService.createTicket(ticket);
        return new ResponseEntity<>(createdTicket, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        Ticket ticket = ticketService.getTicketById(id);
        if (ticket != null) {
            return new ResponseEntity<>(ticket, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        List<Ticket> tickets = ticketService.getAllTickets();
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Ticket>> getTicketsByStatus(@PathVariable String status) {
        List<Ticket> tickets = ticketService.getTicketsByStatus(status);
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }

    @GetMapping("/assignee/{assigneeId}")
    public ResponseEntity<List<Ticket>> getTicketsByAssigneeId(@PathVariable Long assigneeId) {
        List<Ticket> tickets = ticketService.getTicketsByAssigneeId(assigneeId);
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }

    @GetMapping("/creator/{creatorId}")
    public ResponseEntity<List<Ticket>> getTicketsByCreatorId(@PathVariable Long creatorId) {
        List<Ticket> tickets = ticketService.getTicketsByCreatorId(creatorId);
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ticket> updateTicket(@PathVariable Long id, @RequestBody Ticket ticket) {
        ticket.setId(id);
        Ticket updatedTicket = ticketService.updateTicket(ticket);
        if (updatedTicket != null) {
            return new ResponseEntity<>(updatedTicket, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<Ticket> assignTicket(@PathVariable Long id, @RequestParam Long assigneeId) {
        Ticket assignedTicket = ticketService.assignTicket(id, assigneeId);
        if (assignedTicket != null) {
            return new ResponseEntity<>(assignedTicket, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}/transfer")
    public ResponseEntity<Ticket> transferTicket(@PathVariable Long id, @RequestParam Long newAssigneeId) {
        Ticket transferredTicket = ticketService.transferTicket(id, newAssigneeId);
        if (transferredTicket != null) {
            return new ResponseEntity<>(transferredTicket, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<Ticket> startProcessing(@PathVariable Long id) {
        Ticket ticket = ticketService.startProcessing(id);
        if (ticket != null) {
            return new ResponseEntity<>(ticket, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<Ticket> resolveTicket(@PathVariable Long id) {
        Ticket ticket = ticketService.resolveTicket(id);
        if (ticket != null) {
            return new ResponseEntity<>(ticket, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<Ticket> closeTicket(@PathVariable Long id) {
        Ticket ticket = ticketService.closeTicket(id);
        if (ticket != null) {
            return new ResponseEntity<>(ticket, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{id}/replies")
    public ResponseEntity<TicketReply> addReply(@PathVariable Long id, @RequestBody TicketReply reply) {
        Ticket ticket = ticketService.getTicketById(id);
        if (ticket != null) {
            reply.setTicket(ticket);
            TicketReply createdReply = ticketService.addReply(reply);
            return new ResponseEntity<>(createdReply, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}/replies")
    public ResponseEntity<List<TicketReply>> getRepliesByTicketId(@PathVariable Long id) {
        List<TicketReply> replies = ticketService.getRepliesByTicketId(id);
        return new ResponseEntity<>(replies, HttpStatus.OK);
    }

    @PostMapping("/{id}/evaluation")
    public ResponseEntity<TicketEvaluation> addEvaluation(@PathVariable Long id, @RequestBody TicketEvaluation evaluation) {
        Ticket ticket = ticketService.getTicketById(id);
        if (ticket != null) {
            evaluation.setTicket(ticket);
            TicketEvaluation createdEvaluation = ticketService.addEvaluation(evaluation);
            return new ResponseEntity<>(createdEvaluation, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}/evaluation")
    public ResponseEntity<TicketEvaluation> getEvaluationByTicketId(@PathVariable Long id) {
        TicketEvaluation evaluation = ticketService.getEvaluationByTicketId(id);
        if (evaluation != null) {
            return new ResponseEntity<>(evaluation, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/stats/total")
    public ResponseEntity<Long> countTotalTickets() {
        Long count = ticketService.countTotalTickets();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("/stats/status/{status}")
    public ResponseEntity<Long> countTicketsByStatus(@PathVariable String status) {
        Long count = ticketService.countTicketsByStatus(status);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("/stats/average-resolve-time")
    public ResponseEntity<Double> averageResolveTime() {
        Double average = ticketService.averageResolveTime();
        return new ResponseEntity<>(average, HttpStatus.OK);
    }
}