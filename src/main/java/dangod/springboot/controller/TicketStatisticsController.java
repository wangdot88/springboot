package dangod.springboot.controller;

import dangod.springboot.dto.ApiResponse;
import dangod.springboot.dto.TicketStatistics;
import dangod.springboot.service.TicketStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/statistics")
public class TicketStatisticsController {
    
    @Autowired
    private TicketStatisticsService ticketStatisticsService;
    
    /**
     * 获取工单统计信息
     */
    @GetMapping("/tickets")
    public ResponseEntity<ApiResponse> getTicketStatistics() {
        try {
            TicketStatistics statistics = ticketStatisticsService.getTicketStatistics();
            return ResponseEntity.ok(ApiResponse.success("获取工单统计成功", statistics));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 获取客服工作量统计
     */
    @GetMapping("/agent-workload")
    public ResponseEntity<ApiResponse> getAgentWorkloadStatistics() {
        try {
            TicketStatisticsService.AgentWorkloadStatistics statistics = ticketStatisticsService.getAgentWorkloadStatistics();
            return ResponseEntity.ok(ApiResponse.success("获取客服工作量统计成功", statistics));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 获取时间段内的工单统计
     */
    @GetMapping("/tickets/date-range")
    public ResponseEntity<ApiResponse> getTicketStatisticsByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        try {
            TicketStatistics statistics = ticketStatisticsService.getTicketStatisticsByDateRange(startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success("获取时间段工单统计成功", statistics));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}