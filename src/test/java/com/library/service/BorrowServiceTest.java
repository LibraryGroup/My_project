package com.library.service;

import com.library.model.*;
import com.library.repository.BorrowRepository;
import com.library.repository.MediaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class BorrowServiceTest {

    private BorrowRepository borrowRepo;
    private MediaRepository mediaRepo;
    private BorrowService service;

    private User user;
    private Book book;

    @BeforeEach
    void setUp() {
        borrowRepo = mock(BorrowRepository.class);
        mediaRepo = mock(MediaRepository.class);

        service = new BorrowService(mediaRepo, borrowRepo);

        user = new User("mohammad", 0.0);
        book = new Book(1, "Clean Code", "Robert Martin", "111");
    }

    @Test
    void borrowShouldSaveRecordAndSetUnavailable() {

        when(mediaRepo.findById(1)).thenReturn(book);
        when(borrowRepo.findByUser(user)).thenReturn(new ArrayList<>());

        LocalDate today = LocalDate.of(2025, 1, 1);

        BorrowRecord rec = service.borrow(user, 1, today);

        assertNotNull(rec);
        assertFalse(book.isAvailable());
        assertEquals(today.plusDays(28), rec.getDueDate());

        verify(borrowRepo, times(1)).save(any(BorrowRecord.class));
    }

    @Test
    void cannotBorrowIfHasFines() {
        user.setFineBalance(30.0);

        when(borrowRepo.findByUser(user)).thenReturn(new ArrayList<>());

        assertThrows(IllegalStateException.class, () ->
                service.borrow(user, 1, LocalDate.now()));
    }

    @Test
    void cannotBorrowOverdueItemExists() {
        BorrowRecord r = mock(BorrowRecord.class);
        when(r.isReturned()).thenReturn(false);
        when(r.isOverdue(any())).thenReturn(true);

        when(borrowRepo.findByUser(user)).thenReturn(List.of(r));
        when(mediaRepo.findById(1)).thenReturn(book);

        assertThrows(IllegalStateException.class, () ->
                service.borrow(user, 1, LocalDate.now()));
    }

    @Test
    void returnItemShouldUpdateRecord() {
        BorrowRecord r = new BorrowRecord(user, book,
                LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(5));

        List<BorrowRecord> list = new ArrayList<>();
        list.add(r);

        when(borrowRepo.findAll()).thenReturn(list);

        boolean ok = service.returnItem("mohammad", 1, LocalDate.now());

        assertTrue(ok);
        assertTrue(r.isReturned());
        assertTrue(book.isAvailable());
        verify(borrowRepo, times(1)).update(r);
    }
}
