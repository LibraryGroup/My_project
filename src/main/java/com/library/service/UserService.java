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

    // ======================================================
    // Sprint 4 — Unregister user (Final Merged Logic)
    // ======================================================
    public boolean unregister(User admin, User targetUser, BorrowService borrowService) {

        // -------------------------------
        // Admin only
        // -------------------------------
        if (admin == null || !"admin".equals(admin.getUsername())) {
            throw new SecurityException("Only admin can unregister users");
        }

        // Load fresh user from repository
        User user = repository.findByUsername(targetUser.getUsername());
        if (user == null) return false;

        // -------------------------------
        // Cannot unregister if user has unpaid fines
        // -------------------------------
        if (user.getFineBalance() > 0) {
            return false; // لديه غرامات غير مدفوعة
        }

        // -------------------------------
        // Cannot unregister if user has active loans
        // -------------------------------
        List<BorrowRecord> loans = borrowService.getBorrowRecordsForUser(user);

        for (BorrowRecord r : loans) {
            // Active loan = not returned
            if (!r.isReturned()) {
                return false;   // لديه إعارات فعّالة
            }

            // Also: overdue check
            if (r.isOverdue(LocalDate.now())) {
                throw new IllegalStateException("User has overdue loans");
            }
        }

        // -------------------------------
        // Finally delete user
        // -------------------------------
        return repository.deleteUser(user.getUsername());
    }
}

