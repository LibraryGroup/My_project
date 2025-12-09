package com.library.repository;

import com.library.model.Book;
import com.library.model.CD;
import com.library.model.Media;

import java.io.*;
import java.util.*;

public class FileMediaRepository implements MediaRepository {

    private final String filename;
    private final Map<Integer, Media> mediaMap = new HashMap<>();
    private int nextId = 1;

    public FileMediaRepository(String filename) {
        this.filename = filename;
        load();
    }

    private void load() {
        File file = new File(filename);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;
            while ((line = br.readLine()) != null) {

                String[] p = line.split(",");

                String type = p[0];
                int id = Integer.parseInt(p[1]);
                String title = p[2];

                Media m = null;

                if (type.equals("BOOK")) {
                    String author = p[3];
                    String isbn = p[4];
                    int total = Integer.parseInt(p[5]);
                    int available = Integer.parseInt(p[6]);

                    Book b = new Book(id, title, author, isbn);
                    b.setTotalCopies(total);
                    b.setAvailableCopies(available);
                    m = b;
                }

                else if (type.equals("CD")) {
                    String artist = p[3];
                    int total = Integer.parseInt(p[4]);
                    int available = Integer.parseInt(p[5]);

                    CD cd = new CD(id, title, artist);
                    cd.setTotalCopies(total);
                    cd.setAvailableCopies(available);
                    m = cd;
                }

                if (m != null) {
                    mediaMap.put(id, m);
                    if (id >= nextId) nextId = id + 1;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void write() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {

            for (Media m : mediaMap.values()) {

                if (m instanceof Book b) {
                    pw.println("BOOK," +
                            b.getId() + "," +
                            b.getTitle() + "," +
                            b.getAuthor() + "," +
                            b.getIsbn() + "," +
                            b.getTotalCopies() + "," +
                            b.getAvailableCopies());
                }

                else if (m instanceof CD cd) {
                    pw.println("CD," +
                            cd.getId() + "," +
                            cd.getTitle() + "," +
                            cd.getArtist() + "," +
                            cd.getTotalCopies() + "," +
                            cd.getAvailableCopies());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Media findById(int id) {
        return mediaMap.get(id);
    }

    @Override
    public List<Media> findAll() {
        return new ArrayList<>(mediaMap.values());
    }

    @Override
    public Media save(Media media) {
        if (media.getId() <= 0) {
            media.setId(nextId++);
        }

        mediaMap.put(media.getId(), media);
        write();
        return media;
    }

    @Override
    public void delete(int id) {
        mediaMap.remove(id);
        write();
    }
    //Hello
    //world
}
