package com.ksteindl.chemstore.web;

import com.ksteindl.chemstore.domain.entities.Chemical;
import com.ksteindl.chemstore.domain.entities.ChemicalCategory;
import com.ksteindl.chemstore.domain.entities.Manufacturer;
import com.ksteindl.chemstore.domain.input.ChemicalCategoryInput;
import com.ksteindl.chemstore.domain.input.ChemicalInput;
import com.ksteindl.chemstore.domain.input.ManufacturerInput;
import com.ksteindl.chemstore.service.ChemicalCategoryService;
import com.ksteindl.chemstore.service.ChemicalService;
import com.ksteindl.chemstore.service.ManufacturerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;
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
    @Autowired
    private ChemicalCategoryService chemicalCategoryService;

    // MANUFACTURER
    @PostMapping("/manufacturer")
    public ResponseEntity<Manufacturer> createManufacturer(
            @Valid @RequestBody ManufacturerInput manufacturerInput, 
            BindingResult result,
            Principal admin) {
        logger.info("POST '/manufacturer' was called with {}", manufacturerInput);
        mapValidationErrorService.throwExceptionIfNotValid(result);
        Manufacturer manufacturer = manufacturerService.createManufacturer(manufacturerInput, admin);
        logger.info("POST '/manufacturer' was succesful with returned result \n{}", manufacturer);
        return new ResponseEntity<>(manufacturer, HttpStatus.CREATED);
    }

    @PutMapping("/manufacturer/{id}")
    public ResponseEntity<Manufacturer> updateManufacturer(
            @Valid @RequestBody ManufacturerInput manufacturerInput,
            BindingResult result,
            @PathVariable  Long id,
            Principal admin) {
        logger.info("PUT '/manufacturer/{id}' was called with id {} and input {}", id, manufacturerInput);
        mapValidationErrorService.throwExceptionIfNotValid(result);
        Manufacturer manufacturer = manufacturerService.updateManufacturer(manufacturerInput, id, admin);
        logger.info("PUT '/manufacturer/{id}' was succesful with returned result \n{}", manufacturer);
        return new ResponseEntity<>(manufacturer, HttpStatus.CREATED);
    }

    @GetMapping("/manufacturer/{id}")
    public ResponseEntity<Manufacturer> getManufacturer(
            @PathVariable  Long id) {
        logger.info("GET '/manufacturer/{id}' was called with id {}", id);
        Manufacturer manufacturer = manufacturerService.findById(id);
        logger.info("GET '/manufacturer/{id}' was succesful with returned result \n{}", manufacturer);
        return new ResponseEntity<>(manufacturer, HttpStatus.OK);
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
    public void deleteManufacture(@PathVariable Long id, Principal admin) {
        logger.info("DELETE '/manufacturer/{id}' was called with id {}", id);
        manufacturerService.deleteManufacturer(id, admin);
        logger.info("DELETE '/manufacturer/{id}' was successfull");
    }


    //// CHEMICAL
    @PostMapping("/chemical")
    public ResponseEntity<Chemical> createChemical(
            @Valid @RequestBody ChemicalInput chemicalInput,
            BindingResult result,
            Principal principal) {
        logger.info("POST '/chemical' was called with {} by user {}", chemicalInput, principal.getName());
        mapValidationErrorService.throwExceptionIfNotValid(result);
        Chemical chemical = chemicalService.createChemical(chemicalInput, principal);
        logger.info("POST '/chemical' was succesful with returned result {}", chemical);
        return new ResponseEntity<>(chemical, HttpStatus.CREATED);
    }

    @PutMapping("/chemical/{id}")
    public ResponseEntity<Chemical> updateChemical(
            @Valid @RequestBody ChemicalInput chemicalInput,
            @PathVariable  Long id,
            BindingResult result,
            Principal principal) {
        logger.info("PUT '/chemical/{id}' was called with id {} and input {} by user ", id, chemicalInput, principal.getName());
        mapValidationErrorService.throwExceptionIfNotValid(result);
        Chemical chemical = chemicalService.updateChemical(chemicalInput, id, principal);
        logger.info("PUT '/chemical/{id}' was succesful with returned result {}", chemical);
        return new ResponseEntity<>(chemical, HttpStatus.CREATED);
    }

    @GetMapping("/chemical/{id}")
    public ResponseEntity<Chemical> getChemicals(
            @PathVariable Long id,
            Principal principal) {
        logger.info("GET '/chemical/{id}' was called with id {} by user ", id, principal.getName());
        Chemical chemical = chemicalService.findById(id);
        logger.info("GET '/chemical/{id}' was succesful with returned result {}", chemical);
        return new ResponseEntity<>(chemical, HttpStatus.OK);
    }

    @DeleteMapping("/chemical/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteChemical(
            @PathVariable Long id,
            Principal principal) {
        logger.info("DELETE '/chemical/{id}' was called with id {} by {} ", id, principal.getName());
        chemicalService.deleteChemical(id, principal);
        logger.info("DELETE '/chemical/{id}' was successfull");
    }

    //CHEMICAL CATEGORY
    @PostMapping("/chem-category")
    public ResponseEntity<ChemicalCategory> createChemicalCategory(
            @Valid @RequestBody ChemicalCategoryInput categoryInput,
            BindingResult result,
            Principal principal) {
        logger.info("POST '/chem-category' was called with {}", categoryInput);
        mapValidationErrorService.throwExceptionIfNotValid(result);
        ChemicalCategory category = chemicalCategoryService.createCategory(categoryInput, principal);
        logger.info("POST '/chem-category' was succesful with returned result{}", category);
        return new ResponseEntity<>(category, HttpStatus.CREATED);
    }

    @PutMapping("/chem-category/{id}")
    public ResponseEntity<ChemicalCategory> updateChemicalCategory(
            @Valid @RequestBody ChemicalCategoryInput categoryInput,
            BindingResult result,
            @PathVariable Long id,
            Principal principal) {
        logger.info("PUT '/chem-category' was called with {}, with id {}, by {}", categoryInput, id, principal.getName());
        mapValidationErrorService.throwExceptionIfNotValid(result);
        ChemicalCategory category = chemicalCategoryService.updateCategory(categoryInput, id, principal);
        logger.info("PUT '/chem-category' was succesful with returned result{}", category);
        return new ResponseEntity<>(category, HttpStatus.CREATED);
    }

    @GetMapping("/chem-category/{id}")
    public ResponseEntity<ChemicalCategory> getChemicalCategory(
            @PathVariable Long id,
            Principal principal) {
        logger.info("GET '/chem-category/{id} was called with id {}", id);
        ChemicalCategory category = chemicalCategoryService.findById(id, principal);
        logger.info("GET '/chem-category/{id}' was succesful with {} item", category);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @DeleteMapping("/chem-category/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteChemicalCategory(
            @PathVariable Long id,
            Principal principal) {
        logger.info("DELETE '/chem-category' was called with id {}, by {}", id, principal.getName());
        chemicalCategoryService.deleteChemicalCategory(id, principal);
        logger.info("DELETE '/chem-category' was succesful with 204 status");
    }

}
