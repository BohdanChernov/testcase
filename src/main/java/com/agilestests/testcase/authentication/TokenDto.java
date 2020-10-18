package com.agilestests.testcase.authentication;

import lombok.Data;

@Data
public class TokenDto {
    private boolean auth;
    private String token;
}
