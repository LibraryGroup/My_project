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

    
    static class FakeUserRepository implements UserRepository {
        @Override
        public User findByUsername(String username) {
            return null;
        }

        @Override
        public void save(User user) {}

        @Override
        public boolean deleteUser(String username) { return false; }
    }

    @Test
    void testMixedMediaFineCalculation() {

        
        User u = new User("mohammad", 0);

        
        Book book = new Book(1, "Clean Code", "Robert Martin", "111");
        CD cd = new CD(2, "Music", "Singer");

        
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
        int overdueCD   = r2.getOverdueDays(today);

        assertTrue(overdueBook > 0);
        assertTrue(overdueCD > 0);

        
        FineService service = new FineService(new FakeUserRepository());

        double totalFine = service.calculateTotalFine(List.of(r1, r2), today);

       
        double expected = 10.0 + 20.0;

        assertEquals(expected, totalFine, 0.001);
    }
}
