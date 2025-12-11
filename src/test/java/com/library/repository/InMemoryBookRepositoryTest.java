package com.library.repository;

import com.library.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for InMemoryBookRepository to increase repository coverage.
 */
class InMemoryBookRepositoryTest {

    private InMemoryBookRepository repo;

    @BeforeEach
    void setUp() {
        repo = new InMemoryBookRepository();
    }

    @Test
    void addShouldAssignSequentialIds() {
        Book b1 = new Book(0, "Clean Code", "Robert Martin", "111");
        Book b2 = new Book(0, "Effective Java", "Joshua Bloch", "222");

        repo.add(b1);
        repo.add(b2);

        List<Book> all = repo.findAll();

        assertEquals(2, all.size());
        // بحسب الكود: nextId يبدأ من 1 ويتزايد
        assertEquals(1, all.get(0).getId());
        assertEquals(2, all.get(1).getId());
    }

    @Test
    void searchByTitleShouldBeCaseInsensitiveAndUseContains() {
        repo.add(new Book(0, "Clean Code", "Robert Martin", "111"));
        repo.add(new Book(0, "Effective Java", "Joshua Bloch", "222"));
        repo.add(new Book(0, "Algorithms", "Sedgewick", "333"));

        List<Book> result = repo.searchByTitle("java");

        assertEquals(1, result.size());
        assertEquals("Effective Java", result.get(0).getTitle());
    }

    @Test
    void searchByAuthorShouldReturnAllMatchingAuthorIgnoringCase() {
        repo.add(new Book(0, "Clean Code", "Robert Martin", "111"));
        repo.add(new Book(0, "Clean Architecture", "ROBERT MARTIN", "222"));
        repo.add(new Book(0, "Other Book", "Someone Else", "333"));

        List<Book> result = repo.searchByAuthor("robert martin");

        assertEquals(2, result.size());
        assertTrue(result.stream()
                .allMatch(b -> b.getAuthor().toLowerCase().contains("robert martin")));
    }

    @Test
    void searchByIsbnShouldReturnBookOrNull() {
        Book target = new Book(0, "Clean Code", "Robert Martin", "111");
        repo.add(target);

        assertNotNull(repo.searchByIsbn("111"));  // موجود
        assertNull(repo.searchByIsbn("999"));     // غير موجود
    }

    @Test
    void findAllShouldReturnUnmodifiableView() {
        repo.add(new Book(0, "Clean Code", "Robert Martin", "111"));

        List<Book> all = repo.findAll();

        // حسب الكود يرجع Collections.unmodifiableList(..)
        assertThrows(UnsupportedOperationException.class,
                () -> all.add(new Book(0, "Extra", "Someone", "999")));
    }
}
