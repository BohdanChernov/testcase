package com.agilestests.testcase.controllers;

import com.agilestests.testcase.TestcaseApplication;
import com.agilestests.testcase.authentication.AuthenticationHandlerImpl;
import com.agilestests.testcase.dao.PhotoDao;
import com.agilestests.testcase.models.Photo;
import com.agilestests.testcase.service.DataRefresherImpl;
import com.agilestests.testcase.service.SearchImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {TestcaseApplication.class,
        ImagesController.class,
        DataRefresherImpl.class,
        SearchImpl.class,
        AuthenticationHandlerImpl.class})
class ImagesControllerTestIT {

    @MockBean
    private PhotoDao photoDao;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void cacheImages() throws Exception {
        mockMvc.perform(get("/initData")).andExpect(status().isOk());
    }

    @Test
    void getSearchPhotos() throws Exception {
        Photo photoDefault = new Photo("1",
                "Artur",
                "Canon",
                "#camera, #artur, #canon",
                "http://url.com/cropped_picture.jpeg",
                "http://url.com/full_picture.jpeg");
        List<Photo> list = Collections.singletonList(photoDefault);
        Mockito.when(photoDao.findAll(Mockito.any(Specification.class))).thenReturn(list);
        String photosFromResponseJson = this.mockMvc.perform(get("/search/Artur"))
                .andReturn().getResponse().getContentAsString();
        List<Photo> photoList = objectMapper
                .readValue(photosFromResponseJson,
                        new TypeReference<>() {
                        });
        Assertions.assertEquals(photoDefault, photoList.get(0));
    }
}
