package dangod.springboot.controller;

import dangod.springboot.common.Result;
import dangod.springboot.entity.Member;
import dangod.springboot.entity.ApprovalRecord;
import dangod.springboot.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @PostMapping
    public Result<Member> createMember(@RequestBody Member member) {
        return Result.success(memberService.createMember(member));
    }

    @PutMapping("/{id}")
    public Result<Member> updateMember(@PathVariable Long id, @RequestBody Member member) {
        return Result.success(memberService.updateMember(id, member));
    }

    @GetMapping("/{id}")
    public Result<Member> getMember(@PathVariable Long id) {
        return Result.success(memberService.getMemberById(id));
    }

    @GetMapping("/phone/{phone}")
    public Result<Member> getMemberByPhone(@PathVariable String phone) {
        return Result.success(memberService.getMemberByPhone(phone));
    }

    @GetMapping("/store/{storeId}")
    public Result<List<Member>> getMembersByStore(@PathVariable Long storeId) {
        return Result.success(memberService.getMembersByStore(storeId));
    }

    @GetMapping
    public Result<List<Member>> getAllMembers() {
        return Result.success(memberService.getAllMembers());
    }

    @PutMapping("/{id}/upgrade")
    public Result<Member> upgradeCard(@PathVariable Long id, @RequestParam Member.CardLevel level) {
        return Result.success(memberService.upgradeCard(id, level));
    }

    @PutMapping("/{id}/renew")
    public Result<Member> renewCard(@PathVariable Long id, @RequestParam int months) {
        return Result.success(memberService.renewCard(id, months));
    }

    @PostMapping("/{id}/freeze")
    public Result<ApprovalRecord> requestFreezeCard(@PathVariable Long id, @RequestParam String reason) {
        return Result.success(memberService.requestFreezeCard(id, reason));
    }

    @PostMapping("/approve/{approvalId}")
    public Result<Void> approveFreezeRequest(
            @PathVariable Long approvalId,
            @RequestParam Long approverId,
            @RequestParam String approverName,
            @RequestParam(required = false) String comment) {
        memberService.approveFreezeRequest(approvalId, approverId, approverName, comment);
        return Result.success();
    }

    @PostMapping("/reject/{approvalId}")
    public Result<Void> rejectFreezeRequest(
            @PathVariable Long approvalId,
            @RequestParam Long approverId,
            @RequestParam String approverName,
            @RequestParam(required = false) String comment) {
        memberService.rejectFreezeRequest(approvalId, approverId, approverName, comment);
        return Result.success();
    }

    @PutMapping("/{id}/unfreeze")
    public Result<Member> unfreezeCard(@PathVariable Long id) {
        return Result.success(memberService.unfreezeCard(id));
    }

    @GetMapping("/expiring")
    public Result<List<Member>> getExpiringMembers(@RequestParam(defaultValue = "7") int daysBefore) {
        return Result.success(memberService.getExpiringMembers(daysBefore));
    }

    @GetMapping("/expiring/cached")
    public Result<List<Member>> getCachedExpiringMembers() {
        return Result.success(memberService.getCachedExpiringMembers());
    }

    @GetMapping("/level/{level}")
    public Result<List<Member>> getMembersByLevel(@PathVariable Member.CardLevel level) {
        return Result.success(memberService.getMembersByLevel(level));
    }

    @GetMapping("/status/{status}")
    public Result<List<Member>> getMembersByStatus(@PathVariable Member.MemberStatus status) {
        return Result.success(memberService.getMembersByStatus(status));
    }

    @PostMapping("/{id}/points")
    public Result<Void> addPoints(@PathVariable Long id, @RequestParam Integer points) {
        memberService.addPoints(id, points);
        return Result.success();
    }

    @GetMapping("/pending-freeze")
    public Result<List<Member>> getMembersPendingFreeze() {
        return Result.success(memberService.getMembersPendingFreeze());
    }
}
