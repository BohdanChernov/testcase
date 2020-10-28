package com.agilestests.testcase.controllers;

import com.agilestests.testcase.authentication.exceptions.NotFoundException;
import com.agilestests.testcase.models.Photo;
import com.agilestests.testcase.service.DataRefresher;
import com.agilestests.testcase.service.Search;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class ImagesControllerTest {
    @Mock
    private Search search;
    @Mock
    private DataRefresher dataRefresher;
    private MockMvc mockMvc;
    @InjectMocks
    private ImagesController imagesController;

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(imagesController).build();
    }

    @Test
    void cacheImages() throws Exception {
        Mockito.doNothing().when(dataRefresher).refreshPhotos();
        mockMvc.perform(get("/initData")).andExpect(status().isOk());
        Mockito.verify(dataRefresher, Mockito.times(1)).refreshPhotos();
    }

    @Test
    void getSearchPhotosAndGetListSuccessfully() throws Exception {
        Photo photoDefault = new Photo("1",
                "Artur",
                "Canon",
                "#Artur, #Canon",
                "http://images.com/cropped_picture.jpeg",
                "http://images.com/full_picture.jpeg");
        List<Photo> photos = Collections.singletonList(photoDefault);
        Mockito.when(search.searchPhotos("Artur")).thenReturn(photos);
        String photosFromResponseJson = mockMvc.perform(get("/search/Artur"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        CollectionType mapCollectionType = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, Photo.class);

        List<Photo> photoList = objectMapper
                .readValue(photosFromResponseJson, mapCollectionType);
        Photo photoFromRequest = photoList.get(0);
        Assertions.assertEquals(photoDefault, photoFromRequest);
    }


    @Test
    void getSearchPhotosAndGetNotFoundException() {
        Mockito.when(search.searchPhotos("NotArtur")).thenThrow(new NotFoundException("NotArtur"));
        Exception exception = Assertions.assertThrows(NestedServletException.class,
                () -> mockMvc.perform(get("/search/NotArtur")).andExpect(status().isNotFound()));
        String expectedMessage = "Nothing found with the search term: NotArtur";
        String actualMessage = exception.getCause().getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }
}
