package com.library.service;

import com.library.model.BorrowRecord;
import com.library.model.Media;
import com.library.model.User;
import com.library.repository.BorrowRepository;
import com.library.repository.MediaRepository;

import java.time.LocalDate;
import java.util.*;

public class BorrowService {

    private final MediaRepository mediaRepo;
    private final BorrowRepository borrowRepo;

    public BorrowService(MediaRepository mediaRepo, BorrowRepository borrowRepo) {
        this.mediaRepo = mediaRepo;
        this.borrowRepo = borrowRepo;
    }

    public BorrowRecord borrow(User user, int mediaId, LocalDate borrowDate) {

        if (user == null)
            throw new IllegalArgumentException("User is required");

        if (borrowDate == null)
            throw new IllegalArgumentException("Borrow date required");

        for (BorrowRecord r : getBorrowRecordsForUser(user)) {
            if (!r.isReturned() && r.isOverdue(borrowDate)) {
                throw new IllegalStateException("User has overdue items.");
            }
        }

        if (user.getFineBalance() > 0) {
            throw new IllegalStateException("User has unpaid fines.");
        }

        Media media = mediaRepo.findById(mediaId);
        if (media == null)
            throw new IllegalArgumentException("Media not found");

        if (!media.isAvailable())
            throw new IllegalStateException("No copies available");

        // تحديد موعد الإرجاع
        LocalDate dueDate = borrowDate.plusDays(media.getBorrowDays());

        // إنشاء سجل جديد
        BorrowRecord record = new BorrowRecord(user, media, borrowDate, dueDate);

        // ↓↓ حجز نسخة ↓↓
        media.decreaseCopy();
        mediaRepo.save(media);

        // حفظ السجل
        borrowRepo.save(record);

        return record;
    }

    public boolean returnItem(String username, int mediaId, LocalDate returnDate) {

        for (BorrowRecord r : borrowRepo.findAll()) {

            if (r.getUser().getUsername().equals(username)
                    && r.getMedia().getId() == mediaId
                    && !r.isReturned()) {

                // إرجاع نسخة
                r.getMedia().increaseCopy();
                mediaRepo.save(r.getMedia());

                r.setReturned(true);
                r.setReturnDate(returnDate);

                borrowRepo.update(r);
                return true;
            }
        }
        return false;
    }

    public List<BorrowRecord> getBorrowRecordsForUser(User user) {
        return borrowRepo.findByUser(user);
    }

    public Set<User> getAllUsersWithRecords() {
        return borrowRepo.getAllUsers();
    }

    public List<BorrowRecord> findOverdueRecords(LocalDate date) {
        List<BorrowRecord> list = new ArrayList<>();

        for (BorrowRecord r : borrowRepo.findAll()) {
            if (r.isOverdue(date)) {
                list.add(r);
            }
        }

        return list;
    }
}
