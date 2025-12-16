package com.library.notifications;

import com.library.communication.EmailServer;
import com.library.model.EmailMessage;
import com.library.model.User;

public class EmailNotifier implements Observer {

    private final EmailServer emailServer;

    public EmailNotifier(EmailServer emailServer) {
        this.emailServer = emailServer;
    }

    @Override
    public void notify(User user, String message) {

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            return; // ما في إيميل
        }

        EmailMessage email = new EmailMessage(
                user.getEmail(),
                message
        );

        emailServer.send(email);
    }
}

