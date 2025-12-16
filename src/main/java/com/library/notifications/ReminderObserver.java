package com.library.notifications;

import com.library.model.User;

public interface ReminderObserver {
    void update(User user, String message);
}
