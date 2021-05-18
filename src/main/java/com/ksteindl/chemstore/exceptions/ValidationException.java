package com.ksteindl.chemstore.exceptions;

import java.util.HashMap;
import java.util.Map;

public class ValidationException extends RuntimeException{

    private Map<String, String> errorMap = new HashMap<>();

    public ValidationException(String message) {
        super(message);
        errorMap.put("message", message);
    }

    public ValidationException(String key, String message) {
        super(message);
        errorMap.put(key, message);
    }

    public ValidationException(Map<String, String> errorMap) {
        super(errorMap.toString());
        this.errorMap = errorMap;
    }

    public Map<String, String> getErrorMap() {
        return errorMap;
    }
}
