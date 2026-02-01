package dangod.springboot.service.impl;

import dangod.springboot.model.Course;
import dangod.springboot.model.CourseBooking;
import dangod.springboot.model.Member;
import dangod.springboot.model.Trainer;
import dangod.springboot.model.Gym;
import dangod.springboot.dto.CourseCreationDto;
import dangod.springboot.dto.CourseBookingDto;
import dangod.springboot.enums.CourseStatus;
import dangod.springboot.enums.BookingStatus;
import dangod.springboot.repository.CourseRepository;
import dangod.springboot.repository.CourseBookingRepository;
import dangod.springboot.repository.MemberRepository;
import dangod.springboot.repository.TrainerRepository;
import dangod.springboot.repository.GymRepository;
import dangod.springboot.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private CourseBookingRepository bookingRepository;
    
    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private TrainerRepository trainerRepository;
    
    @Autowired
    private GymRepository gymRepository;

    @Override
    public Course createCourse(CourseCreationDto creationDto) {
        // 检查教练是否存在
        Optional<Trainer> trainerOpt = trainerRepository.findById(creationDto.getTrainerId());
        if (!trainerOpt.isPresent()) {
            throw new RuntimeException("教练不存在");
        }
        
        // 检查门店是否存在
        Optional<Gym> gymOpt = gymRepository.findByStoreId(creationDto.getGymId());
        if (!gymOpt.isPresent()) {
            throw new RuntimeException("门店不存在");
        }
        
        // 创建课程
        Course course = new Course(
            creationDto.getName(),
            creationDto.getDescription(),
            trainerOpt.get(),
            gymOpt.get(),
            creationDto.getStartTime(),
            creationDto.getEndTime(),
            creationDto.getMaxCapacity(),
            creationDto.getPrice(),
            creationDto.getCategory(),
            creationDto.getDifficultyLevel()
        );
        
        return courseRepository.save(course);
    }

    @Override
    public Optional<Course> getCourseById(Long courseId) {
        return courseRepository.findById(courseId);
    }

    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    public List<Course> getCoursesByTrainer(Long trainerId) {
        return courseRepository.findByTrainer_Id(trainerId);
    }

    @Override
    public List<Course> getCoursesByGym(String gymId) {
        return courseRepository.findByGym_StoreId(gymId);
    }

    @Override
    public List<Course> getCoursesByStatus(CourseStatus status) {
        return courseRepository.findByStatus(status);
    }

    @Override
    public List<Course> getUpcomingCourses() {
        return courseRepository.findUpcomingCourses(LocalDateTime.now(), CourseStatus.SCHEDULED);
    }

    @Override
    public List<Course> getAvailableCourses() {
        return courseRepository.findAvailableCourses(LocalDateTime.now(), CourseStatus.SCHEDULED);
    }

    @Override
    public List<Course> getFullyBookedCourses() {
        return courseRepository.findFullyBookedCourses(LocalDateTime.now(), CourseStatus.SCHEDULED);
    }

    @Override
    public Course updateCourse(Long courseId, CourseCreationDto updateDto) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (!courseOpt.isPresent()) {
            throw new RuntimeException("课程不存在");
        }
        
        Course course = courseOpt.get();
        
        // 检查教练是否存在
        Optional<Trainer> trainerOpt = trainerRepository.findById(updateDto.getTrainerId());
        if (!trainerOpt.isPresent()) {
            throw new RuntimeException("教练不存在");
        }
        
        // 检查门店是否存在
        Optional<Gym> gymOpt = gymRepository.findByStoreId(updateDto.getGymId());
        if (!gymOpt.isPresent()) {
            throw new RuntimeException("门店不存在");
        }
        
        // 更新课程信息
        course.setName(updateDto.getName());
        course.setDescription(updateDto.getDescription());
        course.setTrainer(trainerOpt.get());
        course.setGym(gymOpt.get());
        course.setStartTime(updateDto.getStartTime());
        course.setEndTime(updateDto.getEndTime());
        course.setMaxCapacity(updateDto.getMaxCapacity());
        course.setPrice(updateDto.getPrice());
        course.setCategory(updateDto.getCategory());
        course.setDifficultyLevel(updateDto.getDifficultyLevel());
        
        return courseRepository.save(course);
    }

    @Override
    public Course cancelCourse(Long courseId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (!courseOpt.isPresent()) {
            throw new RuntimeException("课程不存在");
        }
        
        Course course = courseOpt.get();
        course.setStatus(CourseStatus.CANCELLED);
        
        // 取消所有预约
        List<CourseBooking> bookings = bookingRepository.findByCourse_Id(courseId);
        for (CourseBooking booking : bookings) {
            if (booking.getStatus() == BookingStatus.BOOKED || booking.getStatus() == BookingStatus.WAITING) {
                booking.setStatus(BookingStatus.CANCELLED);
                booking.setCancelTime(LocalDateTime.now());
                booking.setCancelReason("课程取消");
                bookingRepository.save(booking);
            }
        }
        
        return courseRepository.save(course);
    }

    @Override
    public CourseBooking bookCourse(CourseBookingDto bookingDto) {
        // 检查会员是否存在
        Optional<Member> memberOpt = memberRepository.findById(bookingDto.getMemberId());
        if (!memberOpt.isPresent()) {
            throw new RuntimeException("会员不存在");
        }
        
        // 检查课程是否存在
        Optional<Course> courseOpt = courseRepository.findById(bookingDto.getCourseId());
        if (!courseOpt.isPresent()) {
            throw new RuntimeException("课程不存在");
        }
        
        Course course = courseOpt.get();
        
        // 检查课程是否可以预约
        if (!course.isBookingOpen()) {
            throw new RuntimeException("课程当前不可预约");
        }
        
        // 检查会员是否已经预约了该课程
        Optional<CourseBooking> existingBooking = bookingRepository.findByMemberAndCourse(
            bookingDto.getMemberId(), bookingDto.getCourseId());
        if (existingBooking.isPresent() && 
            (existingBooking.get().getStatus() == BookingStatus.BOOKED || 
             existingBooking.get().getStatus() == BookingStatus.WAITING)) {
            throw new RuntimeException("会员已预约该课程");
        }
        
        // 检查会员状态
        Member member = memberOpt.get();
        if (member.getStatus() != dangod.springboot.enums.MemberStatus.ACTIVE) {
            throw new RuntimeException("会员状态不正常，无法预约课程");
        }
        
        // 获取当前预约人数
        Long currentBookings = bookingRepository.countBookingsByCourseAndStatus(
            bookingDto.getCourseId(), BookingStatus.BOOKED);
        
        CourseBooking booking;
        if (currentBookings < course.getMaxCapacity()) {
            // 直接预约
            booking = new CourseBooking(member, course, 0);
            booking.setStatus(BookingStatus.BOOKED);
            
            // 更新课程预约人数
            course.setCurrentBookings(course.getCurrentBookings() + 1);
            courseRepository.save(course);
        } else {
            // 加入排队
            Long waitingCount = bookingRepository.countBookingsByCourseAndStatus(
                bookingDto.getCourseId(), BookingStatus.WAITING);
            booking = new CourseBooking(member, course, waitingCount.intValue() + 1);
            booking.setStatus(BookingStatus.WAITING);
        }
        
        return bookingRepository.save(booking);
    }

    @Override
    public CourseBooking cancelBooking(Long bookingId, String cancelReason) {
        CourseBooking booking = bookingRepository.findOne(bookingId);
        if (booking == null) {
            throw new RuntimeException("预约记录不存在");
        }
        
        // 只有已预约或排队中的预约可以取消
        if (booking.getStatus() != BookingStatus.BOOKED && booking.getStatus() != BookingStatus.WAITING) {
            throw new RuntimeException("当前状态不允许取消预约");
        }
        
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelTime(LocalDateTime.now());
        booking.setCancelReason(cancelReason);
        
        // 如果是已预约状态，需要更新课程预约人数并处理排队
        if (booking.getStatus() == BookingStatus.BOOKED) {
            Course course = booking.getCourse();
            course.setCurrentBookings(course.getCurrentBookings() - 1);
            courseRepository.save(course);
            
            // 处理排队
            processWaitingList(course.getId());
        }
        
        return bookingRepository.save(booking);
    }

    @Override
    public CourseBooking checkIn(Long bookingId) {
        CourseBooking booking = bookingRepository.findOne(bookingId);
        if (booking == null) {
            throw new RuntimeException("预约记录不存在");
        }
        
        // 只有已预约状态可以签到
        if (booking.getStatus() != BookingStatus.BOOKED) {
            throw new RuntimeException("当前状态不允许签到");
        }
        
        booking.setStatus(BookingStatus.ATTENDED);
        booking.setCheckInTime(LocalDateTime.now());
        
        // 更新会员最后访问日期
        Member member = booking.getMember();
        member.setLastVisitDate(LocalDateTime.now().toLocalDate());
        member.setUsedSessions(member.getUsedSessions() + 1);
        memberRepository.save(member);
        
        return bookingRepository.save(booking);
    }

    @Override
    public List<CourseBooking> getBookingsByMember(Long memberId) {
        return bookingRepository.findByMember_Id(memberId);
    }

    @Override
    public List<CourseBooking> getBookingsByCourse(Long courseId) {
        return bookingRepository.findByCourse_Id(courseId);
    }

    @Override
    public List<CourseBooking> getWaitingList(Long courseId) {
        return bookingRepository.findWaitingListByCourse(courseId, BookingStatus.WAITING);
    }

    @Override
    public void processWaitingList(Long courseId) {
        // 获取课程信息
        Course course = courseRepository.findOne(courseId);
        if (course == null) {
            return;
        }
        
        // 检查是否有空位
        if (course.getCurrentBookings() >= course.getMaxCapacity()) {
            return;
        }
        
        // 获取排队列表
        List<CourseBooking> waitingList = bookingRepository.findWaitingListByCourse(
            courseId, BookingStatus.WAITING);
        
        // 处理排队
        for (CourseBooking waitingBooking : waitingList) {
            if (course.getCurrentBookings() < course.getMaxCapacity()) {
                // 更新预约状态
                waitingBooking.setStatus(BookingStatus.BOOKED);
                waitingBooking.setQueuePosition(0);
                bookingRepository.save(waitingBooking);
                
                // 更新课程预约人数
                course.setCurrentBookings(course.getCurrentBookings() + 1);
                courseRepository.save(course);
            } else {
                break;
            }
        }
    }

    @Override
    public List<Course> recommendCoursesForMember(Long memberId) {
        // 检查会员是否存在
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (!memberOpt.isPresent()) {
            throw new RuntimeException("会员不存在");
        }
        
        // 这里可以根据会员的体测数据、历史预约记录等进行推荐
        // 简化实现，返回所有可预约的课程
        return getAvailableCourses();
    }

    @Override
    public List<Course> getCoursesBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return courseRepository.findCoursesBetween(startTime, endTime);
    }

    @Override
    public void deleteCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new RuntimeException("课程不存在");
        }
        
        // 检查是否有预约记录
        List<CourseBooking> bookings = bookingRepository.findByCourse_Id(courseId);
        if (!bookings.isEmpty()) {
            throw new RuntimeException("课程有预约记录，无法删除");
        }
        
        courseRepository.deleteById(courseId);
    }
}