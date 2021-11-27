package com.ksteindl.chemstore.web;

import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.entities.Chemical;
import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.domain.entities.Manufacturer;
import com.ksteindl.chemstore.domain.input.PasswordInput;
import com.ksteindl.chemstore.security.role.Role;
import com.ksteindl.chemstore.security.role.RoleService;
import com.ksteindl.chemstore.service.AppUserService;
import com.ksteindl.chemstore.service.ChemicalService;
import com.ksteindl.chemstore.service.LabService;
import com.ksteindl.chemstore.service.ManufacturerService;
import com.ksteindl.chemstore.service.wrapper.AppUserCard;
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
@RequestMapping("/api/logged-in")
@CrossOrigin
public class LoggedInController {

    private static final Logger logger = LogManager.getLogger(LoggedInController.class);

    @Autowired
    private MapValidationErrorService mapValidationErrorService;
    @Autowired
    private AppUserService appUserService;
    @Autowired
    private LabService labService;
    @Autowired
    private ChemicalService chemicalService;
    @Autowired
    private ManufacturerService manufacturerService;
    @Autowired
    private RoleService roleService;

    @PatchMapping("/user")
    public ResponseEntity<AppUser> updateteLoggedInUserPassword(
            @Valid @RequestBody PasswordInput passwordInput,
            BindingResult result,
            Principal principal) {
        logger.info("PATCH '/api/logged-in/user' was called with and input {}", passwordInput);
        mapValidationErrorService.throwExceptionIfNotValid(result);
        AppUser appUser = appUserService.updatePassword(passwordInput, principal);
        logger.info("PATCH '/api/logged-in/user/{id}' was succesful with returned result{}", appUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(appUser);
    }

    @GetMapping("/user")
    public List<AppUserCard> getAllAppUser() {
        logger.info("GET '/api/public/user' was called");
        List<AppUserCard> users =  appUserService.getAppUserCards();
        logger.info("GET '/api/logged-in/user' is returning with {} item", users.size());
        return appUserService.getAppUserCards();
    }

    @GetMapping("/user/me")
    public AppUser getMyAppUser(Principal principal) {
        return appUserService.getMyAppUser(principal);
    }

    @GetMapping("/lab")
    public List<Lab> getEveryLab(
            @RequestParam(value="only-active", required = false, defaultValue = "true") boolean onlyActive) {
        logger.info("GET '/api/logged-in/lab' was called with onlyActive=" + onlyActive);
        List<Lab> labs = labService.getLabs(onlyActive);
        logger.info("GET '/api/logged-in/lab' was succesful with {} item", labs.size());
        return labs;
    }

    @GetMapping("/manufacturer")
    public List<Manufacturer> getManufactures() {
        logger.info("GET '/api/logged-in/manufacturer' was called");
        List<Manufacturer> manufacturers = manufacturerService.getManufacturers();
        logger.info("GET '/api/logged-in/manufacturer' was succesful with {} item", manufacturers.size());
        return manufacturers;
    }

    @GetMapping("/chemical/{labKey}")
    public List<Chemical> getChemicals(
            @PathVariable String labKey,
            Principal user) {
        logger.info("GET '/api/logged-in/chemical' was called with labKey {} by user {}", labKey, user.getName());
        List<Chemical> chemicals = chemicalService.getChemicalsForUser(labKey, user);
        logger.info("GET '/api/logged-in/chemical' was succesful with {} item", chemicals.size());
        return chemicals;
    }

    @GetMapping("/role")
    public List<Role> getRoles() {
        logger.info("GET '/api/logged-in/role' was called");
        List<Role> roles = roleService.getRoles();
        logger.info("GET '/api/logged-in/role' was succesful with {} item", roles.size());
        return roles;
    }
}
