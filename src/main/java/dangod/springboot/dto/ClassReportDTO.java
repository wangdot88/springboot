package dangod.springboot.dto;

public class ClassReportDTO {
    private Long classId;
    private String className;
    private Integer totalStudents;
    private Integer testedStudents;
    private Integer qualifiedStudents;
    private Double qualifiedRate;
    private Double averageScore;
    private Integer levelA;
    private Integer levelB;
    private Integer levelC;
    private Integer levelD;

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Integer getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(Integer totalStudents) {
        this.totalStudents = totalStudents;
    }

    public Integer getTestedStudents() {
        return testedStudents;
    }

    public void setTestedStudents(Integer testedStudents) {
        this.testedStudents = testedStudents;
    }

    public Integer getQualifiedStudents() {
        return qualifiedStudents;
    }

    public void setQualifiedStudents(Integer qualifiedStudents) {
        this.qualifiedStudents = qualifiedStudents;
    }

    public Double getQualifiedRate() {
        return qualifiedRate;
    }

    public void setQualifiedRate(Double qualifiedRate) {
        this.qualifiedRate = qualifiedRate;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }

    public Integer getLevelA() {
        return levelA;
    }

    public void setLevelA(Integer levelA) {
        this.levelA = levelA;
    }

    public Integer getLevelB() {
        return levelB;
    }

    public void setLevelB(Integer levelB) {
        this.levelB = levelB;
    }

    public Integer getLevelC() {
        return levelC;
    }

    public void setLevelC(Integer levelC) {
        this.levelC = levelC;
    }

    public Integer getLevelD() {
        return levelD;
    }

    public void setLevelD(Integer levelD) {
        this.levelD = levelD;
    }
}
