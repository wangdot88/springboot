package dangod.springboot.service;

import dangod.springboot.core.common.ResultCode;
import dangod.springboot.core.exception.BusinessException;
import dangod.springboot.core.util.IdUtil;
import dangod.springboot.dto.MemberDTO;
import dangod.springboot.entity.Member;
import dangod.springboot.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Transactional
    public Member createMember(MemberDTO dto) {
        if (memberRepository.existsByPhone(dto.getPhone())) {
            throw new BusinessException(ResultCode.MEMBER_EXISTS);
        }
        Member member = new Member();
        BeanUtils.copyProperties(dto, member);
        member.setMemberNo(IdUtil.generateMemberNo());
        member.setStatus(1);
        member.setCreateTime(LocalDateTime.now());
        member.setUpdateTime(LocalDateTime.now());
        return memberRepository.save(member);
    }

    @Transactional
    public Member updateMember(Long id, MemberDTO dto) {
        Member member = memberRepository.findOne(id);
        if (member == null) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }
        BeanUtils.copyProperties(dto, member, "id", "memberNo", "createTime");
        member.setUpdateTime(LocalDateTime.now());
        return memberRepository.save(member);
    }

    public Member getMember(Long id) {
        Member member = memberRepository.findOne(id);
        if (member == null) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }
        return member;
    }

    public Member getMemberByNo(String memberNo) {
        return memberRepository.findByMemberNo(memberNo)
            .orElseThrow(() -> new BusinessException(ResultCode.MEMBER_NOT_FOUND));
    }

    public Member getMemberByPhone(String phone) {
        return memberRepository.findByPhone(phone)
            .orElseThrow(() -> new BusinessException(ResultCode.MEMBER_NOT_FOUND));
    }

    public Page<Member> listMembers(Pageable pageable) {
        return memberRepository.findAll(pageable);
    }

    @Transactional
    public void deleteMember(Long id) {
        Member member = getMember(id);
        member.setStatus(0);
        member.setUpdateTime(LocalDateTime.now());
        memberRepository.save(member);
    }
}
