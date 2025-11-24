package com.library.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BorrowRecordTest {

    @Test
    void overdueRecordShouldBeDetected() {
        User u = new User("mohammad", 0.0);
        Book b = new Book(1, "Clean Code", "Martin", "111");
        LocalDate borrow = LocalDate.of(2025, 1, 1);

        BorrowRecord r = new BorrowRecord(u, b, borrow, borrow.plusDays(28));

        assertTrue(r.isOverdue(borrow.plusDays(40)));  // Overdue
        assertFalse(r.isOverdue(borrow.plusDays(10))); // Not overdue
    }
}
