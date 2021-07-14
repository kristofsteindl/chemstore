package com.ksteindl.chemstore.web;

import com.ksteindl.chemstore.domain.entities.Chemical;
import com.ksteindl.chemstore.domain.entities.Manufacturer;
import com.ksteindl.chemstore.domain.input.ChemicalInput;
import com.ksteindl.chemstore.domain.input.ManufacturerInput;
import com.ksteindl.chemstore.service.ChemicalService;
import com.ksteindl.chemstore.service.ManufacturerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/lab-admin")
@CrossOrigin
public class LabAdminController {

    private static final Logger logger = LogManager.getLogger(LabAdminController.class);

    @Autowired
    private MapValidationErrorService mapValidationErrorService;
    @Autowired
    private ManufacturerService manufacturerService;
    @Autowired
    private ChemicalService chemicalService;

    // MANUFACTURER
    @PostMapping("/manufacturer")
    public ResponseEntity<Manufacturer> createManufacturer(@Valid @RequestBody ManufacturerInput manufacturerInput, BindingResult result) {
        logger.info("POST '/manufacturer' was called with {}", manufacturerInput);
        mapValidationErrorService.throwExceptionIfNotValid(result);
        Manufacturer manufacturer = manufacturerService.createManufacturer(manufacturerInput);
        logger.info("POST '/manufacturer' was succesful with returned result{}", manufacturer);
        return new ResponseEntity<>(manufacturer, HttpStatus.CREATED);
    }

    @PutMapping("/manufacturer/{id}")
    public ResponseEntity<Manufacturer> updateManufacturer(
            @Valid @RequestBody ManufacturerInput manufacturerInput,
            BindingResult result,
            @PathVariable  Long id) {
        logger.info("PUT '/manufacturer/{id}' was called with id {} and input {}", id, manufacturerInput);
        mapValidationErrorService.throwExceptionIfNotValid(result);
        Manufacturer manufacturer = manufacturerService.updateManufacturer(manufacturerInput, id);
        logger.info("PUT '/manufacturer/{id}' was succesful with returned result{}", manufacturer);
        return new ResponseEntity<>(manufacturer, HttpStatus.CREATED);
    }

    @GetMapping("/manufacturer")
    public ResponseEntity<List<Manufacturer>> getManufactures(
            @RequestParam(value="only-active", required = false, defaultValue = "true") boolean onlyActive) {
        logger.info("GET '/manufacturer' was called, with onlyActive {}", onlyActive);
        List<Manufacturer> manufacturers = manufacturerService.getManufacturers(onlyActive);
        logger.info("GET '/manufacturer' was succesful with {} item", manufacturers.size());
        return new ResponseEntity<>(manufacturers, HttpStatus.OK);
    }

    @DeleteMapping("/manufacturer/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteManufacture(@PathVariable Long id) {
        logger.info("DELETE '/manufacturer/{id}' was called with id {}", id);
        manufacturerService.deleteManufacturer(id);
        logger.info("DELETE '/manufacturer/{id}' was successfull");
    }


    //// CHEMICAL
    @PostMapping("/chemical")
    public ResponseEntity<Chemical> createChemical(@Valid @RequestBody ChemicalInput chemicalInput, BindingResult result) {
        logger.info("POST '/chemical' was called with {}", chemicalInput);
        mapValidationErrorService.throwExceptionIfNotValid(result);
        Chemical chemical = chemicalService.createChemical(chemicalInput);
        logger.info("POST '/chemical' was succesful with returned result{}", chemical);
        return new ResponseEntity<>(chemical, HttpStatus.CREATED);
    }

    @PutMapping("/chemical/{id}")
    public ResponseEntity<Chemical> updateChemical(
            @Valid @RequestBody ChemicalInput chemicalInput,
            @PathVariable  Long id,
            BindingResult result) {
        logger.info("PUT '/chemical/{id}' was called with id {} and input {}", id, chemicalInput);
        mapValidationErrorService.throwExceptionIfNotValid(result);
        Chemical chemical = chemicalService.updateChemical(chemicalInput, id);
        logger.info("PUT '/chemical/{id}' was succesful with returned result{}", chemical);
        return new ResponseEntity<>(chemical, HttpStatus.CREATED);
    }

    @GetMapping("/chemical")
    public ResponseEntity<List<Chemical>> getChemicals() {
        logger.info("GET '/chemical' was called");
        List<Chemical> chemicals = chemicalService.getChemicals();
        logger.info("GET '/chemical' was succesful with {} item", chemicals.size());
        return new ResponseEntity<>(chemicals, HttpStatus.OK);
    }

    @DeleteMapping("/chemical/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteChemical(@PathVariable Long id) {
        logger.info("DELETE '/chemical/{id}' was called with id {}", id);
        chemicalService.deleteChemical(id);
        logger.info("DELETE '/chemical/{id}' was successfull");
    }
}
