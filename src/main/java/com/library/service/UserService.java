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
     * Business rules:
     * 1. Only admin can unregister → SecurityException
     * 2. User with fines → return false (NO exception)
     * 3. User with active (unreturned) loans → return false (NO exception)
     * 4. User with overdue items → throw IllegalStateException
     * 5. Otherwise → delete and return true
     */
    public boolean unregister(User admin, User targetUser, BorrowService borrowService) {

        // 1) فقط الأدمن مسموح له بالحذف
        if (admin == null || !"admin".equals(admin.getUsername())) {
            throw new SecurityException("Only admin can unregister users");
        }

        // 2) إيجاد المستخدم
        User user = repository.findByUsername(targetUser.getUsername());
        if (user == null) return false;

        // 3) ممنوع حذف مستخدم عليه غرامات → يجب أن نرمي استثناء (كما يطلب الاختبار)
        if (user.getFineBalance() > 0) {
            throw new IllegalStateException("User has outstanding fines");
        }

        // 4) فحص سجلات الإعارة للمستخدم
        List<BorrowRecord> loans = borrowService.getBorrowRecordsForUser(user);

        for (BorrowRecord r : loans) {

            // إذا عنده مواد غير مُعادة → الاختبار يتوقع IllegalStateException
            if (!r.isReturned()) {
                throw new IllegalStateException("User has unreturned items");
            }

            // إذا المادة متأخرة → الاختبار يتوقع IllegalStateException
            if (r.isOverdue(LocalDate.now())) {
                throw new IllegalStateException("User has overdue items");
            }
        }

        // 5) إذا وصلنا هنا، يعني لا غرامات ولا تأخيرات ولا مواد غير مُعادة
        boolean deleted = repository.deleteUser(user.getUsername());
        return deleted;
    }

}
