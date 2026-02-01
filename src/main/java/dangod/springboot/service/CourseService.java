package dangod.springboot.service;

import dangod.springboot.model.Course;
import dangod.springboot.model.CourseBooking;
import dangod.springboot.dto.CourseCreationDto;
import dangod.springboot.dto.CourseBookingDto;
import dangod.springboot.enums.CourseStatus;
import dangod.springboot.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CourseService {
    
    Course createCourse(CourseCreationDto creationDto);
    
    Optional<Course> getCourseById(Long courseId);
    
    List<Course> getAllCourses();
    
    List<Course> getCoursesByTrainer(Long trainerId);
    
    List<Course> getCoursesByGym(String gymId);
    
    List<Course> getCoursesByStatus(CourseStatus status);
    
    List<Course> getUpcomingCourses();
    
    List<Course> getAvailableCourses();
    
    List<Course> getFullyBookedCourses();
    
    Course updateCourse(Long courseId, CourseCreationDto updateDto);
    
    Course cancelCourse(Long courseId);
    
    CourseBooking bookCourse(CourseBookingDto bookingDto);
    
    CourseBooking cancelBooking(Long bookingId, String cancelReason);
    
    CourseBooking checkIn(Long bookingId);
    
    List<CourseBooking> getBookingsByMember(Long memberId);
    
    List<CourseBooking> getBookingsByCourse(Long courseId);
    
    List<CourseBooking> getWaitingList(Long courseId);
    
    void processWaitingList(Long courseId);
    
    List<Course> recommendCoursesForMember(Long memberId);
    
    List<Course> getCoursesBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    void deleteCourse(Long courseId);
}