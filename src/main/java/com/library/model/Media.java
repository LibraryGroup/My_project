package com.library.model;

import com.library.fines.FineStrategy;

public abstract class Media {

    protected int id;
    protected String title;

   
    protected int totalCopies = 1;        
    protected int availableCopies = 1;    
    protected FineStrategy fineStrategy;

    protected Media(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }

    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
        if (availableCopies > totalCopies) {
            availableCopies = totalCopies;
        }
    }

    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }

   
    public boolean isAvailable() {
        return availableCopies > 0;
    }

    public void decreaseCopy() {
        if (availableCopies <= 0)
            throw new IllegalStateException("No available copies left");
        availableCopies--;
    }

    public void increaseCopy() {
        if (availableCopies < totalCopies)
            availableCopies++;
    }

   
    public FineStrategy getFineStrategy() { return fineStrategy; }
    public void setFineStrategy(FineStrategy fineStrategy) { this.fineStrategy = fineStrategy; }

    
    public abstract int getBorrowDays();
}
