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
        if (user == null) {
            throw new IllegalArgumentException("User is required");
        }
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN is required");
        }
        if (borrowDate == null) {
            throw new IllegalArgumentException("Borrow date is required");
        }
        if (user.getFineBalance() > 0.0) {
            throw new IllegalStateException("User has outstanding fine and cannot borrow");
        }

        Book book = bookRepository.searchByIsbn(isbn);
        if (book == null) {
            throw new IllegalArgumentException("Book not found with ISBN: " + isbn);
        }
        if (!book.isAvailable()) {
            throw new IllegalStateException("Book is already borrowed");
        }

        book.setAvailable(false);

        LocalDate dueDate = borrowDate.plusDays(28);
        BorrowRecord record = new BorrowRecord(user, book, borrowDate, dueDate);

        userBorrows
                .computeIfAbsent(user.getUsername(), k -> new ArrayList<>())
                .add(record);

        return record;
    }

    public List<BorrowRecord> getBorrowRecordsForUser(User user) {
        List<BorrowRecord> list = userBorrows.get(user.getUsername());
        if (list == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(list);
    }

    /** لاستخدام أمين المكتبة لفحص كل الكتب المتأخرة */
    public List<BorrowRecord> findOverdueRecords(LocalDate currentDate) {
        List<BorrowRecord> result = new ArrayList<>();
        for (List<BorrowRecord> records : userBorrows.values()) {
            for (BorrowRecord record : records) {
                if (record.isOverdue(currentDate)) {
                    result.add(record);
                }
            }
        }
        return result;
    }
}
