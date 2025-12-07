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

        // بعد الموعد → Overdue
        assertTrue(r.isOverdue(borrow.plusDays(40)));

        // قبل الموعد → Not overdue
        assertFalse(r.isOverdue(borrow.plusDays(10)));
    }

    @Test
    void overdueShouldBeTrueAfterDueDate() {
        BorrowRecord r = new BorrowRecord(
                new User("x", 0),
                null,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 10)
        );

        assertTrue(r.isOverdue(LocalDate.of(2025, 1, 11)));
    }

    @Test
    void overdueShouldBeFalseBeforeDueDate() {
        BorrowRecord r = new BorrowRecord(
                new User("x", 0),
                null,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 10)
        );

        assertFalse(r.isOverdue(LocalDate.of(2025, 1, 9)));
    }
}
