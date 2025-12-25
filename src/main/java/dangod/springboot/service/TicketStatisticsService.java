package dangod.springboot.service;

import dangod.springboot.dto.TicketStatistics;
import dangod.springboot.entity.Ticket;
import dangod.springboot.entity.Ticket.Status;
import dangod.springboot.entity.User;
import dangod.springboot.entity.User.RoleType;
import dangod.springboot.repository.TicketRepository;
import dangod.springboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class TicketStatisticsService {
    
    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 获取工单统计信息
     */
    public TicketStatistics getTicketStatistics() {
        TicketStatistics stats = new TicketStatistics();
        
        // 总工单数
        stats.setTotalTickets((long) ticketRepository.findAll().size());
        
        // 各状态工单数
        stats.setPendingTickets(ticketRepository.countByStatus(Status.PENDING));
        stats.setInProgressTickets(ticketRepository.countByStatus(Status.IN_PROGRESS));
        stats.setResolvedTickets(ticketRepository.countByStatus(Status.RESOLVED));
        stats.setClosedTickets(ticketRepository.countByStatus(Status.CLOSED));
        
        // 逾期工单数
        Date currentDate = new Date();
        List<Ticket> overdueTickets = ticketRepository.findOverdueTickets(currentDate);
        stats.setOverdueTickets((long) overdueTickets.size());
        
        // 平均解决时间
        Double avgResolutionTime = calculateAverageResolutionTime();
        stats.setAverageResolutionTime(avgResolutionTime);
        stats.setAverageResolutionTimeHours(avgResolutionTime);
        
        return stats;
    }
    
    /**
     * 计算平均解决时间（小时）
     */
    private Double calculateAverageResolutionTime() {
        List<Ticket> resolvedTickets = ticketRepository.findByStatusInOrderByCreatedAtDesc(
                List.of(Status.RESOLVED, Status.CLOSED));
        
        if (resolvedTickets.isEmpty()) {
            return 0.0;
        }
        
        double totalHours = 0;
        int count = 0;
        
        for (Ticket ticket : resolvedTickets) {
            if (ticket.getResolvedAt() != null) {
                long diffInMillis = ticket.getResolvedAt().getTime() - ticket.getCreatedAt().getTime();
                double hours = diffInMillis / (1000.0 * 60 * 60);
                totalHours += hours;
                count++;
            } else if (ticket.getClosedAt() != null) {
                long diffInMillis = ticket.getClosedAt().getTime() - ticket.getCreatedAt().getTime();
                double hours = diffInMillis / (1000.0 * 60 * 60);
                totalHours += hours;
                count++;
            }
        }
        
        return count > 0 ? totalHours / count : 0.0;
    }
    
    /**
     * 获取客服工作量统计
     */
    public AgentWorkloadStatistics getAgentWorkloadStatistics() {
        AgentWorkloadStatistics stats = new AgentWorkloadStatistics();
        
        List<User> agents = userRepository.findByRoleTypeAndIsActiveTrue(RoleType.SUPPORT_AGENT);
        
        for (User agent : agents) {
            AgentWorkload workload = new AgentWorkload();
            workload.setAgentId(agent.getId());
            workload.setAgentName(agent.getFullName());
            workload.setTotalTickets(ticketRepository.countByAssignedAgent(agent));
            
            // 获取该客服的工单状态统计
            List<Ticket> agentTickets = ticketRepository.findByAssignedAgentOrderByCreatedAtDesc(agent);
            
            long pendingCount = 0;
            long inProgressCount = 0;
            long resolvedCount = 0;
            
            for (Ticket ticket : agentTickets) {
                switch (ticket.getStatus()) {
                    case PENDING:
                        pendingCount++;
                        break;
                    case IN_PROGRESS:
                        inProgressCount++;
                        break;
                    case RESOLVED:
                        resolvedCount++;
                        break;
                }
            }
            
            workload.setPendingTickets(pendingCount);
            workload.setInProgressTickets(inProgressCount);
            workload.setResolvedTickets(resolvedCount);
            
            stats.addAgentWorkload(workload);
        }
        
        return stats;
    }
    
    /**
     * 获取时间段内的工单统计
     */
    public TicketStatistics getTicketStatisticsByDateRange(Date startDate, Date endDate) {
        TicketStatistics stats = new TicketStatistics();
        
        // 获取时间段内的工单
        Long ticketsInRange = ticketRepository.countTicketsBetweenDates(startDate, endDate);
        stats.setTotalTickets(ticketsInRange);
        
        // 这里可以进一步细化统计，比如按状态、按分类等
        
        return stats;
    }
    
    /**
     * 客服工作量统计类
     */
    public static class AgentWorkloadStatistics {
        private List<AgentWorkload> agentWorkloads;
        
        public AgentWorkloadStatistics() {
            this.agentWorkloads = new java.util.ArrayList<>();
        }
        
        public void addAgentWorkload(AgentWorkload workload) {
            this.agentWorkloads.add(workload);
        }
        
        public List<AgentWorkload> getAgentWorkloads() {
            return agentWorkloads;
        }
        
        public void setAgentWorkloads(List<AgentWorkload> agentWorkloads) {
            this.agentWorkloads = agentWorkloads;
        }
    }
    
    /**
     * 单个客服工作量
     */
    public static class AgentWorkload {
        private Long agentId;
        private String agentName;
        private Long totalTickets;
        private Long pendingTickets;
        private Long inProgressTickets;
        private Long resolvedTickets;
        
        // Getters and Setters
        public Long getAgentId() {
            return agentId;
        }
        
        public void setAgentId(Long agentId) {
            this.agentId = agentId;
        }
        
        public String getAgentName() {
            return agentName;
        }
        
        public void setAgentName(String agentName) {
            this.agentName = agentName;
        }
        
        public Long getTotalTickets() {
            return totalTickets;
        }
        
        public void setTotalTickets(Long totalTickets) {
            this.totalTickets = totalTickets;
        }
        
        public Long getPendingTickets() {
            return pendingTickets;
        }
        
        public void setPendingTickets(Long pendingTickets) {
            this.pendingTickets = pendingTickets;
        }
        
        public Long getInProgressTickets() {
            return inProgressTickets;
        }
        
        public void setInProgressTickets(Long inProgressTickets) {
            this.inProgressTickets = inProgressTickets;
        }
        
        public Long getResolvedTickets() {
            return resolvedTickets;
        }
        
        public void setResolvedTickets(Long resolvedTickets) {
            this.resolvedTickets = resolvedTickets;
        }
    }
}