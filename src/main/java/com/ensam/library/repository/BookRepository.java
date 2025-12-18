package com.ensam.library.repository;

import com.ensam.library.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByAuthor(String author);
    List<Book> findByGenre(String genre);
    List<Book> findByAvailableTrue();
    List<Book> findByTitleContainingIgnoreCase(String title);
}