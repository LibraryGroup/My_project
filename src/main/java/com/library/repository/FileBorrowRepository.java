package com.library.repository;

import com.library.model.Book;
import com.library.model.BorrowRecord;
import com.library.model.Media;
import com.library.model.User;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class FileBorrowRepository implements BorrowRepository {

    private final String filename;
    private final MediaRepository mediaRepo;
    private final List<BorrowRecord> records = new ArrayList<>();

    public FileBorrowRepository(String filename, MediaRepository mediaRepo) {
        this.filename = filename;
        this.mediaRepo = mediaRepo;
        load();    
    }

   
    private void load() {
        File file = new File(filename);
        if (!file.exists()) return;

        System.out.println("📌 [BorrowRepo] Loading file: " + file.getAbsolutePath());

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;
            while ((line = br.readLine()) != null) {

                String[] p = line.split(",");

                String username = p[0];
                int mediaId = Integer.parseInt(p[1]);

                LocalDate borrowDate = LocalDate.parse(p[2]);
                LocalDate dueDate = LocalDate.parse(p[3]);

                boolean returned = Boolean.parseBoolean(p[4]);
                LocalDate returnDate =
                        p[5].equals("null") ? null : LocalDate.parse(p[5]);

               
                Media media = mediaRepo.findById(mediaId);

                
                if (media == null) {
                    System.out.println("⚠ Warning: Media ID " + mediaId +
                            " not found. Creating placeholder media.");

                    media = new Book(
                            mediaId,
                            "Recovered Media " + mediaId,
                            "Unknown",
                            "N/A"
                    );

                    mediaRepo.save(media);
                }

              
                if (!returned) {
                    media.setAvailable(false);
                }

                User user = new User(username, 0);

                BorrowRecord record = new BorrowRecord(
                        user, media, borrowDate, dueDate
                );

                record.setReturned(returned);
                record.setReturnDate(returnDate);

                records.add(record);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    @Override
    public void save(BorrowRecord r) {
        records.add(r);
        write();
    }

   
    @Override
    public void update(BorrowRecord r) {
        write();
    }

    
    @Override
    public List<BorrowRecord> findByUser(User user) {
        List<BorrowRecord> list = new ArrayList<>();
        for (BorrowRecord r : records) {
            if (r.getUser().getUsername().equals(user.getUsername())) {
                list.add(r);
            }
        }
        return list;
    }

    @Override
    public List<BorrowRecord> findAll() {
        return records;
    }

    @Override
    public Set<User> getAllUsers() {
        Set<User> set = new HashSet<>();
        for (BorrowRecord r : records) {
            set.add(r.getUser());
        }
        return set;
    }

   
   
    private void write() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {

            for (BorrowRecord r : records) {
                pw.println(
                        r.getUser().getUsername() + "," +
                        r.getMedia().getId() + "," +
                        r.getBorrowDate() + "," +
                        r.getDueDate() + "," +
                        r.isReturned() + "," +
                        (r.getReturnDate() == null ? "null" : r.getReturnDate())
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


