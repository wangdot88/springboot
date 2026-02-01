package dangod.springboot.controller;

import dangod.springboot.core.common.PageResult;
import dangod.springboot.core.common.Result;
import dangod.springboot.dto.MemberDTO;
import dangod.springboot.entity.Member;
import dangod.springboot.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @PostMapping
    public Result<Member> create(@Valid @RequestBody MemberDTO dto) {
        return Result.success(memberService.createMember(dto));
    }

    @PutMapping("/{id}")
    public Result<Member> update(@PathVariable Long id, @Valid @RequestBody MemberDTO dto) {
        return Result.success(memberService.updateMember(id, dto));
    }

    @GetMapping("/{id}")
    public Result<Member> get(@PathVariable Long id) {
        return Result.success(memberService.getMember(id));
    }

    @GetMapping("/no/{memberNo}")
    public Result<Member> getByNo(@PathVariable String memberNo) {
        return Result.success(memberService.getMemberByNo(memberNo));
    }

    @GetMapping("/phone/{phone}")
    public Result<Member> getByPhone(@PathVariable String phone) {
        return Result.success(memberService.getMemberByPhone(phone));
    }

    @GetMapping("/list")
    public Result<PageResult<Member>> list(@PageableDefault(size = 20) Pageable pageable) {
        return Result.success(PageResult.of(memberService.listMembers(pageable)));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        memberService.deleteMember(id);
        return Result.success();
    }
}
