package com.library.model;

import com.library.fines.CDFineStrategy;

public class CD extends Media {

    private String artist;

    public CD(int id, String title, String artist) {
        super(id, title);
        this.artist = artist;
        this.fineStrategy = new CDFineStrategy();
    }

    @Override
    public int getBorrowDays() {
        return 7;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    @Override
    public String toString() {
        return "CD{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", artist='" + artist + '\'' +
                ", totalCopies=" + totalCopies +
                ", availableCopies=" + availableCopies +
                '}';
    }
}
