package dangod.springboot.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@ApiModel(description = "跑步打卡记录DTO")
public class RunningRecordDTO {

    @ApiModelProperty("学生ID")
    private Long studentId;

    @ApiModelProperty("打卡日期")
    private String recordDate;

    @ApiModelProperty("距离(km)")
    private BigDecimal distance;

    @ApiModelProperty("时长(分钟)")
    private Integer duration;

    @ApiModelProperty("步数")
    private Integer steps;

    @ApiModelProperty("速度(km/h)")
    private BigDecimal speed;

    @ApiModelProperty("地点")
    private String location;

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public BigDecimal getDistance() {
        return distance;
    }

    public void setDistance(BigDecimal distance) {
        this.distance = distance;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getSteps() {
        return steps;
    }

    public void setSteps(Integer steps) {
        this.steps = steps;
    }

    public BigDecimal getSpeed() {
        return speed;
    }

    public void setSpeed(BigDecimal speed) {
        this.speed = speed;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
