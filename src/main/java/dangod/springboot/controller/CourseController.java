package dangod.springboot.controller;

import dangod.springboot.core.common.PageResult;
import dangod.springboot.core.common.Result;
import dangod.springboot.dto.ReservationDTO;
import dangod.springboot.entity.Course;
import dangod.springboot.entity.Reservation;
import dangod.springboot.service.CourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "课程管理")
@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @ApiOperation("创建课程")
    @PostMapping
    public Result<Course> createCourse(@RequestBody Course course) {
        return Result.success(courseService.createCourse(course));
    }

    @ApiOperation("更新课程")
    @PutMapping("/{id}")
    public Result<Course> updateCourse(@PathVariable Long id, @RequestBody Course course) {
        course.setId(id);
        return Result.success(courseService.updateCourse(course));
    }

    @ApiOperation("删除课程")
    @DeleteMapping("/{id}")
    public Result<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return Result.success(null);
    }

    @ApiOperation("获取课程详情")
    @GetMapping("/{id}")
    public Result<Course> getCourseById(@PathVariable Long id) {
        return Result.success(courseService.getCourseById(id));
    }

    @ApiOperation("获取所有课程")
    @GetMapping
    public Result<PageResult<Course>> getAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Course> coursePage = courseService.getAllCourses(page, size);
        return Result.success(PageResult.of(coursePage));
    }

    @ApiOperation("获取可预约课程")
    @GetMapping("/available")
    public Result<List<Course>> getAvailableCourses(@RequestParam Long gymId) {
        return Result.success(courseService.getAvailableCourses(gymId));
    }

    @ApiOperation("按类型获取课程")
    @GetMapping("/type/{typeId}")
    public Result<List<Course>> getCoursesByType(
            @RequestParam Long gymId,
            @PathVariable Long typeId) {
        return Result.success(courseService.getCoursesByType(gymId, typeId));
    }

    @ApiOperation("按教练获取课程")
    @GetMapping("/coach/{coachId}")
    public Result<List<Course>> getCoursesByCoach(@PathVariable Long coachId) {
        return Result.success(courseService.getCoursesByCoach(coachId));
    }

    @ApiOperation("按日期获取课程")
    @GetMapping("/date")
    public Result<List<Course>> getCoursesByDate(
            @RequestParam Long gymId,
            @RequestParam String date) {
        return Result.success(courseService.getCoursesByDate(gymId, date));
    }

    @ApiOperation("推荐课程（基于体测数据）")
    @GetMapping("/recommend")
    public Result<List<Course>> recommendCourses(
            @RequestParam Long memberId,
            @RequestParam Long gymId) {
        return Result.success(courseService.recommendCourses(memberId, gymId));
    }

    @ApiOperation("预约课程")
    @PostMapping("/reserve")
    public Result<ReservationDTO> reserveCourse(
            @RequestParam Long memberId,
            @RequestParam Long courseId) {
        return Result.success(courseService.reserveCourse(memberId, courseId));
    }

    @ApiOperation("取消预约")
    @PostMapping("/reserve/cancel/{reservationId}")
    public Result<Void> cancelReservation(@PathVariable Long reservationId) {
        courseService.cancelReservation(reservationId);
        return Result.success(null);
    }

    @ApiOperation("上课签到")
    @PostMapping("/reserve/checkin/{reservationId}")
    public Result<Void> checkIn(@PathVariable Long reservationId) {
        courseService.checkIn(reservationId);
        return Result.success(null);
    }

    @ApiOperation("获取会员预约列表")
    @GetMapping("/reserve/member/{memberId}")
    public Result<List<Reservation>> getMemberReservations(@PathVariable Long memberId) {
        return Result.success(courseService.getMemberReservations(memberId));
    }
}
