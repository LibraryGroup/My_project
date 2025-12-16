package com.library.communication;

import com.library.model.EmailMessage;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Transport;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class SMTPEmailServerTest {

    @Test
    void shouldSendEmailSuccessfully() {

        // Arrange
        SMTPEmailServer emailServer =
                new SMTPEmailServer(
                        "smtp.test.com",
                        587,
                        "test@test.com",
                        "password"
                );

        EmailMessage emailMessage =
                new EmailMessage("receiver@test.com", "Test message");

        try (MockedStatic<Transport> mockedTransport =
                     mockStatic(Transport.class)) {

            mockedTransport
                    .when(() -> Transport.send(any(Message.class)))
                    .thenAnswer(invocation -> null);

            // Act + Assert
            assertDoesNotThrow(() ->
                    emailServer.send(emailMessage)
            );

            // Verify
            mockedTransport.verify(
                    () -> Transport.send(any(Message.class)),
                    times(1)
            );
        }
    }

    @Test
    void shouldHandleMessagingExceptionGracefully() {

        // Arrange
        SMTPEmailServer emailServer =
                new SMTPEmailServer(
                        "smtp.test.com",
                        587,
                        "test@test.com",
                        "password"
                );

        EmailMessage emailMessage =
                new EmailMessage("receiver@test.com", "Test failure");

        try (MockedStatic<Transport> mockedTransport =
                     mockStatic(Transport.class)) {

            mockedTransport
                    .when(() -> Transport.send(any(Message.class)))
                    .thenThrow(new MessagingException("SMTP failure"));

            // Act + Assert
            assertDoesNotThrow(() ->
                    emailServer.send(emailMessage)
            );

            // Verify
            mockedTransport.verify(
                    () -> Transport.send(any(Message.class)),
                    times(1)
            );
        }
    }
}

