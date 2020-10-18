package com.agilestests.testcase.authentication;

import org.springframework.stereotype.Component;

@Component
public interface AuthenticationHandler {
    String getNewToken();
}
