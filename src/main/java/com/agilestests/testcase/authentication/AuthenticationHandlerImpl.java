package com.agilestests.testcase.authentication;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
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
        TokenDto tokenDto = new RestTemplate().postForObject(urlToFetchData, dataToRequestAuth, TokenDto.class);
        return isAuthenticationTrue(tokenDto);
    }

    public void initRequestData() {
        dataToRequestAuth = new HashMap<>();
        dataToRequestAuth.put("apiKey", apiKey);
    }

    public String isAuthenticationTrue(TokenDto tokenDto) {
        if (tokenDto.isAuth()) {
            return tokenDto.getToken();
        } else {
            throw new AuthenticationException();
        }
    }

}
