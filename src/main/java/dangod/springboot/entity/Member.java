package dangod.springboot.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "member", indexes = {
        @Index(name = "idx_member_no", columnList = "memberNo"),
        @Index(name = "idx_phone", columnList = "phone"),
        @Index(name = "idx_level_code", columnList = "levelCode"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_card_end_date", columnList = "cardEndDate")
})
public class Member {
    @Id
    @GeneratedValue(generator = "snowflakeId")
    @GenericGenerator(name = "snowflakeId", strategy = "dangod.springboot.core.util.SnowflakeIdGenerator")
    private Long id;

    @Column(name = "member_no", unique = true, nullable = false, length = 50)
    private String memberNo;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(unique = true, nullable = false, length = 20)
    private String phone;

    @Column(length = 100)
    private String email;

    @Column(columnDefinition = "TINYINT")
    private Integer gender;

    @Column(name = "birth_date")
    private Date birthDate;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    @Column(name = "level_code", length = 20)
    private String levelCode;

    @Column(name = "branch_id")
    private Long branchId;

    @Column(name = "card_start_date", nullable = false)
    private Date cardStartDate;

    @Column(name = "card_end_date", nullable = false)
    private Date cardEndDate;

    @Column(columnDefinition = "TINYINT default 1")
    private Integer status;

    @Column(name = "total_consumption", precision = 10, scale = 2)
    private BigDecimal totalConsumption;

    @Column(name = "remaining_free_classes")
    private Integer remainingFreeClasses;

    @Column(name = "create_time", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Column(name = "update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = new Date();
        updateTime = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = new Date();
    }
}
