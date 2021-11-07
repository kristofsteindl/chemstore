package com.ksteindl.chemstore.web;

import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.entities.Chemical;
import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.domain.entities.Manufacturer;
import com.ksteindl.chemstore.domain.input.PasswordInput;
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

    @PatchMapping("/user")
    public ResponseEntity<AppUser> updateteLoggedInUserPassword(
            @Valid @RequestBody PasswordInput passwordInput,
            BindingResult result,
            Principal principal) {
        logger.info("PATCH '/api/logged-in/user' was called with and input {}", passwordInput);
        mapValidationErrorService.throwExceptionIfNotValid(result);
        AppUser appUser = appUserService.updatePassword(passwordInput, principal);
        logger.info("PATCH '/user/{id}' was succesful with returned result{}", appUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(appUser);
    }

    @GetMapping("/user")
    public List<AppUserCard> getAllAppUser() {
        logger.info("GET '/api/public/user' was called");
        List<AppUserCard> users =  appUserService.getAppUserCards();
        logger.info("GET '/api/public/user' is returning with {} item", users.size());
        return appUserService.getAppUserCards();
    }

    @GetMapping("/user/me")
    public AppUser getMyAppUser(Principal principal) {
        return appUserService.getMyAppUser(principal);
    }

    @GetMapping("/lab")
    public List<Lab> getEveryLab(
            @RequestParam(value="only-active", required = false, defaultValue = "true") boolean onlyActive) {
        logger.info("GET '/api/public/lab' was called with onlyActive=" + onlyActive);
        List<Lab> labs = labService.getLabs(onlyActive);
        logger.info("GET '/api/public/lab' was succesful with {} item", labs.size());
        return labs;
    }

    @GetMapping("/manufacturer")
    public List<Manufacturer> getManufactures() {
        logger.info("GET '/api/public/manufacturer' was called");
        List<Manufacturer> manufacturers = manufacturerService.getManufacturers();
        logger.info("GET '/api/public/manufacturer' was succesful with {} item", manufacturers.size());
        return manufacturers;
    }

    @GetMapping("/chemical")
    public List<Chemical> getChemicals() {
        logger.info("GET '/api/public/chemical' was called");
        List<Chemical> chemicals = chemicalService.getChemicals();
        logger.info("GET '/api/public/chemical' was succesful with {} item", chemicals.size());
        return chemicals;
    }
}
