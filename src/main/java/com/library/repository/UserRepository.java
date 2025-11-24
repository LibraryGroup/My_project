package com.library.repository;

import com.library.model.User;

public interface UserRepository {

    User findByUsername(String username);

    void save(User user);        // ← ضروري لإضافة/تحديث المستخدم

    boolean deleteUser(String username);  // Sprint 4
}
