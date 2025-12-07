package com.library.model;

import com.library.fines.FineStrategy;

public abstract class Media {

    protected int id;
    protected String title;

    // ======== New Multiple Copies Fields ========
    protected int totalCopies = 1;        // عدد النسخ الكلي
    protected int availableCopies = 1;    // عدد النسخ المتاحة للاستعارة

    protected FineStrategy fineStrategy;

    public Media(int id, String title) {
        this.id = id;
        this.title = title;
    }

    // ===========================
    // Getters & Setters
    // ===========================

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }

    // ----- NEW -----
    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;

        // إذا زاد المجموع يجب تعديل المتاح أيضاً
        if (availableCopies > totalCopies) {
            availableCopies = totalCopies;
        }
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }

    // ===========================
    // Availability Logic
    // ===========================

    public boolean isAvailable() {
        return availableCopies > 0;
    }

    // عند الاستعارة ↓↓
    public void decreaseCopy() {
        if (availableCopies <= 0)
            throw new IllegalStateException("No available copies left");
        availableCopies--;
    }

    // عند الإرجاع ↓↓
    public void increaseCopy() {
        if (availableCopies < totalCopies)
            availableCopies++;
    }

    // ===========================
    // Fine Strategy
    // ===========================

    public FineStrategy getFineStrategy() {
        return fineStrategy;
    }

    public void setFineStrategy(FineStrategy fineStrategy) {
        this.fineStrategy = fineStrategy;
    }

    // Borrow period (book: 14 days, CD: 7 days)
    public abstract int getBorrowDays();
}
