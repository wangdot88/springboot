package dangod.springboot.enums;

public enum MemberStatus {
    ACTIVE("正常"),
    FROZEN("冻结"),
    EXPIRED("已过期"),
    PENDING_APPROVAL("待审批");

    private String description;

    MemberStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}