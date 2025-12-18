package com.ensam.library.repository;

import com.ensam.library.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MemberRepository memberRepository;

    private Member member;

    @BeforeEach
    void setUp() {
        member = new Member();
        member.setName("John Doe");
        member.setAddress("123 Main St");
        member.setEmail("john.doe@test.com");
        member.setPhoneNumber("+1234567890");

        entityManager.persist(member);
        entityManager.flush();
    }

    @Test
    void testFindByEmail() {
        Optional<Member> found = memberRepository.findByEmail("john.doe@test.com");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("John Doe");
        assertThat(found.get().getEmail()).isEqualTo("john.doe@test.com");
    }

    @Test
    void testFindByEmailNotFound() {
        Optional<Member> found = memberRepository.findByEmail("notfound@test.com");

        assertThat(found).isNotPresent();
    }

    @Test
    void testFindByPhoneNumber() {
        Optional<Member> found = memberRepository.findByPhoneNumber("+1234567890");

        assertThat(found).isPresent();
        assertThat(found.get().getPhoneNumber()).isEqualTo("+1234567890");
    }
}