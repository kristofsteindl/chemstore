package com.ksteindl.chemstore.web;

import com.ksteindl.chemstore.domain.entities.ChemItem;
import com.ksteindl.chemstore.domain.input.ChemItemInput;
import com.ksteindl.chemstore.service.ChemItemService;
import com.ksteindl.chemstore.service.wrapper.PagedList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("api/chem-item")
@CrossOrigin
public class ChemItemController {

    private static final Logger logger = LogManager.getLogger(ChemItemController.class);

    @Autowired
    private MapValidationErrorService mapValidationErrorService;
    @Autowired
    private ChemItemService chemItemService;

    @PostMapping("/{labKey}")
    public ResponseEntity<List<ChemItem>> createChemItems(
            @PathVariable String labKey,
            @Valid @RequestBody ChemItemInput chemItemInput,
            BindingResult result,
            Principal principal) {
        logger.info("POST '/chem-item' was called with {}", chemItemInput);
        mapValidationErrorService.throwExceptionIfNotValid(result);
        List<ChemItem> chemItems = chemItemService.createChemItems(labKey, chemItemInput, principal);
        logger.info("POST '/chem-item' was succesful with returned result{}", chemItems);
        return ResponseEntity.status(HttpStatus.CREATED).body(chemItems);
    }

    @PatchMapping("/open/{chemItemId}")
    public ResponseEntity<ChemItem> openChemItems(
            @PathVariable Long chemItemId,
            Principal principal) {
        logger.info("PATCH '/chem-item/open' was called with {}", chemItemId);
        ChemItem opened = chemItemService.openChemItem(chemItemId, principal);
        logger.info("PATCH '/chem-item/open' was successful with returned result{}", opened);
        return ResponseEntity.status(HttpStatus.OK).body(opened);
    }

    @PatchMapping("/consume/{chemItemId}")
    public ResponseEntity<ChemItem> consumeChemItems(
            @PathVariable Long chemItemId,
            Principal principal) {
        logger.info("PATCH '/chem-item/consume' was called with {}", chemItemId);
        ChemItem consumed = chemItemService.consumeChemItem(chemItemId, principal);
        logger.info("PATCH '/chem-item/consume' was successful with returned result{}", consumed);
        return ResponseEntity.status(HttpStatus.OK).body(consumed);
    }

    @GetMapping("/{labKey}")
    public ResponseEntity<PagedList<ChemItem>> getChemItemsForLab(
            @PathVariable String labKey,
            @RequestParam(value= "available", required = false, defaultValue = "true") boolean available,
            @RequestParam(value="page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value= "size", required = false, defaultValue = "10") Integer size,
            Principal principal) {
        logger.info("GET '/chem-item/{labKey}' was called with labKey {}, page {}, offset {}, onlyActive {}", labKey, page, size, available);
        PagedList<ChemItem> chemItems = chemItemService.findByLab(labKey, principal, available, page, size);
        logger.info("GET '/chem-item' was succesful with returned result{}", chemItems);
        return ResponseEntity.status(HttpStatus.OK).body(chemItems);
    }
}
