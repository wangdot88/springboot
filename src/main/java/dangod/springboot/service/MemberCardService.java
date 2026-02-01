package dangod.springboot.service;

import dangod.springboot.core.common.ResultCode;
import dangod.springboot.core.exception.BusinessException;
import dangod.springboot.core.util.IdUtil;
import dangod.springboot.dto.MemberCardDTO;
import dangod.springboot.entity.CardFreezeApproval;
import dangod.springboot.entity.Member;
import dangod.springboot.entity.MemberCard;
import dangod.springboot.entity.MembershipLevel;
import dangod.springboot.repository.CardFreezeApprovalRepository;
import dangod.springboot.repository.MemberCardRepository;
import dangod.springboot.repository.MemberRepository;
import dangod.springboot.repository.MembershipLevelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class MemberCardService {

    @Autowired
    private MemberCardRepository memberCardRepository;
    
    @Autowired
    private MembershipLevelRepository membershipLevelRepository;
    
    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private CardFreezeApprovalRepository cardFreezeApprovalRepository;

    @Transactional
    public MemberCard createCard(MemberCardDTO dto) {
        Member member = memberRepository.findOne(dto.getMemberId());
        if (member == null) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }
        
        MembershipLevel level = membershipLevelRepository.findOne(dto.getLevelId());
        if (level == null) {
            throw new BusinessException("卡等级不存在");
        }
        
        MemberCard card = new MemberCard();
        card.setCardNo(IdUtil.generateCardNo());
        card.setMemberId(dto.getMemberId());
        card.setLevelId(dto.getLevelId());
        card.setGymId(dto.getGymId());
        card.setActiveTime(LocalDateTime.now());
        card.setExpireTime(LocalDateTime.now().plusDays(level.getDurationDays()));
        card.setStatus(1);
        card.setRemainCourses(level.getMaxCoursesPerWeek() * 4);
        card.setTotalCourses(level.getMaxCoursesPerWeek() * 4);
        card.setCreateTime(LocalDateTime.now());
        card.setUpdateTime(LocalDateTime.now());
        
        return memberCardRepository.save(card);
    }

    public MemberCard getCard(Long id) {
        MemberCard card = memberCardRepository.findOne(id);
        if (card == null) {
            throw new BusinessException(ResultCode.CARD_NOT_FOUND);
        }
        return card;
    }

    public List<MemberCard> getCardsByMember(Long memberId) {
        return memberCardRepository.findByMemberId(memberId);
    }

    @Transactional
    public void renewCard(Long cardId, Long levelId) {
        MemberCard card = getCard(cardId);
        MembershipLevel level = membershipLevelRepository.findOne(levelId);
        if (level == null) {
            throw new BusinessException("卡等级不存在");
        }
        
        LocalDateTime newExpireTime = card.getExpireTime().isAfter(LocalDateTime.now()) 
            ? card.getExpireTime().plusDays(level.getDurationDays())
            : LocalDateTime.now().plusDays(level.getDurationDays());
        
        card.setExpireTime(newExpireTime);
        card.setLevelId(levelId);
        card.setRemainCourses(card.getRemainCourses() + level.getMaxCoursesPerWeek() * 4);
        card.setTotalCourses(card.getTotalCourses() + level.getMaxCoursesPerWeek() * 4);
        card.setStatus(1);
        card.setUpdateTime(LocalDateTime.now());
        
        memberCardRepository.save(card);
    }

    @Transactional
    public CardFreezeApproval applyFreeze(Long cardId, String reason, Integer freezeDays, String applicant) {
        MemberCard card = getCard(cardId);
        if (card.getStatus() == 2) {
            throw new BusinessException("会员卡已冻结");
        }
        
        CardFreezeApproval approval = new CardFreezeApproval();
        approval.setCardId(cardId);
        approval.setMemberId(card.getMemberId());
        approval.setApplyReason(reason);
        approval.setApplyTime(LocalDateTime.now());
        approval.setApplicant(applicant);
        approval.setApproveResult(0);
        approval.setFreezeDays(freezeDays);
        approval.setCreateTime(LocalDateTime.now());
        approval.setUpdateTime(LocalDateTime.now());
        
        return cardFreezeApprovalRepository.save(approval);
    }

    @Transactional
    public void approveFreeze(Long approvalId, Boolean approved, String approver, String rejectReason) {
        CardFreezeApproval approval = cardFreezeApprovalRepository.findOne(approvalId);
        if (approval == null) {
            throw new BusinessException(ResultCode.APPROVAL_NOT_FOUND);
        }
        
        if (approval.getApproveResult() != 0) {
            throw new BusinessException("审批已处理");
        }
        
        approval.setApprover(approver);
        approval.setApproveTime(LocalDateTime.now());
        
        if (approved) {
            approval.setApproveResult(1);
            MemberCard card = getCard(approval.getCardId());
            card.setStatus(2);
            card.setFreezeReason(approval.getApplyReason());
            card.setFreezeTime(LocalDateTime.now());
            card.setExpireTime(card.getExpireTime().plusDays(approval.getFreezeDays()));
            card.setUpdateTime(LocalDateTime.now());
            memberCardRepository.save(card);
        } else {
            approval.setApproveResult(2);
            approval.setRejectReason(rejectReason);
        }
        
        cardFreezeApprovalRepository.save(approval);
    }

    public List<MemberCard> getExpiringCards() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysLater = now.plusDays(7);
        return memberCardRepository.findExpiringCards(now, sevenDaysLater);
    }

    public List<CardFreezeApproval> getPendingApprovals() {
        return cardFreezeApprovalRepository.findByApproveResult(0);
    }
}
