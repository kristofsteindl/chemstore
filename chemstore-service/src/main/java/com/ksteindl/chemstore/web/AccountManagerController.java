package com.ksteindl.chemstore.web;

import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.domain.input.AppUserInput;
import com.ksteindl.chemstore.domain.input.AppUserQuery;
import com.ksteindl.chemstore.domain.input.LabInput;
import com.ksteindl.chemstore.service.AppUserService;
import com.ksteindl.chemstore.service.LabService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("api/account")
@CrossOrigin
public class AccountManagerController {

    private static final Logger logger = LogManager.getLogger(AccountManagerController.class);

    @Autowired
    private MapValidationErrorService mapValidationErrorService;
    @Autowired
    private LabService labService;
    @Autowired
    private AppUserService appUserService;

    // USER
    @PostMapping("/user")
    public ResponseEntity<AppUser> createUser(
            @RequestBody @Valid AppUserInput appUserInput, 
            BindingResult result,
            Principal principal) {
        logger.info("POST '/user' was called with {}", appUserInput);
        mapValidationErrorService.throwExceptionIfNotValid(result);
        AppUser appUser = appUserService.createUser(appUserInput, principal);
        logger.info("POST '/user' was succesful with returned result{}", appUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(appUser);
    }

    @PatchMapping("/user/{id}/restore-password")
    public ResponseEntity<AppUser> restorePassword(
            @PathVariable Long id,
            Principal principal) {
        logger.info("PATCH '/user/{id}/restore-password' was called with id {}", id);
        AppUser appUser = appUserService.restorePassword(id, principal);
        logger.info("PATCH '/user/{id}/restore-password' was succesful with returned result {}", appUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(appUser);
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<AppUser> updateteUser(
            @RequestBody @Valid AppUserInput appUserInput,
            BindingResult result,
            @PathVariable Long id,
            Principal principal) {
        logger.info("PUT '/user/{id}' was called with id {} and input {}", id, appUserInput);
        mapValidationErrorService.throwExceptionIfNotValid(result);
        AppUser appUser = appUserService.updateUser(appUserInput, id, principal);
        logger.info("PUT '/user/{id}' was succesful with returned result{}", appUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(appUser);
    }

    @GetMapping("/user")
    public ResponseEntity<List<AppUser>> getAllAppUser(
            @RequestParam(value="only-active", required = false, defaultValue = "true") boolean onlyActive) {
        logger.info("GET '/user' was called, with onlyActive {}", onlyActive);
        AppUserQuery appUserQuery = AppUserQuery.builder().onlyActive(onlyActive).build();
        List<AppUser> appUsers = appUserService.getAppUsers(appUserQuery);
        logger.info("GET '/user' was succesful with {} item", appUsers.size());
        return ResponseEntity.ok(appUsers);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<AppUser> getUserById(
            @PathVariable Long id) {
        logger.info("GET '/user/{id}' was called, with id {}", id);
        AppUser appUser = appUserService.findById(id);
        logger.info("GET '/user/{id}'was succesful with {} item", appUser);
        return ResponseEntity.ok(appUser);
    }

    @DeleteMapping("/user/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteAppUser(
            @PathVariable Long id,
            Principal principal) {
        logger.info("DELETE '/user/{id}' was called with id {}", id);
        appUserService.deleteAppUser(id, principal);
        logger.info("DELETE '/user/{id}' was successfull");
    }

    // Lab
    @PostMapping("/lab")
    public ResponseEntity<Lab> createLab(
            @Valid @RequestBody LabInput labInput,
            BindingResult bindingResult,
            Principal principal) {
        logger.info("POST '/lab' was called with {}", labInput);
        mapValidationErrorService.throwExceptionIfNotValid(bindingResult);
        Lab lab = labService.createLab(labInput, principal);
        logger.info("POST '/lab' was succesful with returned result{}", lab);
        return ResponseEntity.status(HttpStatus.CREATED).body(lab);
    }

    @PutMapping("/lab/{id}")
    public ResponseEntity<Lab> updateLab(
            @Valid @RequestBody LabInput labInput,
            BindingResult bindingResult,
            @PathVariable Long id,
            Principal principal) {
        logger.info("PUT '/lab/{id}' was called with id {} and input {}", id, labInput);
        mapValidationErrorService.throwExceptionIfNotValid(bindingResult);
        Lab lab = labService.updateLab(labInput, id, principal);
        logger.info("PUT '/lab/{id}' was succesful with returned result{}", lab);
        return ResponseEntity.ok().body(lab);
    }

    @GetMapping("/lab/{id}")
    public ResponseEntity<Lab> getLab(
            @PathVariable Long id) {
        logger.info("GET '/lab/{id}' was called with id {} ", id);
        Lab lab = labService.findById(id);
        logger.info("GET '/lab/{id}' was succesful with returned result{}", lab);
        return ResponseEntity.ok().body(lab);
    }

    @GetMapping("/lab")
    public ResponseEntity<List<Lab>> getEveryLab(
            @RequestParam(value="only-active", required = false, defaultValue = "true") boolean onlyActive) {
        logger.info("GET '/lab' was called");
        List<Lab> labs = labService.getLabs(onlyActive);
        logger.info("GET '/lab' was succesful with {} item", labs.size());
        return ResponseEntity.ok().body(labs);
    }

    @DeleteMapping("/lab/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteLab(@PathVariable Long id, Principal principal) {
        logger.info("DELETE '/lab/{id}' was called with id {}", id);
        labService.deleteLab(id, principal);
        logger.info("DELETE '/lab/{id}' was successfull");
    }

    /*
    * https://www.baeldung.com/spring-requestparam-vs-pathvariable
    * https://www.baeldung.com/spring-boot-bean-validation
    *
    * */

}
