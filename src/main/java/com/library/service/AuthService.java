package com.library.service;

import com.library.model.Admin;
import com.library.repository.AdminRepository;

public class AuthService {

    private final AdminRepository adminRepository;
    private Admin currentAdmin;

    public AuthService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public boolean login(String username, String password) {
        Admin admin = adminRepository.findByUsername(username);
        if (admin == null) {
            return false;
        }
        if (!admin.getPassword().equals(password)) {
            return false;
        }
        currentAdmin = admin;
        return true;
    }

    public void logout() {
        currentAdmin = null;
    }

    public boolean isLoggedIn() {
        return currentAdmin != null;
    }

    public Admin getCurrentAdmin() {
        return currentAdmin;
    }
}
