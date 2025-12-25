package dangod.springboot.service.ticket;

import dangod.springboot.entity.ticket.Ticket;
import dangod.springboot.entity.ticket.TicketEvaluation;
import dangod.springboot.entity.ticket.TicketReply;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TicketServiceTest {
    @Autowired
    private TicketService ticketService;

    @Test
    public void testCreateTicket() {
        Ticket ticket = new Ticket();
        ticket.setTitle("测试工单");
        ticket.setContent("这是一个测试工单");
        ticket.setCategory("技术支持");
        ticket.setPriority(1);
        ticket.setCreatorId(1L);
        
        Ticket createdTicket = ticketService.createTicket(ticket);
        assertNotNull(createdTicket);
        assertEquals("待处理", createdTicket.getStatus());
        assertNotNull(createdTicket.getCreateTime());
    }

    @Test
    public void testAssignTicket() {
        Ticket ticket = new Ticket();
        ticket.setTitle("测试工单");
        ticket.setContent("这是一个测试工单");
        ticket.setCategory("技术支持");
        ticket.setPriority(1);
        ticket.setCreatorId(1L);
        
        Ticket createdTicket = ticketService.createTicket(ticket);
        Ticket assignedTicket = ticketService.assignTicket(createdTicket.getId(), 2L);
        assertNotNull(assignedTicket);
        assertEquals(2L, assignedTicket.getAssigneeId().longValue());
    }

    @Test
    public void testStartProcessing() {
        Ticket ticket = new Ticket();
        ticket.setTitle("测试工单");
        ticket.setContent("这是一个测试工单");
        ticket.setCategory("技术支持");
        ticket.setPriority(1);
        ticket.setCreatorId(1L);
        
        Ticket createdTicket = ticketService.createTicket(ticket);
        Ticket processingTicket = ticketService.startProcessing(createdTicket.getId());
        assertNotNull(processingTicket);
        assertEquals("处理中", processingTicket.getStatus());
    }

    @Test
    public void testResolveTicket() {
        Ticket ticket = new Ticket();
        ticket.setTitle("测试工单");
        ticket.setContent("这是一个测试工单");
        ticket.setCategory("技术支持");
        ticket.setPriority(1);
        ticket.setCreatorId(1L);
        
        Ticket createdTicket = ticketService.createTicket(ticket);
        Ticket resolvedTicket = ticketService.resolveTicket(createdTicket.getId());
        assertNotNull(resolvedTicket);
        assertEquals("已解决", resolvedTicket.getStatus());
        assertNotNull(resolvedTicket.getResolveTime());
    }

    @Test
    public void testCloseTicket() {
        Ticket ticket = new Ticket();
        ticket.setTitle("测试工单");
        ticket.setContent("这是一个测试工单");
        ticket.setCategory("技术支持");
        ticket.setPriority(1);
        ticket.setCreatorId(1L);
        
        Ticket createdTicket = ticketService.createTicket(ticket);
        Ticket closedTicket = ticketService.closeTicket(createdTicket.getId());
        assertNotNull(closedTicket);
        assertEquals("已关闭", closedTicket.getStatus());
        assertNotNull(closedTicket.getCloseTime());
    }

    @Test
    public void testAddReply() {
        Ticket ticket = new Ticket();
        ticket.setTitle("测试工单");
        ticket.setContent("这是一个测试工单");
        ticket.setCategory("技术支持");
        ticket.setPriority(1);
        ticket.setCreatorId(1L);
        
        Ticket createdTicket = ticketService.createTicket(ticket);
        
        TicketReply reply = new TicketReply();
        reply.setTicket(createdTicket);
        reply.setUserId(2L);
        reply.setContent("这是一个测试回复");
        
        TicketReply createdReply = ticketService.addReply(reply);
        assertNotNull(createdReply);
        assertNotNull(createdReply.getCreateTime());
    }

    @Test
    public void testAddEvaluation() {
        Ticket ticket = new Ticket();
        ticket.setTitle("测试工单");
        ticket.setContent("这是一个测试工单");
        ticket.setCategory("技术支持");
        ticket.setPriority(1);
        ticket.setCreatorId(1L);
        
        Ticket createdTicket = ticketService.createTicket(ticket);
        
        TicketEvaluation evaluation = new TicketEvaluation();
        evaluation.setTicket(createdTicket);
        evaluation.setScore(5);
        evaluation.setComment("非常满意");
        
        TicketEvaluation createdEvaluation = ticketService.addEvaluation(evaluation);
        assertNotNull(createdEvaluation);
        assertNotNull(createdEvaluation.getEvaluateTime());
    }
}