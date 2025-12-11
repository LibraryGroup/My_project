package com.library.notifications;

import com.library.model.User;

public class PushNotifier implements Observer {

    @Override
    public void notify(User user, String message) {
        System.out.println("🔔 PUSH Notification to " + user.getUsername() + ": " + message);
    }
}
