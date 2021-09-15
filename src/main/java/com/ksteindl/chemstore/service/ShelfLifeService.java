package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.domain.entities.*;
import com.ksteindl.chemstore.domain.input.ShelfLifeInput;
import com.ksteindl.chemstore.domain.repositories.ShelfLifeRepositoy;
import com.ksteindl.chemstore.exceptions.ResourceNotFoundException;
import com.ksteindl.chemstore.exceptions.ValidationException;
import com.ksteindl.chemstore.util.Lang;
import com.ksteindl.chemstore.web.MapValidationErrorService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import javax.validation.Valid;
import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ShelfLifeService implements UniqueEntityService<ShelfLifeInput>{

    private static final Logger logger = LogManager.getLogger(ManufacturerService.class);

    @Autowired
    private MapValidationErrorService mapValidationErrorService;
    @Autowired
    private ShelfLifeRepositoy shelfLifeRepositoy;
    @Autowired
    private LabService labService;
    @Autowired
    private ChemTypeService chemTypeService;
    @Autowired
    private AppUserService appUserService;

    public ShelfLife createShelfLife(@Valid ShelfLifeInput shelfLifeInput, BindingResult result, Principal principal) {
        ShelfLife shelfLife = new ShelfLife();
        ShelfLifeVlidatorWrapper validatorWrapper = ShelfLifeVlidatorWrapper.builder()
                .shelfLifeInput(shelfLifeInput)
                .shelfLife(shelfLife)
                .id(null)
                .result(result)
                .principal(principal)
                .build();
        return createOrUpdateShelfLife(validatorWrapper);
    }

    public ShelfLife updateShelfLife(@Valid ShelfLifeInput shelfLifeInput, Long id, BindingResult result, Principal principal) {
        ShelfLife shelfLife = findById(id);
        ShelfLifeVlidatorWrapper validatorWrapper = ShelfLifeVlidatorWrapper.builder()
                .shelfLifeInput(shelfLifeInput)
                .shelfLife(shelfLife)
                .id(id)
                .result(result)
                .principal(principal)
                .build();
        return createOrUpdateShelfLife(validatorWrapper);
    }

    private ShelfLife createOrUpdateShelfLife(ShelfLifeVlidatorWrapper validatorWrapper) {
        ShelfLife shelfLife = validatorWrapper.shelfLife;
        ShelfLifeInput shelfLifeInput = validatorWrapper.shelfLifeInput;
        mapValidationErrorService.throwExceptionIfNotValid(validatorWrapper.result);
        ChemType chemType = chemTypeService.findById(shelfLifeInput.getChemTypeId());
        Lab lab = getAndValidateLab(shelfLifeInput.getLabKey(), validatorWrapper.principal);
        throwExceptionIfNotUnique(shelfLifeInput, validatorWrapper.id);
        shelfLife.setDuration(convertToDuration(shelfLifeInput));
        shelfLife.setChemType(chemType);
        shelfLife.setLab(lab);
        shelfLifeRepositoy.save(shelfLife);
        return findById(shelfLife.getId());
    }

    public Optional<ShelfLife> findByLabAndChemType(Lab lab, ChemType chemType) {
        return shelfLifeRepositoy.findByLabAndChemType(lab, chemType);
    }

    private Duration convertToDuration(ShelfLifeInput shelfLifeInput) {
        Integer amount = shelfLifeInput.getAmount();
        switch (shelfLifeInput.getUnit()) {
            case "d": return Duration.ofDays(amount);
            case "w": return Duration.between(LocalDateTime.now(), LocalDateTime.now().plusWeeks(amount));
            case "m": return Duration.between(LocalDateTime.now(), LocalDateTime.now().plusMonths(amount));
            case "y": return Duration.between(LocalDateTime.now(), LocalDateTime.now().plusYears(amount));
        }
        throw new RuntimeException("Invalid shelfLifeInput.unit. Must be 'd', 'w', 'm' or 'y'");
    }

    private Lab getAndValidateLab(String labKey, Principal principal) {
        Lab lab = labService.findLabByKey(labKey);
        AppUser appUser = appUserService.getMyAppUser(principal);
        if (!lab.getLabManagers().stream().anyMatch(manager -> manager.equals(appUser))
                && !appUser.getLabsAsAdmin().stream().anyMatch(labAsAdmin -> labAsAdmin.equals(lab))) {
            throw new ValidationException(String.format(Lang.SHELF_TIME_SET_FORBIDDEN, lab.getName()));
        }
        return lab;
    }


    public List<ShelfLife> getShelfLifesForLab(String labKey, Principal principal) {
        return getShelfLifesForLab(labKey, true, principal);
    }

    public List<ShelfLife> getShelfLifesForLab(String labKey, boolean onlyActive, Principal principal) {
        Lab lab = getAndValidateLab(labKey, principal);
        return onlyActive ?
                shelfLifeRepositoy.findByLab(lab) :
                shelfLifeRepositoy.findByLabOnlyActive(lab);
    }

    public List<ShelfLife> getShelfLifes() {
        return getShelfLifes(true);
    }

    public List<ShelfLife> getShelfLifes(boolean onlyActive) {
        return onlyActive ?
                shelfLifeRepositoy.findAllActive() :
                shelfLifeRepositoy.findAll();
    }

    public void deleteSHelfLife(Long id) {
        ShelfLife shelfLife = findById(id);
        shelfLife.setDeleted(true);
        shelfLifeRepositoy.save(shelfLife);
    }

    public ShelfLife findById(Long id) {
        return findById(id, true);
    }

    public ShelfLife findById(Long id, Boolean onlyActive) {
        ShelfLife shelfLife = shelfLifeRepositoy.findById(id).orElseThrow(() -> new ResourceNotFoundException(Lang.MANUFACTURER_ENTITY_NAME, id));
        if (onlyActive && shelfLife.getDeleted()) {
            throw new ValidationException(String.format(Lang.SHELF_LIFE_ALREADY_DELETED, shelfLife.getChemType().getName(), shelfLife.getLab().getName()));
        }
        return shelfLife;
    }

    @Override
    public void throwExceptionIfNotUnique(ShelfLifeInput input, Long id) {
        ChemType chemType = chemTypeService.findById(input.getChemTypeId());
        Lab lab = labService.findLabByKey(input.getLabKey());
        Optional<ShelfLife> optional = findByLabAndChemType(lab, chemType);
        optional.ifPresent(shelfLife -> {
            if (!shelfLife.getId().equals(id)) {
                throw new ValidationException(
                        String.format(Lang.SHELF_LIFE_ALREADY_EXISTS,
                                chemType.getName(),
                                lab.getName()));
            }
        });
    }

}
