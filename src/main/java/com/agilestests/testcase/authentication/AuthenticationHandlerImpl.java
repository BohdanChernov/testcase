package com.agilestests.testcase.authentication;

import com.agilestests.testcase.authentication.exceptions.AuthenticationException;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@Data
public class AuthenticationHandlerImpl implements AuthenticationHandler {
    @Value("${api.key}")
    private String apiKey;
    @Value("${url.get.auth}")
    private String urlToFetchData;
    private String token;
    private Map<String, String> dataToRequestAuth;

    @Override
    public String getNewToken() {
        initRequestData();
        TokenDto tokenDto;
        try {
            tokenDto = new RestTemplate().postForObject(urlToFetchData, dataToRequestAuth, TokenDto.class);
        } catch (RestClientException e) {
            throw new AuthenticationException(apiKey);
        }

        return tokenDto != null ? tokenDto.getToken() : "No token";
    }

    public void initRequestData() {
        dataToRequestAuth = new HashMap<>();
        dataToRequestAuth.put("apiKey", apiKey);
    }

}
