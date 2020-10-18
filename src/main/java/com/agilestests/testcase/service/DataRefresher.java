package com.agilestests.testcase.service;

import org.springframework.stereotype.Service;

@Service
public interface DataRefresher {
    void refreshPhotos();
}
