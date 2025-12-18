package com.ensam.library.repository;

import com.ensam.library.model.Book;
import com.ensam.library.model.Loan;
import com.ensam.library.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class LoanRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LoanRepository loanRepository;

    private Book book;
    private Member member;
    private Loan loan;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setGenre("Fiction");
        book.setAvailable(false);

        member = new Member();
        member.setName("John Doe");
        member.setAddress("123 Main St");
        member.setEmail("john@test.com");
        member.setPhoneNumber("+1234567890");

        loan = new Loan();
        loan.setBook(book);
        loan.setMember(member);
        loan.setLoanDate(LocalDate.now());
        loan.setReturnDate(null);

        entityManager.persist(book);
        entityManager.persist(member);
        entityManager.persist(loan);
        entityManager.flush();
    }

    @Test
    void testSaveAndFindLoan() {
        Optional<Loan> found = loanRepository.findById(loan.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getBook().getTitle()).isEqualTo("Test Book");
        assertThat(found.get().getMember().getName()).isEqualTo("John Doe");
    }

    @Test
    void testFindByReturnDateIsNull() {
        List<Loan> activeLoans = loanRepository.findByReturnDateIsNull();

        assertThat(activeLoans).hasSize(1);
        assertThat(activeLoans.get(0).getReturnDate()).isNull();
    }

    @Test
    void testFindByMemberId() {
        List<Loan> memberLoans = loanRepository.findByMemberId(member.getId());

        assertThat(memberLoans).hasSize(1);
        assertThat(memberLoans.get(0).getMember().getName()).isEqualTo("John Doe");
    }

    @Test
    void testFindByBookId() {
        List<Loan> bookLoans = loanRepository.findByBookId(book.getId());

        assertThat(bookLoans).hasSize(1);
        assertThat(bookLoans.get(0).getBook().getTitle()).isEqualTo("Test Book");
    }

    @Test
    void testFindByBookIdAndReturnDateIsNull() {
        Optional<Loan> activeLoan = loanRepository.findByBookIdAndReturnDateIsNull(book.getId());

        assertThat(activeLoan).isPresent();
        assertThat(activeLoan.get().getBook().getTitle()).isEqualTo("Test Book");
    }
}
