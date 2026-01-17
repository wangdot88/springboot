package dangod.springboot.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "notification", indexes = {
        @Index(name = "idx_member_id", columnList = "memberId"),
        @Index(name = "idx_is_read", columnList = "isRead"),
        @Index(name = "idx_send_time", columnList = "sendTime")
})
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 50)
    private String type;

    @Column(name = "is_read", columnDefinition = "TINYINT default 0")
    private Integer isRead;

    @Column(name = "send_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date sendTime;

    @Column(name = "read_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date readTime;

    @Column(name = "create_time", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @PrePersist
    protected void onCreate() {
        createTime = new Date();
        sendTime = new Date();
    }
}
