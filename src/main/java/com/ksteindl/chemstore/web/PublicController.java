package com.ksteindl.chemstore.web;

import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.entities.Chemical;
import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.domain.entities.Manufacturer;
import com.ksteindl.chemstore.service.AppUserService;
import com.ksteindl.chemstore.service.ChemicalService;
import com.ksteindl.chemstore.service.LabService;
import com.ksteindl.chemstore.service.ManufacturerService;
import com.ksteindl.chemstore.service.wrapper.AppUserCard;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/public") // TODO rename me
@CrossOrigin
public class PublicController {

    private static final Logger logger = LogManager.getLogger(PublicController.class);

    @Autowired
    private AppUserService appUserService;
    @Autowired
    private LabService labService;
    @Autowired
    private ChemicalService chemicalService;
    @Autowired
    private ManufacturerService manufacturerService;

    @GetMapping("/user")
    public List<AppUserCard> getAllAppUser() {
        return appUserService.getAppUserCards();
    }

    @GetMapping("/user/me")
    public AppUser getMyAppUser(Principal principal) {
        return appUserService.getMyAppUser(principal);
    }

    @GetMapping("/lab")
    public List<Lab> getEveryLab(
            @RequestParam(value="only-active", required = false, defaultValue = "true") boolean onlyActive) {
        logger.info("GET '/api/public/lab' was called");
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
