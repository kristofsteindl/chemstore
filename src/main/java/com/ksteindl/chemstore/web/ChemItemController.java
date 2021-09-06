package com.ksteindl.chemstore.web;

import com.ksteindl.chemstore.domain.entities.ChemItem;
import com.ksteindl.chemstore.domain.input.ChemItemInput;
import com.ksteindl.chemstore.service.ChemItemService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/chem-item")
@CrossOrigin
public class ChemItemController {

    private static final Logger logger = LogManager.getLogger(ChemItemController.class);

    @Autowired
    private MapValidationErrorService mapValidationErrorService;
    @Autowired
    private ChemItemService chemItemService;

    @PostMapping
    public ResponseEntity<ChemItem> createChemItem(
            @RequestBody @Valid ChemItemInput chemItemInput,
            BindingResult result,
            Principal principal) {
        logger.info("POST '/chem-item' was called with {}", chemItemInput);
        mapValidationErrorService.throwExceptionIfNotValid(result);
        ChemItem chemItem = chemItemService.createChemItem(chemItemInput, principal);
        logger.info("POST '/chem-item' was succesful with returned result{}", chemItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(chemItem);
    }
}
