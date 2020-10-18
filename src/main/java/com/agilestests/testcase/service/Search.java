package com.agilestests.testcase.service;

import com.agilestests.testcase.models.Photo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface Search {
    List<Photo> searchPhotos(String search);
}
