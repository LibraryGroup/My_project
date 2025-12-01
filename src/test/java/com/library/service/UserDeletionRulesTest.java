package com.library.service;

import com.library.model.*;
import com.library.repository.*;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserDeletionRulesTest {

    @Test
    void cannotDeleteUserWithOverdue() {

        BorrowRepository borrowRepo = mock(BorrowRepository.class);
        MediaRepository mediaRepo = mock(MediaRepository.class);

        BorrowService borrowService = new BorrowService(mediaRepo, borrowRepo);

        UserRepository userRepo = mock(UserRepository.class);
        UserService userService = new UserService(userRepo);

        User target = new User("test", 0);

        BorrowRecord r = mock(BorrowRecord.class);
        when(r.isReturned()).thenReturn(false);
        when(r.isOverdue(any())).thenReturn(true);

        when(borrowRepo.findByUser(target)).thenReturn(List.of(r));

        assertThrows(IllegalStateException.class,
                () -> userService.unregister(
                        new User("admin", 0),
                        target,
                        borrowService
                ));
    }

    @Test
    void cannotDeleteUserWithFines() {
        UserService userService = new UserService(mock(UserRepository.class));

        User admin = new User("admin", 0);
        User target = new User("x", 50);

        assertThrows(IllegalStateException.class,
                () -> userService.unregister(admin, target, null));
    }

    @Test
    void deleteSucceedsWhenNoIssues() {

        UserRepository repo = mock(UserRepository.class);
        UserService userService = new UserService(repo);

        User admin = new User("admin", 0);
        User target = new User("john", 0);

        BorrowRepository borrowRepo = mock(BorrowRepository.class);
        MediaRepository mediaRepo = mock(MediaRepository.class);
        BorrowService borrowService = new BorrowService(mediaRepo, borrowRepo);

        when(borrowRepo.findByUser(target)).thenReturn(new ArrayList<>());

        boolean ok = userService.unregister(admin, target, borrowService);

        assertTrue(ok);
        verify(repo, times(1)).deleteUser("john");
    }
}

