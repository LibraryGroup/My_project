package com.library.service;

import com.library.model.BorrowRecord;
import com.library.model.User;
import com.library.repository.UserRepository;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    
    @Test
    void findUserReturnsCorrect() {
        UserRepository repo = mock(UserRepository.class);
        UserService service = new UserService(repo);

        User expected = new User("mohammad", 0);
        when(repo.findByUsername("mohammad")).thenReturn(expected);

        User actual = service.findUser("mohammad");

        assertEquals(expected, actual);
        verify(repo, times(1)).findByUsername("mohammad");
    }

    
    @Test
    void userCanBorrowWhenNoFines() {
        UserService service = new UserService(mock(UserRepository.class));

        User user = new User("ali", 0.0);

        assertTrue(service.canBorrow(user));
    }

    @Test
    void userCannotBorrowWhenHasFines() {
        UserService service = new UserService(mock(UserRepository.class));

        User user = new User("ali", 50.0);

        assertFalse(service.canBorrow(user));
    }

   
    @Test
    void payFineReducesBalance() {
        UserService service = new UserService(mock(UserRepository.class));

        User user = new User("ali", 30.0);

        service.payFine(user, 10.0);

        assertEquals(20.0, user.getFineBalance());
    }

    @Test
    void payFineClearsBalanceIfAmountTooLarge() {
        UserService service = new UserService(mock(UserRepository.class));

        User user = new User("ali", 25.0);

        service.payFine(user, 100.0);

        assertEquals(0.0, user.getFineBalance());
    }

    @Test
    void payFineRejectsZeroOrNegative() {
        UserService service = new UserService(mock(UserRepository.class));
        User user = new User("ali", 20.0);

        assertThrows(IllegalArgumentException.class,
                () -> service.payFine(user, 0));

        assertThrows(IllegalArgumentException.class,
                () -> service.payFine(user, -5));
    }

    
    @Test
    void unregisterFailsIfNotAdmin() {
        UserRepository repo = mock(UserRepository.class);
        BorrowService borrowService = mock(BorrowService.class);

        UserService service = new UserService(repo);

        User notAdmin = new User("someone", 0);
        User target = new User("x", 0);

        assertThrows(SecurityException.class,
                () -> service.unregister(notAdmin, target, borrowService));
    }

    @Test
    void unregisterFailsWhenUserHasFines() {
        UserRepository repo = mock(UserRepository.class);
        BorrowService borrowService = mock(BorrowService.class);

        User target = new User("x", 20.0);
        when(repo.findByUsername("x")).thenReturn(target);

        UserService service = new UserService(repo);

        User admin = new User("admin", 0);

        assertFalse(service.unregister(admin, target, borrowService));
    }

    @Test
    void unregisterFailsWhenHasActiveLoans() {
        UserRepository repo = mock(UserRepository.class);
        BorrowService borrowService = mock(BorrowService.class);

        User target = new User("x", 0);
        when(repo.findByUsername("x")).thenReturn(target);

        BorrowRecord active = mock(BorrowRecord.class);
        when(active.isReturned()).thenReturn(false);

        when(borrowService.getBorrowRecordsForUser(target))
                .thenReturn(List.of(active));

        UserService service = new UserService(repo);

        User admin = new User("admin", 0);

        assertFalse(service.unregister(admin, target, borrowService));
    }

    @Test
    void unregisterFailsWhenOverdue() {
        UserRepository repo = mock(UserRepository.class);
        BorrowService borrowService = mock(BorrowService.class);

        User target = new User("x", 0);
        when(repo.findByUsername("x")).thenReturn(target);

        BorrowRecord overdue = mock(BorrowRecord.class);
        when(overdue.isReturned()).thenReturn(true);
        when(overdue.isOverdue(any(LocalDate.class))).thenReturn(true);

        when(borrowService.getBorrowRecordsForUser(target))
                .thenReturn(List.of(overdue));

        UserService service = new UserService(repo);
        User admin = new User("admin", 0);

        assertThrows(IllegalStateException.class, () ->
                service.unregister(admin, target, borrowService));
    }

    @Test
    void unregisterSucceeds() {
        UserRepository repo = mock(UserRepository.class);
        BorrowService borrowService = mock(BorrowService.class);

        User target = new User("x", 0);
        when(repo.findByUsername("x")).thenReturn(target);
        when(borrowService.getBorrowRecordsForUser(target))
                .thenReturn(List.of());
        when(repo.deleteUser("x")).thenReturn(true);

        UserService service = new UserService(repo);
        User admin = new User("admin", 0);

        boolean ok = service.unregister(admin, target, borrowService);

        assertTrue(ok);
        verify(repo).deleteUser("x");
    }
}
