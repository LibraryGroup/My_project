package com.library.service;

import com.library.model.User;
import com.library.repository.UserRepository;

public class FineService {

    private final UserRepository userRepository;

    public FineService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addFine(String username, double amount) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setFineBalance(user.getFineBalance() + amount);
            userRepository.save(user);
        }
    }

    public boolean payFine(String username, double amount) {
        User user = userRepository.findByUsername(username);
        if (user == null) return false;

        if (amount <= 0 || amount > user.getFineBalance()) return false;

        user.setFineBalance(user.getFineBalance() - amount);
        userRepository.save(user);
        return true;
    }

    public boolean hasUnpaidFines(String username) {
        User user = userRepository.findByUsername(username);
        return user != null && user.getFineBalance() > 0;
    }
}
