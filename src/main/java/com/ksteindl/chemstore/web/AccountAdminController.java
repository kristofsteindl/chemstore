package com.ksteindl.chemstore.web;

import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.domain.input.LabInput;
import com.ksteindl.chemstore.service.LabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/account-admin")
@CrossOrigin
public class AccountAdminController {

    @Autowired
    private MapValidationErrorService mapValidationErrorService;
    @Autowired
    private LabService labService;

    @PostMapping("/lab")
    public ResponseEntity<Lab> createLab(@Valid @RequestBody LabInput labInput, BindingResult bindingResult) {
        mapValidationErrorService.throwExceptionIfNotValid(bindingResult);
        Lab lab = labService.createLab(labInput);
        return ResponseEntity.ok().body(lab);
    }

    @PutMapping("/lab/{id}")
    public ResponseEntity<Lab> updateLab(
            @Valid @RequestBody LabInput labInput,
            BindingResult bindingResult,
            @PathVariable Long id) {
        mapValidationErrorService.throwExceptionIfNotValid(bindingResult);
        Lab lab = labService.updateLab(labInput, id);
        return ResponseEntity.ok().body(lab);
    }

    @GetMapping("/lab")
    public ResponseEntity<List<Lab>> getEveryLab() {
        List<Lab> labs = labService.getEveryLab();
        return ResponseEntity.ok().body(labs);
    }

}
