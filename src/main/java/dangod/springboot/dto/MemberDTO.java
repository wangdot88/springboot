package dangod.springboot.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
public class MemberDTO {
    @NotBlank(message = "姓名不能为空")
    private String name;
    private Integer gender;
    @NotBlank(message = "手机号不能为空")
    private String phone;
    private String idCard;
    private LocalDate birthday;
    private String avatar;
    private String email;
    private String address;
    private String emergencyContact;
    private String emergencyPhone;
    private Long registerGymId;
    private String remark;
}
