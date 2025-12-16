package com.library.service;

import com.library.notifications.EmailNotifier;
import com.library.communication.EmailServer;
import com.library.model.EmailMessage;
import com.library.model.User;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class EmailNotifierTest {

    @Test
    void emailNotifierShouldSendEmail() {

        EmailServer emailServer = mock(EmailServer.class);
        EmailNotifier notifier = new EmailNotifier(emailServer);

        User user = new User("mohammad", 0.0);
        user.setEmail("mohammad@test.com");

        String message = "You have overdue books!";

        notifier.notify(user, message);

        ArgumentCaptor<EmailMessage> captor =
                ArgumentCaptor.forClass(EmailMessage.class);

        verify(emailServer, times(1)).send(captor.capture());

        EmailMessage sent = captor.getValue();

        assertEquals("mohammad@test.com", sent.getTo());
        assertEquals("You have overdue books!", sent.getContent());
    }
}
