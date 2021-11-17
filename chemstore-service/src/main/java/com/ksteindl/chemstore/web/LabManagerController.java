package com.ksteindl.chemstore.web;

import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.entities.ChemType;
import com.ksteindl.chemstore.domain.input.ChemTypeInput;
import com.ksteindl.chemstore.service.AppUserService;
import com.ksteindl.chemstore.service.ChemTypeService;
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
import java.util.Map;

@RestController
@RequestMapping("/api/lab-manager")
@CrossOrigin
public class LabManagerController {

    private static final Logger logger = LogManager.getLogger(LabManagerController.class);

    @Autowired
    private AppUserService appUserService;
    @Autowired
    private ChemTypeService chemTypeService;
    @Autowired
    private MapValidationErrorService mapValidationErrorService;

    @GetMapping("/user")
    public Map<String, List<AppUser>> getUersFromManagedLabs(Principal principal) {
        logger.info("'/lab-manager/user' was called by {}", principal.getName());
        return appUserService.getUsersFromManagedLabs(principal);
    }

    @GetMapping("/user/{labKey}")
    public List<AppUser> getUersFromManagedLab(@PathVariable String labKey, Principal principal) {
        logger.info("'/lab-manager/user/{labKey}' was called by {}", principal.getName());
        return appUserService.getUsersFromManagedLab(principal, labKey);
    }

    //CHEM TYPE
    @PostMapping("/chem-type")
    public ResponseEntity<ChemType> createChemType(@Valid @RequestBody ChemTypeInput chemTypeInput, BindingResult result) {
        logger.info("POST '/chem-type' was called with {}", chemTypeInput);
        mapValidationErrorService.throwExceptionIfNotValid(result);
        ChemType chemType = chemTypeService.createChemType(chemTypeInput);
        logger.info("POST '/chem-type' was succesful with returned result{}", chemType);
        return new ResponseEntity<>(chemType, HttpStatus.CREATED);
    }

    @PutMapping("/chem-type/{id}")
    public ResponseEntity<ChemType> updateChemType(
            @Valid @RequestBody ChemTypeInput chemTypeInput,
            BindingResult result,
            @PathVariable  Long id) {
        logger.info("PUT '/chem-type/{id}' was called with id {} and input {}", id, chemTypeInput);
        mapValidationErrorService.throwExceptionIfNotValid(result);
        ChemType chemType = chemTypeService.updateChemType(chemTypeInput, id);
        logger.info("PUT '/chem-type/{id}' was succesful with returned result{}", chemType);
        return new ResponseEntity<>(chemType, HttpStatus.CREATED);
    }

    @GetMapping("/chem-type/{id}")
    public ResponseEntity<ChemType> getChemType(
            @PathVariable  Long id) {
        logger.info("GET '/chem-type/{id}' was called with id {}", id);
        ChemType chemType = chemTypeService.findById(id);
        logger.info("GET '/chem-type/{id}' was succesful with returned result{}", chemType);
        return new ResponseEntity<>(chemType, HttpStatus.OK);
    }

    @GetMapping("/chem-type")
    public ResponseEntity<List<ChemType>> getChemTypes(
            @RequestParam(value="only-active", required = false, defaultValue = "true") boolean onlyActive) {
        logger.info("GET '/chem-type' was called, with onlyActive {}", onlyActive);
        List<ChemType> chemTypes = chemTypeService.getChemTypes(onlyActive);
        logger.info("GET '/chem-type' was succesful with {} item", chemTypes.size());
        return new ResponseEntity<>(chemTypes, HttpStatus.OK);
    }

    @DeleteMapping("/chem-type/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteChemType(@PathVariable Long id) {
        logger.info("DELETE '/chem-type/{id}' was called with id {}", id);
        chemTypeService.deleteChemType(id);
        logger.info("DELETE '/chem-type/{id}' was successfull");
    }


}
