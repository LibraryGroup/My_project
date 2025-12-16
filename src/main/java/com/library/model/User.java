package com.library.model;

public class User {

    private final String username;
    private double fineBalance;
    private String phone;
    private String email;   // ⭐ إضافة الإيميل

    public User(String username, double fineBalance) {
        this.username = username;
        this.fineBalance = fineBalance;
    }

    // ===== phone =====
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // ===== email =====
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // ===== others =====
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
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
