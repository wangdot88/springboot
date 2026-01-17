package dangod.springboot.controller;

import dangod.springboot.common.BusinessException;
import dangod.springboot.common.Result;
import dangod.springboot.entity.Member;
import dangod.springboot.entity.MemberCardLevel;
import dangod.springboot.entity.MemberFreezeRecord;
import dangod.springboot.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/member")
public class MemberController {
    @Autowired
    private MemberService memberService;
    
    @PostMapping("/create")
    public Result<Member> createMember(@RequestBody Member member) {
        try {
            Member created = memberService.createMember(member);
            return Result.success("创建成功", created);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public Result<Member> getMember(@PathVariable Long id) {
        Member member = memberService.getMemberById(id);
        if (member == null) {
            return Result.error("会员不存在");
        }
        return Result.success(member);
    }
    
    @GetMapping("/phone/{phone}")
    public Result<Member> getMemberByPhone(@PathVariable String phone) {
        Member member = memberService.getMemberByPhone(phone);
        if (member == null) {
            return Result.error("会员不存在");
        }
        return Result.success(member);
    }
    
    @GetMapping("/list")
    public Result<List<Member>> getAllMembers() {
        List<Member> members = memberService.getAllMembers();
        return Result.success(members);
    }
    
    @GetMapping("/store/{storeId}")
    public Result<List<Member>> getMembersByStore(@PathVariable Long storeId) {
        List<Member> members = memberService.getMembersByStore(storeId);
        return Result.success(members);
    }
    
    @PutMapping("/update")
    public Result<Member> updateMember(@RequestBody Member member) {
        try {
            Member updated = memberService.updateMember(member);
            return Result.success("更新成功", updated);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/renew")
    public Result<Member> renewMember(@RequestBody Map<String, Object> params) {
        try {
            Long memberId = Long.valueOf(params.get("memberId").toString());
            Long cardLevelId = Long.valueOf(params.get("cardLevelId").toString());
            String operator = params.get("operator").toString();
            
            Member member = memberService.renewMember(memberId, cardLevelId, operator);
            return Result.success("续费成功", member);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/freeze/apply")
    public Result<Void> applyFreeze(@RequestBody Map<String, Object> params) {
        try {
            Long memberId = Long.valueOf(params.get("memberId").toString());
            Integer freezeDays = Integer.valueOf(params.get("freezeDays").toString());
            String reason = params.get("reason").toString();
            String operator = params.get("operator").toString();
            
            memberService.applyFreezeMember(memberId, freezeDays, reason, operator);
            return Result.success("申请提交成功");
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/freeze/approve")
    public Result<Void> approveFreeze(@RequestBody Map<String, Object> params) {
        try {
            Long recordId = Long.valueOf(params.get("recordId").toString());
            Integer approveStatus = Integer.valueOf(params.get("approveStatus").toString());
            String opinion = params.get("opinion").toString();
            String operator = params.get("operator").toString();
            
            memberService.approveFreeze(recordId, approveStatus, opinion, operator);
            return Result.success("审批完成");
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/unfreeze")
    public Result<Void> unfreezeMember(@RequestBody Map<String, Object> params) {
        try {
            Long memberId = Long.valueOf(params.get("memberId").toString());
            String operator = params.get("operator").toString();
            
            memberService.unfreezeMember(memberId, operator);
            return Result.success("解冻成功");
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/freeze/pending")
    public Result<List<MemberFreezeRecord>> getPendingFreezeApprovals() {
        List<MemberFreezeRecord> records = memberService.getPendingFreezeApprovals();
        return Result.success(records);
    }
    
    @GetMapping("/freeze/history/{memberId}")
    public Result<List<MemberFreezeRecord>> getFreezeHistory(@PathVariable Long memberId) {
        List<MemberFreezeRecord> records = memberService.getMemberFreezeHistory(memberId);
        return Result.success(records);
    }
    
    @GetMapping("/card-levels")
    public Result<List<MemberCardLevel>> getAllCardLevels() {
        List<MemberCardLevel> levels = memberService.getAllCardLevels();
        return Result.success(levels);
    }
    
    @PostMapping("/card-level/create")
    public Result<MemberCardLevel> createCardLevel(@RequestBody MemberCardLevel level) {
        try {
            MemberCardLevel created = memberService.createCardLevel(level);
            return Result.success("创建成功", created);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/card-level/update")
    public Result<MemberCardLevel> updateCardLevel(@RequestBody MemberCardLevel level) {
        try {
            MemberCardLevel updated = memberService.updateCardLevel(level);
            return Result.success("更新成功", updated);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/check-expire-warnings")
    public Result<Void> checkExpireWarnings() {
        memberService.checkAndSendExpireWarnings();
        return Result.success("检查完成");
    }
}
