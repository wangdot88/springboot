package dangod.springboot.enums;

public enum UserRole {
    ADMIN("管理员"),
    MANAGER("店长"),
    TRAINER("教练"),
    MEMBER("会员");

    private String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}