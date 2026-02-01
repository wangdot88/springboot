package dangod.springboot.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class ReservationDTO {
    private Long id;
    private String reservationNo;
    @NotNull(message = "会员ID不能为空")
    private Long memberId;
    @NotNull(message = "会员卡不能为空")
    private Long cardId;
    @NotNull(message = "课程不能为空")
    private Long courseId;
    private LocalDateTime reservationTime;
    private Integer status;
    private String courseName;
    private LocalDateTime courseStartTime;
    private LocalDateTime courseEndTime;
    private Long coachId;
    private String message;
    private Boolean success;
}
