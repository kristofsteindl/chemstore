package com.ksteindl.chemstore.web;

import com.ksteindl.chemstore.domain.entities.Chemical;
import com.ksteindl.chemstore.domain.entities.Manufacturer;
import com.ksteindl.chemstore.domain.input.ChemicalInput;
import com.ksteindl.chemstore.domain.input.ManufacturerInput;
import com.ksteindl.chemstore.service.ChemicalService;
import com.ksteindl.chemstore.service.ManufacturerService;
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

    @Autowired
    private MapValidationErrorService mapValidationErrorService;
    @Autowired
    private ManufacturerService manufacturerService;
    @Autowired
    private ChemicalService chemicalService;

    // MANUFACTURER
    @PostMapping("/manufacturer")
    public ResponseEntity<Manufacturer> createManufacturer(@Valid @RequestBody ManufacturerInput manufacturerInput, BindingResult result) {
        mapValidationErrorService.throwExceptionIfNotValid(result);
        Manufacturer manufacturer = manufacturerService.createManufacturer(manufacturerInput);
        return new ResponseEntity<>(manufacturer, HttpStatus.CREATED);
    }

    @PutMapping("/manufacturer/{id}")
    public ResponseEntity<Manufacturer> updateManufacturer(
            @Valid @RequestBody ManufacturerInput manufacturerInput,
            @PathVariable  Long id,
            BindingResult result) {
        mapValidationErrorService.throwExceptionIfNotValid(result);
        Manufacturer manufacturer = manufacturerService.updateManufacturer(manufacturerInput, id);
        return new ResponseEntity<>(manufacturer, HttpStatus.CREATED);
    }

    @GetMapping("/manufacturer")
    public ResponseEntity<List<Manufacturer>> getManufactures() {
        List<Manufacturer> manufacturers = manufacturerService.getManufacturers();
        return new ResponseEntity<>(manufacturers, HttpStatus.OK);
    }

    @DeleteMapping("/manufacturer/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteManufacture(@PathVariable Long id) {
       manufacturerService.deleteManufacturer(id);
    }


    //// CHEMICAL
    @PostMapping("/chemical")
    public ResponseEntity<Chemical> createChemical(@Valid @RequestBody ChemicalInput chemicalInput, BindingResult result) {
        mapValidationErrorService.throwExceptionIfNotValid(result);
        Chemical chemical = chemicalService.createChemical(chemicalInput);
        return new ResponseEntity<>(chemical, HttpStatus.CREATED);
    }

    @PutMapping("/chemical/{id}")
    public ResponseEntity<Chemical> updateChemical(
            @Valid @RequestBody ChemicalInput chemicalInput,
            @PathVariable  Long id,
            BindingResult result) {
        mapValidationErrorService.throwExceptionIfNotValid(result);
        Chemical chemical = chemicalService.updateChemical(chemicalInput, id);
        return new ResponseEntity<>(chemical, HttpStatus.CREATED);
    }

    @GetMapping("/chemical")
    public ResponseEntity<List<Chemical>> getChemicals() {
        List<Chemical> chemicals = chemicalService.getChemicals();
        return new ResponseEntity<>(chemicals, HttpStatus.OK);
    }

    @DeleteMapping("/chemical/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteChemical(@PathVariable Long id) {
        chemicalService.deleteChemical(id);
    }
}
