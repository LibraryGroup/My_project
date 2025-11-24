package com.library.repository;

import com.library.model.User;

public interface UserRepository {

    User findByUsername(String username);
}
