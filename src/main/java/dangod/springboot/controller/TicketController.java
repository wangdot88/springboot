package dangod.springboot.controller;

import dangod.springboot.dto.ApiResponse;
import dangod.springboot.dto.CreateTicketRequest;
import dangod.springboot.dto.UpdateTicketStatusRequest;
import dangod.springboot.dto.AssignTicketRequest;
import dangod.springboot.dto.ReplyTicketRequest;
import dangod.springboot.entity.Ticket;
import dangod.springboot.entity.TicketCategory;
import dangod.springboot.entity.TicketReply;
import dangod.springboot.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    
    @Autowired
    private TicketService ticketService;
    
    /**
     * 创建工单
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createTicket(@RequestBody CreateTicketRequest request,
                                                   @RequestParam Long userId) {
        try {
            Ticket ticket = ticketService.createTicket(request, userId);
            return ResponseEntity.ok(ApiResponse.success("工单创建成功", ticket));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 获取用户的工单列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getUserTickets(@PathVariable Long userId) {
        try {
            List<Ticket> tickets = ticketService.getUserTickets(userId);
            return ResponseEntity.ok(ApiResponse.success("获取工单列表成功", tickets));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 获取工单详情
     */
    @GetMapping("/{ticketId}")
    public ResponseEntity<ApiResponse> getTicketById(@PathVariable Long ticketId) {
        try {
            Ticket ticket = ticketService.getTicketById(ticketId);
            return ResponseEntity.ok(ApiResponse.success("获取工单详情成功", ticket));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 获取所有工单分类
     */
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse> getAllCategories() {
        try {
            List<TicketCategory> categories = ticketService.getAllCategories();
            return ResponseEntity.ok(ApiResponse.success("获取分类列表成功", categories));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 更新工单状态
     */
    @PutMapping("/{ticketId}/status")
    public ResponseEntity<ApiResponse> updateTicketStatus(@PathVariable Long ticketId,
                                                           @RequestBody UpdateTicketStatusRequest request,
                                                           @RequestParam Long userId) {
        try {
            Ticket ticket = ticketService.updateTicketStatus(ticketId, request, userId);
            return ResponseEntity.ok(ApiResponse.success("工单状态更新成功", ticket));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 手动分配工单
     */
    @PutMapping("/{ticketId}/assign")
    public ResponseEntity<ApiResponse> assignTicket(@PathVariable Long ticketId,
                                                     @RequestBody AssignTicketRequest request,
                                                     @RequestParam Long operatorId) {
        try {
            Ticket ticket = ticketService.assignTicket(ticketId, request, operatorId);
            return ResponseEntity.ok(ApiResponse.success("工单分配成功", ticket));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 自动分配工单
     */
    @PutMapping("/{ticketId}/auto-assign")
    public ResponseEntity<ApiResponse> autoAssignTicket(@PathVariable Long ticketId) {
        try {
            Ticket ticket = ticketService.autoAssignTicket(ticketId);
            return ResponseEntity.ok(ApiResponse.success("工单自动分配成功", ticket));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 转交工单
     */
    @PutMapping("/{ticketId}/transfer")
    public ResponseEntity<ApiResponse> transferTicket(@PathVariable Long ticketId,
                                                       @RequestParam Long newAgentId,
                                                       @RequestParam Long operatorId,
                                                       @RequestParam(required = false) String reason) {
        try {
            Ticket ticket = ticketService.transferTicket(ticketId, newAgentId, operatorId, reason);
            return ResponseEntity.ok(ApiResponse.success("工单转交成功", ticket));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 回复工单
     */
    @PostMapping("/{ticketId}/reply")
    public ResponseEntity<ApiResponse> replyToTicket(@PathVariable Long ticketId,
                                                    @RequestBody ReplyTicketRequest request,
                                                    @RequestParam Long userId) {
        try {
            TicketReply reply = ticketService.replyToTicket(ticketId, request, userId);
            return ResponseEntity.ok(ApiResponse.success("回复成功", reply));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 获取工单回复列表
     */
    @GetMapping("/{ticketId}/replies")
    public ResponseEntity<ApiResponse> getTicketReplies(@PathVariable Long ticketId,
                                                       @RequestParam Long userId) {
        try {
            List<TicketReply> replies = ticketService.getTicketReplies(ticketId, userId);
            return ResponseEntity.ok(ApiResponse.success("获取回复列表成功", replies));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 关闭工单
     */
    @PutMapping("/{ticketId}/close")
    public ResponseEntity<ApiResponse> closeTicket(@PathVariable Long ticketId,
                                                  @RequestParam Long userId,
                                                  @RequestParam(required = false) String reason) {
        try {
            Ticket ticket = ticketService.closeTicket(ticketId, userId, reason);
            return ResponseEntity.ok(ApiResponse.success("工单关闭成功", ticket));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}