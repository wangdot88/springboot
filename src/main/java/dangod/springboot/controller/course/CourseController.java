package dangod.springboot.controller.course;

import dangod.springboot.model.Course;
import dangod.springboot.model.CourseBooking;
import dangod.springboot.dto.CourseCreationDto;
import dangod.springboot.dto.CourseBookingDto;
import dangod.springboot.enums.CourseStatus;
import dangod.springboot.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @PostMapping
    public ResponseEntity<Course> createCourse(@Valid @RequestBody CourseCreationDto creationDto) {
        try {
            Course course = courseService.createCourse(creationDto);
            return ResponseEntity.ok(course);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long courseId) {
        return courseService.getCourseById(courseId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<List<Course>> getCoursesByTrainer(@PathVariable Long trainerId) {
        List<Course> courses = courseService.getCoursesByTrainer(trainerId);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/gym/{gymId}")
    public ResponseEntity<List<Course>> getCoursesByGym(@PathVariable String gymId) {
        List<Course> courses = courseService.getCoursesByGym(gymId);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Course>> getCoursesByStatus(@PathVariable CourseStatus status) {
        List<Course> courses = courseService.getCoursesByStatus(status);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<Course>> getUpcomingCourses() {
        List<Course> courses = courseService.getUpcomingCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/available")
    public ResponseEntity<List<Course>> getAvailableCourses() {
        List<Course> courses = courseService.getAvailableCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/fully-booked")
    public ResponseEntity<List<Course>> getFullyBookedCourses() {
        List<Course> courses = courseService.getFullyBookedCourses();
        return ResponseEntity.ok(courses);
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<Course> updateCourse(@PathVariable Long courseId, 
                                              @Valid @RequestBody CourseCreationDto updateDto) {
        try {
            Course course = courseService.updateCourse(courseId, updateDto);
            return ResponseEntity.ok(course);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{courseId}/cancel")
    public ResponseEntity<Course> cancelCourse(@PathVariable Long courseId) {
        try {
            Course course = courseService.cancelCourse(courseId);
            return ResponseEntity.ok(course);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long courseId) {
        try {
            courseService.deleteCourse(courseId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/between")
    public ResponseEntity<List<Course>> getCoursesBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        List<Course> courses = courseService.getCoursesBetween(startTime, endTime);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/member/{memberId}/recommendations")
    public ResponseEntity<List<Course>> recommendCoursesForMember(@PathVariable Long memberId) {
        try {
            List<Course> courses = courseService.recommendCoursesForMember(memberId);
            return ResponseEntity.ok(courses);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}