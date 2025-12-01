package com.library.communication;

import com.library.model.EmailMessage;

import java.util.ArrayList;
import java.util.List;

public class MockEmailServer implements EmailServer {

    private final List<EmailMessage> sentMessages = new ArrayList<>();

    @Override
    public void send(EmailMessage message) {
        sentMessages.add(message);
    }

   
    public List<EmailMessage> getSentMessages() {
        return sentMessages;
    }

    public void clear() {
        sentMessages.clear();
    }
}
