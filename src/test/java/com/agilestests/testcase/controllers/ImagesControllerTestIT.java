package com.agilestests.testcase.controllers;

import com.agilestests.testcase.authentication.TokenDto;
import com.agilestests.testcase.dao.PhotoDao;
import com.agilestests.testcase.models.Photo;
import com.agilestests.testcase.service.ImageResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@Rollback
class ImagesControllerTestIT {

    @Autowired
    private PhotoDao photoDao;
    @Autowired
    private TestRestTemplate restTemplate;
    @Rule
    public final WireMockRule wireMockRule = new WireMockRule(8089);
    @Autowired
    private ObjectMapper objectMapper;
    @Value("${api.key}")
    private String apiKey;

    @Test
    void cacheImages() throws JsonProcessingException {
        setUpImageMockServer();
        setUpAuthMockServer();
        wireMockRule.start();
        ResponseEntity<String> response = restTemplate.getForEntity("/initData", String.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private void setUpImageMockServer() throws JsonProcessingException {
        Photo photoDefault = new Photo("1",
                "Artur",
                "Canon",
                "#Artur, #Canon",
                "http://images.com/cropped_picture.jpeg",
                "http://images.com/full_picture.jpeg");
        photoDao.save(photoDefault);
        String urlToGetImages = "/images";
        List<Photo> photosForRequest = Collections.singletonList(photoDefault);
        ImageResponseDto imageResponseDto = new ImageResponseDto();
        imageResponseDto.setPictures(photosForRequest);
        imageResponseDto.setHasMore(false);
        imageResponseDto.setPage(1);
        imageResponseDto.setHasMore(false);
        String imageResponseJson = objectMapper.writeValueAsString(imageResponseDto);
        wireMockRule.stubFor(get(urlPathEqualTo(urlToGetImages))
                .withQueryParam("page", WireMock.equalTo("1"))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(imageResponseJson)
                        .withStatus(200)));
    }

    private void setUpAuthMockServer() throws JsonProcessingException {
        String urlToGetAuthentication = "/auth";
        TokenDto tokenDto = new TokenDto();
        tokenDto.setAuth(true);
        tokenDto.setToken("testToken");
        String tokenDtoJson = objectMapper.writeValueAsString(tokenDto);
        wireMockRule.stubFor(post(urlPathEqualTo(urlToGetAuthentication))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(tokenDtoJson)
                        .withStatus(200)));
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
