package com.library.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    private static final String BOOK_TITLE = "Clean Code";

    @Test
    void bookFieldsShouldBeStoredCorrectly() {
        Book b = new Book(10, BOOK_TITLE, "Martin", "111");

        assertEquals(10, b.getId());
        assertEquals(BOOK_TITLE, b.getTitle());
        assertEquals("Martin", b.getAuthor());
        assertEquals("111", b.getIsbn());
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
        Book b = new Book(1, BOOK_TITLE, "Martin", "111");
        String s = b.toString();

        assertTrue(s.contains(BOOK_TITLE));
        assertTrue(s.contains("Martin"));
        assertTrue(s.contains("111"));
    }
}
