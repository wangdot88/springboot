package dangod.springboot.dto;

public class CreateReviewRequest {
    private Integer rating; // 1-5分
    private String reviewContent;
    
    // Getters and Setters
    public Integer getRating() {
        return rating;
    }
    
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    
    public String getReviewContent() {
        return reviewContent;
    }
    
    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }
}