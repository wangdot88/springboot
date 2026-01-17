package dangod.springboot.service;

import dangod.springboot.common.BusinessException;
import dangod.springboot.entity.Member;
import dangod.springboot.entity.MemberCardLevel;
import dangod.springboot.entity.Store;
import dangod.springboot.repository.MemberCardLevelRepository;
import dangod.springboot.repository.MemberRepository;
import dangod.springboot.repository.StoreRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberCardLevelRepository memberCardLevelRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Test
    public void testRegisterMember() {
        Store store = new Store();
        store.setName("测试门店");
        store.setAddress("测试地址");
        store.setPhone("1234567890");
        store = storeRepository.save(store);

        MemberCardLevel cardLevel = new MemberCardLevel();
        cardLevel.setName("测试银卡");
        cardLevel.setLevel(2);
        cardLevel.setPrice(2000.0);
        cardLevel.setDuration(180);
        cardLevel.setCourseDiscount(0.9);
        cardLevel.setEquipmentAccess(true);
        cardLevel.setPrivateCoach(false);
        cardLevel.setBodyTestCount(4);
        cardLevel = memberCardLevelRepository.save(cardLevel);

        Member member = new Member();
        member.setName("测试用户");
        member.setPhone("13800138001");
        member.setPassword("123456");
        member.setEmail("test@example.com");
        member.setGender(1);
        member.setStore(store);
        member.setCardLevel(cardLevel);
        member.setExpireDate(LocalDateTime.now().plusDays(180));

        Member registered = memberService.register(member);

        assertNotNull(registered.getId());
        assertEquals("测试用户", registered.getName());
        assertEquals("13800138001", registered.getPhone());
        assertEquals(Member.Status.ACTIVE, registered.getStatus());
    }

    @Test(expected = BusinessException.class)
    public void testRegisterDuplicatePhone() {
        Member member = new Member();
        member.setName("重复用户");
        member.setPhone("13800138002");
        member.setPassword("123456");
        member.setStatus(Member.Status.ACTIVE);
        memberRepository.save(member);

        Member duplicate = new Member();
        duplicate.setName("重复用户2");
        duplicate.setPhone("13800138002");
        duplicate.setPassword("123456");
        
        memberService.register(duplicate);
    }

    @Test
    public void testLoginSuccess() {
        Member member = new Member();
        member.setName("登录用户");
        member.setPhone("13800138003");
        member.setPassword("123456");
        member.setStatus(Member.Status.ACTIVE);
        memberRepository.save(member);

        Member loggedIn = memberService.login("13800138003", "123456");
        assertNotNull(loggedIn);
        assertEquals("登录用户", loggedIn.getName());
    }

    @Test(expected = BusinessException.class)
    public void testLoginInvalidPassword() {
        Member member = new Member();
        member.setName("密码错误用户");
        member.setPhone("13800138004");
        member.setPassword("123456");
        member.setStatus(Member.Status.ACTIVE);
        memberRepository.save(member);

        memberService.login("13800138004", "wrongpassword");
    }

    @Test
    public void testRenewMember() {
        Member member = new Member();
        member.setName("续费用户");
        member.setPhone("13800138005");
        member.setPassword("123456");
        member.setStatus(Member.Status.ACTIVE);
        member.setExpireDate(LocalDateTime.now().minusDays(1));
        memberRepository.save(member);

        LocalDateTime originalExpire = member.getExpireDate();
        memberService.renew(member.getId(), 365);
        
        Member renewed = memberRepository.findById(member.getId()).orElse(null);
        assertNotNull(renewed);
        assertTrue(renewed.getExpireDate().isAfter(originalExpire));
    }
}
