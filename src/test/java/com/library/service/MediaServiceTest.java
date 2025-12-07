package com.library.service;

import com.library.model.Media;
import com.library.model.Book;
import com.library.repository.MediaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MediaServiceTest {

    private MediaRepository repo;
    private MediaService service;

    @BeforeEach
    void setUp() {
        repo = mock(MediaRepository.class);
        service = new MediaService(repo);
    }

    @Test
    void addMediaShouldSaveToRepository() {
        Media m = new Book(1, "Clean Code", "Robert Martin", "111");

        service.add(m);

        verify(repo, times(1)).save(m);
    }

    @Test
    void findByIdShouldReturnMedia() {
        Media m = new Book(1, "Clean Code", "Robert Martin", "111");

        when(repo.findById(1)).thenReturn(m);

        Media result = service.findById(1);

        assertNotNull(result);
        assertEquals("Clean Code", result.getTitle());
    }

    @Test
    void findByIdShouldReturnNullWhenNotFound() {
        when(repo.findById(999)).thenReturn(null);

        Media result = service.findById(999);

        assertNull(result);
    }

    @Test
    void findAllShouldReturnAllMedia() {
        Media m1 = new Book(1, "Clean Code", "Robert Martin", "111");
        Media m2 = new Book(2, "Effective Java", "Joshua Bloch", "222");

        when(repo.findAll()).thenReturn(List.of(m1, m2));

        List<Media> result = service.findAll();

        assertEquals(2, result.size());
        assertTrue(result.contains(m1));
        assertTrue(result.contains(m2));
    }

    @Test
    void findAvailableShouldReturnOnlyAvailableMedia() {
        Media m1 = mock(Media.class);
        when(m1.isAvailable()).thenReturn(true);

        Media m2 = mock(Media.class);
        when(m2.isAvailable()).thenReturn(false);

        when(repo.findAll()).thenReturn(List.of(m1, m2));

        List<Media> result = service.findAvailableMedia();

        assertEquals(1, result.size());
        assertTrue(result.contains(m1));
    }

    @Test
    void searchByTitleShouldReturnMatchingMedia() {
        Media m1 = new Book(1, "Clean Code", "Robert Martin", "111");
        Media m2 = new Book(2, "Java Concurrency", "Brian Goetz", "333");

        when(repo.findAll()).thenReturn(List.of(m1, m2));

        List<Media> result = service.searchByTitle("java");

        assertEquals(1, result.size());
        assertEquals("Java Concurrency", result.get(0).getTitle());
    }

    @Test
    void searchByTitleShouldReturnEmptyListIfNoMatch() {
        Media m1 = new Book(1, "Clean Code", "Robert Martin", "111");

        when(repo.findAll()).thenReturn(List.of(m1));

        List<Media> result = service.searchByTitle("python");

        assertTrue(result.isEmpty());
    }

    @Test
    void searchByTypeShouldReturnOnlyBooks() {
        Media m1 = new Book(1, "Clean Code", "Robert Martin", "111");
        Media m2 = mock(Media.class); // أي نوع آخر

        when(repo.findAll()).thenReturn(List.of(m1, m2));

        List<? extends Media> result = service.searchByType(Book.class);
        assertEquals(1, result.size());

       
        assertEquals(m1, result.get(0));
    }
    @Test
    void addBookShouldCreateBookWithCopies() {
        MediaRepository repo = mock(MediaRepository.class);
        MediaService service = new MediaService(repo);

        Media m = service.addBook("Clean", "Martin", "111", 5);

        assertEquals("Clean", m.getTitle());
        assertEquals(5, m.getTotalCopies());
        verify(repo).save(any());
    }

    @Test
    void addCDShouldCreateCDWithCopies() {
        MediaRepository repo = mock(MediaRepository.class);
        MediaService service = new MediaService(repo);

        Media m = service.addCD("Hits", "Artist", 3);

        assertEquals("Hits", m.getTitle());
        verify(repo).save(any());
    }

}
