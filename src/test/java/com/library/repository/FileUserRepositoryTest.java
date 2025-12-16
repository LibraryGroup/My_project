package com.library.repository;

import com.library.model.User;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class FileUserRepositoryTest {

    private File temp;
    private FileUserRepository repo;

    @BeforeEach
    void init() throws IOException {
        temp = File.createTempFile("user_test", ".txt");
        repo = new FileUserRepository(temp.getAbsolutePath());
    }

    @AfterEach
    void clean() throws IOException {
        Files.delete(temp.toPath());
    }

    @Test
    void saveAndFindUserShouldWork() {
        User u = new User("moh", 10);
        repo.save(u);

        User found = repo.findByUsername("moh");
        assertNotNull(found);
        assertEquals(10, found.getFineBalance());
    }

    @Test
    void deleteUserShouldRemoveUser() {
        repo.save(new User("moh", 0));

        assertTrue(repo.deleteUser("moh"));
        assertNull(repo.findByUsername("moh"));
    }
}

