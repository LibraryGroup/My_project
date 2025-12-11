package com.library.model;

import com.library.fines.FineStrategy;
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
        assertEquals(1, cd.getTotalCopies());
        assertEquals(1, cd.getAvailableCopies());
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

    @Test
    void totalCopiesSetterAndGetterShouldWork() {
        CD cd = new CD(1, "X", "Y");
        cd.setTotalCopies(5);
        assertEquals(5, cd.getTotalCopies());
        assertEquals(1, cd.getAvailableCopies()); // availableCopies remains 1 initially
    }

    @Test
    void availableCopiesSetterAndGetterShouldWork() {
        CD cd = new CD(1, "X", "Y");
        cd.setAvailableCopies(3);
        assertEquals(3, cd.getAvailableCopies());
    }

    @Test
    void isAvailableShouldReturnTrueWhenCopiesAvailable() {
        CD cd = new CD(1, "X", "Y");
        assertTrue(cd.isAvailable());
    }

    @Test
    void isAvailableShouldReturnFalseWhenNoCopies() {
        CD cd = new CD(1, "X", "Y");
        cd.setAvailableCopies(0);
        assertFalse(cd.isAvailable());
    }

    @Test
    void decreaseCopyShouldDecreaseAvailableCopies() {
        CD cd = new CD(1, "X", "Y");
        cd.setAvailableCopies(2);
        cd.decreaseCopy();
        assertEquals(1, cd.getAvailableCopies());
    }

    @Test
    void decreaseCopyShouldThrowWhenNoCopies() {
        CD cd = new CD(1, "X", "Y");
        cd.setAvailableCopies(0);
        assertThrows(IllegalStateException.class, cd::decreaseCopy);
    }

    @Test
    void increaseCopyShouldIncreaseAvailableCopies() {
        CD cd = new CD(1, "X", "Y");
        cd.setTotalCopies(5);
        cd.setAvailableCopies(3);
        cd.increaseCopy();
        assertEquals(4, cd.getAvailableCopies());
    }

    @Test
    void increaseCopyShouldNotExceedTotalCopies() {
        CD cd = new CD(1, "X", "Y");
        cd.setTotalCopies(3);
        cd.setAvailableCopies(3);
        cd.increaseCopy();
        assertEquals(3, cd.getAvailableCopies());
    }

    @Test
    void fineStrategySetterAndGetterShouldWork() {
        CD cd = new CD(1, "X", "Y");
        FineStrategy strategy = (daysLate) -> daysLate * 2;
        cd.setFineStrategy(strategy);
        assertEquals(strategy, cd.getFineStrategy());
    }
}

