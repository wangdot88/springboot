package dangod.springboot.enums;

public enum BookingStatus {
    BOOKED("已预约"),
    WAITING("排队中"),
    ATTENDED("已签到"),
    ABSENT("未出席"),
    CANCELLED("已取消");

    private String description;

    BookingStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}