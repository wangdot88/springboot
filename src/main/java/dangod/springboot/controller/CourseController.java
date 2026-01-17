package dangod.springboot.controller;

import dangod.springboot.common.BusinessException;
import dangod.springboot.common.Result;
import dangod.springboot.entity.Course;
import dangod.springboot.entity.CourseReservation;
import dangod.springboot.entity.CourseType;
import dangod.springboot.service.CourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/course")
@Api("课程管理API")
public class CourseController {
    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);
    
    @Autowired
    private CourseService courseService;
    
    @PostMapping("/create")
    @ApiOperation("创建课程")
    public Result createCourse(@RequestBody Course course) {
        try {
            Course created = courseService.createCourse(course);
            return Result.success(created);
        } catch (BusinessException e) {
            logger.error("创建课程失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            logger.error("创建课程异常", e);
            return Result.error("创建课程异常: " + e.getMessage());
        }
    }
    
    @PutMapping("/update")
    @ApiOperation("更新课程")
    public Result updateCourse(@RequestBody Course course) {
        try {
            Course updated = courseService.updateCourse(course);
            return Result.success(updated);
        } catch (BusinessException e) {
            logger.error("更新课程失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            logger.error("更新课程异常", e);
            return Result.error("更新课程异常: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/delete/{courseId}")
    @ApiOperation("删除课程")
    public Result deleteCourse(@PathVariable Long courseId) {
        try {
            courseService.deleteCourse(courseId);
            return Result.success("删除成功");
        } catch (BusinessException e) {
            logger.error("删除课程失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            logger.error("删除课程异常", e);
            return Result.error("删除课程异常: " + e.getMessage());
        }
    }
    
    @GetMapping("/get/{courseId}")
    @ApiOperation("获取课程详情")
    public Result getCourse(@PathVariable Long courseId) {
        try {
            Course course = courseService.getCourseById(courseId);
            if (course == null) {
                return Result.error("课程不存在");
            }
            return Result.success(course);
        } catch (Exception e) {
            logger.error("获取课程异常", e);
            return Result.error("获取课程异常: " + e.getMessage());
        }
    }
    
    @GetMapping("/by-store/{storeId}")
    @ApiOperation("获取门店课程列表")
    public Result getCoursesByStore(@PathVariable Long storeId) {
        try {
            List<Course> courses = courseService.getCoursesByStore(storeId);
            return Result.success(courses);
        } catch (Exception e) {
            logger.error("获取门店课程异常", e);
            return Result.error("获取门店课程异常: " + e.getMessage());
        }
    }
    
    @GetMapping("/upcoming/{storeId}")
    @ApiOperation("获取即将开始的课程")
    public Result getUpcomingCourses(@PathVariable Long storeId) {
        try {
            List<Course> courses = courseService.getUpcomingCourses(storeId);
            return Result.success(courses);
        } catch (Exception e) {
            logger.error("获取即将开始的课程异常", e);
            return Result.error("获取即将开始的课程异常: " + e.getMessage());
        }
    }
    
    @GetMapping("/date-range")
    @ApiOperation("按日期范围查询课程")
    public Result getCoursesByDateRange(
            @ApiParam("门店ID") @RequestParam Long storeId,
            @ApiParam("开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime startDate,
            @ApiParam("结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime endDate) {
        try {
            List<Course> courses = courseService.getCoursesByDateRange(storeId, startDate, endDate);
            return Result.success(courses);
        } catch (Exception e) {
            logger.error("按日期范围查询课程异常", e);
            return Result.error("按日期范围查询课程异常: " + e.getMessage());
        }
    }
    
    @PostMapping("/reserve")
    @ApiOperation("预约课程")
    public Result reserveCourse(
            @ApiParam("会员ID") @RequestParam Long memberId,
            @ApiParam("课程ID") @RequestParam Long courseId) {
        try {
            CourseReservation reservation = courseService.reserveCourse(memberId, courseId);
            String message = reservation.getStatus() == 1 ? "预约成功" : "已加入等待队列，位置: " + reservation.getWaitingPosition();
            return Result.success(message, reservation);
        } catch (BusinessException e) {
            logger.error("预约课程失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            logger.error("预约课程异常", e);
            return Result.error("预约课程异常: " + e.getMessage());
        }
    }
    
    @PostMapping("/cancel")
    @ApiOperation("取消预约")
    public Result cancelReservation(
            @ApiParam("预约ID") @RequestParam Long reservationId,
            @ApiParam("会员ID") @RequestParam Long memberId) {
        try {
            courseService.cancelReservation(reservationId, memberId);
            return Result.success("取消成功");
        } catch (BusinessException e) {
            logger.error("取消预约失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            logger.error("取消预约异常", e);
            return Result.error("取消预约异常: " + e.getMessage());
        }
    }
    
    @PostMapping("/sign-in")
    @ApiOperation("课程签到")
    public Result signIn(@ApiParam("预约ID") @RequestParam Long reservationId) {
        try {
            courseService.signIn(reservationId);
            return Result.success("签到成功");
        } catch (BusinessException e) {
            logger.error("签到失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            logger.error("签到异常", e);
            return Result.error("签到异常: " + e.getMessage());
        }
    }
    
    @GetMapping("/member-reservations/{memberId}")
    @ApiOperation("获取会员预约列表")
    public Result getMemberReservations(@PathVariable Long memberId) {
        try {
            List<CourseReservation> reservations = courseService.getMemberReservations(memberId);
            return Result.success(reservations);
        } catch (Exception e) {
            logger.error("获取会员预约列表异常", e);
            return Result.error("获取会员预约列表异常: " + e.getMessage());
        }
    }
    
    @GetMapping("/course-reservations/{courseId}")
    @ApiOperation("获取课程预约列表")
    public Result getCourseReservations(@PathVariable Long courseId) {
        try {
            List<CourseReservation> reservations = courseService.getCourseReservations(courseId);
            return Result.success(reservations);
        } catch (Exception e) {
            logger.error("获取课程预约列表异常", e);
            return Result.error("获取课程预约列表异常: " + e.getMessage());
        }
    }
    
    @GetMapping("/recommend/{memberId}")
    @ApiOperation("推荐课程")
    public Result recommendCourses(@PathVariable Long memberId) {
        try {
            List<Course> recommendations = courseService.recommendCourses(memberId);
            return Result.success(recommendations);
        } catch (BusinessException e) {
            logger.error("推荐课程失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            logger.error("推荐课程异常", e);
            return Result.error("推荐课程异常: " + e.getMessage());
        }
    }
    
    @GetMapping("/hot")
    @ApiOperation("热门课程")
    public Result getHotCourses(@ApiParam("数量限制") @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Course> hotCourses = courseService.getHotCourses(limit);
            return Result.success(hotCourses);
        } catch (Exception e) {
            logger.error("获取热门课程异常", e);
            return Result.error("获取热门课程异常: " + e.getMessage());
        }
    }
    
    @GetMapping("/types")
    @ApiOperation("课程类型列表")
    public Result getCourseTypes() {
        try {
            List<CourseType> types = courseService.getCourseTypes();
            return Result.success(types);
        } catch (Exception e) {
            logger.error("获取课程类型异常", e);
            return Result.error("获取课程类型异常: " + e.getMessage());
        }
    }
    
    @GetMapping("/type/{typeId}")
    @ApiOperation("课程类型详情")
    public Result getCourseType(@PathVariable Long typeId) {
        try {
            CourseType type = courseService.getCourseTypeById(typeId);
            if (type == null) {
                return Result.error("课程类型不存在");
            }
            return Result.success(type);
        } catch (Exception e) {
            logger.error("获取课程类型异常", e);
            return Result.error("获取课程类型异常: " + e.getMessage());
        }
    }
    
    @PostMapping("/type/create")
    @ApiOperation("创建课程类型")
    public Result createCourseType(@RequestBody CourseType courseType) {
        try {
            CourseType created = courseService.createCourseType(courseType);
            return Result.success(created);
        } catch (BusinessException e) {
            logger.error("创建课程类型失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            logger.error("创建课程类型异常", e);
            return Result.error("创建课程类型异常: " + e.getMessage());
        }
    }
    
    @PutMapping("/type/update")
    @ApiOperation("更新课程类型")
    public Result updateCourseType(@RequestBody CourseType courseType) {
        try {
            CourseType updated = courseService.updateCourseType(courseType);
            return Result.success(updated);
        } catch (BusinessException e) {
            logger.error("更新课程类型失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            logger.error("更新课程类型异常", e);
            return Result.error("更新课程类型异常: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/type/delete/{typeId}")
    @ApiOperation("删除课程类型")
    public Result deleteCourseType(@PathVariable Long typeId) {
        try {
            courseService.deleteCourseType(typeId);
            return Result.success("删除成功");
        } catch (BusinessException e) {
            logger.error("删除课程类型失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            logger.error("删除课程类型异常", e);
            return Result.error("删除课程类型异常: " + e.getMessage());
        }
    }
}
