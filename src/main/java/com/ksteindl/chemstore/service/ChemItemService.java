package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.domain.entities.*;
import com.ksteindl.chemstore.domain.input.ChemItemInput;
import com.ksteindl.chemstore.domain.repositories.ChemItemRepository;
import com.ksteindl.chemstore.exceptions.ValidationException;
import com.ksteindl.chemstore.util.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChemItemService {

    @Autowired
    private AppUserService appUserService;
    @Autowired
    private LabService labService;
    @Autowired
    private ChemicalService chemicalService;
    @Autowired
    private ManufacturerService manufacturerService;
    @Autowired
    private ChemItemRepository chemItemRepository;


    public List<ChemItem> createChemItems(ChemItemInput chemItemInput, Principal principal) {
        AppUser appUser = appUserService.getMyAppUser(principal);
        Lab lab = getLabAndValidateAuthority(chemItemInput.getLabKey(), appUser);
        LocalDate arrivalDate = getArrivalDateAndValidate(chemItemInput.getArrivalDate());
        Chemical chemical = chemicalService.findByShortName(chemItemInput.getChemicalShortName());
        Manufacturer manufacturer = manufacturerService.findById(chemItemInput.getManufacturerId());
        LocalDate expirationDateBeforeOpened = getExpirationDateBeforeOpenedAndValidate(chemItemInput.getExpirationDateBeforeOpened());
        String batchNumber = chemItemInput.getBatchNumber();
        Integer nextSeqNumber = getNextSeqNumber(chemical, batchNumber);
        ArrayList<ChemItem> chemItems = new ArrayList<>();
        for (int i = 0; i < chemItemInput.getAmount(); i++) {
            ChemItem chemItem = new ChemItem();
            chemItem.setArrivedBy(appUser);
            chemItem.setLab(lab);
            chemItem.setArrivalDate(arrivalDate);
            chemItem.setChemical(chemical);
            chemItem.setManufacturer(manufacturer);
            chemItem.setExpirationDateBeforeOpened(expirationDateBeforeOpened);
            chemItem.setBatchNumber(batchNumber);
            chemItem.setSeqNumber(nextSeqNumber + i);
            chemItem.setQuantity(chemItemInput.getQuantity());
            chemItems.add(chemItemRepository.save(chemItem));
        }
        return chemItems;

    }

    private Integer getNextSeqNumber(Chemical chemical, String batchNumber) {
        return 1 + chemItemRepository.findByChemicalAndBatchNumber(chemical, batchNumber).size();
    }

    public ChemItem findById(Long id) {
        return null;
    }

    private LocalDate getArrivalDateAndValidate(LocalDate arrivalDate) {
        if (arrivalDate == null) {
            arrivalDate = LocalDate.now();
        } else if (arrivalDate.isAfter(LocalDate.now())) {
            throw new ValidationException(Lang.CHEM_ITEM_ARRIVAL_DATE_ATTRIBUTE_NAME,
                    String.format(Lang.CHEM_ITEM_ARRIVAL_DATE_IS_FUTURE, arrivalDate));
        }
        return arrivalDate;
    }

    private LocalDate getExpirationDateBeforeOpenedAndValidate(LocalDate expirationDateBeforeOpened) {
        if (expirationDateBeforeOpened.isBefore(LocalDate.now())) {
            throw new ValidationException(String.format(Lang.CHEM_ITEM_EXP_DATE_IS_IN_PAST, expirationDateBeforeOpened));
        }
        return expirationDateBeforeOpened;
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
