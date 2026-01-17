package dangod.springboot.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "器材借出DTO")
public class EquipmentBorrowDTO {

    @ApiModelProperty("器材ID")
    private Long equipmentId;

    @ApiModelProperty("学生ID")
    private Long studentId;

    @ApiModelProperty("借出数量")
    private Integer borrowQuantity;

    @ApiModelProperty("预计归还日期")
    private String expectedReturnDate;

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Integer getBorrowQuantity() {
        return borrowQuantity;
    }

    public void setBorrowQuantity(Integer borrowQuantity) {
        this.borrowQuantity = borrowQuantity;
    }

    public String getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public void setExpectedReturnDate(String expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
    }
}
