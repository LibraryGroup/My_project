package com.library.fines;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JournalFineStrategyTest {

    @Test
    void calculateFineShouldAlwaysReturnFixedValue() {
        JournalFineStrategy strategy = new JournalFineStrategy();

        assertEquals(15.0, strategy.calculateFine(0));
        assertEquals(15.0, strategy.calculateFine(1));
        assertEquals(15.0, strategy.calculateFine(10));
        assertEquals(15.0, strategy.calculateFine(999));
    }

    @Test
    void calculateFineShouldNotDependOnOverdueDays() {
        JournalFineStrategy strategy = new JournalFineStrategy();

        double fine1 = strategy.calculateFine(3);
        double fine2 = strategy.calculateFine(30);

        assertEquals(fine1, fine2);          // القيمة ثابتة
        assertEquals(15.0, fine1);           // القيمة الصحيحة
    }
}
