package com.library.service;

import com.library.model.User;
import com.library.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;

    // Fake Repository يوفر كل الفروع التي نحتاجها للتغطية الكاملة
    private static class FakeUserRepository implements UserRepository {

        @Override
        public User findByUsername(String username) {
            if ("mohammad".equals(username)) {
                return new User("mohammad", 0.0);
            }
            if ("ahmed".equals(username)) {
                return new User("ahmed", 50.0);
            }
            return null;  // سيتم تغطيته باختبار خاص
        }
    }

    @BeforeEach
    void setUp() {
        userService = new UserService(new FakeUserRepository());
    }

    // ===================
    //   100% Coverage Tests
    // ===================

    @Test
    void findUserMohammadShouldReturnUser() {
        User user = userService.findUser("mohammad");
        assertNotNull(user);
        assertEquals("mohammad", user.getUsername());
    }

    @Test
    void findUserAhmedShouldReturnUser() {
        User user = userService.findUser("ahmed");
        assertNotNull(user);
        assertEquals("ahmed", user.getUsername());
        assertEquals(50.0, user.getFineBalance());
    }

    @Test
    void findUnknownUserShouldReturnNull() {
        User user = userService.findUser("xyz");
        assertNull(user);
    }

    @Test
    void canBorrowWhenNoFine() {
        User user = userService.findUser("mohammad");
        assertNotNull(user);
        assertTrue(userService.canBorrow(user));
    }

    @Test
    void cannotBorrowWhenHasFine() {
        User user = userService.findUser("ahmed");
        assertNotNull(user);
        assertFalse(userService.canBorrow(user));
    }

    @Test
    void payFinePartialShouldReduceBalance() {
        User user = userService.findUser("ahmed");
        userService.payFine(user, 20.0);
        assertEquals(30.0, user.getFineBalance(), 0.001);
    }

    @Test
    void payFineFullShouldSetBalanceToZero() {
        User user = userService.findUser("ahmed");
        userService.payFine(user, 100.0);
        assertEquals(0.0, user.getFineBalance(), 0.001);
    }

    @Test
    void payFineWithNonPositiveAmountShouldThrow() {
        User user = userService.findUser("mohammad");
        assertThrows(IllegalArgumentException.class,
                () -> userService.payFine(user, 0.0));
    }
}
