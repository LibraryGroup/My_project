package com.library.notifications;

import com.library.model.User;
import java.util.logging.Logger;

public class PushNotifier implements Observer {

    private static final Logger logger = Logger.getLogger(PushNotifier.class.getName());

    @Override
    public void notify(User user, String message) {
        logger.info("🔔 PUSH Notification to " + user.getUsername() + ": " + message);
    }
}
