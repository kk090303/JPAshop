package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    //findBy + Name => select m from Member m where m.name = ?
    //spring data jpa 에서 규칙에 따라 알아서 JPQL을 만들어 준다.
    List<Member> findByName(String name);
}
