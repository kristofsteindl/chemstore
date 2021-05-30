package com.ksteindl.chemstore.web;

import com.ksteindl.chemstore.exceptions.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MapValidationErrorService {

    private static final Logger logger = LogManager.getLogger(MapValidationErrorService.class);

    public void throwExceptionIfNotValid(BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errorMap = result.getFieldErrors().stream().collect(Collectors.toMap(
                    FieldError::getField,
                    FieldError::getDefaultMessage,
                    (errorMessage1, errorMessage2) -> errorMessage1 + ", " + errorMessage2));
            logger.info("ValidationException is being thrown with errorMap: {0}", errorMap);
            throw new ValidationException(errorMap);
        }
    }
}
