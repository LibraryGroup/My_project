package com.library.repository;

import com.library.model.BorrowRecord;
import com.library.model.User;
import java.util.List;
import java.util.Set;

public interface BorrowRepository {

    void save(BorrowRecord record);

    void update(BorrowRecord record);

    List<BorrowRecord> findByUser(User user);

    Set<User> getAllUsers();

    List<BorrowRecord> findAll();
}
