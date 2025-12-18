package com.ensam.library.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "loans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La date d'emprunt est obligatoire")
    @Column(nullable = false)
    private LocalDate loanDate;

    private LocalDate returnDate;

    @NotNull(message = "Le livre est obligatoire")
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @NotNull(message = "Le membre est obligatoire")
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
}