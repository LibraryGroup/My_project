package com.library.service;

import com.library.model.User;
import com.library.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FineServiceTest {

    private FineService fineService;
    private FakeUserRepository repo;

    
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
    void payFineWithAmountLessThanBalanceShouldFailAndKeepBalance() {
        boolean ok = fineService.payFine("ahmed", 20.0);

        assertFalse(ok);
        
        assertEquals(50.0, repo.store.getFineBalance(), 0.001);
        
        assertNull(repo.saved);
    }

    @Test
    void payFineWithExactBalanceShouldSucceedAndSetBalanceToZero() {
        boolean ok = fineService.payFine("ahmed", 50.0);

        assertTrue(ok);
        assertNotNull(repo.saved);
        assertEquals(0.0, repo.saved.getFineBalance(), 0.001);
    }

    @Test
    void payFineWithMoreThanBalanceShouldAlsoClearBalance() {
        boolean ok = fineService.payFine("ahmed", 100.0);

        assertTrue(ok);
        assertNotNull(repo.saved);
        assertEquals(0.0, repo.saved.getFineBalance(), 0.001);
    }

    @Test
    void payFineForUnknownUserShouldFail() {
        repo.store = null;  

        boolean ok = fineService.payFine("unknown", 50.0);

        assertFalse(ok);
        assertNull(repo.saved);
    }
}
