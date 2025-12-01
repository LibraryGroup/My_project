package com.library.service;

import com.library.model.*;
import com.library.repository.*;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CDFineTest {

    @Test
    void cdFineShouldBe20() {

        CD cd = new CD(1, "Hits", "Artist");
        User user = new User("mohammad", 0);

        BorrowRecord r = new BorrowRecord(
                user,
                cd,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 8)
        );

        FineService fineService = new FineService(null);

        double total = fineService.calculateTotalFine(
                List.of(r),
                LocalDate.of(2025, 1, 20)
        );

        assertEquals(20.0, total, 0.001);
    }
}

