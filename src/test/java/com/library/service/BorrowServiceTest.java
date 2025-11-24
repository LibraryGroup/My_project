package com.library.service;

import com.library.model.Book;
import com.library.model.BorrowRecord;
import com.library.model.User;
import com.library.repository.InMemoryBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BorrowServiceTest {

    private BorrowService borrowService;
    private InMemoryBookRepository bookRepository;
    private User userNoFine;
    private User userWithFine;

    @BeforeEach
    void setUp() {
        bookRepository = new InMemoryBookRepository();

        // نضيف كتب للمكتبة
        BookService bookService = new BookService(bookRepository);
        bookService.addBook("Clean Code", "Robert Martin", "111");
        bookService.addBook("Effective Java", "Joshua Bloch", "222");

        borrowService = new BorrowService(bookRepository);

        userNoFine = new User("mohammad", 0.0);
        userWithFine = new User("ahmed", 20.0);
    }

    @Test
    void borrowBookShouldMakeBookUnavailableAndSetDueDate() {
        LocalDate borrowDate = LocalDate.of(2025, 1, 1);

        BorrowRecord record = borrowService.borrowBook(userNoFine, "111", borrowDate);

        assertNotNull(record);
        assertEquals(borrowDate.plusDays(28), record.getDueDate());

        Book borrowedBook = record.getBook();
        assertFalse(borrowedBook.isAvailable());
    }

    @Test
    void cannotBorrowWhenUserHasFine() {
        assertThrows(IllegalStateException.class,
                () -> borrowService.borrowBook(userWithFine, "111", LocalDate.now()));
    }

    @Test
    void cannotBorrowSameBookTwice() {
        borrowService.borrowBook(userNoFine, "111", LocalDate.now());

        assertThrows(IllegalStateException.class,
                () -> borrowService.borrowBook(userNoFine, "111", LocalDate.now()));
    }

    @Test
    void overdueDetectionShouldReturnOverdueRecords() {
        LocalDate borrowDate = LocalDate.of(2025, 1, 1);
        BorrowRecord record = borrowService.borrowBook(userNoFine, "222", borrowDate);

        LocalDate notOverdueDate = borrowDate.plusDays(10);
        List<BorrowRecord> none = borrowService.findOverdueRecords(notOverdueDate);
        assertTrue(none.isEmpty());

        LocalDate overdueDate = borrowDate.plusDays(29);
        List<BorrowRecord> overdue = borrowService.findOverdueRecords(overdueDate);

        assertEquals(1, overdue.size());
        assertTrue(overdue.get(0).isOverdue(overdueDate));
        assertSame(record, overdue.get(0));
    }
}
