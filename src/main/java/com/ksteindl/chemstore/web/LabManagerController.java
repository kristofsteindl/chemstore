package com.ksteindl.chemstore.web;

import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lab-manager")
@CrossOrigin
public class LabManagerController {

    @Autowired
    private AppUserService appUserService;

    @GetMapping("/user")
    public Map<String, List<AppUser>> getUersFromMyLabs(Principal principal) {
        return appUserService.getUsersFromMyLabs(principal);
    }

    @GetMapping("/user/{labKey}")
    public List<AppUser> getUersFromMyLabs(@PathVariable String labKey, Principal principal) {
        return appUserService.getUsersFromManagedLab(principal, labKey);
    }


}
