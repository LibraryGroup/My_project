package com.library.communication;

import com.library.model.EmailMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MockEmailServerTest {

    private MockEmailServer server;

    @BeforeEach
    void setUp() {
        server = new MockEmailServer();
    }

    @Test
    void sendShouldStoreEmailMessage() {
        EmailMessage msg = new EmailMessage("moh@example.com", "Hello!");

        server.send(msg);

        assertEquals(1, server.getSentMessages().size());
        assertEquals("moh@example.com", server.getSentMessages().get(0).getTo());
        assertEquals("Hello!", server.getSentMessages().get(0).getContent());
    }

    @Test
    void multipleMessagesShouldBeStored() {
        EmailMessage m1 = new EmailMessage("a@a.com", "Body1");
        EmailMessage m2 = new EmailMessage("b@b.com", "Body2");

        server.send(m1);
        server.send(m2);

        assertEquals(2, server.getSentMessages().size());
        assertEquals("a@a.com", server.getSentMessages().get(0).getTo());
        assertEquals("b@b.com", server.getSentMessages().get(1).getTo());
    }

    @Test
    void clearShouldRemoveAllMessages() {
        server.send(new EmailMessage("x@x.com", "Test"));

        assertFalse(server.getSentMessages().isEmpty());

        server.clear();

        assertTrue(server.getSentMessages().isEmpty());
    }

    @Test
    void getSentMessagesShouldReturnListReferenceNotNull() {
        assertNotNull(server.getSentMessages());
        assertEquals(0, server.getSentMessages().size());
    }
}
