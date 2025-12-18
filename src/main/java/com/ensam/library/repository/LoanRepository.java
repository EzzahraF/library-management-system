package com.ensam.library.repository;

import com.ensam.library.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByMemberId(Long memberId);
    List<Loan> findByBookId(Long bookId);
    List<Loan> findByReturnDateIsNull();
    List<Loan> findByReturnDateIsNullAndLoanDateBefore(LocalDate date);
    Optional<Loan> findByBookIdAndReturnDateIsNull(Long bookId);
    List<Loan> findByLoanDateBetween(LocalDate startDate, LocalDate endDate);
}