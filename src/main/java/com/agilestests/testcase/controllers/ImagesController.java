package com.agilestests.testcase.controllers;

import com.agilestests.testcase.models.Photo;
import com.agilestests.testcase.service.DataRefresher;
import com.agilestests.testcase.service.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ImagesController {
    @Autowired
    private DataRefresher dataRefresher;
    @Autowired
    private Search search;

    @GetMapping("/initData")
    public void cacheImages() {
        dataRefresher.refreshPhotos();
    }

    @GetMapping("/search/{searchTerm}")
    public List<Photo> getSearchPhotos(@PathVariable("searchTerm") String searchTerm) {
        return search.searchPhotos(searchTerm);
    }

}
