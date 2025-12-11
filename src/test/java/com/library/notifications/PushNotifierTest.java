package com.library.notifications;

import com.library.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class PushNotifierTest {

    @Test
    void notifyShouldPrintPushMessage() {
        PushNotifier notifier = new PushNotifier();
        User user = new User("moh", 0);

        assertDoesNotThrow(() ->
                notifier.notify(user, "Test PUSH")
        );
    }
}
