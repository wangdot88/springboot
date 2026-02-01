package dangod.springboot.controller.course;

import dangod.springboot.model.CourseBooking;
import dangod.springboot.dto.CourseBookingDto;
import dangod.springboot.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/course-bookings")
public class CourseBookingController {

    @Autowired
    private CourseService courseService;

    @PostMapping
    public ResponseEntity<CourseBooking> bookCourse(@Valid @RequestBody CourseBookingDto bookingDto) {
        try {
            CourseBooking booking = courseService.bookCourse(bookingDto);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<CourseBooking> getBookingById(@PathVariable Long bookingId) {
        // 这里需要在CourseService中添加getBookingById方法
        // 暂时返回空响应
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<CourseBooking>> getBookingsByMember(@PathVariable Long memberId) {
        List<CourseBooking> bookings = courseService.getBookingsByMember(memberId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<CourseBooking>> getBookingsByCourse(@PathVariable Long courseId) {
        List<CourseBooking> bookings = courseService.getBookingsByCourse(courseId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/course/{courseId}/waiting-list")
    public ResponseEntity<List<CourseBooking>> getWaitingList(@PathVariable Long courseId) {
        List<CourseBooking> waitingList = courseService.getWaitingList(courseId);
        return ResponseEntity.ok(waitingList);
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<CourseBooking> cancelBooking(@PathVariable Long bookingId, 
                                                       @RequestParam(required = false) String cancelReason) {
        try {
            CourseBooking booking = courseService.cancelBooking(bookingId, cancelReason);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{bookingId}/check-in")
    public ResponseEntity<CourseBooking> checkIn(@PathVariable Long bookingId) {
        try {
            CourseBooking booking = courseService.checkIn(bookingId);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/course/{courseId}/process-waiting-list")
    public ResponseEntity<Void> processWaitingList(@PathVariable Long courseId) {
        courseService.processWaitingList(courseId);
        return ResponseEntity.ok().build();
    }
}