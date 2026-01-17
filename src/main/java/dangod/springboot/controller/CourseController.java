package dangod.springboot.controller;

import dangod.springboot.common.Result;
import dangod.springboot.entity.Course;
import dangod.springboot.entity.CourseSchedule;
import dangod.springboot.entity.CourseBooking;
import dangod.springboot.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @PostMapping
    public Result<Course> createCourse(@RequestBody Course course) {
        return Result.success(courseService.createCourse(course));
    }

    @PutMapping("/{id}")
    public Result<Course> updateCourse(@PathVariable Long id, @RequestBody Course course) {
        return Result.success(courseService.updateCourse(id, course));
    }

    @GetMapping("/{id}")
    public Result<Course> getCourse(@PathVariable Long id) {
        return Result.success(courseService.getCourseById(id));
    }

    @GetMapping("/store/{storeId}")
    public Result<List<Course>> getCoursesByStore(@PathVariable Long storeId) {
        return Result.success(courseService.getCoursesByStore(storeId));
    }

    @GetMapping("/type/{type}")
    public Result<List<Course>> getCoursesByType(@PathVariable Course.CourseType type) {
        return Result.success(courseService.getCoursesByType(type));
    }

    @GetMapping
    public Result<List<Course>> getAllCourses() {
        return Result.success(courseService.getAllCourses());
    }

    @GetMapping("/recommend/{memberId}")
    public Result<List<Course>> getRecommendedCourses(@PathVariable Long memberId, @RequestParam Long storeId) {
        return Result.success(courseService.getRecommendedCourses(memberId, storeId));
    }

    @PostMapping("/schedule")
    public Result<CourseSchedule> createSchedule(@RequestBody CourseSchedule schedule) {
        return Result.success(courseService.createSchedule(schedule));
    }

    @GetMapping("/schedule/course/{courseId}")
    public Result<List<CourseSchedule>> getSchedulesByCourse(@PathVariable Long courseId) {
        return Result.success(courseService.getSchedulesByCourse(courseId));
    }

    @GetMapping("/schedule/store/{storeId}")
    public Result<List<CourseSchedule>> getSchedulesByStore(@PathVariable Long storeId) {
        return Result.success(courseService.getSchedulesByStore(storeId));
    }

    @PostMapping("/booking")
    public Result<CourseBooking> bookCourse(@RequestParam Long memberId, @RequestParam Long scheduleId) {
        return Result.success(courseService.bookCourse(memberId, scheduleId));
    }

    @DeleteMapping("/booking/{bookingId}")
    public Result<Void> cancelBooking(@PathVariable Long bookingId) {
        courseService.cancelBooking(bookingId);
        return Result.success();
    }

    @PostMapping("/booking/{bookingId}/checkin")
    public Result<Void> checkIn(@PathVariable Long bookingId) {
        courseService.checkIn(bookingId);
        return Result.success();
    }

    @GetMapping("/booking/member/{memberId}")
    public Result<List<CourseBooking>> getMemberBookings(@PathVariable Long memberId) {
        return Result.success(courseService.getMemberBookings(memberId));
    }

    @GetMapping("/booking/schedule/{scheduleId}")
    public Result<List<CourseBooking>> getScheduleBookings(@PathVariable Long scheduleId) {
        return Result.success(courseService.getScheduleBookings(scheduleId));
    }

    @GetMapping("/booking/upcoming/{memberId}")
    public Result<List<CourseBooking>> getUpcomingBookings(@PathVariable Long memberId) {
        return Result.success(courseService.getUpcomingBookings(memberId));
    }

    @GetMapping("/popular")
    public Result<List<Course>> getPopularCourses() {
        return Result.success(courseService.getPopularCourses());
    }
}
