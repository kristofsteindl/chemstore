package com.ksteindl.chemstore.exceptions;

public class ResourceNotFoundException extends RuntimeException{

    private final String resource;

    private final static String MESSAGE_TEMPLATE = "Resource '%s' with key '%s' cannot be found";

    public ResourceNotFoundException(String resource, Object key) {
        super(String.format(MESSAGE_TEMPLATE, resource, key));
        this.resource = resource;
    }

    public String getResource() {
        return resource;
    }
}
