package com.library.service;

import com.library.model.Book;
import com.library.repository.InMemoryBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookServiceTest {

    private BookService bookService;

    @BeforeEach
    void setUp() {
        InMemoryBookRepository repository = new InMemoryBookRepository();
        bookService = new BookService(repository);

        bookService.addBook("Clean Code", "Robert Martin", "111");
        bookService.addBook("Effective Java", "Joshua Bloch", "222");
        bookService.addBook("Java Concurrency", "Brian Goetz", "333");
    }

    @Test
    void addBookShouldAssignId() {
        Book book = bookService.addBook("Test Book", "Author", "444");
        assertTrue(book.getId() > 0);
        List<Book> all = bookService.findAll();
        assertTrue(all.contains(book));
    }

    @Test
    void searchByTitleShouldReturnMatchingBooks() {
        List<Book> result = bookService.searchByTitle("java");
        assertFalse(result.isEmpty());
        boolean allMatch = result.stream()
                .allMatch(b -> b.getTitle().toLowerCase().contains("java"));
        assertTrue(allMatch);
    }

    @Test
    void searchByAuthorShouldReturnMatchingBooks() {
        List<Book> result = bookService.searchByAuthor("martin");
        assertEquals(1, result.size());
        assertEquals("Clean Code", result.get(0).getTitle());
    }

    @Test
    void searchByIsbnShouldReturnSingleBook() {
        Book book = bookService.searchByIsbn("222");
        assertNotNull(book);
        assertEquals("Effective Java", book.getTitle());
    }

    @Test
    void searchByIsbnWithUnknownIsbnShouldReturnNull() {
        Book book = bookService.searchByIsbn("999");
        assertNull(book);
    }

    @Test
    void addBookWithEmptyTitleShouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> bookService.addBook("   ", "Author", "555"));
    }

    @Test
    void addBookWithEmptyAuthorShouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> bookService.addBook("Some Title", "   ", "555"));
    }

    @Test
    void addBookWithEmptyIsbnShouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> bookService.addBook("Some Title", "Author", "   "));
    }
}
