package com.agilestests.testcase.service;

import com.agilestests.testcase.dao.PhotoDao;
import com.agilestests.testcase.models.Photo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.jpa.domain.Specification.where;

@Service
public class SearchImpl implements Search {
    private final PhotoDao photoDao;

    @Autowired
    public SearchImpl(PhotoDao photoDao) {
        this.photoDao = photoDao;
    }

    @Override
    public List<Photo> searchPhotos(String search) {
        return photoDao.findAll(where(hasAuthor(search).or(hasCamera(search).or(hasTag(search)))));
    }

    public Specification<Photo> hasAuthor(String author) {
        return (photo, cq, cb) -> cb.like(photo.get("author"), "%" + author + "%");
    }

    public Specification<Photo> hasCamera(String camera) {
        return (photo, cq, cb) -> cb.like(photo.get("camera"), "%" + camera + "%");
    }

    public Specification<Photo> hasTag(String tag) {
        return (photo, cq, cb) -> cb.like(photo.get("tags"), "%" + tag + "%");
    }
}
