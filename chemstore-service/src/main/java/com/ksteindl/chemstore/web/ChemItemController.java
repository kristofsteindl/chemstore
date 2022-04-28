package com.ksteindl.chemstore.web;

import com.ksteindl.chemstore.domain.entities.ChemItem;
import com.ksteindl.chemstore.domain.input.ChemItemInput;
import com.ksteindl.chemstore.domain.input.ChemItemQuery;
import com.ksteindl.chemstore.service.ChemItemService;
import com.ksteindl.chemstore.service.UnitService;
import com.ksteindl.chemstore.service.wrapper.PagedList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
    @Autowired
    private UnitService unitService;

    @PostMapping
    public ResponseEntity<List<ChemItem>> createChemItems(
            @Valid @RequestBody ChemItemInput chemItemInput,
            BindingResult result,
            Principal principal) {
        logger.info("POST '/chem-item' was called with {}", chemItemInput);
        mapValidationErrorService.throwExceptionIfNotValid(result);
        List<ChemItem> chemItems = chemItemService.createChemItems(chemItemInput, principal);
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
            @RequestParam(value= "chemicalId", required = false) Long chemicalId,
            @RequestParam(value= "opened", required = false) Boolean opened,
            @RequestParam(value= "expired", required = false) Boolean expired,
            @RequestParam(value= "consumed", required = false) Boolean consumed,
            @RequestParam(value="page", defaultValue = "0") Integer page,
            @RequestParam(value= "size", defaultValue = "10") Integer size,
            Principal principal) {
        logger.info("GET '/chem-item/{labKey}' was called with " +
                "labKey {}, chemicalId {}, page {}, size {}, opened {}, expired {}, consumed {}", 
                labKey, chemicalId, page, size, opened, expired, consumed);
        ChemItemQuery chemItemQuery = ChemItemQuery.builder()
                .labKey(labKey)
                .chemicalId(chemicalId)
                .principal(principal)
                .page(page)
                .opened(opened)
                .expired(expired)
                .consumed(consumed)
                .size(size)
                .build();
        PagedList<ChemItem> chemItems = chemItemService.getChemItemsByLab(chemItemQuery);
        logger.info("GET '/chem-item' was succesful with returned result{}", chemItems);
        return ResponseEntity.status(HttpStatus.OK).body(chemItems);
    }


    @GetMapping("/unit")
    public List<String> getUnits() {
        logger.info("GET '/api/chem-item/unit' was called");
        List<String> units = unitService.getUnits();
        logger.info("GET '/api/chem-item/unit' was succesful with {} item", units.size());
        return units;
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void hardDeleteChemItem(
            @PathVariable Long id,
            Principal principal) {
        logger.info("DELETE '/api/chem-item/{id}' was called with id {}", id);
        chemItemService.hardDeleteChemItem(id, principal);
        logger.info("DELETE '/api/chem-item/{id}' was SUCCESSFUL with id {}", id);
    }
    
}
