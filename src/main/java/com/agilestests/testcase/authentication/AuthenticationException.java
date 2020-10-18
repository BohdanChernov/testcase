package com.agilestests.testcase.authentication;

public class AuthenticationException extends RuntimeException{
    @Override
    public String getMessage() {
        return "Something wrong with authentication";
    }
}
