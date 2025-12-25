package dangod.springboot.dto;

import java.util.Date;

public class TicketStatistics {
    private Long totalTickets;
    private Long pendingTickets;
    private Long inProgressTickets;
    private Long resolvedTickets;
    private Long closedTickets;
    private Double averageResolutionTime; // 平均解决时间（小时）
    private Double averageResolutionTimeHours;
    private Long overdueTickets;
    private Date statisticsDate;
    
    public TicketStatistics() {
        this.statisticsDate = new Date();
    }
    
    // Getters and Setters
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
    
    public Long getClosedTickets() {
        return closedTickets;
    }
    
    public void setClosedTickets(Long closedTickets) {
        this.closedTickets = closedTickets;
    }
    
    public Double getAverageResolutionTime() {
        return averageResolutionTime;
    }
    
    public void setAverageResolutionTime(Double averageResolutionTime) {
        this.averageResolutionTime = averageResolutionTime;
    }
    
    public Double getAverageResolutionTimeHours() {
        return averageResolutionTimeHours;
    }
    
    public void setAverageResolutionTimeHours(Double averageResolutionTimeHours) {
        this.averageResolutionTimeHours = averageResolutionTimeHours;
    }
    
    public Long getOverdueTickets() {
        return overdueTickets;
    }
    
    public void setOverdueTickets(Long overdueTickets) {
        this.overdueTickets = overdueTickets;
    }
    
    public Date getStatisticsDate() {
        return statisticsDate;
    }
    
    public void setStatisticsDate(Date statisticsDate) {
        this.statisticsDate = statisticsDate;
    }
}