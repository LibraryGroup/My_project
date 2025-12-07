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

    // ---------------------------------------------------------
    // 1) دعم إضافة Media مباشرة (كما يطلب الاختبار addMediaShouldSave)
    // ---------------------------------------------------------
    public void add(Media media) {
        repo.save(media);
    }

    // ---------------------------------------------------------
    // 2) التست يريد findById
    // ---------------------------------------------------------
    public Media findById(int id) {
        return repo.findById(id);
    }

    // ---------------------------------------------------------
    // 3) التست يحتاج findAll
    // ---------------------------------------------------------
    public List<Media> findAll() {
        return repo.findAll();
    }

    // ---------------------------------------------------------
    // 4) findAvailableMedia (كما في التست)
    // ---------------------------------------------------------
    public List<Media> findAvailableMedia() {
        return repo.findAll().stream()
                .filter(Media::isAvailable)
                .collect(Collectors.toList());
    }

    // ---------------------------------------------------------
    // 5) searchByTitle (موجود أصلاً – فقط نتركه كما هو)
    // ---------------------------------------------------------
    public List<Media> searchByTitle(String keyword) {
        return repo.findAll().stream()
                .filter(m -> m.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    // ---------------------------------------------------------
    // 6) searchByType(Class<T>) — كما يطلب التست
    // ---------------------------------------------------------
    public <T extends Media> List<T> searchByType(Class<T> type) {
        return repo.findAll().stream()
                .filter(type::isInstance)
                .map(type::cast)
                .collect(Collectors.toList());
    }

    // ---------------------------------------------------------
    // 7) إضافة الكتب والأقراص (لا نغيّرها)
    // ---------------------------------------------------------
    public Media addBook(String title, String author, String isbn, int copies) {
        Book book = new Book(0, title, author, isbn);
        book.setTotalCopies(copies);
        book.setAvailableCopies(copies);
        repo.save(book);
        return book;
    }

    public Media addCD(String title, String artist, int copies) {
        CD cd = new CD(0, title, artist);
        cd.setTotalCopies(copies);
        cd.setAvailableCopies(copies);
        repo.save(cd);
        return cd;
    }
}
