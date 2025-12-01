package com.library.service;

import com.library.model.Book;
import com.library.model.BorrowRecord;
import com.library.model.User;
import com.library.notifications.Observer;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ReminderIntegrationTest {

    @Test
    void reminderShouldTriggerObserverNotification() {

        
        BorrowService borrowService = mock(BorrowService.class);
        Observer observer = mock(Observer.class);

        ReminderService reminderService = new ReminderService(borrowService);
        reminderService.addObserver(observer);

        User user = new User("ahmed", 0);

        Book book = new Book(1, "Algorithms", "CLRS", "999");
        BorrowRecord record = new BorrowRecord(
                user,
                book,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 10)
        );

        LocalDate today = LocalDate.of(2025, 2, 1);

        when(borrowService.getAllUsersWithRecords()).thenReturn(Set.of(user));
        when(borrowService.getBorrowRecordsForUser(user)).thenReturn(List.of(record));

       
        int count = reminderService.sendOverdueReminders(today);

      
        assertEquals(1, count);

        ArgumentCaptor<User> userCap = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<String> msgCap = ArgumentCaptor.forClass(String.class);

        verify(observer, times(1)).notify(userCap.capture(), msgCap.capture());

        assertEquals("ahmed", userCap.getValue().getUsername());
        assertEquals("You have 1 overdue item(s).", msgCap.getValue());
    }
}
