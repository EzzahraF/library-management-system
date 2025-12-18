package com.ensam.library.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 2, max = 100, message = "Le titre doit contenir entre 2 et 100 caractères")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "L'auteur est obligatoire")
    @Size(min = 2, max = 100, message = "L'auteur doit contenir entre 2 et 100 caractères")
    @Column(nullable = false)
    private String author;

    @NotBlank(message = "Le genre est obligatoire")
    @Size(min = 2, max = 50, message = "Le genre doit contenir entre 2 et 50 caractères")
    @Column(nullable = false)
    private String genre;

    @Column(nullable = false)
    private Boolean available = true;
}