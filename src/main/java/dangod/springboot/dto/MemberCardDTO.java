package dangod.springboot.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MemberCardDTO {
    @NotNull(message = "会员ID不能为空")
    private Long memberId;
    @NotNull(message = "卡等级不能为空")
    private Long levelId;
    @NotNull(message = "门店不能为空")
    private Long gymId;
}
