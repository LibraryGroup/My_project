package com.library.repository;

import com.library.model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FileUserRepository implements UserRepository {

    private final Map<String, User> users = new HashMap<>();

    public FileUserRepository(String fileName) {
        loadUsers(fileName);
    }

    private void loadUsers(String fileName) {
        InputStream in = getClass().getClassLoader().getResourceAsStream(fileName);
        if (in == null) {
            throw new IllegalStateException("User file not found: " + fileName);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(in, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                String[] parts = trimmed.split(",");
                if (parts.length >= 2) {
                    String username = parts[0].trim();
                    double fine = Double.parseDouble(parts[1].trim());
                    users.put(username, new User(username, fine));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read user file", e);
        }
    }

    @Override
    public User findByUsername(String username) {
        return users.get(username);
    }
}
