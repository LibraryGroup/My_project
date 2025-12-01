package com.library.repository;

import com.library.model.Media;
import java.util.List;

public interface MediaRepository {

    Media findById(int id);

    Media save(Media media);   

    List<Media> findAll();

    void delete(int id);       
}
