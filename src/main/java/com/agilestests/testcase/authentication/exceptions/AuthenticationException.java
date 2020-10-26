package com.agilestests.testcase.authentication.exceptions;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super("The API KEY is not valid for the authentication! The API KEY: " + message);
    }
}
