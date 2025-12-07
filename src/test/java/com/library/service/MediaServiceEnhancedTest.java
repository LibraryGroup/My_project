package com.library.service;

import com.library.model.*;
import com.library.repository.MediaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MediaServiceEnhancedTest {

    private MediaRepository repo;
    private MediaService service;

    @BeforeEach
    void setup() {
        repo = mock(MediaRepository.class);
        service = new MediaService(repo);
    }

    @Test
    void addShouldCallRepositorySave() {
        Media m = new Book(1, "Clean Code", "Martin", "111");
        service.add(m);
        verify(repo).save(m);
    }

    @Test
    void findByIdReturnsCorrectMedia() {
        Media m = new Book(1, "Clean Code", "Martin", "111");
        when(repo.findById(1)).thenReturn(m);

        assertEquals(m, service.findById(1));
    }

    @Test
    void findAllReturnsList() {
        when(repo.findAll()).thenReturn(List.of(
                new Book(1, "A", "B", "C"),
                new CD(2, "X", "Y")
        ));

        assertEquals(2, service.findAll().size());
    }

    @Test
    void findAvailableReturnsOnlyAvailable() {
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
    void searchByTypeReturnsOnlyBooks() {
        Media m1 = new Book(1, "Clean Code", "Martin", "111");
        Media m2 = new CD(2, "Hits", "Adele");

        when(repo.findAll()).thenReturn(List.of(m1, m2));

        List<Book> result = service.searchByType(Book.class);

        assertEquals(1, result.size());
        assertEquals(m1, result.get(0));
    }

    @Test
    void addBookShouldCreateBookCorrectly() {
        service.addBook("Clean Code", "Martin", "111", 3);

        verify(repo, times(1)).save(any(Book.class));
    }

    @Test
    void addCDShouldCreateCDCorrectly() {
        service.addCD("Hits", "Adele", 2);

        verify(repo, times(1)).save(any(CD.class));
    }
}
