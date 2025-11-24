package com.library.service;

import com.library.communication.EmailServer;
import com.library.model.Book;
import com.library.model.BorrowRecord;
import com.library.model.EmailMessage;
import com.library.model.User;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReminderServiceTest {

    @Mock
    EmailServer emailServer;

    @Test
    void sendReminderForOneUserWithTwoOverdueBooks() {

        User user = new User("mohammad", 0.0);

        // نصنع كتابين لأن BorrowRecord يحتاج Book
        Book b1 = new Book(1, "Clean Code", "Robert Martin", "111");
        Book b2 = new Book(2, "Effective Java", "Joshua Bloch", "222");

        // نحدد borrowDate و dueDate
        LocalDate d1 = LocalDate.of(2025, 1, 1);
        LocalDate d2 = LocalDate.of(2025, 1, 2);

        BorrowRecord r1 = new BorrowRecord(user, b1, d1, d1.plusDays(28));
        BorrowRecord r2 = new BorrowRecord(user, b2, d2, d2.plusDays(28));

        List<BorrowRecord> records = List.of(r1, r2);

        ReminderService service = new ReminderService(records, emailServer);

        // بعد 30 يوم تعتبر Overdue
        LocalDate now = LocalDate.of(2025, 2, 1);

        int affectedUsers = service.sendReminders(now);

        assertEquals(1, affectedUsers);

        ArgumentCaptor<EmailMessage> captor = ArgumentCaptor.forClass(EmailMessage.class);
        verify(emailServer, times(1)).send(captor.capture());

        EmailMessage msg = captor.getValue();
        assertEquals("mohammad", msg.getTo());
        assertEquals("You have 2 overdue book(s).", msg.getContent());
    }
}
