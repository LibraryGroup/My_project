package com.library.service;

import com.library.model.*;
import com.library.repository.*;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class BorrowRestrictionsTest {

    @Test
    void cannotBorrowIfUserHasOverdue() {

        MediaRepository mediaRepo = mock(MediaRepository.class);
        BorrowRepository borrowRepo = mock(BorrowRepository.class);

        BorrowService service = new BorrowService(mediaRepo, borrowRepo);

        User user = new User("ali", 0);

        Book book = new Book(1, "Java", "Author", "111");

        BorrowRecord old = mock(BorrowRecord.class);
        when(old.isReturned()).thenReturn(false);
        when(old.isOverdue(any())).thenReturn(true);

        when(borrowRepo.findByUser(user)).thenReturn(List.of(old));
        when(mediaRepo.findById(1)).thenReturn(book);

        assertThrows(IllegalStateException.class, () ->
                service.borrow(user, 1, LocalDate.now()));
    }
}
