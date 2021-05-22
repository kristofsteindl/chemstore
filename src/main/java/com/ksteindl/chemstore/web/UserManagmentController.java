package com.ksteindl.chemstore.web;

import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.input.RegUserInput;
import com.ksteindl.chemstore.domain.input.UpdateUserInput;
import com.ksteindl.chemstore.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/user")
@CrossOrigin
public class UserManagmentController {

    @Autowired
    private MapValidationErrorService mapValidationErrorService;
    @Autowired
    private AppUserService appUserService;


    @PostMapping
    public ResponseEntity<AppUser> createUser(@RequestBody @Valid RegUserInput regUserInput, BindingResult result) {
        mapValidationErrorService.throwExceptionIfNotValid(result);
        AppUser appUser = appUserService.crateUser(regUserInput);
        return ResponseEntity.status(HttpStatus.CREATED).body(appUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppUser> updateteUser(
            @RequestBody @Valid UpdateUserInput updateUserInput,
            BindingResult result,
            @PathVariable Long id) {
        mapValidationErrorService.throwExceptionIfNotValid(result);
        AppUser appUser = appUserService.updateUser(updateUserInput, id);
        return ResponseEntity.status(HttpStatus.CREATED).body(appUser);
    }

    @GetMapping
    public ResponseEntity<List<AppUser>> getAllAppUser() {
        List<AppUser> appUsers = appUserService.getAllAppUser();
        return ResponseEntity.ok(appUsers);
    }

    @DeleteMapping("/{id}")
    public void deleteAppUser() {
        List<AppUser> appUsers = appUserService.getAllAppUser();
        return ResponseEntity.ok(appUsers);
    }


}