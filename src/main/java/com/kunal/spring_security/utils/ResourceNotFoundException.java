package com.kunal.spring_security.utils;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    ResourceNotFoundException() {

        super("Resource not found");
    }

}
