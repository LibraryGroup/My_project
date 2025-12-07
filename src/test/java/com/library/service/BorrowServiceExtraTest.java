package com.library.service;

import com.library.model.*;
import com.library.repository.BorrowRepository;
import com.library.repository.MediaRepository;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BorrowServiceExtraTest {

    // ───────────────────────────────────────────
    // Media not found → يجب أن يرمي IllegalArgumentException
    // ───────────────────────────────────────────
    @Test
    void borrowFailsWhenMediaDoesNotExist() {
        MediaRepository mediaRepo = mock(MediaRepository.class);
        BorrowRepository borrowRepo = mock(BorrowRepository.class);

        BorrowService service = new BorrowService(mediaRepo, borrowRepo);

        when(mediaRepo.findById(10)).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> service.borrow(new User("moh", 0), 10, LocalDate.now()));
    }

    // ───────────────────────────────────────────
    // Media unavailable → يرمي IllegalStateException
    // ───────────────────────────────────────────
    @Test
    void borrowFailsWhenMediaUnavailable() {

        Media m = mock(Media.class);
        when(m.isAvailable()).thenReturn(false);

        MediaRepository mediaRepo = mock(MediaRepository.class);
        BorrowRepository borrowRepo = mock(BorrowRepository.class);

        when(mediaRepo.findById(1)).thenReturn(m);

        BorrowService service = new BorrowService(mediaRepo, borrowRepo);

        assertThrows(IllegalStateException.class,
                () -> service.borrow(new User("moh", 0), 1, LocalDate.now()));
    }

    // ───────────────────────────────────────────
    // إرجاع عنصر already returned → يجب أن يعيد false
    // ───────────────────────────────────────────
    @Test
    void returnItemFailsWhenAlreadyReturned() {

        Media m = mock(Media.class);
        when(m.getId()).thenReturn(1);

        User u = new User("x", 0);

        BorrowRecord rec = new BorrowRecord(
                u, m,
                LocalDate.now(),
                LocalDate.now().plusDays(3)
        );

        rec.setReturned(true); // ❗ العنصر راجع مسبقًا

        BorrowRepository repo = mock(BorrowRepository.class);
        when(repo.findAll()).thenReturn(List.of(rec));

        BorrowService service = new BorrowService(mock(MediaRepository.class), repo);

        boolean result = service.returnItem("x", 1, LocalDate.now());

        assertFalse(result);
    }

    // ───────────────────────────────────────────
    // User has overdue loan → IllegalStateException
    // ───────────────────────────────────────────
    @Test
    void borrowFailsWhenUserHasOverdue() {

        BorrowRecord r = mock(BorrowRecord.class);
        when(r.isReturned()).thenReturn(false);
        when(r.isOverdue(any(LocalDate.class))).thenReturn(true);

        BorrowRepository repo = mock(BorrowRepository.class);
        when(repo.findByUser(any())).thenReturn(List.of(r));

        MediaRepository mediaRepo = mock(MediaRepository.class);
        Media m = mock(Media.class);
        when(m.isAvailable()).thenReturn(true);
        when(mediaRepo.findById(anyInt())).thenReturn(m);

        BorrowService service = new BorrowService(mediaRepo, repo);

        assertThrows(IllegalStateException.class,
                () -> service.borrow(new User("moh", 0), 1, LocalDate.now()));
    }
}
