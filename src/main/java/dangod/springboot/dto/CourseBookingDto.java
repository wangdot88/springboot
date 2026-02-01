package dangod.springboot.dto;

import javax.validation.constraints.NotNull;

public class CourseBookingDto {
    
    @NotNull(message = "会员ID不能为空")
    private Long memberId;
    
    @NotNull(message = "课程ID不能为空")
    private Long courseId;
    
    private String notes;
    
    // Getters and Setters
    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}