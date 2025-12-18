package com.ensam.library.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {
    private Long id;

    @NotNull(message = "Le livre est obligatoire")
    private Long bookId;

    @NotNull(message = "Le membre est obligatoire")
    private Long memberId;

    private LocalDate loanDate;
    private LocalDate returnDate;
}