package com.library.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CDTest {

    
    private static final String TITLE_BEST_HITS = "Best Hits";
    private static final String TITLE_HITS = "Hits";
    private static final String ARTIST_ADELE = "Adele";
    private static final String ARTIST_NEW = "New Artist";

    @Test
    void cdFieldsShouldBeStoredCorrectly() {
        CD cd = new CD(5, TITLE_BEST_HITS, ARTIST_ADELE);

        assertEquals(5, cd.getId());
        assertEquals(TITLE_BEST_HITS, cd.getTitle());
        assertEquals(ARTIST_ADELE, cd.getArtist());
    }

    @Test
    void borrowDaysShouldBe7() {
        CD cd = new CD(1, "X", "Y");
        assertEquals(7, cd.getBorrowDays());
    }

    @Test
    void updateArtistShouldWork() {
        CD cd = new CD(1, "X", "Y");
        cd.setArtist(ARTIST_NEW);
        assertEquals(ARTIST_NEW, cd.getArtist());
    }

    @Test
    void toStringShouldContainImportantFields() {
        CD cd = new CD(1, TITLE_HITS, ARTIST_ADELE);
        String s = cd.toString();

        assertTrue(s.contains(TITLE_HITS));
        assertTrue(s.contains(ARTIST_ADELE));
    }
}
