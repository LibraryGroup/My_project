package com.library.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    @Test
    void bookFieldsShouldBeStoredCorrectly() {
        Book b = new Book(10, "Clean Code", "Martin", "111");

        assertEquals(10, b.getId());
        assertEquals("Clean Code", b.getTitle());
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
        Book b = new Book(1, "Clean Code", "Martin", "111");

        String s = b.toString();
        assertTrue(s.contains("Clean Code"));
        assertTrue(s.contains("Martin"));
        assertTrue(s.contains("111"));
    }
}
