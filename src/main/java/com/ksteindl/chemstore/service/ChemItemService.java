package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.domain.entities.*;
import com.ksteindl.chemstore.domain.input.ChemItemInput;
import com.ksteindl.chemstore.domain.repositories.ChemItemRepository;
import com.ksteindl.chemstore.exceptions.ValidationException;
import com.ksteindl.chemstore.util.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChemItemService {

    //This can be moved somewhere else, eg ./etc/chemstore or ./home/user/chemstore
    public static final String UNIT_FILE_NAME = "unit.txt";

    private static final Logger logger = LoggerFactory.getLogger(ChemItemService.class);

    public List<String> units;
    public static final List<String> DEFAULT_UNITS = List.of(
            "ug", "mg", "g", "kg",
            "ul", "ml", "l");

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
        String unit = getUnitAndValidate(chemItemInput.getUnit());

        ChemItem chemItemTemplate = new ChemItem();

        chemItemTemplate.setArrivedBy(appUser);
        chemItemTemplate.setLab(lab);
        chemItemTemplate.setArrivalDate(arrivalDate);
        chemItemTemplate.setChemical(chemical);
        chemItemTemplate.setManufacturer(manufacturer);
        chemItemTemplate.setExpirationDateBeforeOpened(expirationDateBeforeOpened);
        chemItemTemplate.setUnit(unit);
        chemItemTemplate.setBatchNumber(chemItemInput.getBatchNumber());
        chemItemTemplate.setQuantity(chemItemInput.getQuantity());

        return createBatchedChemItems(chemItemTemplate, chemItemInput.getAmount());
    }

    private String getUnitAndValidate(String unit) {
        if (!units.contains(unit)) {
            throw new ValidationException(String.format(Lang.INVALID_UNIT, unit, units.toString()));
        }
        return unit;
    }

    private List<ChemItem> createBatchedChemItems(ChemItem chemItemTemplate, Integer amount) {
        Integer nextSeqNumber = getNextSeqNumber(chemItemTemplate);
        ArrayList<ChemItem> chemItems = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            chemItemTemplate.setSeqNumber(nextSeqNumber + i);
            chemItems.add(chemItemRepository.save(chemItemTemplate));
        }
        return chemItems;
    }


    @PostConstruct
    private void loadUnits() {
        try {
            units = Files.readAllLines(Paths.get(UNIT_FILE_NAME), StandardCharsets.UTF_8);
            logger.info("units loaded succesfully from: " + UNIT_FILE_NAME);
        }
        catch (IOException exception) {
            logger.error("IOException is thrown when tríing to read units from " + UNIT_FILE_NAME, exception);
            units = DEFAULT_UNITS;
        }
        logger.info("units are: " + units);
    }

    private Integer getNextSeqNumber(ChemItem chemItemTemplate) {
        Lab lab = chemItemTemplate.getLab();
        Chemical chemical = chemItemTemplate.getChemical();
        String batchNumber = chemItemTemplate.getBatchNumber();
        Sort sortBySeqDesc = Sort.by(Sort.Direction.DESC, "seqNumber");
        List<ChemItem> equalChemItems = chemItemRepository.findEqualChemItems(lab, chemical, batchNumber, sortBySeqDesc);
        if (equalChemItems.size() == 0) {
           return 1;
        }
        return 1 + equalChemItems.get(0).getSeqNumber();
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
