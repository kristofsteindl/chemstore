package com.ksteindl.chemstore.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.AbstractMap;
import java.util.Map;

@RestController
@ControllerAdvice
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public final ResponseEntity<AbstractMap.SimpleEntry<String, String>> handleProjectNotFoundException(ResourceNotFoundException exception, WebRequest request) {
        return new ResponseEntity(new AbstractMap.SimpleEntry(exception.getResource(), exception.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public final ResponseEntity<Map<String, String>> handleFieldValidationException(ValidationException exception, WebRequest request) {
        return new ResponseEntity(exception.getErrorMap(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler
    public final ResponseEntity<String> handleFieldValidationException(ForbiddenException exception, WebRequest request) {
        return new ResponseEntity(Map.of("message", exception.getMessage()), HttpStatus.FORBIDDEN);
    }


}
