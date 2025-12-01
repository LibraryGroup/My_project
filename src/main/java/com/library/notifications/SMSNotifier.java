package com.library.notifications;

import com.library.model.User;

public class SMSNotifier implements Observer {

    @Override
    public void notify(User user, String message) {
        System.out.println("📱 SMS to " + user.getUsername() + ": " + message);
    }
}

