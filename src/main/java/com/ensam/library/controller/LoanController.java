package com.ensam.library.controller;

import com.ensam.library.dto.LoanDTO;
import com.ensam.library.model.Loan;
import com.ensam.library.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @PostMapping
    public ResponseEntity<Loan> createLoan(@Valid @RequestBody LoanDTO loanDTO) {
        Loan loan = loanService.createLoan(loanDTO);
        return ResponseEntity.ok(loan);
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<Loan> returnBook(@PathVariable Long id) {
        Optional<Loan> loan = loanService.returnBook(id);
        return loan.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Loan>> getActiveLoans() {
        List<Loan> loans = loanService.getActiveLoans();
        return ResponseEntity.ok(loans);
    }
}