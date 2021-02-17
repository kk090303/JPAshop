package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class) //Junit 실행 할 때 spring과 함께 실행할지에 대한 여부
@SpringBootTest  //스프링부트를 띄운 상태로 테스트할 것인지 여부
@Transactional   //매 태스트 별로 데이터 조작을 rollback
public class MemberServiceTest {
    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepositoryOld;

    @Test
    public void 회원가입() throws Exception {
        //Given
        Member member = new Member();
        member.setName("kim");
         //When
        Long saveId = memberService.join(member);
         //Then
        assertEquals(member, memberRepositoryOld.findOne(saveId));
    }

    @Test(expected = IllegalStateException.class) //예외 발생시 IllegalStateException이어야 한다.
    public void 중복_회원_예외() throws Exception {
        //Given
        Member member1 = new Member();
        member1.setName("kim");
        Member member2 = new Member();
        member2.setName("kim");
        //When
        memberService.join(member1);
        memberService.join(member2); //예외가 발생해야 한다.
        //Then
        fail("예외가 발생해야 한다."); //fail에 도달할 경우 로그 출력
    }
}