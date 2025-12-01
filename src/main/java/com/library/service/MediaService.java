package com.library.service;

import com.library.model.Book;
import com.library.model.CD;
import com.library.model.Media;
import com.library.repository.MediaRepository;

import java.util.List;
import java.util.stream.Collectors;

public class MediaService {

    private final MediaRepository repo;

    public MediaService(MediaRepository repo) {
        this.repo = repo;
    }

    public Media addBook(String title, String author, String isbn) {
        Book book = new Book(0, title, author, isbn);
        repo.save(book);
        return book;
    }

    public Media addCD(String title, String artist) {
        CD cd = new CD(0, title, artist);
        repo.save(cd);
        return cd;
    }

    public List<Media> searchByTitle(String keyword) {
        return repo.findAll().stream()
                .filter(m -> m.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }
}
