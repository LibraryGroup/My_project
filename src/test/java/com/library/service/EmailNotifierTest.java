package com.library.service;
import com.library.notifications.*;

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

        // Arrange
        EmailServer emailServer = mock(EmailServer.class);
        EmailNotifier notifier = new EmailNotifier(emailServer);

        User user = new User("mohammad", 0.0);
        String message = "You have overdue books!";

        // Act
        notifier.notify(user, message);

        // Assert
        ArgumentCaptor<EmailMessage> captor = ArgumentCaptor.forClass(EmailMessage.class);

        verify(emailServer, times(1)).send(captor.capture());

        EmailMessage sent = captor.getValue();

        assertEquals("mohammad", sent.getTo());
        assertEquals("You have overdue books!", sent.getContent());
    }
}
