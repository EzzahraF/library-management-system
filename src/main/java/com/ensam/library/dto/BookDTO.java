package com.ensam.library.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 2, max = 100, message = "Le titre doit contenir entre 2 et 100 caractères")
    private String title;

    @NotBlank(message = "L'auteur est obligatoire")
    @Size(min = 2, max = 100, message = "L'auteur doit contenir entre 2 et 100 caractères")
    private String author;

    @NotBlank(message = "Le genre est obligatoire")
    @Size(min = 2, max = 50, message = "Le genre doit contenir entre 2 et 50 caractères")
    private String genre;
}