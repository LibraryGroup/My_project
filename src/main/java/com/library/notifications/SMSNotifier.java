package com.library.notifications;

import com.library.model.User;
import java.util.logging.Logger;

public class SMSNotifier implements Observer {

    private static final Logger logger = Logger.getLogger(SMSNotifier.class.getName());

    @Override
    public void notify(User user, String message) {
        logger.info("📱 SMS to " + user.getUsername() + ": " + message);
    }
}
