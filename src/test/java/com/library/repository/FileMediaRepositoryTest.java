package com.library.repository;

import com.library.model.Book;
import com.library.model.Media;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileMediaRepositoryTest {

    private File tempFile;
    private FileMediaRepository repo;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("media_test", ".txt");
        repo = new FileMediaRepository(tempFile.getAbsolutePath());
    }

    @AfterEach
    void tearDown() {
        tempFile.delete();
    }

    @Test
    void saveAndFindAllShouldWork() {
        Media b = new Book(1, "Clean Code", "Martin", "111");
        repo.save(b);

        List<Media> list = repo.findAll();
        assertEquals(1, list.size());
        assertEquals("Clean Code", list.get(0).getTitle());
    }

    @Test
    void findByIdShouldReturnCorrectMedia() {
        Media b = new Book(10, "Java", "Josh", "222");
        repo.save(b);

        Media result = repo.findById(10);
        assertNotNull(result);
        assertEquals("Java", result.getTitle());
    }
}
