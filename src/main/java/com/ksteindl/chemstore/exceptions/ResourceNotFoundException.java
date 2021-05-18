package com.ksteindl.chemstore.exceptions;

public class ResourceNotFoundException extends RuntimeException{

    private String key;

    public ResourceNotFoundException(String key, String message) {
        super(message);
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
