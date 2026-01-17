package dangod.springboot.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "器材报修DTO")
public class EquipmentRepairDTO {

    @ApiModelProperty("器材ID")
    private Long equipmentId;

    @ApiModelProperty("报修人")
    private String reporterName;

    @ApiModelProperty("报修人电话")
    private String reporterPhone;

    @ApiModelProperty("报修数量")
    private Integer repairQuantity;

    @ApiModelProperty("问题描述")
    private String problemDescription;

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public String getReporterPhone() {
        return reporterPhone;
    }

    public void setReporterPhone(String reporterPhone) {
        this.reporterPhone = reporterPhone;
    }

    public Integer getRepairQuantity() {
        return repairQuantity;
    }

    public void setRepairQuantity(Integer repairQuantity) {
        this.repairQuantity = repairQuantity;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }
}
