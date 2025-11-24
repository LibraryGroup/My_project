package com.library.communication;

import com.library.model.EmailMessage;

public interface EmailServer {
    void send(EmailMessage message);
}
