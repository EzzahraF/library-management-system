package com.ensam.library.service;

import com.ensam.library.dto.LoanDTO;
import com.ensam.library.model.Book;
import com.ensam.library.model.Loan;
import com.ensam.library.model.Member;
import com.ensam.library.repository.BookRepository;
import com.ensam.library.repository.LoanRepository;
import com.ensam.library.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Transactional
    public Loan createLoan(LoanDTO loanDTO) {
        log.info("Création d'un nouveau prêt pour le livre ID: {} et membre ID: {}",
                loanDTO.getBookId(), loanDTO.getMemberId());

        // Vérifier si le livre existe et est disponible
        Book book = bookRepository.findById(loanDTO.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("Livre non trouvé"));

        if (!book.getAvailable()) {
            throw new IllegalStateException("Le livre n'est pas disponible");
        }

        // Vérifier si le membre existe
        Member member = memberRepository.findById(loanDTO.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Membre non trouvé"));

        // Vérifier si le membre n'a pas déjà ce livre en prêt
        boolean alreadyLoaned = loanRepository.findByBookIdAndReturnDateIsNull(loanDTO.getBookId())
                .isPresent();

        if (alreadyLoaned) {
            throw new IllegalStateException("Ce livre est déjà en prêt");
        }

        // Créer le prêt
        Loan loan = new Loan();
        loan.setBook(book);
        loan.setMember(member);
        loan.setLoanDate(loanDTO.getLoanDate() != null ? loanDTO.getLoanDate() : LocalDate.now());
        loan.setReturnDate(null); // Pas encore retourné

        // Marquer le livre comme non disponible
        book.setAvailable(false);
        bookRepository.save(book);

        return loanRepository.save(loan);
    }

    @Transactional
    public Optional<Loan> returnBook(Long loanId) {
        log.info("Retour du livre pour le prêt ID: {}", loanId);

        return loanRepository.findById(loanId).map(loan -> {
            if (loan.getReturnDate() != null) {
                throw new IllegalStateException("Ce livre a déjà été retourné");
            }

            loan.setReturnDate(LocalDate.now());

            // Marquer le livre comme disponible
            Book book = loan.getBook();
            book.setAvailable(true);
            bookRepository.save(book);

            return loanRepository.save(loan);
        });
    }

    public List<Loan> getActiveLoans() {
        log.info("Récupération des prêts actifs");
        return loanRepository.findByReturnDateIsNull();
    }

    public List<Loan> getMemberLoans(Long memberId) {
        log.info("Récupération des prêts du membre ID: {}", memberId);
        return loanRepository.findByMemberId(memberId);
    }

    public List<Loan> getBookLoans(Long bookId) {
        log.info("Récupération de l'historique des prêts du livre ID: {}", bookId);
        return loanRepository.findByBookId(bookId);
    }

    public Optional<Loan> getLoanById(Long id) {
        log.info("Récupération du prêt avec ID: {}", id);
        return loanRepository.findById(id);
    }

    public List<Loan> getOverdueLoans() {
        log.info("Récupération des prêts en retard");
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        return loanRepository.findByReturnDateIsNullAndLoanDateBefore(thirtyDaysAgo);
    }
}