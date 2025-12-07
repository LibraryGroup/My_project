package com.library.service;

import com.library.model.BorrowRecord;
import com.library.model.User;
import com.library.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User findUser(String username) {
        return repository.findByUsername(username);
    }

    public boolean canBorrow(User user) {
        return user.getFineBalance() <= 0.0;
    }

    public void payFine(User user, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        double current = user.getFineBalance();
        if (amount >= current) {
            user.setFineBalance(0.0);
        } else {
            user.setFineBalance(current - amount);
        }
    }

    /**
     * Business rules aligned with test expectations:
     * 1) Only admin can unregister → SecurityException
     * 2) User with fines → IllegalStateException
     * 3) User with unreturned loans → return false
     * 4) User with overdue items → IllegalStateException
     * 5) Otherwise delete → return true
     */
    public boolean unregister(User admin, User targetUser, BorrowService borrowService) {

        // Only admin allowed
        if (admin == null || !"admin".equals(admin.getUsername())) {
            throw new SecurityException("Only admin can unregister users");
        }

        User user = repository.findByUsername(targetUser.getUsername());
        if (user == null) return false;

        // (2) User has fines → Exception (tests expect this!)
        if (user.getFineBalance() > 0) {
            throw new IllegalStateException("User has outstanding fines");
        }

        List<BorrowRecord> loans = borrowService.getBorrowRecordsForUser(user);

        for (BorrowRecord r : loans) {

            // (3) Active loan → return false
            if (!r.isReturned()) {
                return false;
            }

            // (4) Overdue → Exception
            if (r.isOverdue(LocalDate.now())) {
                throw new IllegalStateException("User has overdue items");
            }
        }

        // (5) Success → delete user
        return repository.deleteUser(user.getUsername());
    }
}
