package com.ksteindl.chemstore.web;

import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.service.AppUserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lab-manager")
@CrossOrigin
public class LabManagerController {

    private static final Logger logger = LogManager.getLogger(LabManagerController.class);

    @Autowired
    private AppUserService appUserService;
    @Autowired
    private MapValidationErrorService mapValidationErrorService;

    @GetMapping("/user")
    public Map<String, List<AppUser>> getUersFromManagedLabs(Principal principal) {
        logger.info("'/lab-manager/user' was called by {}", principal.getName());
        return appUserService.getUsersFromManagedLabs(principal);
    }

    @GetMapping("/user/{labKey}")
    public List<AppUser> getUersFromManagedLab(@PathVariable String labKey, Principal principal) {
        logger.info("'/lab-manager/user/{labKey}' was called by {}", principal.getName());
        return appUserService.getUsersFromManagedLab(principal, labKey);
    }


}
