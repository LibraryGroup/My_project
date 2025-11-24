package com.library.service;

import com.library.model.Book;
import com.library.model.BorrowRecord;
import com.library.model.User;
import com.library.repository.InMemoryBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BorrowRestrictionsTest {

    private BorrowService borrowService;
    private InMemoryBookRepository bookRepo;

    @BeforeEach
    void setUp() {
        bookRepo = new InMemoryBookRepository();
        borrowService = new BorrowService(bookRepo);
    }

    @Test
    void cannotBorrowWhenUserHasFine() {
        User u = new User("x", 20.0);
        Book b = new Book(1, "T", "A", "111");
        bookRepo.add(b);

        assertThrows(IllegalStateException.class,
                () -> borrowService.borrowBook(u, "111", LocalDate.now()));
    }

    @Test
    void cannotBorrowWhenUserHasOverdueLoan() {
        User u = new User("x", 0.0);
        Book b1 = new Book(1, "B1", "A", "111");
        Book b2 = new Book(2, "B2", "A", "222");

        bookRepo.add(b1);
        bookRepo.add(b2);

        // first borrow (overdue)
        BorrowRecord r = borrowService.borrowBook(u, "111",
                LocalDate.now().minusDays(40));
        r.setReturned(false);

        assertThrows(IllegalStateException.class,
                () -> borrowService.borrowBook(u, "222", LocalDate.now()));
    }

    @Test
    void cannotBorrowWhenUserHasActiveLoan() {
        User u = new User("x", 0.0);
        Book b1 = new Book(1, "B1", "A", "111");
        Book b2 = new Book(2, "B2", "A", "222");

        bookRepo.add(b1);
        bookRepo.add(b2);

        BorrowRecord r = borrowService.borrowBook(u, "111", LocalDate.now());
        r.setReturned(false);

        assertThrows(IllegalStateException.class,
                () -> borrowService.borrowBook(u, "222", LocalDate.now()));
    }
}
