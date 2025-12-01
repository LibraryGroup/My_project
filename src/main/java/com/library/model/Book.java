package com.library.model;

import com.library.fines.BookFineStrategy;

public class Book extends Media {

    private String author;
    private String isbn;

    public Book(int id, String title, String author, String isbn) {
        super(id, title); 
        this.author = author;
        this.isbn = isbn;
    }

    @Override
    public int getBorrowDays() {
        return 28; 
    }

    
    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    
    public void setAuthor(String author) {
        this.author = author;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    
    @Override
    public void setId(int id) {
        super.setId(id);
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", available=" + isAvailable() +
                '}';
    }
}
