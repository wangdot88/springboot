package dangod.springboot.service;

import dangod.springboot.core.common.ResultCode;
import dangod.springboot.dto.ReservationDTO;
import dangod.springboot.entity.*;
import dangod.springboot.core.exception.BusinessException;
import dangod.springboot.repository.*;
import dangod.springboot.core.util.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private WaitingListRepository waitingListRepository;

    @Autowired
    private MemberCardRepository memberCardRepository;

    @Autowired
    private PhysicalTestRepository physicalTestRepository;

    @Autowired
    private NotificationService notificationService;

    public Course createCourse(Course course) {
        course.setCourseNo(IdUtil.generateCourseNo());
        course.setCurrentCapacity(0);
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());
        return courseRepository.save(course);
    }

    public Course updateCourse(Course course) {
        course.setUpdatedAt(LocalDateTime.now());
        return courseRepository.save(course);
    }

    public void deleteCourse(Long courseId) {
        Course course = courseRepository.findOne(courseId);
        if (course != null) {
            courseRepository.delete(course);
        }
    }

    public Course getCourseById(Long courseId) {
        Course course = courseRepository.findOne(courseId);
        if (course == null) {
            throw new BusinessException(ResultCode.COURSE_NOT_FOUND);
        }
        return course;
    }

    public Page<Course> getAllCourses(int page, int size) {
        return courseRepository.findAll(new PageRequest(page, size));
    }

    public List<Course> getAvailableCourses(Long gymId) {
        return courseRepository.findBookableCourses(gymId, LocalDateTime.now());
    }

    public List<Course> getCoursesByType(Long gymId, Long typeId) {
        return courseRepository.findByGymIdAndCourseTypeIdAndStatus(gymId, typeId, 1);
    }

    public List<Course> getCoursesByCoach(Long coachId) {
        return courseRepository.findByCoachIdAndStatus(coachId, 1);
    }

    public List<Course> getCoursesByDate(Long gymId, String date) {
        LocalDateTime startOfDay = LocalDateTime.parse(date + "T00:00:00");
        LocalDateTime endOfDay = LocalDateTime.parse(date + "T23:59:59");
        return courseRepository.findByGymIdAndStartTimeBetween(gymId, startOfDay, endOfDay);
    }

    @Transactional
    public ReservationDTO reserveCourse(Long memberId, Long courseId) {
        Course course = courseRepository.findOne(courseId);
        if (course == null) {
            throw new BusinessException(ResultCode.COURSE_NOT_FOUND);
        }

        if (course.getStatus() != 1) {
            throw new BusinessException(ResultCode.COURSE_NOT_AVAILABLE);
        }

        if (course.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ResultCode.COURSE_ALREADY_STARTED);
        }

        List<Reservation> conflicts = reservationRepository.findConflictingReservations(
                memberId, course.getStartTime(), course.getEndTime());
        if (!conflicts.isEmpty()) {
            throw new BusinessException(ResultCode.RESERVATION_CONFLICT);
        }

        Optional<Reservation> existing = reservationRepository.findByMemberIdAndCourseId(memberId, courseId);
        if (existing.isPresent()) {
            throw new BusinessException(ResultCode.RESERVATION_EXISTS);
        }

        MemberCard card = memberCardRepository.findByMemberIdAndStatus(memberId, 1)
                .orElseThrow(() -> new BusinessException(ResultCode.CARD_EXPIRED));

        if (course.getCurrentCapacity() >= course.getMaxCapacity()) {
            return addToWaitingList(memberId, courseId);
        }

        return createReservation(memberId, course, card);
    }

    private ReservationDTO createReservation(Long memberId, Course course, MemberCard card) {
        Reservation reservation = new Reservation();
        reservation.setReservationNo(IdUtil.generateReservationNo());
        reservation.setMemberId(memberId);
        reservation.setCourseId(course.getId());
        reservation.setCardId(card.getId());
        reservation.setReservationTime(LocalDateTime.now());
        reservation.setStatus(1);
        reservation.setCreateTime(LocalDateTime.now());
        reservation.setUpdateTime(LocalDateTime.now());
        reservation = reservationRepository.save(reservation);

        course.setCurrentCapacity(course.getCurrentCapacity() + 1);
        courseRepository.save(course);

        return convertToDTO(reservation, course, card);
    }

    private ReservationDTO addToWaitingList(Long memberId, Long courseId) {
        Optional<WaitingList> existingWait = waitingListRepository.findByMemberIdAndCourseId(memberId, courseId);
        if (existingWait.isPresent()) {
            throw new BusinessException(ResultCode.ALREADY_IN_WAITING_LIST);
        }

        WaitingList waitingList = new WaitingList();
        waitingList.setMemberId(memberId);
        waitingList.setCourseId(courseId);
        waitingList.setJoinTime(LocalDateTime.now());
        waitingList.setStatus(0);
        waitingList.setCreateTime(LocalDateTime.now());
        waitingList.setUpdateTime(LocalDateTime.now());
        waitingListRepository.save(waitingList);

        ReservationDTO dto = new ReservationDTO();
        dto.setCourseId(courseId);
        dto.setMemberId(memberId);
        dto.setStatus(99);
        dto.setMessage("课程已满，已加入排队等待");
        return dto;
    }

    @Transactional
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findOne(reservationId);
        if (reservation == null) {
            throw new BusinessException(ResultCode.RESERVATION_NOT_FOUND);
        }

        Course course = courseRepository.findOne(reservation.getCourseId());
        if (course == null) {
            throw new BusinessException(ResultCode.COURSE_NOT_FOUND);
        }

        reservation.setStatus(3);
        reservation.setUpdateTime(LocalDateTime.now());
        reservationRepository.save(reservation);

        course.setCurrentCapacity(course.getCurrentCapacity() - 1);
        courseRepository.save(course);

        processWaitingList(course.getId());
    }

    private void processWaitingList(Long courseId) {
        Optional<WaitingList> firstInLine = waitingListRepository.findFirstByCourseIdOrderByCreatedAtAsc(courseId);
        if (firstInLine.isPresent()) {
            WaitingList waiting = firstInLine.get();
            waiting.setStatus(1);
            waiting.setUpdateTime(LocalDateTime.now());
            waitingListRepository.save(waiting);

            Course course = courseRepository.findOne(courseId);
            if (course != null) {
                MemberCard card = memberCardRepository.findByMemberIdAndStatus(waiting.getMemberId(), 1).orElse(null);
                if (card != null) {
                    createReservation(waiting.getMemberId(), course, card);
                }
            }

            notificationService.createNotification(
                    waiting.getMemberId(),
                    "课程空位通知",
                    "您预约的课程已有空位，已自动为您完成预约");
        }
    }

    @Transactional
    public void checkIn(Long reservationId) {
        Reservation reservation = reservationRepository.findOne(reservationId);
        if (reservation == null) {
            throw new BusinessException(ResultCode.RESERVATION_NOT_FOUND);
        }

        Course course = courseRepository.findOne(reservation.getCourseId());
        if (course == null) {
            throw new BusinessException(ResultCode.COURSE_NOT_FOUND);
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(course.getStartTime().minus(30, ChronoUnit.MINUTES))) {
            throw new BusinessException(ResultCode.CHECKIN_TOO_EARLY);
        }
        if (now.isAfter(course.getStartTime().plus(15, ChronoUnit.MINUTES))) {
            throw new BusinessException(ResultCode.CHECKIN_TOO_LATE);
        }

        reservation.setStatus(2);
        reservation.setCheckinTime(now);
        reservation.setUpdateTime(now);
        reservationRepository.save(reservation);
    }

    public List<Reservation> getMemberReservations(Long memberId) {
        return reservationRepository.findByMemberIdAndStatus(memberId, 1);
    }

    public List<Course> recommendCourses(Long memberId, Long gymId) {
        List<PhysicalTest> latestTests = physicalTestRepository.findTop5ByMemberIdOrderByTestTimeDesc(memberId);
        
        if (latestTests.isEmpty()) {
            return courseRepository.findBookableCourses(gymId, LocalDateTime.now());
        }

        PhysicalTest latestTest = latestTests.get(0);
        String recommendType = determineCourseType(latestTest);
        
        return courseRepository.recommendCoursesByType(gymId, recommendType, LocalDateTime.now());
    }

    private String determineCourseType(PhysicalTest test) {
        if (test.getBmi().compareTo(new BigDecimal("28")) > 0) {
            return "有氧训练";
        } else if (test.getBodyFatRate().compareTo(new BigDecimal("25")) > 0) {
            return "减脂训练";
        } else if (test.getMuscleMass().compareTo(new BigDecimal("40")) < 0) {
            return "力量训练";
        } else if (test.getFlexibilityScore() != null && test.getFlexibilityScore() < 80) {
            return "瑜伽拉伸";
        }
        return "综合训练";
    }

    private ReservationDTO convertToDTO(Reservation reservation, Course course, MemberCard card) {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());
        dto.setReservationNo(reservation.getReservationNo());
        dto.setMemberId(reservation.getMemberId());
        dto.setCourseId(reservation.getCourseId());
        dto.setCardId(reservation.getCardId());
        dto.setReservationTime(reservation.getReservationTime());
        dto.setStatus(reservation.getStatus());
        dto.setCourseName(course.getCourseName());
        dto.setCourseStartTime(course.getStartTime());
        dto.setCourseEndTime(course.getEndTime());
        dto.setCoachId(course.getCoachId());
        return dto;
    }
}
