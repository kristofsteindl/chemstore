package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.entities.ChemType;
import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.domain.entities.ShelfLife;
import com.ksteindl.chemstore.domain.input.ShelfLifeInput;
import com.ksteindl.chemstore.domain.repositories.ShelfLifeRepositoy;
import com.ksteindl.chemstore.exceptions.ResourceNotFoundException;
import com.ksteindl.chemstore.exceptions.ValidationException;
import com.ksteindl.chemstore.util.Lang;
import com.ksteindl.chemstore.web.MapValidationErrorService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public ShelfLife createShelfLife(ShelfLifeInput shelfLifeInput,Principal principal) {
        ShelfLife shelfLife = new ShelfLife();
        ShelfLifeValidatorWrapper validatorWrapper = ShelfLifeValidatorWrapper.builder()
                .shelfLifeInput(shelfLifeInput)
                .shelfLife(shelfLife)
                .id(null)
                .principal(principal)
                .build();
        return createOrUpdateShelfLife(validatorWrapper);
    }

    public ShelfLife updateShelfLife(@Valid ShelfLifeInput shelfLifeInput, Long id, Principal principal) {
        ShelfLife shelfLife = findById(id);
        ShelfLifeValidatorWrapper validatorWrapper = ShelfLifeValidatorWrapper.builder()
                .shelfLifeInput(shelfLifeInput)
                .shelfLife(shelfLife)
                .id(id)
                .principal(principal)
                .build();
        return createOrUpdateShelfLife(validatorWrapper);
    }


    public ShelfLife findById(Long id) {
        return findById(id, true);
    }

    public ShelfLife findById(Long id, Boolean onlyActive) {
        ShelfLife shelfLife = shelfLifeRepositoy.findById(id).orElseThrow(() -> new ResourceNotFoundException(Lang.MANUFACTURER_ENTITY_NAME, id));
        if (onlyActive && shelfLife.getDeleted()) {
            throw new ResourceNotFoundException(String.format(Lang.SHELF_LIFE_ALREADY_DELETED, shelfLife.getChemType().getName(), shelfLife.getLab().getName()));
        }
        return shelfLife;
    }

    public Optional<ShelfLife> findByLabAndChemType(Lab lab, ChemType chemType) {
        return shelfLifeRepositoy.findByLabAndChemType(lab, chemType);
    }

    public List<ShelfLife> findByLab(String labKey, Principal principal) {
        return findByLab(labKey, true, principal);
    }


    public List<ShelfLife> findByLab(String labKey, boolean onlyActive, Principal principal) {
        Lab lab = getAndValidateLab(labKey, principal);
        return onlyActive ?
                shelfLifeRepositoy.findByLabOnlyActive(lab) :
                shelfLifeRepositoy.findByLab(lab);
    }

    public List<ShelfLife> getShelfLifes() {
        return getShelfLifes(true);
    }

    public List<ShelfLife> getShelfLifes(boolean onlyActive) {
        return onlyActive ?
                shelfLifeRepositoy.findAllActive() :
                shelfLifeRepositoy.findAll();
    }

    public void deleteShelfLife(Long id, Principal principal) {
        ShelfLife shelfLife = findById(id);
        getAndValidateLab(shelfLife.getLab().getKey(), principal);
        shelfLife.setDeleted(true);
        shelfLifeRepositoy.save(shelfLife);
    }

    private ShelfLife createOrUpdateShelfLife(ShelfLifeValidatorWrapper validatorWrapper) {
        ShelfLife shelfLife = validatorWrapper.shelfLife;
        ShelfLifeInput shelfLifeInput = validatorWrapper.shelfLifeInput;
        ChemType chemType = chemTypeService.findById(shelfLifeInput.getChemTypeId());
        Lab lab = getAndValidateLab(shelfLifeInput.getLabKey(), validatorWrapper.principal);
        throwExceptionIfNotUnique(shelfLifeInput, validatorWrapper.id);
        shelfLife.setDuration(convertToDuration(shelfLifeInput));
        shelfLife.setChemType(chemType);
        shelfLife.setLab(lab);
        shelfLifeRepositoy.save(shelfLife);
        return findById(shelfLife.getId());
    }



    private Duration convertToDuration(ShelfLifeInput shelfLifeInput) {
        Integer amount = shelfLifeInput.getAmount();
        switch (shelfLifeInput.getUnit()) {
            case "d": return Duration.ofDays(amount);
            case "w": return Duration.between(LocalDateTime.now(), LocalDateTime.now().plusWeeks(amount));
            case "m": return Duration.between(LocalDateTime.now(), LocalDateTime.now().plusMonths(amount));
            case "y": return Duration.between(LocalDateTime.now(), LocalDateTime.now().plusYears(amount));
        }
        throw new ValidationException("Invalid shelfLifeInput.unit. Must be 'd', 'w', 'm' or 'y'");
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
