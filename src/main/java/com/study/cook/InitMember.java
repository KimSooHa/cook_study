package com.study.cook;

import com.study.cook.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Profile("local")
@Component
public class InitMember {

    @PersistenceContext
    private EntityManager em;

    @PostConstruct
    @Transactional
    public void init() {
        for (int i = 0; i < 20; i++) {
            em.persist(new Member("member"+i, "member1234", "member1234*", "member" +i +"@email.com", "010-1234-1234"));
        }
    }

}
