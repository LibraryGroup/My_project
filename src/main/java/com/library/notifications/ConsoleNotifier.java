package com.library.notifications;

import com.library.model.User;
import java.util.logging.Logger;

public class ConsoleNotifier implements Observer {

    private static final Logger logger = Logger.getLogger(ConsoleNotifier.class.getName());

    @Override
    public void notify(User user, String message) {
        logger.info("🖥️ Console Notification for " + user.getUsername() + ": " + message);
    }
}
