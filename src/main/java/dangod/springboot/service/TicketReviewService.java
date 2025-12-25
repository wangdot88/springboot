package dangod.springboot.service;

import dangod.springboot.dto.CreateReviewRequest;
import dangod.springboot.entity.*;
import dangod.springboot.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@Transactional
public class TicketReviewService {
    
    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TicketReviewRepository ticketReviewRepository;
    
    /**
     * 创建工单评价
     */
    public TicketReview createReview(Long ticketId, Long customerId, CreateReviewRequest request) {
        // 验证工单
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("工单不存在"));
        
        // 验证用户
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 验证工单是否属于该客户
        if (!ticket.getCustomer().getId().equals(customerId)) {
            throw new RuntimeException("只能评价自己的工单");
        }
        
        // 验证工单状态
        if (ticket.getStatus() != Ticket.Status.RESOLVED && ticket.getStatus() != Ticket.Status.CLOSED) {
            throw new RuntimeException("只能评价已解决或已关闭的工单");
        }
        
        // 验证是否已经评价过
        if (ticketReviewRepository.existsByTicket(ticket)) {
            throw new RuntimeException("该工单已经评价过了");
        }
        
        // 验证评分范围
        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new RuntimeException("评分必须在1-5分之间");
        }
        
        TicketReview review = new TicketReview();
        review.setTicket(ticket);
        review.setCustomer(customer);
        review.setRating(request.getRating());
        review.setReviewContent(request.getReviewContent());
        
        return ticketReviewRepository.save(review);
    }
    
    /**
     * 获取工单评价
     */
    public TicketReview getReviewByTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("工单不存在"));
        
        return ticketReviewRepository.findByTicket(ticket)
                .orElseThrow(() -> new RuntimeException("该工单还没有评价"));
    }
    
    /**
     * 获取用户的评价列表
     */
    public Optional<TicketReview> getReviewByCustomerAndTicket(Long customerId, Long ticketId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("工单不存在"));
        
        return ticketReviewRepository.findByTicket(ticket);
    }
    
    /**
     * 获取平均评分
     */
    public Double getAverageRating() {
        return ticketReviewRepository.findAll().stream()
                .mapToInt(TicketReview::getRating)
                .average()
                .orElse(0.0);
    }
    
    /**
     * 获取评分统计
     */
    public RatingStatistics getRatingStatistics() {
        RatingStatistics stats = new RatingStatistics();
        
        ticketReviewRepository.findAll().forEach(review -> {
            stats.addRating(review.getRating());
        });
        
        return stats;
    }
    
    /**
     * 评分统计类
     */
    public static class RatingStatistics {
        private int totalCount = 0;
        private int[] ratingCounts = new int[6]; // 索引1-5对应1-5星
        private double averageRating = 0.0;
        
        public void addRating(int rating) {
            if (rating >= 1 && rating <= 5) {
                ratingCounts[rating]++;
                totalCount++;
                calculateAverage();
            }
        }
        
        private void calculateAverage() {
            if (totalCount > 0) {
                double sum = 0;
                for (int i = 1; i <= 5; i++) {
                    sum += i * ratingCounts[i];
                }
                averageRating = sum / totalCount;
            }
        }
        
        public int getTotalCount() {
            return totalCount;
        }
        
        public int getRatingCount(int rating) {
            if (rating >= 1 && rating <= 5) {
                return ratingCounts[rating];
            }
            return 0;
        }
        
        public double getAverageRating() {
            return averageRating;
        }
        
        public double getRatingPercentage(int rating) {
            if (totalCount > 0 && rating >= 1 && rating <= 5) {
                return (double) ratingCounts[rating] / totalCount * 100;
            }
            return 0.0;
        }
    }
}