package com.library.repository;

import com.library.model.Book;

import java.util.List;

public interface BookRepository {

    void add(Book book);

    List<Book> findAll();

    List<Book> searchByTitle(String title);

    List<Book> searchByAuthor(String author);

    Book searchByIsbn(String isbn);
}
