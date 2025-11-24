package com.library.service;

import com.library.model.User;
import com.library.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FineServiceTest {

    private FineService fineService;
    private FakeUserRepository repo;

    // Fake repo
    static class FakeUserRepository implements UserRepository {
        User saved;
        User store;

        @Override
        public User findByUsername(String username) {
            return store;
        }

        @Override
        public void save(User user) {
            this.saved = user;
            this.store = user;
        }

        @Override
        public boolean deleteUser(String username) {
            return false;
        }
    }

    @BeforeEach
    void setUp() {
        repo = new FakeUserRepository();
        repo.store = new User("ahmed", 50.0);
        fineService = new FineService(repo);
    }

    @Test
    void addFineIncreasesBalance() {
        fineService.addFine("ahmed", 20.0);
        assertEquals(70.0, repo.saved.getFineBalance(), 0.001);
    }

    @Test
    void payFinePartialReducesBalance() {
        boolean ok = fineService.payFine("ahmed", 20.0);
        assertTrue(ok);
        assertEquals(30.0, repo.saved.getFineBalance(), 0.001);
    }

    @Test
    void payFineFullMakesBalanceZero() {
        boolean ok = fineService.payFine("ahmed", 50.0);
        assertTrue(ok);
        assertEquals(0.0, repo.saved.getFineBalance(), 0.001);
    }

    @Test
    void payFineInvalidAmountFails() {
        boolean ok = fineService.payFine("ahmed", -10.0);
        assertFalse(ok);
    }

    @Test
    void hasUnpaidFinesReturnsTrue() {
        assertTrue(fineService.hasUnpaidFines("ahmed"));
    }

    @Test
    void hasUnpaidFinesFalseWhenZero() {
        repo.store.setFineBalance(0.0);
        assertFalse(fineService.hasUnpaidFines("ahmed"));
    }
}
