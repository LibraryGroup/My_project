package com.library.model;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class BookTest {

    // Constants to avoid duplicating literals
    private static final String TITLE_CLEAN_CODE = "Clean Code";
    private static final String AUTHOR_MARTIN = "Martin";
    private static final String ISBN_111 = "111";

    @Test
    void bookFieldsShouldBeStoredCorrectly() {
        Book b = new Book(10, TITLE_CLEAN_CODE, AUTHOR_MARTIN, ISBN_111);

        assertEquals(10, b.getId());
        assertEquals(TITLE_CLEAN_CODE, b.getTitle());
        assertEquals(AUTHOR_MARTIN, b.getAuthor());
        assertEquals(ISBN_111, b.getIsbn());
    }

    @Test
    void borrowDaysShouldBe28() {
        Book b = new Book(1, "X", "Y", "Z");
        assertEquals(28, b.getBorrowDays());
    }

    @Test
    void settersShouldWork() {
        Book b = new Book(1, "A", "B", "C");
        b.setAuthor("New A");
        b.setIsbn("999");

        assertEquals("New A", b.getAuthor());
        assertEquals("999", b.getIsbn());
    }

    @Test
    void toStringShouldContainImportantFields() {
        Book b = new Book(1, TITLE_CLEAN_CODE, AUTHOR_MARTIN, ISBN_111);
        String s = b.toString();

        assertTrue(s.contains(TITLE_CLEAN_CODE));
        assertTrue(s.contains(AUTHOR_MARTIN));
        assertTrue(s.contains(ISBN_111));
    }
}
