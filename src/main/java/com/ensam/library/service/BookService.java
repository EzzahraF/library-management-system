package com.ensam.library.service;

import com.ensam.library.dto.BookDTO;
import com.ensam.library.model.Book;
import com.ensam.library.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> getAllBooks() {
        log.info("Récupération de tous les livres");
        return bookRepository.findAll();
    }

    public Optional<Book> getBookById(Long id) {
        log.info("Récupération du livre avec ID: {}", id);
        return bookRepository.findById(id);
    }

    @Transactional
    public Book createBook(BookDTO bookDTO) {
        log.info("Création d'un nouveau livre: {}", bookDTO.getTitle());
        Book book = new Book();
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setGenre(bookDTO.getGenre());
        book.setAvailable(true);
        return bookRepository.save(book);
    }

    @Transactional
    public Optional<Book> updateBook(Long id, BookDTO bookDTO) {
        log.info("Mise à jour du livre avec ID: {}", id);
        return bookRepository.findById(id).map(existingBook -> {
            existingBook.setTitle(bookDTO.getTitle());
            existingBook.setAuthor(bookDTO.getAuthor());
            existingBook.setGenre(bookDTO.getGenre());
            return bookRepository.save(existingBook);
        });
    }

    @Transactional
    public boolean deleteBook(Long id) {
        log.info("Suppression du livre avec ID: {}", id);
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Book> getAvailableBooks() {
        log.info("Récupération des livres disponibles");
        return bookRepository.findByAvailableTrue();
    }

    public List<Book> searchBooksByTitle(String title) {
        log.info("Recherche de livres par titre: {}", title);
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }
}