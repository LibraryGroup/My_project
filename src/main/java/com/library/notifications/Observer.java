package com.library.notifications;

import com.library.model.User;

public interface Observer {
    void notify(User user, String message);
}
