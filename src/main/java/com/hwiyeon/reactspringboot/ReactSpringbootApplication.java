package com.hwiyeon.reactspringboot;

import com.hwiyeon.reactspringboot.vo.Member;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class ReactSpringbootApplication {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello"); //persistence.xml - persistence-unit

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();     // JPA에서는 트랜잭션 개념이 중요
        tx.begin();

        try{    // try ~ catch 문 없이 쓰면 문제가 생겼을 때 em.close, emf.close 등 뒤에 코드가 실행되지 않기 때문에 아래와 같은 형식의 코드가 정석
            // 1. 회원 저장
            Member member = new Member();
            member.setMemberId(1L);
            member.setName("one"); // 여기까진 비영속 상태

            em.persist(member); // 여기부터 영속 상태

            // 2. 회원 수정
            Member findMember = em.find(Member.class, 1L);
            findMember.setName("findMember1");

            // em.persist(findMember); -> 할 필요가 없다!!! 자바 컬렉션과 똑같은 구조!

            // 3. 회원 삭제
            em.remove(findMember);

            // 4. JPQL
            List<Member> result = em.createQuery("select m from Member as m", Member.class)
                    .setFirstResult(1)  // 페이징이 그냥 알아서 된당.. 오라클로 디비만 바꾸면 rownum ~ 어쩌구가 된다.
                    .setMaxResults(10)
                    .getResultList();
            // 대상이 테이블이 아닌 객체

            for(Member m : result){
                System.out.println(m.getMemberId() + " : " + m.getName());
            }

            // 5. 1차 캐시
/*            Member member4 = new Member(4L, "four");
            Member member5 = new Member(5L, "five");
            Member member6 = new Member(6L, "six");
            Member member7 = new Member(7L, "seven");
            Member member8 = new Member(8L, "eight");
            Member member9 = new Member(9L, "nine");
            Member member10 = new Member(10L, "ten");
            Member member11 = new Member(11L, "eleven");
            Member member12 = new Member(12L, "twelve");
            Member member13 = new Member(13L, "thirteen");
            Member member14 = new Member(14L, "fourteen");
            Member member15 = new Member(15L, "fifteen");

            em.persist(member4);
            em.persist(member5);    // 이 때 까지는 1차 캐시에 저장될 뿐 실제 DB 에 저장되지 않는다.
            em.persist(member6);
            em.persist(member7 );
            em.persist(member8 );
            em.persist(member9 );
            em.persist(member10);
            em.persist(member11);
            em.persist(member12);
            em.persist(member13);
            em.persist(member14);
            em.persist(member15);

            System.out.println("======================"); */

            // 6. 플러시 : 영속성 컨텍스트의 변경내용을 데이터베이스에 반영
            Member flushMember = new Member(200L, "memeber200");
            em.persist(flushMember);

            em.flush(); // commit을 주석처리 했더니 쿼리는 찍히나 db에 저장은 안됨
            // 트랜잭션이라는 작업단위가 중요 -> 커밋 직전에만 동기화 하면 됨

            // 7. 준영속
            em.detach(findMember);
 //           em.clear(); // entityManager 에 있는 모든 영속성 컨텍스트를 다 지움

            tx.commit();            // 실제 DB에 저장되는 시기
        }catch (Exception e){
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();

        SpringApplication.run(ReactSpringbootApplication.class, args);
    }

}
