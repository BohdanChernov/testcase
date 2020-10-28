package com.agilestests.testcase.controllers;

import com.agilestests.testcase.dao.PhotoDao;
import com.agilestests.testcase.models.Photo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@Rollback
class ImagesControllerTestIT {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);
    @Autowired
    private PhotoDao photoDao;
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void cacheImages() {
        ResponseEntity<String> response = restTemplate.getForEntity("/initData", String.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getSearchPhotosAndGetListSuccessfully() throws Exception {
        Photo photoDefault = new Photo("1",
                "Artur",
                "Canon",
                "#Artur, #Canon",
                "http://images.com/cropped_picture.jpeg",
                "http://images.com/full_picture.jpeg");
        photoDao.save(photoDefault);

        ResponseEntity<String> photosFromResponseJson = restTemplate.getForEntity("/search/Artur", String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        CollectionType mapCollectionType = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, Photo.class);

        List<Photo> photoList = objectMapper
                .readValue(photosFromResponseJson.getBody(), mapCollectionType);
        Photo photoFromRequest = photoList.get(0);
        Assertions.assertEquals(photoDefault, photoFromRequest);
        Assertions.assertEquals(HttpStatus.OK, photosFromResponseJson.getStatusCode());
    }

    @Test
    void getSearchPhotosAndGetNotFoundException() {
        ResponseEntity<String> response = restTemplate.getForEntity("/search/NotArtur", String.class);

        String expectedMessage = "Nothing found with the search term: NotArtur";
        String actualMessage = response.getBody();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
