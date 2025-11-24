package com.library.repository;

import com.library.model.Admin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FileAdminRepository implements AdminRepository {

    private final Map<String, Admin> admins = new HashMap<>();

    public FileAdminRepository(String fileName) {
        loadAdmins(fileName);
    }

    private void loadAdmins(String fileName) {
        InputStream in = getClass().getClassLoader().getResourceAsStream(fileName);
        if (in == null) {
            throw new IllegalStateException("Admin file not found: " + fileName);
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
                    String password = parts[1].trim();
                    admins.put(username, new Admin(username, password));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read admin file", e);
        }
    }

    @Override
    public Admin findByUsername(String username) {
        return admins.get(username);
    }
}
