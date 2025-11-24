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

class UserDeletionRulesTest {

    private UserService userService;
    private FakeUserRepo repo;
    private BorrowService borrowService;

    static class FakeUserRepo implements UserRepository {
        User stored;
        boolean deleted = false;

        @Override
        public User findByUsername(String username) {
            return stored;
        }

        @Override
        public void save(User user) {
            stored = user;
        }

        @Override
        public boolean deleteUser(String username) {
            deleted = true;
            return true;
        }
    }

    @BeforeEach
    void setUp() {
        repo = new FakeUserRepo();
        userService = new UserService(repo);
        borrowService = new BorrowService(new InMemoryBookRepository());
    }

    @Test
    void unregisterFailsIfNotAdmin() {
        repo.stored = new User("test", 0.0);
        User normal = new User("moh", 0.0);

        assertThrows(SecurityException.class,
                () -> userService.unregister(normal, repo.stored, borrowService));
    }

    @Test
    void unregisterFailsIfUserHasFines() {
        repo.stored = new User("test", 30.0);

        assertFalse(userService.unregister(new User("admin", 0.0),
                repo.stored, borrowService));
    }

    @Test
    void unregisterFailsIfUserHasActiveLoans() {
        repo.stored = new User("test", 0.0);

        InMemoryBookRepository br = new InMemoryBookRepository();
        BorrowService bs = new BorrowService(br);

        Book b = new Book(1, "X", "A", "111");
        br.add(b);

        BorrowRecord r = bs.borrowBook(repo.stored, "111", LocalDate.now());
        r.setReturned(false);

        assertFalse(userService.unregister(new User("admin", 0.0),
                repo.stored, bs));
    }

    @Test
    void unregisterFailsIfUserHasOverdueLoans() {
        repo.stored = new User("test", 0.0);

        InMemoryBookRepository br = new InMemoryBookRepository();
        BorrowService bs = new BorrowService(br);

        Book b = new Book(1, "X", "A", "111");
        br.add(b);

        BorrowRecord r = bs.borrowBook(repo.stored, "111",
                LocalDate.now().minusDays(40));
        r.setReturned(false);

        assertThrows(IllegalStateException.class,
                () -> userService.unregister(new User("admin", 0.0),
                        repo.stored, bs));
    }

    @Test
    void unregisterSucceedsWhenUserIsClean() {
        repo.stored = new User("test", 0.0);

        boolean ok = userService.unregister(
                new User("admin", 0.0),
                repo.stored,
                borrowService
        );

        assertTrue(ok);
        assertTrue(repo.deleted);
    }
}
