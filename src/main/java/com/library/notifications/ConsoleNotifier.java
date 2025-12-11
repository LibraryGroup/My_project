package com.library.notifications;

import com.library.model.User;

public class ConsoleNotifier implements Observer {

    @Override
    public void notify(User user, String message) {
        System.out.println("🖥️ Console Notification for " + user.getUsername() + ": " + message);
    }
}
