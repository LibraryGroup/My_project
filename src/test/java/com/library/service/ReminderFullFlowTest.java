package com.library.service;

import com.library.model.*;
import com.library.notifications.Observer;
import com.library.repository.*;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ReminderFullFlowTest {

    @Test
    void reminderNotifiesAllObservers() {

        BorrowRepository borrowRepo = mock(BorrowRepository.class);
        MediaRepository mediaRepo = mock(MediaRepository.class);

        BorrowService borrowService = new BorrowService(mediaRepo, borrowRepo);

        Observer obs1 = mock(Observer.class);
        Observer obs2 = mock(Observer.class);

        ReminderService reminder = new ReminderService(borrowService);
        reminder.addObserver(obs1);
        reminder.addObserver(obs2);

        User user = new User("mohammad", 0);
        Book b = new Book(1, "X", "A", "111");

        BorrowRecord r = mock(BorrowRecord.class);
        when(r.getUser()).thenReturn(user);
        when(r.isOverdue(any())).thenReturn(true);

        when(borrowRepo.findAll()).thenReturn(List.of(r));
        when(borrowRepo.getAllUsers()).thenReturn(Set.of(user));
        when(borrowRepo.findByUser(user)).thenReturn(List.of(r));

        int count = reminder.sendOverdueReminders(LocalDate.now());

        assertEquals(1, count);
        verify(obs1, times(1)).notify(eq(user), any());
        verify(obs2, times(1)).notify(eq(user), any());
    }
}
