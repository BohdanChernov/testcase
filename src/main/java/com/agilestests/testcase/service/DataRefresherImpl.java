package com.agilestests.testcase.service;

import com.agilestests.testcase.authentication.AuthenticationHandler;
import com.agilestests.testcase.dao.PhotoDao;
import com.agilestests.testcase.models.Photo;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Data
public class DataRefresherImpl implements DataRefresher {
    @Autowired
    private PhotoDao photoDao;
    @Autowired
    private AuthenticationHandler authenticationHandler;
    @Value("${url.get.page.images}")
    private String urlToGetImagePage;
    @Value("${url.get.page.images.details}")
    private String urlToGetPhotoDetails;

    private String token;
    private HttpEntity<String> entity;
    private String urlToGet;
    int initPageNumber = 1;

    @Override
    public void refreshPhotos() {
        refreshData();
    }

    public String getToken() {
        return authenticationHandler.getNewToken();
    }

    public void refreshData() {
        ResponseEntity<ImageResponseDto> response = tryConnect();
        getAnotherPages(response);
    }

    public ResponseEntity<ImageResponseDto> tryConnect() {
        ResponseEntity<ImageResponseDto> response = null;
        try {
            urlToGet = urlToGetImagePage + initPageNumber;
            RestTemplate restTemplate = new RestTemplate();
            response = restTemplate.exchange(urlToGet, HttpMethod.GET, entity, ImageResponseDto.class);
        } catch (Exception e) {
            e.printStackTrace();
            initCredentials();
            response = tryConnect();

        }
        return response;
    }

    public void initCredentials() {
        this.token = "Bearer " + getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        this.entity = new HttpEntity<>(headers);
    }

    public void getAnotherPages(ResponseEntity<ImageResponseDto> response) {
        while (response.getBody().isHasMore()) {
            RestTemplate restTemplate = new RestTemplate();
            urlToGet = urlToGetImagePage + initPageNumber;
            response = restTemplate.exchange(urlToGet, HttpMethod.GET, entity, ImageResponseDto.class);
            processPage(response.getBody().getPictures());
            initPageNumber += 1;
        }
    }

    public void processPage(List<Map<String, String>> list) {
        for (Map<String, String> stringStringLinkedHashMap : list) {
            String id = stringStringLinkedHashMap.get("id");
            Photo photo = getPhotoDetails(id);
            photoDao.save(photo);
        }
    }

    public Photo getPhotoDetails(String id) {
        String url = urlToGetPhotoDetails + id;
        ResponseEntity<Photo> response = new RestTemplate().exchange(url, HttpMethod.GET, entity, Photo.class);
        return response.getBody();
    }
}
