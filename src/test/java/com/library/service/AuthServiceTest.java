package com.library.service;

import com.library.model.Admin;
import com.library.repository.AdminRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    private AuthService authService;

    private static class FakeAdminRepository implements AdminRepository {

        @Override
        public Admin findByUsername(String username) {
            if ("admin".equals(username)) {
                return new Admin("admin", "1234");
            }
            return null;
        }
    }

    @BeforeEach
    void setUp() {
        authService = new AuthService(new FakeAdminRepository());
    }

    @Test
    void loginWithValidCredentialsShouldSucceed() {
        boolean result = authService.login("admin", "1234");

        assertTrue(result);
        assertTrue(authService.isLoggedIn());
        assertNotNull(authService.getCurrentAdmin());
    }

    @Test
    void loginWithInvalidPasswordShouldFail() {
        boolean result = authService.login("admin", "wrong");

        assertFalse(result);
        assertFalse(authService.isLoggedIn());
    }

    @Test
    void loginWithUnknownUserShouldFail() {
        boolean result = authService.login("someone", "1234");

        assertFalse(result);
        assertFalse(authService.isLoggedIn());
    }

    @Test
    void logoutShouldClearCurrentAdmin() {
        authService.login("admin", "1234");

        assertTrue(authService.isLoggedIn());

        authService.logout();

        assertFalse(authService.isLoggedIn());
        assertNull(authService.getCurrentAdmin());
    }
}
