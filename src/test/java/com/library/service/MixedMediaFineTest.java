package com.library.service;

import com.library.fines.BookFineStrategy;
import com.library.fines.CDFineStrategy;
import com.library.model.Book;
import com.library.model.BorrowRecord;
import com.library.model.CD;
import com.library.model.User;
import com.library.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MixedMediaFineTest {

    // Constants for test data
    private static final String USERNAME = "mohammad";
    private static final String BOOK_TITLE = "Clean Code";
    private static final String BOOK_AUTHOR = "Robert Martin";
    private static final String BOOK_ISBN = "111";
    private static final String CD_TITLE = "Music";
    private static final String CD_ARTIST = "Singer";
    private static final double BOOK_FINE = 10.0;
    private static final double CD_FINE = 20.0;

    static class FakeUserRepository implements UserRepository {
        @Override
        public User findByUsername(String username) {
            throw new UnsupportedOperationException("Not implemented in test");
        }

        @Override
        public void save(User user) {
            throw new UnsupportedOperationException("Not implemented in test");
        }

        @Override
        public boolean deleteUser(String username) {
            throw new UnsupportedOperationException("Not implemented in test");
        }
    }

    @Test
    void testMixedMediaFineCalculation() {

        User u = new User(USERNAME, 0);

        Book book = new Book(1, BOOK_TITLE, BOOK_AUTHOR, BOOK_ISBN);
        CD cd = new CD(2, CD_TITLE, CD_ARTIST);

        assertTrue(book.getFineStrategy() instanceof BookFineStrategy);
        assertTrue(cd.getFineStrategy() instanceof CDFineStrategy);

        LocalDate borrowDate = LocalDate.of(2025, 1, 1);
        BorrowRecord r1 = new BorrowRecord(
                u, book, borrowDate, borrowDate.plusDays(book.getBorrowDays())
        );
        BorrowRecord r2 = new BorrowRecord(
                u, cd, borrowDate, borrowDate.plusDays(cd.getBorrowDays())
        );

        LocalDate today = LocalDate.of(2025, 2, 10);

        int overdueBook = r1.getOverdueDays(today);
        int overdueCD = r2.getOverdueDays(today);
        assertTrue(overdueBook > 0);
        assertTrue(overdueCD > 0);

        FineService service = new FineService(new FakeUserRepository());
        double totalFine = service.calculateTotalFine(List.of(r1, r2), today);

        double expected = BOOK_FINE + CD_FINE;
        assertEquals(expected, totalFine, 0.001);
    }
}
