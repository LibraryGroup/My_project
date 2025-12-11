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

    /**
     * Borrow a media item for a user.
     */
    public BorrowRecord borrow(User user, int mediaId, LocalDate borrowDate) {

        if (user == null)
            throw new IllegalArgumentException("User is required");

        if (borrowDate == null)
            throw new IllegalArgumentException("Borrow date required");

        // Check if user has active overdue or unreturned items
        for (BorrowRecord r : getBorrowRecordsForUser(user)) {

            // Active + overdue
            if (!r.isReturned() && r.isOverdue(borrowDate)) {
                throw new IllegalStateException("User has overdue items.");
            }
        }

        // Cannot borrow if user has fines
        if (user.getFineBalance() > 0) {
            throw new IllegalStateException("User has unpaid fines.");
        }

        // Find media
        Media media = mediaRepo.findById(mediaId);
        if (media == null)
            throw new IllegalArgumentException("Media not found");

        if (!media.isAvailable())
            throw new IllegalStateException("No copies available");

        // Determine due date
        LocalDate dueDate = borrowDate.plusDays(media.getBorrowDays());

        // Create record
        BorrowRecord record = new BorrowRecord(user, media, borrowDate, dueDate);

        // Reduce available copy
        media.decreaseCopy();
        mediaRepo.save(media);

        // Save borrow record
        borrowRepo.save(record);

        return record;
    }

    /**
     * Return a borrowed media item.
     */
    public boolean returnItem(String username, int mediaId, LocalDate returnDate) {

        for (BorrowRecord r : borrowRepo.findAll()) {

            if (r.getUser().getUsername().equals(username)
                    && r.getMedia().getId() == mediaId
                    && !r.isReturned()) {

                // Restore media copy
                r.getMedia().increaseCopy();
                mediaRepo.save(r.getMedia());

                // Mark as returned
                r.setReturned(true);
                r.setReturnDate(returnDate);

                borrowRepo.update(r);
                return true;
            }
        }
        return false;
    }

    /**
     * Get all borrow records for a given user.
     */
    public List<BorrowRecord> getBorrowRecordsForUser(User user) {
        return borrowRepo.findByUser(user);
    }

    /**
     * All users that have borrow records.
     */
    public Set<User> getAllUsersWithRecords() {
        return borrowRepo.getAllUsers();
    }

    /**
     * Find all records overdue by a specific date.
     */
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
