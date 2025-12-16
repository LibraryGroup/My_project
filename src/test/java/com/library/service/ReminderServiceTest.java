package com.library.service;

import com.library.model.Book;
import com.library.model.CD;
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

    @Test
    void sendReminderForMultipleUsersWithMixedMedia() {

        BorrowService borrowService = mock(BorrowService.class);
        Observer observer = mock(Observer.class);

        ReminderService reminderService = new ReminderService(borrowService);
        reminderService.addObserver(observer);

        User user1 = new User("user1", 0.0);
        User user2 = new User("user2", 0.0);

        Book book = new Book(1, "Java", "Author", "111");
        CD cd = new CD(2, "Rock Album", "Artist");

        LocalDate borrowBook = LocalDate.of(2025, 1, 1);
        LocalDate borrowCD = LocalDate.of(2025, 1, 5);

        BorrowRecord r1 = new BorrowRecord(user1, book, borrowBook, borrowBook.plusDays(28));
        BorrowRecord r2 = new BorrowRecord(user1, cd, borrowCD, borrowCD.plusDays(7));
        BorrowRecord r3 = new BorrowRecord(user2, cd, borrowCD, borrowCD.plusDays(7));

        LocalDate now = LocalDate.of(2025, 2, 10);

        when(borrowService.getAllUsersWithRecords()).thenReturn(Set.of(user1, user2));
        when(borrowService.getBorrowRecordsForUser(user1)).thenReturn(List.of(r1, r2));
        when(borrowService.getBorrowRecordsForUser(user2)).thenReturn(List.of(r3));

        int affectedUsers = reminderService.sendOverdueReminders(now);

        assertEquals(2, affectedUsers);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);

        verify(observer, times(2)).notify(userCaptor.capture(), msgCaptor.capture());

        List<User> notifiedUsers = userCaptor.getAllValues();
        List<String> messages = msgCaptor.getAllValues();

        assertTrue(notifiedUsers.stream().anyMatch(u -> u.getUsername().equals("user1")));
        assertTrue(notifiedUsers.stream().anyMatch(u -> u.getUsername().equals("user2")));

        assertTrue(messages.stream().anyMatch(m -> m.equals("You have 2 overdue item(s).")));
        assertTrue(messages.stream().anyMatch(m -> m.equals("You have 1 overdue item(s).")));
    }

    @Test
    void sendReminderForUserWithNoOverdueItems() {

        BorrowService borrowService = mock(BorrowService.class);
        Observer observer = mock(Observer.class);

        ReminderService reminderService = new ReminderService(borrowService);
        reminderService.addObserver(observer);

        User user = new User("nooverdue", 0.0);
        Book book = new Book(1, "Clean Code", "Author", "111");
        LocalDate borrowDate = LocalDate.of(2025, 2, 1);
        BorrowRecord r = new BorrowRecord(user, book, borrowDate, borrowDate.plusDays(28));

        LocalDate now = LocalDate.of(2025, 2, 10);

        when(borrowService.getAllUsersWithRecords()).thenReturn(Set.of(user));
        when(borrowService.getBorrowRecordsForUser(user)).thenReturn(List.of(r));

        int affectedUsers = reminderService.sendOverdueReminders(now);

        assertEquals(0, affectedUsers);

        verify(observer, times(0)).notify(any(), any());
    }
}
