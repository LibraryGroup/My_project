package com.library.model;

public class User {

    private final String username;
    private double fineBalance;

    public User(String username, double fineBalance) {
        this.username = username;
        this.fineBalance = fineBalance;
    }

    public String getUsername() {
        return username;
    }

    public double getFineBalance() {
        return fineBalance;
    }

    public void setFineBalance(double fineBalance) {
        this.fineBalance = fineBalance;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", fineBalance=" + fineBalance +
                '}';
    }
}
