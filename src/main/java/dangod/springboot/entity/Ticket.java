package dangod.springboot.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_agent_id")
    private User assignedAgent;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private TicketCategory category;
    
    @Column(name = "priority", nullable = false)
    @Enumerated(EnumType.STRING)
    private Priority priority;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
    
    @Column(name = "due_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dueDate;
    
    @Column(name = "resolved_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date resolvedAt;
    
    @Column(name = "closed_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date closedAt;
    
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    
    public enum Priority {
        LOW,      // 低
        MEDIUM,   // 中
        HIGH,     // 高
        URGENT    // 紧急
    }
    
    public enum Status {
        PENDING,    // 待处理
        IN_PROGRESS, // 处理中
        RESOLVED,   // 已解决
        CLOSED      // 已关闭
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
        if (status == null) {
            status = Status.PENDING;
        }
        if (priority == null) {
            priority = Priority.MEDIUM;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public User getCustomer() {
        return customer;
    }
    
    public void setCustomer(User customer) {
        this.customer = customer;
    }
    
    public User getAssignedAgent() {
        return assignedAgent;
    }
    
    public void setAssignedAgent(User assignedAgent) {
        this.assignedAgent = assignedAgent;
    }
    
    public TicketCategory getCategory() {
        return category;
    }
    
    public void setCategory(TicketCategory category) {
        this.category = category;
    }
    
    public Priority getPriority() {
        return priority;
    }
    
    public void setPriority(Priority priority) {
        this.priority = priority;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public Date getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
    
    public Date getResolvedAt() {
        return resolvedAt;
    }
    
    public void setResolvedAt(Date resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
    
    public Date getClosedAt() {
        return closedAt;
    }
    
    public void setClosedAt(Date closedAt) {
        this.closedAt = closedAt;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public Date getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}