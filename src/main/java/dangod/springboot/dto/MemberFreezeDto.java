package dangod.springboot.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class MemberFreezeDto {
    
    @NotBlank(message = "冻结原因不能为空")
    private String freezeReason;
    
    @NotNull(message = "审批人ID不能为空")
    private Long approvedById;
    
    // Getters and Setters
    public String getFreezeReason() {
        return freezeReason;
    }

    public void setFreezeReason(String freezeReason) {
        this.freezeReason = freezeReason;
    }

    public Long getApprovedById() {
        return approvedById;
    }

    public void setApprovedById(Long approvedById) {
        this.approvedById = approvedById;
    }
}