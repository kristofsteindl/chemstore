package com.ksteindl.chemstore.web;

import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.domain.input.LabInput;
import com.ksteindl.chemstore.domain.input.AppUserInput;
import com.ksteindl.chemstore.service.AppUserService;
import com.ksteindl.chemstore.service.LabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    @Autowired
    private AppUserService appUserService;

    // USER
    @PostMapping("/user")
    public ResponseEntity<AppUser> createUser(@RequestBody @Valid AppUserInput appUserInput, BindingResult result) {
        mapValidationErrorService.throwExceptionIfNotValid(result);
        AppUser appUser = appUserService.crateUser(appUserInput);
        return ResponseEntity.status(HttpStatus.CREATED).body(appUser);
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<AppUser> updateteUser(
            @RequestBody @Valid AppUserInput appUserInput,
            BindingResult result,
            @PathVariable Long id) {
        mapValidationErrorService.throwExceptionIfNotValid(result);
        AppUser appUser = appUserService.updateUser(appUserInput, id);
        return ResponseEntity.status(HttpStatus.CREATED).body(appUser);
    }

    @GetMapping("/user")
    public ResponseEntity<List<AppUser>> getAllAppUser(
            @RequestParam(value="only-active", required = false, defaultValue = "true") boolean onlyActive) {
        List<AppUser> appUsers = appUserService.getAppUsers(onlyActive);
        return ResponseEntity.ok(appUsers);
    }

    @DeleteMapping("/user/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteAppUser(@PathVariable Long id) {
        appUserService.deleteAppUser(id);
    }

    // Lab
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
    public ResponseEntity<List<Lab>> getEveryLab(
            @RequestParam(value="only-active", required = false, defaultValue = "true") boolean onlyActive) {
        List<Lab> labs = labService.getLabs(onlyActive);
        return ResponseEntity.ok().body(labs);
    }

    @DeleteMapping("/lab/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteLab(@PathVariable Long id) {
        labService.deleteLab(id);
    }

    /*
    * https://www.baeldung.com/spring-requestparam-vs-pathvariable
    * https://www.baeldung.com/spring-boot-bean-validation
    *
    * */

}
