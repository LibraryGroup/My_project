package com.library.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CDTest {

    private static final String ARTIST = "Adele";

    @Test
    void cdFieldsShouldBeStoredCorrectly() {
        CD cd = new CD(5, "Best Hits", ARTIST);

        assertEquals(5, cd.getId());
        assertEquals("Best Hits", cd.getTitle());
        assertEquals(ARTIST, cd.getArtist());
    }

    @Test
    void borrowDaysShouldBe7() {
        CD cd = new CD(1, "X", "Y");
        assertEquals(7, cd.getBorrowDays());
    }

    @Test
    void updateArtistShouldWork() {
        CD cd = new CD(1, "X", "Y");
        cd.setArtist("New Artist");
        assertEquals("New Artist", cd.getArtist());
    }

    @Test
    void toStringShouldContainImportantFields() {
        CD cd = new CD(1, "Hits", ARTIST);
        String s = cd.toString();

        assertTrue(s.contains("Hits"));
        assertTrue(s.contains(ARTIST));
    }
}

