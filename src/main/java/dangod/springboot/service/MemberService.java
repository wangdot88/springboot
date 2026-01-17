package dangod.springboot.service;

import dangod.springboot.entity.*;
import dangod.springboot.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MemberService {
    private static final Logger logger = LoggerFactory.getLogger(MemberService.class);
    private static final int EXPIRY_REMINDER_DAYS = 7;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MembershipLevelRepository membershipLevelRepository;

    @Autowired
    private MemberFreezeApplicationRepository freezeApplicationRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private GymBranchRepository gymBranchRepository;

    @Transactional
    public Member createMember(Member member) {
        // 生成会员编号
        member.setMemberNo(generateMemberNo());
        // 设置默认等级
        if (member.getLevelCode() == null) {
            member.setLevelCode("BRONZE");
        }
        // 设置初始状态
        if (member.getStatus() == null) {
            member.setStatus(1);
        }
        // 初始化免费课程次数
        MembershipLevel level = membershipLevelRepository.findByLevelCode(member.getLevelCode()).orElse(null);
        if (level != null) {
            member.setRemainingFreeClasses(level.getFreeClassesPerMonth());
        }
        return memberRepository.save(member);
    }

    private String generateMemberNo() {
        return "MEM" + System.currentTimeMillis();
    }

    public Optional<Member> getMemberById(Long id) {
        return memberRepository.findById(id);
    }

    public Optional<Member> getMemberByPhone(String phone) {
        return memberRepository.findByPhone(phone);
    }

    public Optional<Member> getMemberByMemberNo(String memberNo) {
        return memberRepository.findByMemberNo(memberNo);
    }

    public List<Member> getMembersByLevel(String levelCode) {
        return memberRepository.findByLevelCode(levelCode);
    }

    public List<Member> getMembersByStatus(Integer status) {
        return memberRepository.findByStatus(status);
    }

    @Transactional
    public Member updateMember(Member member) {
        return memberRepository.save(member);
    }

    @Transactional
    public Member updateMemberLevel(Long memberId, String newLevelCode) {
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            member.setLevelCode(newLevelCode);
            // 更新免费课程次数
            MembershipLevel level = membershipLevelRepository.findByLevelCode(newLevelCode).orElse(null);
            if (level != null) {
                member.setRemainingFreeClasses(level.getFreeClassesPerMonth());
            }
            return memberRepository.save(member);
        }
        return null;
    }

    @Transactional
    public MemberFreezeApplication applyForFreeze(Long memberId, String reason, int requestDays) {
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (!memberOpt.isPresent()) {
            throw new RuntimeException("会员不存在");
        }
        Member member = memberOpt.get();

        // 检查是否有未完成的冻结申请
        List<MemberFreezeApplication> pendingApps = freezeApplicationRepository.findByMemberIdAndStatus(memberId, 0);
        if (!pendingApps.isEmpty()) {
            throw new RuntimeException("存在未处理的冻结申请");
        }

        // 检查会员等级是否允许冻结
        MembershipLevel level = membershipLevelRepository.findByLevelCode(member.getLevelCode()).orElse(null);
        if (level == null || level.getCanFreeze() != 1) {
            throw new RuntimeException("当前会员等级不支持冻结");
        }

        // 检查申请天数是否超过限制
        if (requestDays > level.getFreezeDuration()) {
            throw new RuntimeException("申请冻结天数超过最大限制");
        }

        // 创建申请
        MemberFreezeApplication application = new MemberFreezeApplication();
        application.setMemberId(memberId);
        application.setReason(reason);
        application.setRequestDays(requestDays);
        application.setStatus(0); // 待审批

        return freezeApplicationRepository.save(application);
    }

    @Transactional
    public MemberFreezeApplication approveFreezeApplication(Long applicationId, Long approverId, boolean approved, String remark) {
        Optional<MemberFreezeApplication> appOpt = freezeApplicationRepository.findById(applicationId);
        if (!appOpt.isPresent()) {
            throw new RuntimeException("申请不存在");
        }

        MemberFreezeApplication application = appOpt.get();
        if (application.getStatus() != 0) {
            throw new RuntimeException("申请已处理");
        }

        application.setApproverId(approverId);
        application.setApproveTime(new Date());
        application.setRemark(remark);

        if (approved) {
            application.setStatus(1); // 已通过
            // 延长会员卡有效期
            Optional<Member> memberOpt = memberRepository.findById(application.getMemberId());
            if (memberOpt.isPresent()) {
                Member member = memberOpt.get();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(member.getCardEndDate());
                calendar.add(Calendar.DAY_OF_MONTH, application.getRequestDays());
                member.setCardEndDate(calendar.getTime());
                memberRepository.save(member);

                // 发送通知
                sendNotification(member.getId(), "会员卡冻结申请已通过",
                        "您的会员卡已成功冻结" + application.getRequestDays() + "天，有效期已自动延长。", "FREEZE_APPROVED");
            }
        } else {
            application.setStatus(2); // 已拒绝
            // 发送通知
            Optional<Member> memberOpt = memberRepository.findById(application.getMemberId());
            memberOpt.ifPresent(member -> sendNotification(member.getId(), "会员卡冻结申请已拒绝",
                    "您的会员卡冻结申请已被拒绝，原因：" + remark, "FREEZE_REJECTED"));
        }

        return freezeApplicationRepository.save(application);
    }

    public List<MemberFreezeApplication> getPendingFreezeApplications() {
        return freezeApplicationRepository.findByStatus(0);
    }

    public List<MemberFreezeApplication> getMemberFreezeApplications(Long memberId) {
        return freezeApplicationRepository.findByMemberId(memberId);
    }

    public List<Member> getMembersWithCardExpiringWithinDays(int days) {
        return memberRepository.findMembersWithCardExpiringWithinDays(days);
    }

    @Transactional
    public void sendExpiryReminders() {
        List<Member> expiringMembers = getMembersWithCardExpiringWithinDays(EXPIRY_REMINDER_DAYS);
        for (Member member : expiringMembers) {
            long daysLeft = calculateDaysUntilExpiry(member.getCardEndDate());
            String content = "您的会员卡将于" + daysLeft + "天后到期，请及时续费，避免影响使用。";
            sendNotification(member.getId(), "会员卡即将到期", content, "CARD_EXPIRE");
            logger.info("发送到期提醒给会员: {}", member.getName());
        }
    }

    private long calculateDaysUntilExpiry(Date endDate) {
        long diff = endDate.getTime() - new Date().getTime();
        return diff / (1000 * 60 * 60 * 24) + 1;
    }

    private void sendNotification(Long memberId, String title, String content, String type) {
        Notification notification = new Notification();
        notification.setMemberId(memberId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setIsRead(0);
        notificationRepository.save(notification);
    }

    public List<Notification> getMemberNotifications(Long memberId) {
        return notificationRepository.findByMemberId(memberId);
    }

    @Transactional
    public void markNotificationAsRead(Long notificationId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        notificationOpt.ifPresent(notification -> {
            notification.setIsRead(1);
            notification.setReadTime(new Date());
            notificationRepository.save(notification);
        });
    }

    public MembershipLevel getMembershipLevel(String levelCode) {
        return membershipLevelRepository.findByLevelCode(levelCode).orElse(null);
    }

    public List<MembershipLevel> getAllMembershipLevels() {
        return membershipLevelRepository.findAll();
    }

    public List<Member> getMembersByBranch(Long branchId) {
        return memberRepository.findByBranchId(branchId);
    }

    @Transactional
    public boolean useFreeClass(Long memberId) {
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            if (member.getRemainingFreeClasses() != null && member.getRemainingFreeClasses() > 0) {
                member.setRemainingFreeClasses(member.getRemainingFreeClasses() - 1);
                memberRepository.save(member);
                return true;
            }
        }
        return false;
    }

    @Transactional
    public void resetMonthlyFreeClasses() {
        List<Member> allMembers = memberRepository.findAll();
        for (Member member : allMembers) {
            MembershipLevel level = membershipLevelRepository.findByLevelCode(member.getLevelCode()).orElse(null);
            if (level != null) {
                member.setRemainingFreeClasses(level.getFreeClassesPerMonth());
            }
        }
        memberRepository.saveAll(allMembers);
    }

    public List<Member> getActiveMembers() {
        return memberRepository.findByStatus(1);
    }

    public List<Member> getExpiredMembers() {
        return memberRepository.findByStatus(0);
    }
}
