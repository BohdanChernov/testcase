package com.agilestests.testcase.authentication.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super("Nothing found with the search term: " + message);
    }
}
