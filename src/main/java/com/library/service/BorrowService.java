package com.library.service;

import com.library.model.Book;
import com.library.model.BorrowRecord;
import com.library.model.User;
import com.library.repository.BookRepository;

import java.time.LocalDate;
import java.util.*;

public class BorrowService {

    private final BookRepository bookRepository;
    private final Map<String, List<BorrowRecord>> userBorrows = new HashMap<>();

    public BorrowService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public BorrowRecord borrowBook(User user, String isbn, LocalDate borrowDate) {

        if (user == null)
            throw new IllegalArgumentException("User is required");

        if (borrowDate == null)
            throw new IllegalArgumentException("Borrow date required");

        // -----------------------------------------
        // Sprint 4 Logic: Check active borrowed books
        // -----------------------------------------
        List<BorrowRecord> records = userBorrows.get(user.getUsername());
        if (records != null) {
            for (BorrowRecord r : records) {
                // إذا كان السجل غير مُرجع فهو إعارة فعالة active loan
                if (!r.isReturned()) {

                    // إذا الإعارة الفعالة نفسها متأخرة → منع الاقتراض
                    if (r.isOverdue(borrowDate)) {
                        throw new IllegalStateException("User has overdue books.");
                    }
                }
            }
        }

        // -----------------------------------------
        // Sprint 4 Logic: Unpaid fines
        // -----------------------------------------
        if (user.getFineBalance() > 0) {
            throw new IllegalStateException("User has unpaid fines.");
        }

        // -----------------------------------------
        // Book validation
        // -----------------------------------------
        Book book = bookRepository.searchByIsbn(isbn);
        if (book == null)
            throw new IllegalArgumentException("Book not found");

        if (!book.isAvailable())
            throw new IllegalStateException("Book already borrowed");

        // -----------------------------------------
        // Borrow logic
        // -----------------------------------------
        book.setAvailable(false);
        LocalDate due = borrowDate.plusDays(28);

        BorrowRecord record = new BorrowRecord(user, book, borrowDate, due);

        userBorrows
                .computeIfAbsent(user.getUsername(), k -> new ArrayList<>())
                .add(record);

        return record;
    }

    // Get all records for a specific user
    public List<BorrowRecord> getBorrowRecordsForUser(User user) {
        List<BorrowRecord> list = userBorrows.get(user.getUsername());
        if (list == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(list);
    }

    // Find all overdue records
    public List<BorrowRecord> findOverdueRecords(LocalDate date) {
        List<BorrowRecord> result = new ArrayList<>();
        for (List<BorrowRecord> list : userBorrows.values()) {
            for (BorrowRecord r : list) {
                if (r.isOverdue(date)) {
                    result.add(r);
                }
            }
        }
        return result;
    }

    // Test helper: access records for specific user
    List<BorrowRecord> _testGetRecords(String username) {
        return userBorrows.computeIfAbsent(username, k -> new ArrayList<>());
    }
}
