package com.agilestests.testcase.authentication;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TokenDto {
    private boolean auth;
    private String token;

    @JsonCreator
    public TokenDto(@JsonProperty("auth") boolean auth,
                    @JsonProperty("token") String token) {
        this.auth = auth;
        this.token = token;
    }
}
