package com.library.repository;

import com.library.model.Admin;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class FileAdminRepositoryTest {

    private File tempFile;
    private FileAdminRepository repo;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("admins_test", ".txt");

        // كتابة بيانات تجريبية للملف
        Files.writeString(tempFile.toPath(),
                "admin,1234\n" +
                "user1,pass1\n");

        repo = new FileAdminRepository(tempFile.getAbsolutePath());
    }

    @AfterEach
    void tearDown() {
        tempFile.delete();
    }

    @Test
    void findByUsernameShouldReturnCorrectAdmin() {
        Admin a = repo.findByUsername("admin");

        assertNotNull(a);
        assertEquals("admin", a.getUsername());
        assertEquals("1234", a.getPassword());
    }

    @Test
    void findByUsernameShouldReturnNullWhenNotFound() {
        Admin a = repo.findByUsername("not_exists");

        assertNull(a);
    }

    @Test
    void repositoryShouldLoadAllAdminsFromFile() {
        Admin a1 = repo.findByUsername("admin");
        Admin a2 = repo.findByUsername("user1");

        assertNotNull(a1);
        assertNotNull(a2);
        assertEquals("pass1", a2.getPassword());
    }

    @Test
    void fileShouldHandleEmptyFileSafely() throws IOException {
        File emptyFile = File.createTempFile("empty_admin", ".txt");

        FileAdminRepository emptyRepo = new FileAdminRepository(emptyFile.getAbsolutePath());

        assertNull(emptyRepo.findByUsername("anything"));
        emptyFile.delete();
    }
}
