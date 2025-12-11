package com.library.repository;

import com.library.model.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileBorrowRepositoryTest {

    private File tempFile;
    private MediaRepository mediaRepo;
    private FileBorrowRepository repo;

    @BeforeEach
    void setup() throws Exception {

        tempFile = File.createTempFile("borrow_repo_test", ".txt");

        mediaRepo = mock(MediaRepository.class);

        // عند التحميل repository يحتاج mediaRepo.findById() → نرجع null لأن الملف فاضي
        when(mediaRepo.findById(anyInt())).thenReturn(null);

        repo = new FileBorrowRepository(tempFile.getAbsolutePath(), mediaRepo);
    }

    @AfterEach
    void cleanup() {
        tempFile.delete();
    }

    @Test
    void saveShouldWriteRecordAndFindAllShouldReturnIt() {

        User u = new User("ahmed", 0);
        Media m = new Book(1, "Clean Code", "Martin", "111");

        BorrowRecord rec = new BorrowRecord(
                u, m,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 10)
        );

        repo.save(rec);

        List<BorrowRecord> all = repo.findAll();
        assertEquals(1, all.size());
        assertEquals("ahmed", all.get(0).getUser().getUsername());
        assertEquals(1, all.get(0).getMedia().getId());
    }

    @Test
    void updateShouldRewriteFileAndPreserveData() {

        User u = new User("x", 0);
        Media m = new Book(2, "Java", "Josh", "222");

        BorrowRecord rec = new BorrowRecord(
                u, m,
                LocalDate.now(),
                LocalDate.now().plusDays(7)
        );

        repo.save(rec);

        rec.setReturned(true);
        repo.update(rec);

        List<BorrowRecord> all = repo.findAll();
        assertTrue(all.get(0).isReturned());
    }

    @Test
    void findByUserShouldReturnCorrectRecords() {

        User u1 = new User("moh", 0);
        User u2 = new User("lara", 0);

        Media m = new Book(5, "X", "Y", "Z");

        BorrowRecord r1 = new BorrowRecord(u1, m, LocalDate.now(), LocalDate.now());
        BorrowRecord r2 = new BorrowRecord(u2, m, LocalDate.now(), LocalDate.now());

        repo.save(r1);
        repo.save(r2);

        List<BorrowRecord> result = repo.findByUser(new User("moh", 0));

        assertEquals(1, result.size());
        assertEquals("moh", result.get(0).getUser().getUsername());
    }

    @Test
    void getAllUsersShouldReturnUniqueSet() {

        Media m = new Book(1, "A", "B", "C");

        repo.save(new BorrowRecord(new User("moh", 0), m, LocalDate.now(), LocalDate.now()));
        repo.save(new BorrowRecord(new User("lina", 0), m, LocalDate.now(), LocalDate.now()));

        Set<User> users = repo.getAllUsers();

        assertEquals(2, users.size());
    }
    @Test
    void loadShouldReadRecordsFromFileCorrectly() throws Exception {

        String content =
                "moh,10,2025-01-01,2025-01-10,false,null\n";

        Files.writeString(tempFile.toPath(), content);

        // إنشاء Media حقيقية وليس mock
        Media realMedia = new Book(10, "Loaded Book", "Author", "ISBN");

        when(mediaRepo.findById(10)).thenReturn(realMedia);

        // إعادة تحميل repo (load() تعمل الآن بدون مشاكل)
        FileBorrowRepository loadedRepo =
                new FileBorrowRepository(tempFile.getAbsolutePath(), mediaRepo);

        List<BorrowRecord> list = loadedRepo.findAll();

        assertEquals(1, list.size());

        BorrowRecord r = list.get(0);

        assertEquals("moh", r.getUser().getUsername());
        assertEquals(10, r.getMedia().getId());
        assertEquals(LocalDate.of(2025, 1, 1), r.getBorrowDate());
        assertEquals(LocalDate.of(2025, 1, 10), r.getDueDate());
        assertFalse(r.isReturned());

        // التأكد أن decreaseCopy عملت
        assertEquals(0, realMedia.getAvailableCopies()); // لأنه كان 1 ونقص
    }


}
