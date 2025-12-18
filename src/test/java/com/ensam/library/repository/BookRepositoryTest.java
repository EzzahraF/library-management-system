package com.ensam.library.repository;

import com.ensam.library.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookRepository bookRepository;

    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        book1 = new Book();
        book1.setTitle("Clean Code");
        book1.setAuthor("Robert Martin");
        book1.setGenre("Programming");
        book1.setAvailable(true);

        book2 = new Book();
        book2.setTitle("Design Patterns");
        book2.setAuthor("Gang of Four");
        book2.setGenre("Programming");
        book2.setAvailable(false);

        entityManager.persist(book1);
        entityManager.persist(book2);
        entityManager.flush();
    }

    @Test
    void testFindByAvailableTrue() {
        List<Book> availableBooks = bookRepository.findByAvailableTrue();

        assertThat(availableBooks).hasSize(1);
        assertThat(availableBooks.get(0).getTitle()).isEqualTo("Clean Code");
        assertThat(availableBooks.get(0).getAvailable()).isTrue();
    }

    @Test
    void testFindByTitleContainingIgnoreCase() {
        List<Book> books = bookRepository.findByTitleContainingIgnoreCase("clean");

        assertThat(books).hasSize(1);
        assertThat(books.get(0).getTitle()).isEqualTo("Clean Code");
    }

    @Test
    void testFindByAuthor() {
        List<Book> books = bookRepository.findByAuthor("Robert Martin");

        assertThat(books).hasSize(1);
        assertThat(books.get(0).getAuthor()).isEqualTo("Robert Martin");
    }

    @Test
    void testFindByGenre() {
        List<Book> books = bookRepository.findByGenre("Programming");

        assertThat(books).hasSize(2);
    }
}