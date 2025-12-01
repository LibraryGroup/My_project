package com.library.fines;

public class CDFineStrategy implements FineStrategy {

    @Override
    public double calculateFine(int overdueDays) {
        return 20.0; 
    }
}
