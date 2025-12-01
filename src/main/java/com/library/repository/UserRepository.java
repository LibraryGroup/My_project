package com.library.repository;

import com.library.model.User;

public interface UserRepository {

    User findByUsername(String username);

    void save(User user);        

    boolean deleteUser(String username);  
}
