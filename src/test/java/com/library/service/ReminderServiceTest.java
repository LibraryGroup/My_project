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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReminderServiceTest {

    @Test
    void sendReminderForOneUserWithTwoOverdueItems() {

        
        BorrowService borrowService = mock(BorrowService.class);
        Observer observer = mock(Observer.class);

        ReminderService reminderService = new ReminderService(borrowService);
        reminderService.addObserver(observer);

        User user = new User("mohammad", 0.0);

        Book b1 = new Book(1, "Clean Code", "Robert Martin", "111");
        Book b2 = new Book(2, "Effective Java", "Joshua Bloch", "222");

        LocalDate borrow1 = LocalDate.of(2025, 1, 1);
        LocalDate borrow2 = LocalDate.of(2025, 1, 2);

        BorrowRecord r1 = new BorrowRecord(user, b1, borrow1, borrow1.plusDays(28));
        BorrowRecord r2 = new BorrowRecord(user, b2, borrow2, borrow2.plusDays(28));

        
        LocalDate now = LocalDate.of(2025, 2, 10);

        
        when(borrowService.getAllUsersWithRecords()).thenReturn(Set.of(user));
        when(borrowService.getBorrowRecordsForUser(user)).thenReturn(List.of(r1, r2));

        
        int affectedUsers = reminderService.sendOverdueReminders(now);

        
        assertEquals(1, affectedUsers);

        
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);

        verify(observer, times(1)).notify(userCaptor.capture(), msgCaptor.capture());

        assertEquals("mohammad", userCaptor.getValue().getUsername());
        assertEquals("You have 2 overdue item(s).", msgCaptor.getValue());
    }
}
