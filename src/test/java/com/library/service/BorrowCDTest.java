package com.library.service;

import com.library.model.*;
import com.library.repository.*;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class BorrowCDTest {

    @Test
    void cdBorrowDueIn7Days() {
        MediaRepository mediaRepo = mock(MediaRepository.class);
        BorrowRepository borrowRepo = mock(BorrowRepository.class);

        BorrowService service = new BorrowService(mediaRepo, borrowRepo);

        User user = new User("mohammad", 0);
        CD cd = new CD(5, "Greatest Hits", "Artist");

        when(borrowRepo.findByUser(user)).thenReturn(new ArrayList<>());
        when(mediaRepo.findById(5)).thenReturn(cd);

        LocalDate today = LocalDate.of(2025, 1, 1);

        BorrowRecord rec = service.borrow(user, 5, today);

        assertEquals(today.plusDays(7), rec.getDueDate());
    }
}

