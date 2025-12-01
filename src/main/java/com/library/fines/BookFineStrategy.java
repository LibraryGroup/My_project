package com.library.fines;

public class BookFineStrategy implements FineStrategy {

    @Override
    public double calculateFine(int overdueDays) {
        return 10.0; 
    }
}
