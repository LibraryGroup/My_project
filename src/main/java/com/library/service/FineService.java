package com.library.service;

import com.library.model.BorrowRecord;
import com.library.model.User;

import java.time.LocalDate;
import java.util.List;

public class FineService {

    private final com.library.repository.UserRepository userRepo;

    public FineService(com.library.repository.UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    
    public double calculateTotalFine(List<BorrowRecord> records, LocalDate today) {
        double total = 0.0;

        for (BorrowRecord r : records) {
            if (r.isOverdue(today)) {
                int overdueDays = r.getOverdueDays(today);

                
                total += r.getMedia().getFineStrategy()
                        .calculateFine(overdueDays);
            }
        }
        return total;
    }

    
    public boolean payFine(String username, double amount) {
        User user = userRepo.findByUsername(username);
        if (user == null) return false;

        if (amount < user.getFineBalance()) {
            return false;
        }

        user.setFineBalance(0.0);
        userRepo.save(user);
        return true;
    }
}
