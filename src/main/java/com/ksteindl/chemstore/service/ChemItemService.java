package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.entities.ChemItem;
import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.domain.input.ChemItemInput;
import com.ksteindl.chemstore.exceptions.ValidationException;
import com.ksteindl.chemstore.util.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;

@Service
public class ChemItemService {

    @Autowired
    private AppUserService appUserService;
    @Autowired
    private LabService labService;


    public ChemItem createChemItem(ChemItemInput chemItemInput, Principal principal) {
        AppUser appUser = appUserService.getMyAppUser(principal);
        Lab lab = getLabAndValidateAuthority(chemItemInput.getLabKey(), appUser);
        LocalDate arrivalDate = getArrivalDateAndValidate(chemItemInput.getArrivalDate());
        return null;

    }

    public ChemItem findById(Long id) {
        return null;
    }

    private LocalDate getArrivalDateAndValidate(LocalDate arrivalDate) {
        if (arrivalDate == null) {
            arrivalDate = LocalDate.now();
        } else if (arrivalDate.isAfter(LocalDate.now())) {
            throw new ValidationException(Lang.CHEM_ITEM_ARRIVAL_DATE_ATTRIBUTE_NAME,
                    String.format(Lang.CHEM_ITEM_CREATION_NOT_AUTHORIZED, arrivalDate));
        }
        return arrivalDate;
    }

    private Lab getLabAndValidateAuthority(String labKey, AppUser appUser) {
        String username = appUser.getUsername();
        Lab lab = labService.findLabByKey(labKey);
        if (lab.getLabManagers().stream().anyMatch(manager -> manager.getUsername().equals(username))) {
            return lab;
        }
        if (appUser.getLabsAsAdmin().stream().anyMatch(labAsAdmin -> labAsAdmin.equals(lab))) {
            return lab;
        }
        if (appUser.getLabsAsUser().stream().anyMatch(labAsUser -> labAsUser.equals(lab))) {
            return lab;
        }
        throw new ValidationException(Lang.CHEM_ITEM_LAB_KEY_ATTRIBUTE_NAME, String.format(Lang.CHEM_ITEM_CREATION_NOT_AUTHORIZED, lab.getName(), username));

    }
}
