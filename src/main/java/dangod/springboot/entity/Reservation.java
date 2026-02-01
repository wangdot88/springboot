package dangod.springboot.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_reservation")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "reservation_no", length = 32, unique = true)
    private String reservationNo;
    
    @Column(name = "member_id")
    private Long memberId;
    
    @Column(name = "card_id")
    private Long cardId;
    
    @Column(name = "course_id")
    private Long courseId;
    
    @Column(name = "reservation_time")
    private LocalDateTime reservationTime;
    
    @Column(name = "status")
    private Integer status;
    
    @Column(name = "checkin_time")
    private LocalDateTime checkinTime;
    
    @Column(name = "cancel_time")
    private LocalDateTime cancelTime;
    
    @Column(name = "cancel_reason")
    private String cancelReason;
    
    @Column(name = "from_waiting_list")
    private Integer fromWaitingList;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
