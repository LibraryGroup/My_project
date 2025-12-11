package com.library.repository;

import com.library.model.User;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileUserRepositoryTest {

    private Path tempFile;
    private FileUserRepository repo;

    @BeforeEach
    void init() throws IOException {
        tempFile = Files.createTempFile("user_test", ".txt");
        repo = new FileUserRepository(tempFile.toAbsolutePath().toString());
    }

    @AfterEach
    void clean() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    void saveAndFindUserShouldWork() {
        User u = new User("moh", 10);
        repo.save(u);

        User found = repo.findByUsername("moh");
        assertNotNull(found, "User should be found after saving");
        assertEquals(10, found.getFineBalance(), "Fine balance should match");
    }

    @Test
    void deleteUserShouldRemoveUser() {
        repo.save(new User("moh", 0));

        assertTrue(repo.deleteUser("moh"), "User deletion should return true");
        assertNull(repo.findByUsername("moh"), "Deleted user should not be found");
    }
}

