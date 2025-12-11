package com.library.service;

import com.library.model.*;
import com.library.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FineServiceFullTest {

    @Test
    void calculateTotalFineShouldIncludeOverdueItems() {
        Book book = new Book(1, "Clean Code", "Martin", "111");
        User u = new User("moh", 0);

        LocalDate borrow = LocalDate.of(2025, 1, 1);
        LocalDate due = borrow.plusDays(28);

        BorrowRecord r = new BorrowRecord(u, book, borrow, due);

        LocalDate now = LocalDate.of(2025, 2, 10); // overdue ~ 12 days

        FineService service = new FineService(mock(UserRepository.class));

        double total = service.calculateTotalFine(List.of(r), now);

        assertTrue(total > 0);
    }

    @Test
    void payFineSucceedsWhenAmountEnough() {
        UserRepository repo = mock(UserRepository.class);

        User u = new User("x", 50);
        when(repo.findByUsername("x")).thenReturn(u);

        FineService s = new FineService(repo);

        boolean ok = s.payFine("x", 100);

        assertTrue(ok);
        verify(repo).save(u);
        assertEquals(0.0, u.getFineBalance());
    }

    @Test
    void payFineFailsWhenAmountTooSmall() {
        UserRepository repo = mock(UserRepository.class);

        User u = new User("x", 50);
        when(repo.findByUsername("x")).thenReturn(u);

        FineService s = new FineService(repo);

        assertFalse(s.payFine("x", 20));
        assertEquals(50, u.getFineBalance());
    }

    @Test
    void payFineFailsWhenUserNotFound() {
        UserRepository repo = mock(UserRepository.class);
        when(repo.findByUsername("missing")).thenReturn(null);

        FineService s = new FineService(repo);

        assertFalse(s.payFine("missing", 100));
    }
}
