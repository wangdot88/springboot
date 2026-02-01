package dangod.springboot.controller;

import dangod.springboot.core.common.Result;
import dangod.springboot.dto.MemberCardDTO;
import dangod.springboot.entity.CardFreezeApproval;
import dangod.springboot.entity.MemberCard;
import dangod.springboot.service.MemberCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/card")
public class MemberCardController {

    @Autowired
    private MemberCardService memberCardService;

    @PostMapping
    public Result<MemberCard> create(@Valid @RequestBody MemberCardDTO dto) {
        return Result.success(memberCardService.createCard(dto));
    }

    @GetMapping("/{id}")
    public Result<MemberCard> get(@PathVariable Long id) {
        return Result.success(memberCardService.getCard(id));
    }

    @GetMapping("/member/{memberId}")
    public Result<List<MemberCard>> getByMember(@PathVariable Long memberId) {
        return Result.success(memberCardService.getCardsByMember(memberId));
    }

    @PostMapping("/{cardId}/renew")
    public Result<Void> renew(@PathVariable Long cardId, @RequestParam Long levelId) {
        memberCardService.renewCard(cardId, levelId);
        return Result.success();
    }

    @PostMapping("/{cardId}/freeze/apply")
    public Result<CardFreezeApproval> applyFreeze(@PathVariable Long cardId,
                                                   @RequestParam String reason,
                                                   @RequestParam Integer freezeDays,
                                                   @RequestParam String applicant) {
        return Result.success(memberCardService.applyFreeze(cardId, reason, freezeDays, applicant));
    }

    @PostMapping("/freeze/approve/{approvalId}")
    public Result<Void> approveFreeze(@PathVariable Long approvalId,
                                      @RequestParam Boolean approved,
                                      @RequestParam String approver,
                                      @RequestParam(required = false) String rejectReason) {
        memberCardService.approveFreeze(approvalId, approved, approver, rejectReason);
        return Result.success();
    }

    @GetMapping("/expiring")
    public Result<List<MemberCard>> getExpiringCards() {
        return Result.success(memberCardService.getExpiringCards());
    }

    @GetMapping("/freeze/pending")
    public Result<List<CardFreezeApproval>> getPendingApprovals() {
        return Result.success(memberCardService.getPendingApprovals());
    }
}
