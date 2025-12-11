package com.library.repository;

import com.library.model.Admin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FileAdminRepository implements AdminRepository {

    private final Map<String, Admin> admins = new HashMap<>();

    public FileAdminRepository(String fileName) {
        loadAdmins(fileName);
    }

    private void loadAdmins(String fileName) {

        // 1) جرّب القراءة من ملف النظام (tempFile)
        File file = new File(fileName);
        if (file.exists() && file.isFile()) {
            loadFromFileSystem(file);
            return;
        }

        // 2) جرّب القراءة من classpath resources
        InputStream in = getClass().getClassLoader().getResourceAsStream(fileName);

        if (in == null) {
            // بدلاً من throw → السماح بملف فارغ (حسب التست)
            return;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(in, StandardCharsets.UTF_8))) {

            readLines(reader);

        } catch (IOException e) {
            throw new RuntimeException("Failed to read admin file", e);
        }
    }

    private void loadFromFileSystem(File file) {
        try (BufferedReader reader = new BufferedReader(
                new FileReader(file, StandardCharsets.UTF_8))) {

            readLines(reader);

        } catch (IOException e) {
            throw new RuntimeException("Failed to read admin file", e);
        }
    }

    private void readLines(BufferedReader reader) throws IOException {
        String line;

        while ((line = reader.readLine()) != null) {
            String trimmed = line.trim();

            if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;

            String[] parts = trimmed.split(",");
            if (parts.length >= 2) {
                String username = parts[0].trim();
                String password = parts[1].trim();
                admins.put(username, new Admin(username, password));
            }
        }
    }

    @Override
    public Admin findByUsername(String username) {
        return admins.get(username);
    }
}
