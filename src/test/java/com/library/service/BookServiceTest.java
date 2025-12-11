package com.library.service;

import com.library.model.Book;
import com.library.model.BorrowRecord;
import com.library.model.Media;
import com.library.model.User;
import com.library.repository.BorrowRepository;
import com.library.repository.InMemoryBookRepository;
import com.library.repository.MediaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        assertTrue(bookService.findAll().contains(book));
    }

    @Test
    void searchByTitleShouldReturnMatchingBooks() {
        List<Book> result = bookService.searchByTitle("java");

        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(b -> b.getTitle().toLowerCase().contains("java")));
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
        assertNull(bookService.searchByIsbn("999"));
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

    @Test
    void returnItemSucceeds() {
        Media media = mock(Media.class);
        User user = new User("moh", 0);
        BorrowRecord record = new BorrowRecord(user, media,
                LocalDate.now(), LocalDate.now().plusDays(3));

        BorrowRepository repo = mock(BorrowRepository.class);
        MediaRepository mediaRepo = mock(MediaRepository.class);

        when(repo.findAll()).thenReturn(List.of(record));

        BorrowService service = new BorrowService(mediaRepo, repo);

        boolean result = service.returnItem("moh", media.getId(), LocalDate.now());

        assertTrue(result);
        verify(mediaRepo).save(media);
        assertTrue(record.isReturned());
    }

    @Test
    void returnItemFails_WhenUserDoesNotMatch() {
        Media media = mock(Media.class);
        User user = new User("moh", 0);
        BorrowRecord record = new BorrowRecord(user, media,
                LocalDate.now(), LocalDate.now().plusDays(3));

        BorrowRepository repo = mock(BorrowRepository.class);
        when(repo.findAll()).thenReturn(List.of(record));

        BorrowService service = new BorrowService(mock(MediaRepository.class), repo);

        assertFalse(service.returnItem("wrong", media.getId(), LocalDate.now()));
    }

    @Test
    void returnItemFails_WhenMediaDoesNotMatch() {
        Media media = mock(Media.class);
        when(media.getId()).thenReturn(5);

        User user = new User("moh", 0);
        BorrowRecord record = new BorrowRecord(user, media,
                LocalDate.now(), LocalDate.now().plusDays(3));

        BorrowRepository repo = mock(BorrowRepository.class);
        when(repo.findAll()).thenReturn(List.of(record));

        BorrowService service = new BorrowService(mock(MediaRepository.class), repo);

        assertFalse(service.returnItem("moh", 999, LocalDate.now()));
    }
}
