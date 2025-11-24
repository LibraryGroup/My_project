package com.library.service;

import com.library.model.Book;
import com.library.repository.BookRepository;

import java.util.List;

public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public Book addBook(String title, String author, String isbn) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author is required");
        }
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN is required");
        }

        Book book = new Book(0, title.trim(), author.trim(), isbn.trim());
        repository.add(book);
        return book;
    }

    public List<Book> searchByTitle(String title) {
        return repository.searchByTitle(title);
    }

    public List<Book> searchByAuthor(String author) {
        return repository.searchByAuthor(author);
    }

    public Book searchByIsbn(String isbn) {
        return repository.searchByIsbn(isbn);
    }

    public List<Book> findAll() {
        return repository.findAll();
    }
}
