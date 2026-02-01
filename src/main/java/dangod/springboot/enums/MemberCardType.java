package dangod.springboot.enums;

public enum MemberCardType {
    BRONZE("铜卡", 1),
    SILVER("银卡", 2),
    GOLD("金卡", 3),
    DIAMOND("钻石卡", 4);

    private String name;
    private int level;

    MemberCardType(String name, int level) {
        this.name = name;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public static MemberCardType fromLevel(int level) {
        for (MemberCardType type : values()) {
            if (type.level == level) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid member card level: " + level);
    }
}