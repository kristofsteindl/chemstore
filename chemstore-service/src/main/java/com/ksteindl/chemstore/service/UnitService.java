package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.exceptions.ValidationException;
import com.ksteindl.chemstore.util.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class UnitService {

    private static final Logger logger = LoggerFactory.getLogger(UnitService.class);

    //This can be moved somewhere else, eg ./etc/chemstore or ./home/user/chemstore
    public static final String UNIT_FILE_NAME = "unit.txt";

    public List<String> units;
    public static final List<String> DEFAULT_UNITS = List.of(
            "ug", "mg", "g", "kg",
            "ul", "ml", "l");


    @PostConstruct
    private void loadUnits() {
        units = DEFAULT_UNITS;
        try {
            Path filePath = Paths.get(UNIT_FILE_NAME);
            if (Files.exists(filePath)) {
                units = Files.readAllLines(Paths.get(UNIT_FILE_NAME), StandardCharsets.UTF_8);
                logger.info("units loaded succesfully from: " + UNIT_FILE_NAME);
            } else {
                logger.warn(UNIT_FILE_NAME + " does not exists, falling back to default units");
            }
            logger.info("units are: " + units);
        }
        catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public List<String> getUnits() {
        return units;
    }

    public void validate(String unit) {
        validate(unit, Lang.INVALID_UNIT);
    }

    public void validate(String unit, String msgTemplate) {
        if (!units.contains(unit)) {
            throw new ValidationException(String.format(msgTemplate, unit, units));
        }
    }
}
