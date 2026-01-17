package dangod.springboot.service;

import dangod.springboot.entity.BodyTest;
import dangod.springboot.entity.Member;
import dangod.springboot.entity.MemberCardLevel;
import dangod.springboot.repository.BodyTestRepository;
import dangod.springboot.repository.MemberCardLevelRepository;
import dangod.springboot.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class BodyTestServiceTest {

    @Autowired
    private BodyTestService bodyTestService;

    @Autowired
    private BodyTestRepository bodyTestRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberCardLevelRepository memberCardLevelRepository;

    @Test
    public void testCreateBodyTest() {
        MemberCardLevel cardLevel = new MemberCardLevel();
        cardLevel.setName("测试卡");
        cardLevel.setLevel(1);
        cardLevel = memberCardLevelRepository.save(cardLevel);

        Member member = new Member();
        member.setName("体测用户");
        member.setPhone("13800138040");
        member.setPassword("123456");
        member.setStatus(Member.Status.ACTIVE);
        member.setCardLevel(cardLevel);
        member.setExpireDate(LocalDateTime.now().plusDays(365));
        member = memberRepository.save(member);

        BodyTest bodyTest = new BodyTest();
        bodyTest.setMember(member);
        bodyTest.setHeight(175.0);
        bodyTest.setWeight(70.0);
        bodyTest.setBmi(22.9);
        bodyTest.setBodyFatRate(15.0);
        bodyTest.setVisceralFat(5);
        bodyTest.setMuscleMass(55.0);
        bodyTest.setBoneMass(3.5);
        bodyTest.setBasalMetabolism(1500.0);
        bodyTest.setSystolicBloodPressure(120.0);
        bodyTest.setDiastolicBloodPressure(80.0);
        bodyTest.setHeartRate(72.0);
        bodyTest.setTestDate(LocalDateTime.now());

        BodyTest created = bodyTestService.createBodyTest(bodyTest);

        assertNotNull(created.getId());
        assertEquals(70.0, created.getWeight(), 0.01);
        assertEquals(22.9, created.getBmi(), 0.01);
    }

    @Test
    public void testGetBodyTestsByMember() {
        Member member = new Member();
        member.setName("多体测用户");
        member.setPhone("13800138041");
        member.setPassword("123456");
        member.setStatus(Member.Status.ACTIVE);
        member.setExpireDate(LocalDateTime.now().plusDays(365));
        member = memberRepository.save(member);

        for (int i = 0; i < 3; i++) {
            BodyTest bodyTest = new BodyTest();
            bodyTest.setMember(member);
            bodyTest.setHeight(175.0);
            bodyTest.setWeight(70.0 + i);
            bodyTest.setBmi(22.9 + i * 0.5);
            bodyTest.setTestDate(LocalDateTime.now().minusMonths(i));
            bodyTestRepository.save(bodyTest);
        }

        List<BodyTest> bodyTests = bodyTestService.getBodyTestsByMember(member.getId());
        assertEquals(3, bodyTests.size());
    }

    @Test
    public void testCheckAnomaly() {
        Member member = new Member();
        member.setName("异常体测用户");
        member.setPhone("13800138042");
        member.setEmail("anomaly@example.com");
        member.setPassword("123456");
        member.setStatus(Member.Status.ACTIVE);
        member.setExpireDate(LocalDateTime.now().plusDays(365));
        member = memberRepository.save(member);

        BodyTest bodyTest = new BodyTest();
        bodyTest.setMember(member);
        bodyTest.setHeight(175.0);
        bodyTest.setWeight(100.0);
        bodyTest.setBmi(32.7);
        bodyTest.setBodyFatRate(30.0);
        bodyTest.setVisceralFat(15);
        bodyTest.setSystolicBloodPressure(145.0);
        bodyTest.setDiastolicBloodPressure(95.0);
        bodyTest.setHeartRate(110.0);
        bodyTest.setTestDate(LocalDateTime.now());

        BodyTest created = bodyTestService.createBodyTest(bodyTest);
        assertNotNull(created);
    }
}
