package com.library.model;

import java.time.LocalDate;

public class BorrowRecord {

    private final User user;
    private final Media media;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;

    private boolean returned = false;
    private LocalDate returnDate;

    public BorrowRecord(User user, Media media, LocalDate borrowDate, LocalDate dueDate) {
        this.user = user;
        this.media = media;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
    }

    public User getUser() {
        return user;
    }

    public Media getMedia() {
        return media;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

   
    public int getOverdueDays(LocalDate today) {
        if (returned) return 0;
        if (!today.isAfter(dueDate)) return 0;
        return (int) java.time.temporal.ChronoUnit.DAYS.between(dueDate, today);
    }

   
    public boolean isOverdue(LocalDate today) {
        return getOverdueDays(today) > 0;
    }

    @Override
    public String toString() {
        return "BorrowRecord{" +
                "user=" + user.getUsername() +
                ", media=" + media.getTitle() +
                ", borrowDate=" + borrowDate +
                ", dueDate=" + dueDate +
                ", returned=" + returned +
                ", returnDate=" + returnDate +
                '}';
    }
}
