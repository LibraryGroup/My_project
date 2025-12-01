package com.library.model;

import com.library.fines.FineStrategy;

public abstract class Media {

    protected int id;
    protected String title;
    protected boolean available = true;

    protected FineStrategy fineStrategy;

    public Media(int id, String title) {
        this.id = id;
        this.title = title;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {  
        this.id = id;
    }

   
    public String getTitle() {
        return title;
    }

    
    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    
    public FineStrategy getFineStrategy() {
        return fineStrategy;
    }

    public void setFineStrategy(FineStrategy fineStrategy) {
        this.fineStrategy = fineStrategy;
    }

    
    public abstract int getBorrowDays();
}

