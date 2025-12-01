package com.library.repository;

import com.library.model.User;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class FileUserRepository implements UserRepository {

    private final String filePath;
    private final Map<String, User> users = new HashMap<>();

    public FileUserRepository(String filename) {
        File file = new File(filename);

       
        this.filePath = file.getAbsolutePath();

     
        System.out.println("📌 [UserRepo] Using file: " + this.filePath);

        
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadFromFile();
    }

    @Override
    public User findByUsername(String username) {
        return users.get(username);
    }

    @Override
    public void save(User user) {
        users.put(user.getUsername(), user);
        writeToFile(); 
    }

    @Override
    public boolean deleteUser(String username) {
        if (users.remove(username) != null) {
            writeToFile();
            return true;
        }
        return false;
    }

    
    private void loadFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    users.put(parts[0], new User(parts[0], Double.parseDouble(parts[1])));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   
    private void writeToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {

            for (User u : users.values()) {
                writer.println(u.getUsername() + "," + u.getFineBalance());
            }

            System.out.println("✅ [UserRepo] File updated successfully.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

