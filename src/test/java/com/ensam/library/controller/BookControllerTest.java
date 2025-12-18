package com.ensam.library.controller;

import com.ensam.library.dto.BookDTO;
import com.ensam.library.model.Book;
import com.ensam.library.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(BookController.class)
@DisplayName("Book Controller Tests")
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    private Book testBook;
    private BookDTO testBookDTO;

    @BeforeEach
    void setUp() {
        testBook = new Book(1L, "Test Book", "Test Author", "Fiction", true);
        testBookDTO = new BookDTO(null, "Test Book", "Test Author", "Fiction");
    }

    @Test
    @DisplayName("GET /api/books - Should return all books")
    void testGetAllBooks() throws Exception {
        // Given
        List<Book> books = Arrays.asList(testBook);
        when(bookService.getAllBooks()).thenReturn(books);

        // When & Then
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Test Book"))
                .andExpect(jsonPath("$[0].author").value("Test Author"));

        verify(bookService, times(1)).getAllBooks();
    }

    @Test
    @DisplayName("GET /api/books/{id} - Should return book when exists")
    void testGetBookById_Success() throws Exception {
        // Given
        when(bookService.getBookById(1L)).thenReturn(Optional.of(testBook));

        // When & Then
        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.author").value("Test Author"));

        verify(bookService, times(1)).getBookById(1L);
    }

    @Test
    @DisplayName("GET /api/books/{id} - Should return 404 when book not found")
    void testGetBookById_NotFound() throws Exception {
        // Given
        when(bookService.getBookById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/books/999"))
                .andExpect(status().isNotFound());

        verify(bookService, times(1)).getBookById(999L);
    }

    @Test
    @DisplayName("POST /api/books - Should create book successfully")
    void testCreateBook_Success() throws Exception {
        // Given
        when(bookService.createBook(any(BookDTO.class))).thenReturn(testBook);

        // When & Then
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBookDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Book"));

        verify(bookService, times(1)).createBook(any(BookDTO.class));
    }

    @Test
    @DisplayName("POST /api/books - Should return 400 when title is blank")
    void testCreateBook_InvalidTitle() throws Exception {
        // Given
        BookDTO invalidDTO = new BookDTO(null, "", "Test Author", "Fiction");

        // When & Then
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(bookService, never()).createBook(any(BookDTO.class));
    }

    @Test
    @DisplayName("POST /api/books - Should return 400 when title is too short")
    void testCreateBook_TitleTooShort() throws Exception {
        // Given
        BookDTO invalidDTO = new BookDTO(null, "A", "Test Author", "Fiction");

        // When & Then
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(bookService, never()).createBook(any(BookDTO.class));
    }

    @Test
    @DisplayName("PUT /api/books/{id} - Should update book successfully")
    void testUpdateBook_Success() throws Exception {
        // Given
        Book updatedBook = new Book(1L, "Updated Title", "Updated Author", "Fiction", true);
        when(bookService.updateBook(eq(1L), any(BookDTO.class))).thenReturn(Optional.of(updatedBook));

        // When & Then
        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBookDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Updated Title"));

        verify(bookService, times(1)).updateBook(eq(1L), any(BookDTO.class));
    }

    @Test
    @DisplayName("PUT /api/books/{id} - Should return 404 when book not found")
    void testUpdateBook_NotFound() throws Exception {
        // Given
        when(bookService.updateBook(eq(999L), any(BookDTO.class))).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/api/books/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBookDTO)))
                .andExpect(status().isNotFound());

        verify(bookService, times(1)).updateBook(eq(999L), any(BookDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/books/{id} - Should delete book successfully")
    void testDeleteBook_Success() throws Exception {
        // Given
        when(bookService.deleteBook(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());

        verify(bookService, times(1)).deleteBook(1L);
    }

    @Test
    @DisplayName("DELETE /api/books/{id} - Should return 404 when book not found")
    void testDeleteBook_NotFound() throws Exception {
        // Given
        when(bookService.deleteBook(999L)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/books/999"))
                .andExpect(status().isNotFound());

        verify(bookService, times(1)).deleteBook(999L);
    }

    @Test
    @DisplayName("GET /api/books/available - Should return available books")
    void testGetAvailableBooks() throws Exception {
        // Given
        List<Book> availableBooks = Arrays.asList(testBook);
        when(bookService.getAvailableBooks()).thenReturn(availableBooks);

        // When & Then
        mockMvc.perform(get("/api/books/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].available").value(true));

        verify(bookService, times(1)).getAvailableBooks();
    }

    @Test
    @DisplayName("GET /api/books/search - Should search books by title")
    void testSearchBooks() throws Exception {
        // Given
        List<Book> searchResults = Arrays.asList(testBook);
        when(bookService.searchBooksByTitle("Test")).thenReturn(searchResults);

        // When & Then
        mockMvc.perform(get("/api/books/search")
                        .param("title", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Test Book"));

        verify(bookService, times(1)).searchBooksByTitle("Test");
    }
}