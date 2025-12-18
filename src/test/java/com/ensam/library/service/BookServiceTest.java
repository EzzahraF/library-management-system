// ============================================================================
package com.ensam.library.service;

import com.ensam.library.dto.BookDTO;
import com.ensam.library.model.Book;
import com.ensam.library.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Book Service Tests")
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book testBook1;
    private Book testBook2;
    private BookDTO testBookDTO;

    @BeforeEach
    void setUp() {
        testBook1 = new Book(1L, "Clean Code", "Robert C. Martin", "Programming", true);
        testBook2 = new Book(2L, "Design Patterns", "Gang of Four", "Programming", false);
        testBookDTO = new BookDTO(null, "Test Book", "Test Author", "Fiction");
    }

    @Test
    @DisplayName("Should return all books")
    void testGetAllBooks() {
        // Given
        List<Book> expectedBooks = Arrays.asList(testBook1, testBook2);
        when(bookRepository.findAll()).thenReturn(expectedBooks);

        // When
        List<Book> result = bookService.getAllBooks();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Clean Code", result.get(0).getTitle());
        assertEquals("Design Patterns", result.get(1).getTitle());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return book by ID when book exists")
    void testGetBookById_Success() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook1));

        // When
        Optional<Book> result = bookService.getBookById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Clean Code", result.get().getTitle());
        assertEquals("Robert C. Martin", result.get().getAuthor());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when book does not exist")
    void testGetBookById_NotFound() {
        // Given
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Book> result = bookService.getBookById(999L);

        // Then
        assertFalse(result.isPresent());
        verify(bookRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should create a new book successfully")
    void testCreateBook() {
        // Given
        Book savedBook = new Book(1L, "Test Book", "Test Author", "Fiction", true);
        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        // When
        Book result = bookService.createBook(testBookDTO);

        // Then
        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
        assertEquals("Test Author", result.getAuthor());
        assertEquals("Fiction", result.getGenre());
        assertTrue(result.getAvailable());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    @DisplayName("Should update book when book exists")
    void testUpdateBook_Success() {
        // Given
        BookDTO updateDTO = new BookDTO(1L, "Updated Title", "Updated Author", "Updated Genre");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook1));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Optional<Book> result = bookService.updateBook(1L, updateDTO);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Updated Title", result.get().getTitle());
        assertEquals("Updated Author", result.get().getAuthor());
        assertEquals("Updated Genre", result.get().getGenre());
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    @DisplayName("Should return empty when updating non-existent book")
    void testUpdateBook_NotFound() {
        // Given
        BookDTO updateDTO = new BookDTO(999L, "Updated Title", "Updated Author", "Updated Genre");
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Book> result = bookService.updateBook(999L, updateDTO);

        // Then
        assertFalse(result.isPresent());
        verify(bookRepository, times(1)).findById(999L);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Should delete book when book exists")
    void testDeleteBook_Success() {
        // Given
        when(bookRepository.existsById(1L)).thenReturn(true);
        doNothing().when(bookRepository).deleteById(1L);

        // When
        boolean result = bookService.deleteBook(1L);

        // Then
        assertTrue(result);
        verify(bookRepository, times(1)).existsById(1L);
        verify(bookRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should return false when deleting non-existent book")
    void testDeleteBook_NotFound() {
        // Given
        when(bookRepository.existsById(999L)).thenReturn(false);

        // When
        boolean result = bookService.deleteBook(999L);

        // Then
        assertFalse(result);
        verify(bookRepository, times(1)).existsById(999L);
        verify(bookRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should return only available books")
    void testGetAvailableBooks() {
        // Given
        List<Book> availableBooks = Arrays.asList(testBook1);
        when(bookRepository.findByAvailableTrue()).thenReturn(availableBooks);

        // When
        List<Book> result = bookService.getAvailableBooks();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getAvailable());
        verify(bookRepository, times(1)).findByAvailableTrue();
    }

    @Test
    @DisplayName("Should search books by title")
    void testSearchBooksByTitle() {
        // Given
        String searchTerm = "clean";
        List<Book> searchResults = Arrays.asList(testBook1);
        when(bookRepository.findByTitleContainingIgnoreCase(searchTerm)).thenReturn(searchResults);

        // When
        List<Book> result = bookService.searchBooksByTitle(searchTerm);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getTitle().toLowerCase().contains(searchTerm.toLowerCase()));
        verify(bookRepository, times(1)).findByTitleContainingIgnoreCase(searchTerm);
    }

    @Test
    @DisplayName("Should return empty list when no books match search")
    void testSearchBooksByTitle_NoResults() {
        // Given
        String searchTerm = "nonexistent";
        when(bookRepository.findByTitleContainingIgnoreCase(searchTerm)).thenReturn(Arrays.asList());

        // When
        List<Book> result = bookService.searchBooksByTitle(searchTerm);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(bookRepository, times(1)).findByTitleContainingIgnoreCase(searchTerm);
    }
}