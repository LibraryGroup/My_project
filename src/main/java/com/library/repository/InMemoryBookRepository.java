package com.library.repository;

import com.library.model.Book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InMemoryBookRepository implements BookRepository {

    private final List<Book> books = new ArrayList<>();
    private int nextId = 1;

    @Override
    public void add(Book book) {
        if (book.getId() == 0) {
            book.setId(nextId++);
        }
        books.add(book);
    }

    @Override
    public List<Book> findAll() {
        return Collections.unmodifiableList(books);
    }

    @Override
    public List<Book> searchByTitle(String title) {
        List<Book> result = new ArrayList<>();
        String q = title.toLowerCase();
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(q)) {
                result.add(book);
            }
        }
        return result;
    }

    @Override
    public List<Book> searchByAuthor(String author) {
        List<Book> result = new ArrayList<>();
        String q = author.toLowerCase();
        for (Book book : books) {
            if (book.getAuthor().toLowerCase().contains(q)) {
                result.add(book);
            }
        }
        return result;
    }

    @Override
    public Book searchByIsbn(String isbn) {
        for (Book book : books) {
            if (book.getIsbn().equalsIgnoreCase(isbn)) {
                return book;
            }
        }
        return null;
    }
}
