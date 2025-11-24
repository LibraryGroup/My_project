package com.library.repository;

import com.library.model.Admin;

public interface AdminRepository {

    Admin findByUsername(String username);
}
