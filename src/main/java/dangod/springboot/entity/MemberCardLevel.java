package dangod.springboot.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "member_card_level")
public class MemberCardLevel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String levelName;

    @Column(nullable = false, length = 10)
    private String levelCode;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer durationDays;

    @Column(nullable = false)
    private Integer courseDiscount;

    @Column(nullable = false)
    private Integer privateCourseCount;

    @Column(nullable = false)
    private Integer guestCount;

    @Column(nullable = false)
    private Boolean freezeAllowed;

    @Column(nullable = false)
    private Integer freezeMaxDays;

    @Column(columnDefinition = "TEXT")
    private String benefits;

    @Column(nullable = false)
    private Integer pointRate;

    public MemberCardLevel() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getLevelCode() {
        return levelCode;
    }

    public void setLevelCode(String levelCode) {
        this.levelCode = levelCode;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public Integer getCourseDiscount() {
        return courseDiscount;
    }

    public void setCourseDiscount(Integer courseDiscount) {
        this.courseDiscount = courseDiscount;
    }

    public Integer getPrivateCourseCount() {
        return privateCourseCount;
    }

    public void setPrivateCourseCount(Integer privateCourseCount) {
        this.privateCourseCount = privateCourseCount;
    }

    public Integer getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(Integer guestCount) {
        this.guestCount = guestCount;
    }

    public Boolean getFreezeAllowed() {
        return freezeAllowed;
    }

    public void setFreezeAllowed(Boolean freezeAllowed) {
        this.freezeAllowed = freezeAllowed;
    }

    public Integer getFreezeMaxDays() {
        return freezeMaxDays;
    }

    public void setFreezeMaxDays(Integer freezeMaxDays) {
        this.freezeMaxDays = freezeMaxDays;
    }

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public Integer getPointRate() {
        return pointRate;
    }

    public void setPointRate(Integer pointRate) {
        this.pointRate = pointRate;
    }
}
