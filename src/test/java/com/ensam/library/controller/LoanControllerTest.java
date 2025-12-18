package com.ensam.library.controller;

import com.ensam.library.dto.LoanDTO;
import com.ensam.library.model.Book;
import com.ensam.library.model.Loan;
import com.ensam.library.model.Member;
import com.ensam.library.service.LoanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(LoanController.class)
@DisplayName("Loan Controller Tests")
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoanService loanService;

    private Loan testLoan;
    private LoanDTO testLoanDTO;
    private Book testBook;
    private Member testMember;

    @BeforeEach
    void setUp() {
        testBook = new Book(1L, "Test Book", "Test Author", "Fiction", true);
        testMember = new Member(1L, "John Doe", "123 Main St", "john@test.com", "+1234567890");
        testLoan = new Loan(1L, LocalDate.now(), null, testBook, testMember);
        testLoanDTO = new LoanDTO(null, 1L, 1L, LocalDate.now(), null);
    }

    @Test
    @DisplayName("POST /api/loans - Should create loan successfully")
    void testCreateLoan_Success() throws Exception {
        // Given
        when(loanService.createLoan(any(LoanDTO.class))).thenReturn(testLoan);

        // When & Then
        mockMvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLoanDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.book.title").value("Test Book"))
                .andExpect(jsonPath("$.member.name").value("John Doe"));

        verify(loanService, times(1)).createLoan(any(LoanDTO.class));
    }

    @Test
    @DisplayName("POST /api/loans - Should return 400 when bookId is null")
    void testCreateLoan_NullBookId() throws Exception {
        // Given
        LoanDTO invalidDTO = new LoanDTO(null, null, 1L, LocalDate.now(), null);

        // When & Then
        mockMvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(loanService, never()).createLoan(any(LoanDTO.class));
    }

    @Test
    @DisplayName("POST /api/loans - Should return 400 when memberId is null")
    void testCreateLoan_NullMemberId() throws Exception {
        // Given
        LoanDTO invalidDTO = new LoanDTO(null, 1L, null, LocalDate.now(), null);

        // When & Then
        mockMvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(loanService, never()).createLoan(any(LoanDTO.class));
    }

    @Test
    @DisplayName("PUT /api/loans/{id}/return - Should return book successfully")
    void testReturnBook_Success() throws Exception {
        // Given
        testLoan.setReturnDate(LocalDate.now());
        when(loanService.returnBook(1L)).thenReturn(Optional.of(testLoan));

        // When & Then
        mockMvc.perform(put("/api/loans/1/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.returnDate").isNotEmpty());

        verify(loanService, times(1)).returnBook(1L);
    }

    @Test
    @DisplayName("PUT /api/loans/{id}/return - Should return 404 when loan not found")
    void testReturnBook_NotFound() throws Exception {
        // Given
        when(loanService.returnBook(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/api/loans/999/return"))
                .andExpect(status().isNotFound());

        verify(loanService, times(1)).returnBook(999L);
    }

    @Test
    @DisplayName("GET /api/loans/active - Should return active loans")
    void testGetActiveLoans() throws Exception {
        // Given
        List<Loan> activeLoans = Arrays.asList(testLoan);
        when(loanService.getActiveLoans()).thenReturn(activeLoans);

        // When & Then
        mockMvc.perform(get("/api/loans/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].returnDate").isEmpty());

        verify(loanService, times(1)).getActiveLoans();
    }
}
