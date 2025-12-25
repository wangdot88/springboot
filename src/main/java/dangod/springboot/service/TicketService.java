package dangod.springboot.service;

import dangod.springboot.dto.CreateTicketRequest;
import dangod.springboot.dto.UpdateTicketStatusRequest;
import dangod.springboot.dto.AssignTicketRequest;
import dangod.springboot.dto.ReplyTicketRequest;
import dangod.springboot.entity.*;
import dangod.springboot.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class TicketService {
    
    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TicketCategoryRepository ticketCategoryRepository;
    
    @Autowired
    private TicketReplyRepository ticketReplyRepository;
    
    @Autowired
    private TicketReviewRepository ticketReviewRepository;
    
    /**
     * 创建工单
     */
    public Ticket createTicket(CreateTicketRequest request, Long customerId) {
        // 验证用户
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 验证分类
        TicketCategory category = ticketCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("工单分类不存在"));
        
        // 创建工单
        Ticket ticket = new Ticket();
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setCustomer(customer);
        ticket.setCategory(category);
        ticket.setPriority(request.getPriority() != null ? request.getPriority() : Ticket.Priority.MEDIUM);
        ticket.setStatus(Ticket.Status.PENDING);
        
        // 设置截止时间（根据优先级）
        ticket.setDueDate(calculateDueDate(request.getPriority()));
        
        return ticketRepository.save(ticket);
    }
    
    /**
     * 根据优先级计算截止时间
     */
    private Date calculateDueDate(Ticket.Priority priority) {
        Calendar calendar = Calendar.getInstance();
        
        switch (priority) {
            case URGENT:
                calendar.add(Calendar.HOUR, 2); // 2小时内
                break;
            case HIGH:
                calendar.add(Calendar.HOUR, 8); // 8小时内
                break;
            case MEDIUM:
                calendar.add(Calendar.DAY_OF_MONTH, 1); // 1天内
                break;
            case LOW:
                calendar.add(Calendar.DAY_OF_MONTH, 3); // 3天内
                break;
            default:
                calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        
        return calendar.getTime();
    }
    
    /**
     * 获取用户的工单列表
     */
    public List<Ticket> getUserTickets(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return ticketRepository.findByCustomerOrderByCreatedAtDesc(user);
    }
    
    /**
     * 根据ID获取工单
     */
    public Ticket getTicketById(Long ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("工单不存在"));
    }
    
    /**
     * 获取所有工单分类
     */
    public List<TicketCategory> getAllCategories() {
        return ticketCategoryRepository.findByIsActiveTrueOrderByName();
    }
    
    /**
     * 更新工单状态
     */
    public Ticket updateTicketStatus(Long ticketId, UpdateTicketStatusRequest request, Long userId) {
        Ticket ticket = getTicketById(ticketId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 验证权限 - 只有分配的客服或管理员可以更新状态
        if (ticket.getAssignedAgent() != null && !ticket.getAssignedAgent().getId().equals(userId) 
            && !user.getRoleType().equals(User.RoleType.ADMIN)) {
            throw new RuntimeException("没有权限更新此工单");
        }
        
        Ticket.Status oldStatus = ticket.getStatus();
        ticket.setStatus(request.getStatus());
        
        // 记录状态变更时间
        if (request.getStatus() == Ticket.Status.RESOLVED) {
            ticket.setResolvedAt(new Date());
        } else if (request.getStatus() == Ticket.Status.CLOSED) {
            ticket.setClosedAt(new Date());
        }
        
        ticket = ticketRepository.save(ticket);
        
        // 添加状态变更记录
        if (request.getNote() != null && !request.getNote().trim().isEmpty()) {
            addReplyInternal(ticket, user, "状态变更: " + oldStatus + " -> " + request.getStatus() + ". 备注: " + request.getNote());
        }
        
        return ticket;
    }
    
    /**
     * 分配工单给客服
     */
    public Ticket assignTicket(Long ticketId, AssignTicketRequest request, Long operatorId) {
        Ticket ticket = getTicketById(ticketId);
        User operator = userRepository.findById(operatorId)
                .orElseThrow(() -> new RuntimeException("操作员不存在"));
        User agent = userRepository.findById(request.getAgentId())
                .orElseThrow(() -> new RuntimeException("客服不存在"));
        
        // 验证客服角色
        if (!agent.getRoleType().equals(User.RoleType.SUPPORT_AGENT)) {
            throw new RuntimeException("指定用户不是客服");
        }
        
        ticket.setAssignedAgent(agent);
        if (ticket.getStatus() == Ticket.Status.PENDING) {
            ticket.setStatus(Ticket.Status.IN_PROGRESS);
        }
        
        ticket = ticketRepository.save(ticket);
        
        // 添加分配记录
        if (request.getNote() != null && !request.getNote().trim().isEmpty()) {
            addReplyInternal(ticket, operator, "工单分配给: " + agent.getFullName() + ". 备注: " + request.getNote());
        }
        
        return ticket;
    }
    
    /**
     * 自动分配工单
     */
    public Ticket autoAssignTicket(Long ticketId) {
        Ticket ticket = getTicketById(ticketId);
        
        if (ticket.getAssignedAgent() != null) {
            throw new RuntimeException("工单已分配");
        }
        
        // 查找工作量最少的可用客服
        List<User> availableAgents = userRepository.findAvailableAgentsByWorkload(User.RoleType.SUPPORT_AGENT);
        
        if (availableAgents.isEmpty()) {
            throw new RuntimeException("没有可用的客服");
        }
        
        User assignedAgent = availableAgents.get(0);
        ticket.setAssignedAgent(assignedAgent);
        ticket.setStatus(Ticket.Status.IN_PROGRESS);
        
        ticket = ticketRepository.save(ticket);
        
        // 添加自动分配记录
        addReplyInternal(ticket, assignedAgent, "工单自动分配给: " + assignedAgent.getFullName());
        
        return ticket;
    }
    
    /**
     * 转交工单
     */
    public Ticket transferTicket(Long ticketId, Long newAgentId, Long operatorId, String reason) {
        Ticket ticket = getTicketById(ticketId);
        User operator = userRepository.findById(operatorId)
                .orElseThrow(() -> new RuntimeException("操作员不存在"));
        User newAgent = userRepository.findById(newAgentId)
                .orElseThrow(() -> new RuntimeException("新客服不存在"));
        
        // 验证客服角色
        if (!newAgent.getRoleType().equals(User.RoleType.SUPPORT_AGENT)) {
            throw new RuntimeException("指定用户不是客服");
        }
        
        User oldAgent = ticket.getAssignedAgent();
        ticket.setAssignedAgent(newAgent);
        
        ticket = ticketRepository.save(ticket);
        
        // 添加转交记录
        String transferNote = "工单从 " + (oldAgent != null ? oldAgent.getFullName() : "未分配") + 
                               " 转交给 " + newAgent.getFullName();
        if (reason != null && !reason.trim().isEmpty()) {
            transferNote += ". 原因: " + reason;
        }
        addReplyInternal(ticket, operator, transferNote);
        
        return ticket;
    }
    
    /**
     * 回复工单
     */
    public TicketReply replyToTicket(Long ticketId, ReplyTicketRequest request, Long userId) {
        Ticket ticket = getTicketById(ticketId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 验证权限
        if (!canUserReplyToTicket(ticket, user)) {
            throw new RuntimeException("没有权限回复此工单");
        }
        
        TicketReply reply = new TicketReply();
        reply.setTicket(ticket);
        reply.setUser(user);
        reply.setContent(request.getContent());
        reply.setImageUrl(request.getImageUrl());
        reply.setIsInternal(request.getIsInternal() != null ? request.getIsInternal() : false);
        
        reply = ticketReplyRepository.save(reply);
        
        // 如果客服回复，更新状态为处理中
        if (user.getRoleType().equals(User.RoleType.SUPPORT_AGENT) && 
            ticket.getStatus() == Ticket.Status.PENDING) {
            ticket.setStatus(Ticket.Status.IN_PROGRESS);
            ticketRepository.save(ticket);
        }
        
        return reply;
    }
    
    /**
     * 获取工单回复列表
     */
    public List<TicketReply> getTicketReplies(Long ticketId, Long userId) {
        Ticket ticket = getTicketById(ticketId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 如果是客服或管理员，可以看到所有回复
        if (user.getRoleType().equals(User.RoleType.SUPPORT_AGENT) || 
            user.getRoleType().equals(User.RoleType.ADMIN)) {
            return ticketReplyRepository.findByTicketOrderByCreatedAtAsc(ticket);
        } else {
            // 客户只能看到非内部回复
            return ticketReplyRepository.findByTicketAndIsInternalFalseOrderByCreatedAtAsc(ticket);
        }
    }
    
    /**
     * 关闭工单
     */
    public Ticket closeTicket(Long ticketId, Long userId, String reason) {
        Ticket ticket = getTicketById(ticketId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 验证权限
        if (!canUserCloseTicket(ticket, user)) {
            throw new RuntimeException("没有权限关闭此工单");
        }
        
        ticket.setStatus(Ticket.Status.CLOSED);
        ticket.setClosedAt(new Date());
        
        ticket = ticketRepository.save(ticket);
        
        // 添加关闭记录
        if (reason != null && !reason.trim().isEmpty()) {
            addReplyInternal(ticket, user, "工单已关闭. 原因: " + reason);
        }
        
        return ticket;
    }
    
    /**
     * 验证用户是否可以回复工单
     */
    private boolean canUserReplyToTicket(Ticket ticket, User user) {
        // 工单创建者可以回复
        if (ticket.getCustomer().getId().equals(user.getId())) {
            return true;
        }
        
        // 分配的客服可以回复
        if (ticket.getAssignedAgent() != null && ticket.getAssignedAgent().getId().equals(user.getId())) {
            return true;
        }
        
        // 管理员可以回复
        if (user.getRoleType().equals(User.RoleType.ADMIN)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * 验证用户是否可以关闭工单
     */
    private boolean canUserCloseTicket(Ticket ticket, User user) {
        // 管理员可以关闭
        if (user.getRoleType().equals(User.RoleType.ADMIN)) {
            return true;
        }
        
        // 分配的客服可以关闭
        if (ticket.getAssignedAgent() != null && ticket.getAssignedAgent().getId().equals(user.getId())) {
            return true;
        }
        
        return false;
    }
    
    /**
     * 添加内部备注
     */
    private void addReplyInternal(Ticket ticket, User user, String content) {
        TicketReply reply = new TicketReply();
        reply.setTicket(ticket);
        reply.setUser(user);
        reply.setContent(content);
        reply.setIsInternal(true);
        ticketReplyRepository.save(reply);
    }
}