package com.library.fines;

public class JournalFineStrategy implements FineStrategy {

    @Override
    public double calculateFine(int overdueDays) {
        return 15.0; 
    }
}
