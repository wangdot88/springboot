package dangod.springboot.controller.member;

import dangod.springboot.model.Member;
import dangod.springboot.dto.MemberRegistrationDto;
import dangod.springboot.dto.MemberFreezeDto;
import dangod.springboot.enums.MemberCardType;
import dangod.springboot.enums.MemberStatus;
import dangod.springboot.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity<Member> registerMember(@Valid @RequestBody MemberRegistrationDto registrationDto) {
        Member member = memberService.registerMember(registrationDto);
        return ResponseEntity.ok(member);
    }

    @GetMapping("/{memberNumber}")
    public ResponseEntity<Member> getMemberByNumber(@PathVariable String memberNumber) {
        return memberService.findByMemberNumber(memberNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/phone/{phone}")
    public ResponseEntity<Member> getMemberByPhone(@PathVariable String phone) {
        return memberService.findByPhone(phone)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Member>> getAllMembers() {
        List<Member> members = memberService.findAllMembers();
        return ResponseEntity.ok(members);
    }

    @GetMapping("/card-type/{cardType}")
    public ResponseEntity<List<Member>> getMembersByCardType(@PathVariable MemberCardType cardType) {
        List<Member> members = memberService.findByCardType(cardType);
        return ResponseEntity.ok(members);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Member>> getMembersByStatus(@PathVariable MemberStatus status) {
        List<Member> members = memberService.findByStatus(status);
        return ResponseEntity.ok(members);
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<Member>> getMembersByStore(@PathVariable String storeId) {
        List<Member> members = memberService.findByStoreId(storeId);
        return ResponseEntity.ok(members);
    }

    @PutMapping("/{memberId}")
    public ResponseEntity<Member> updateMember(@PathVariable Long memberId, 
                                              @Valid @RequestBody MemberRegistrationDto updateDto) {
        try {
            Member member = memberService.updateMember(memberId, updateDto);
            return ResponseEntity.ok(member);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{memberId}/freeze")
    public ResponseEntity<Member> freezeMember(@PathVariable Long memberId, 
                                               @Valid @RequestBody MemberFreezeDto freezeDto) {
        try {
            Member member = memberService.freezeMember(memberId, freezeDto);
            return ResponseEntity.ok(member);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{memberId}/unfreeze")
    public ResponseEntity<Member> unfreezeMember(@PathVariable Long memberId) {
        try {
            Member member = memberService.unfreezeMember(memberId);
            return ResponseEntity.ok(member);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/expiring-soon")
    public ResponseEntity<List<Member>> getMembersExpiringSoon() {
        List<Member> members = memberService.findMembersExpiringSoon();
        return ResponseEntity.ok(members);
    }

    @PostMapping("/send-renewal-reminders")
    public ResponseEntity<Void> sendRenewalReminders() {
        memberService.sendRenewalReminders();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/expired")
    public ResponseEntity<List<Member>> getExpiredMembers() {
        List<Member> members = memberService.findExpiredMembers();
        return ResponseEntity.ok(members);
    }

    @GetMapping("/chain-store")
    public ResponseEntity<List<Member>> getChainStoreMembers() {
        List<Member> members = memberService.findChainStoreMembers();
        return ResponseEntity.ok(members);
    }

    @GetMapping("/frozen/{managerId}")
    public ResponseEntity<List<Member>> getFrozenMembersByManager(@PathVariable Long managerId) {
        List<Member> members = memberService.findFrozenMembersByManager(managerId);
        return ResponseEntity.ok(members);
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long memberId) {
        try {
            memberService.deleteMember(memberId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{memberId}/renew")
    public ResponseEntity<Member> renewMembership(@PathVariable Long memberId, 
                                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newExpiryDate) {
        try {
            Member member = memberService.renewMembership(memberId, newExpiryDate);
            return ResponseEntity.ok(member);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{memberId}/upgrade")
    public ResponseEntity<Member> upgradeCardType(@PathVariable Long memberId, 
                                                   @RequestParam MemberCardType newCardType) {
        try {
            Member member = memberService.upgradeCardType(memberId, newCardType);
            return ResponseEntity.ok(member);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}