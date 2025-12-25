package dangod.springboot.controller;

import dangod.springboot.dto.ApiResponse;
import dangod.springboot.dto.CreateReviewRequest;
import dangod.springboot.entity.TicketReview;
import dangod.springboot.service.TicketReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class TicketReviewController {
    
    @Autowired
    private TicketReviewService ticketReviewService;
    
    /**
     * 创建工单评价
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createReview(@RequestParam Long ticketId,
                                                   @RequestParam Long customerId,
                                                   @RequestBody CreateReviewRequest request) {
        try {
            TicketReview review = ticketReviewService.createReview(ticketId, customerId, request);
            return ResponseEntity.ok(ApiResponse.success("评价创建成功", review));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 获取工单评价
     */
    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<ApiResponse> getReviewByTicket(@PathVariable Long ticketId) {
        try {
            TicketReview review = ticketReviewService.getReviewByTicket(ticketId);
            return ResponseEntity.ok(ApiResponse.success("获取评价成功", review));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 获取平均评分
     */
    @GetMapping("/average-rating")
    public ResponseEntity<ApiResponse> getAverageRating() {
        try {
            Double averageRating = ticketReviewService.getAverageRating();
            return ResponseEntity.ok(ApiResponse.success("获取平均评分成功", averageRating));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 获取评分统计
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse> getRatingStatistics() {
        try {
            TicketReviewService.RatingStatistics statistics = ticketReviewService.getRatingStatistics();
            return ResponseEntity.ok(ApiResponse.success("获取评分统计成功", statistics));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}