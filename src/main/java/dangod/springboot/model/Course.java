package dangod.springboot.model;

import dangod.springboot.enums.CourseStatus;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_id", nullable = false)
    private Gym gym;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private Integer maxCapacity;

    @Column(nullable = false)
    private Integer currentBookings = 0;

    @Column(precision = 10, scale = 2)
    private Double price;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private CourseStatus status = CourseStatus.SCHEDULED;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private Integer difficultyLevel;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CourseBooking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EquipmentUsage> equipmentUsages = new ArrayList<>();

    public Course() {
    }

    public Course(String name, String description, Trainer trainer, Gym gym, 
                  LocalDateTime startTime, LocalDateTime endTime, Integer maxCapacity, 
                  Double price, String category, Integer difficultyLevel) {
        this.name = name;
        this.description = description;
        this.trainer = trainer;
        this.gym = gym;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxCapacity = maxCapacity;
        this.price = price;
        this.category = category;
        this.difficultyLevel = difficultyLevel;
    }

    public Integer getAvailableSlots() {
        return maxCapacity - currentBookings;
    }

    public Boolean isFullyBooked() {
        return currentBookings >= maxCapacity;
    }

    public Boolean isBookingOpen() {
        return status == CourseStatus.SCHEDULED && startTime.isAfter(LocalDateTime.now());
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public Gym getGym() {
        return gym;
    }

    public void setGym(Gym gym) {
        this.gym = gym;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public Integer getCurrentBookings() {
        return currentBookings;
    }

    public void setCurrentBookings(Integer currentBookings) {
        this.currentBookings = currentBookings;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public CourseStatus getStatus() {
        return status;
    }

    public void setStatus(CourseStatus status) {
        this.status = status;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(Integer difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public List<CourseBooking> getBookings() {
        return bookings;
    }

    public void setBookings(List<CourseBooking> bookings) {
        this.bookings = bookings;
    }

    public List<EquipmentUsage> getEquipmentUsages() {
        return equipmentUsages;
    }

    public void setEquipmentUsages(List<EquipmentUsage> equipmentUsages) {
        this.equipmentUsages = equipmentUsages;
    }
}