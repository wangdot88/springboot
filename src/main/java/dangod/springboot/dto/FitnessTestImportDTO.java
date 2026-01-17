package dangod.springboot.dto;

import java.util.List;

public class FitnessTestImportDTO {
    private String studentNo;
    private String name;
    private Integer year;
    private Integer semester;
    private Double run1000;
    private Double run800;
    private Double run50;
    private Integer sitAndReach;
    private Integer standingLongJump;
    private Integer sitUp;
    private Integer pullUp;
    private Double bmi;

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public Double getRun1000() {
        return run1000;
    }

    public void setRun1000(Double run1000) {
        this.run1000 = run1000;
    }

    public Double getRun800() {
        return run800;
    }

    public void setRun800(Double run800) {
        this.run800 = run800;
    }

    public Double getRun50() {
        return run50;
    }

    public void setRun50(Double run50) {
        this.run50 = run50;
    }

    public Integer getSitAndReach() {
        return sitAndReach;
    }

    public void setSitAndReach(Integer sitAndReach) {
        this.sitAndReach = sitAndReach;
    }

    public Integer getStandingLongJump() {
        return standingLongJump;
    }

    public void setStandingLongJump(Integer standingLongJump) {
        this.standingLongJump = standingLongJump;
    }

    public Integer getSitUp() {
        return sitUp;
    }

    public void setSitUp(Integer sitUp) {
        this.sitUp = sitUp;
    }

    public Integer getPullUp() {
        return pullUp;
    }

    public void setPullUp(Integer pullUp) {
        this.pullUp = pullUp;
    }

    public Double getBmi() {
        return bmi;
    }

    public void setBmi(Double bmi) {
        this.bmi = bmi;
    }
}
