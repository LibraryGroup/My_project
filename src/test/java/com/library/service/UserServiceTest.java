package com.library.service;

import com.library.model.Book;
import com.library.model.BorrowRecord;
import com.library.model.User;
import com.library.repository.UserRepository;
import com.library.repository.InMemoryBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;

    // Fake Repository يحتوي find + delete
    private static class FakeUserRepository implements UserRepository {

        boolean deleted = false;
        User savedUser = null; // لمعرفة أنه تم حفظه
        @Override
        public User findByUsername(String username) {
            if ("mohammad".equals(username)) {
                return new User("mohammad", 0.0);
            }
            if ("ahmed".equals(username)) {
                return new User("ahmed", 50.0);
            }
            if ("test".equals(username)) {
                return new User("test", 0.0);
            }
            return null;
        }
        @Override
        public void save(User user) {
            this.savedUser = user;  // فقط نخزن النسخة بدون Database
        }

        @Override
        public boolean deleteUser(String username) {
            deleted = true;
            return true;
        }
    }

    @BeforeEach
    void setUp() {
        userService = new UserService(new FakeUserRepository());
    }

    // ===============================
    // Sprint 1 + Sprint 2 Tests
    // ===============================

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
        assertTrue(userService.canBorrow(user));
    }

    @Test
    void cannotBorrowWhenHasFine() {
        User user = userService.findUser("ahmed");
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

    // ===============================
    // Sprint 4 — Unregister Tests
    // ===============================

    @Test
    void unregisterShouldFailIfNotAdmin() {
        User normalUser = new User("mohammad", 0.0);
        User target = new User("test", 0.0);

        BorrowService bs = new BorrowService(new InMemoryBookRepository());

        assertThrows(SecurityException.class,
                () -> userService.unregister(normalUser, target, bs));
    }

    @Test
    void unregisterShouldFailIfUserHasFine() {
        User admin = new User("admin", 0.0);
        User target = new User("ahmed", 50.0); // عنده غرامة

        BorrowService bs = new BorrowService(new InMemoryBookRepository());

        assertThrows(IllegalStateException.class,
                () -> userService.unregister(admin, target, bs));
    }

    @Test
    void unregisterShouldFailIfUserHasActiveLoans() {
        User admin = new User("admin", 0.0);
        User target = new User("test", 0.0);

        InMemoryBookRepository bookRepo = new InMemoryBookRepository();
        BorrowService bs = new BorrowService(bookRepo);

        // نضيف كتاب واستخدام استعارة فعالة
        Book book = new Book(1, "X", "Y", "999");
        bookRepo.add(book);

        BorrowRecord r = new BorrowRecord(
                target,
                book,
                LocalDate.now(),
                LocalDate.now().plusDays(5)
        );

        // نجعل السجل فعال (not returned)
        r.setReturned(false);

        // إضافة السجل إلى borrowService عبر test helper
        bs._testGetRecords(target.getUsername()).add(r);

        assertThrows(IllegalStateException.class,
                () -> userService.unregister(admin, target, bs));
    }

    @Test
    void unregisterShouldSucceedForCleanUser() {

        FakeUserRepository repo = new FakeUserRepository();
        userService = new UserService(repo);

        User admin = new User("admin", 0.0);
        User target = new User("test", 0.0);

        BorrowService bs = new BorrowService(new InMemoryBookRepository());

        boolean result = userService.unregister(admin, target, bs);

        assertTrue(result);
        assertTrue(repo.deleted);
    }
}
