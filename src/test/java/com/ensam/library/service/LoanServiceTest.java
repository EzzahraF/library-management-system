package com.ensam.library.service;

import com.ensam.library.dto.LoanDTO;
import com.ensam.library.model.Book;
import com.ensam.library.model.Loan;
import com.ensam.library.model.Member;
import com.ensam.library.repository.BookRepository;
import com.ensam.library.repository.LoanRepository;
import com.ensam.library.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Loan Service Tests")
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private LoanService loanService;

    private Book availableBook;
    private Book unavailableBook;
    private Member testMember;
    private Loan testLoan;
    private LoanDTO testLoanDTO;

    @BeforeEach
    void setUp() {
        availableBook = new Book(1L, "Available Book", "Author 1", "Fiction", true);
        unavailableBook = new Book(2L, "Unavailable Book", "Author 2", "Fiction", false);
        testMember = new Member(1L, "John Doe", "123 Main St", "john@test.com", "+1234567890");
        testLoan = new Loan(1L, LocalDate.now(), null, availableBook, testMember);
        testLoanDTO = new LoanDTO(null, 1L, 1L, LocalDate.now(), null);
    }

    @Test
    @DisplayName("Should create loan successfully")
    void testCreateLoan_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(availableBook));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
        when(loanRepository.findByBookIdAndReturnDateIsNull(1L)).thenReturn(Optional.empty());
        when(loanRepository.save(any(Loan.class))).thenReturn(testLoan);
        when(bookRepository.save(any(Book.class))).thenReturn(availableBook);

        Loan result = loanService.createLoan(testLoanDTO);

        assertNotNull(result);
        assertNotNull(result.getLoanDate());
        assertNull(result.getReturnDate());
        assertEquals(availableBook, result.getBook());
        assertEquals(testMember, result.getMember());
        verify(bookRepository).save(argThat(book -> !book.getAvailable()));
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    @DisplayName("Should throw exception when book not found")
    void testCreateLoan_BookNotFound() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        LoanDTO invalidDTO = new LoanDTO(null, 999L, 1L, LocalDate.now(), null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> loanService.createLoan(invalidDTO)
        );

        assertEquals("Livre non trouvé", exception.getMessage());
        verify(bookRepository, times(1)).findById(999L);
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    @DisplayName("Should throw exception when member not found")
    void testCreateLoan_MemberNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(availableBook));
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        LoanDTO invalidDTO = new LoanDTO(null, 1L, 999L, LocalDate.now(), null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> loanService.createLoan(invalidDTO)
        );

        assertEquals("Membre non trouvé", exception.getMessage());
        verify(memberRepository, times(1)).findById(999L);
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    @DisplayName("Should throw exception when book is not available")
    void testCreateLoan_BookNotAvailable() {
        // FIX: Ne mocker que ce qui est réellement utilisé
        when(bookRepository.findById(2L)).thenReturn(Optional.of(unavailableBook));
        // SUPPRIMÉ: when(memberRepository.findById(1L))... car pas utilisé dans ce cas

        LoanDTO invalidDTO = new LoanDTO(null, 2L, 1L, LocalDate.now(), null);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> loanService.createLoan(invalidDTO)
        );

        assertEquals("Le livre n'est pas disponible", exception.getMessage());
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    @DisplayName("Should throw exception when book already loaned")
    void testCreateLoan_BookAlreadyLoaned() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(availableBook));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
        when(loanRepository.findByBookIdAndReturnDateIsNull(1L)).thenReturn(Optional.of(testLoan));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> loanService.createLoan(testLoanDTO)
        );

        assertEquals("Ce livre est déjà en prêt", exception.getMessage());
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    @DisplayName("Should use provided loan date")
    void testCreateLoan_WithCustomDate() {
        LocalDate customDate = LocalDate.of(2024, 1, 15);
        LoanDTO customDTO = new LoanDTO(null, 1L, 1L, customDate, null);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(availableBook));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
        when(loanRepository.findByBookIdAndReturnDateIsNull(1L)).thenReturn(Optional.empty());
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Loan result = loanService.createLoan(customDTO);

        assertNotNull(result);
        assertEquals(customDate, result.getLoanDate());
    }

    @Test
    @DisplayName("Should return book successfully")
    void testReturnBook_Success() {
        when(loanRepository.findById(1L)).thenReturn(Optional.of(testLoan));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bookRepository.save(any(Book.class))).thenReturn(availableBook);

        Optional<Loan> result = loanService.returnBook(1L);

        assertTrue(result.isPresent());
        assertNotNull(result.get().getReturnDate());
        assertEquals(LocalDate.now(), result.get().getReturnDate());
        verify(bookRepository).save(argThat(book -> book.getAvailable()));
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    @DisplayName("Should return empty when loan not found")
    void testReturnBook_LoanNotFound() {
        when(loanRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Loan> result = loanService.returnBook(999L);

        assertFalse(result.isPresent());
        verify(loanRepository, times(1)).findById(999L);
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    @DisplayName("Should throw exception when book already returned")
    void testReturnBook_AlreadyReturned() {
        testLoan.setReturnDate(LocalDate.now().minusDays(5));
        when(loanRepository.findById(1L)).thenReturn(Optional.of(testLoan));

        assertThrows(
                IllegalStateException.class,
                () -> loanService.returnBook(1L)
        );

        verify(loanRepository, times(1)).findById(1L);
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    @DisplayName("Should return all active loans")
    void testGetActiveLoans() {
        Loan activeLoan1 = new Loan(1L, LocalDate.now(), null, availableBook, testMember);
        Loan activeLoan2 = new Loan(2L, LocalDate.now(), null, unavailableBook, testMember);
        List<Loan> activeLoans = Arrays.asList(activeLoan1, activeLoan2);

        when(loanRepository.findByReturnDateIsNull()).thenReturn(activeLoans);

        List<Loan> result = loanService.getActiveLoans();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(loan -> loan.getReturnDate() == null));
        verify(loanRepository, times(1)).findByReturnDateIsNull();
    }

    @Test
    @DisplayName("Should return loans for specific member")
    void testGetMemberLoans() {
        List<Loan> memberLoans = Arrays.asList(testLoan);
        when(loanRepository.findByMemberId(1L)).thenReturn(memberLoans);

        List<Loan> result = loanService.getMemberLoans(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testMember, result.get(0).getMember());
        verify(loanRepository, times(1)).findByMemberId(1L);
    }

    @Test
    @DisplayName("Should return loan history for specific book")
    void testGetBookLoans() {
        List<Loan> bookLoans = Arrays.asList(testLoan);
        when(loanRepository.findByBookId(1L)).thenReturn(bookLoans);

        List<Loan> result = loanService.getBookLoans(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(availableBook, result.get(0).getBook());
        verify(loanRepository, times(1)).findByBookId(1L);
    }

    @Test
    @DisplayName("Should return loan by ID")
    void testGetLoanById() {
        when(loanRepository.findById(1L)).thenReturn(Optional.of(testLoan));

        Optional<Loan> result = loanService.getLoanById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(loanRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return overdue loans (older than 30 days)")
    void testGetOverdueLoans() {
        LocalDate overdueDate = LocalDate.now().minusDays(35);
        Loan overdueLoan = new Loan(1L, overdueDate, null, availableBook, testMember);
        List<Loan> overdueLoans = Arrays.asList(overdueLoan);

        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        when(loanRepository.findByReturnDateIsNullAndLoanDateBefore(thirtyDaysAgo))
                .thenReturn(overdueLoans);

        List<Loan> result = loanService.getOverdueLoans();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getLoanDate().isBefore(LocalDate.now().minusDays(30)));
        assertNull(result.get(0).getReturnDate());
        verify(loanRepository, times(1)).findByReturnDateIsNullAndLoanDateBefore(thirtyDaysAgo);
    }
}
