package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.domain.entities.ChemicalCategory;
import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.domain.input.ChemicalCategoryInput;
import com.ksteindl.chemstore.domain.repositories.ChemicalCategoryRepositoy;
import com.ksteindl.chemstore.domain.repositories.ChemicalRepository;
import com.ksteindl.chemstore.exceptions.ResourceNotFoundException;
import com.ksteindl.chemstore.exceptions.ValidationException;
import com.ksteindl.chemstore.util.Lang;
import lombok.Builder;
import lombok.Data;
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
public class ChemicalCategoryService implements UniqueEntityService<ChemicalCategoryInput>{

    private static final Logger logger = LogManager.getLogger(ChemicalCategoryService.class);

    @Autowired
    private ChemicalCategoryRepositoy chemicalCategoryRepositoy;
    @Autowired
    private LabService labService;
    @Autowired
    private ChemicalRepository chemicalRepository;

    public ChemicalCategory createCategory(ChemicalCategoryInput chemicalCategoryInput, Principal principal) {
        ChemicalCategory chemicalCategory = new ChemicalCategory();
        ChemicalCategoryValidatorWrapper validatorWrapper = ChemicalCategoryValidatorWrapper.builder()
                .chemicalCategoryInput(chemicalCategoryInput)
                .chemicalCategory(chemicalCategory)
                .id(null)
                .principal(principal)
                .build();
        return createOrUpdateCategory(validatorWrapper);
    }

    public ChemicalCategory updateCategory(@Valid ChemicalCategoryInput chemicalCategoryInput, Long id, Principal principal) {
        ChemicalCategory category = getById(id);
        ChemicalCategoryValidatorWrapper validatorWrapper = ChemicalCategoryValidatorWrapper.builder()
                .chemicalCategoryInput(chemicalCategoryInput)
                .chemicalCategory(category)
                .id(id)
                .principal(principal)
                .build();
        return createOrUpdateCategory(validatorWrapper);
    }

    private ChemicalCategory createOrUpdateCategory(ChemicalCategoryValidatorWrapper validatorWrapper) {
        ChemicalCategory category = validatorWrapper.chemicalCategory;
        ChemicalCategoryInput input = validatorWrapper.chemicalCategoryInput;
        Lab lab = labService.findLabForAdmin(input.getLabKey(), validatorWrapper.principal);
        throwExceptionIfNotUnique(input, validatorWrapper.id);
        category.setLab(lab);
        category.setName(input.getName());
        category.setShelfLife(convertToDuration(input));
        chemicalCategoryRepositoy.save(category);
        return getById(category.getId());
    }


    public ChemicalCategory getById(Long id) {
        ChemicalCategory category = findById(id);
        if (category.getDeleted()) {
            throw new ResourceNotFoundException(String.format(Lang.CHEMICAL_CATEGORY_IS_DELETED, category.getName(), category.getLab().getName()));
        }
        return category;
    }

    public ChemicalCategory findById(Long id) {
        return chemicalCategoryRepositoy.findById(id).orElseThrow(() -> new ResourceNotFoundException(Lang.CHEMICAL_CATEGORY_ENTITY_NAME, id));
    }

    public List<ChemicalCategory> getByLab(String labKey, Principal principal) {
        return findByLab(labKey, true, principal);
    }


    public List<ChemicalCategory> findByLab(String labKey, boolean onlyActive, Principal principal) {
        Lab lab = labService.findLabForAdmin(labKey, principal);
        return onlyActive ?
                chemicalCategoryRepositoy.findByLabOnlyActive(lab) :
                chemicalCategoryRepositoy.findByLab(lab);
    }

    public void deleteChemicalCategory(Long id, Principal principal) {
        ChemicalCategory category = getById(id);
        labService.validateLabForAdmin(category.getLab(), principal);
        chemicalRepository.findByCategory(category).stream().forEach(chemical -> {
            chemical.setCategory(null);
            chemicalRepository.save(chemical);
            });
        category.setDeleted(true);
        chemicalCategoryRepositoy.save(category);
    }

    private Duration convertToDuration(ChemicalCategoryInput chemicalCategoryInput) {
        Integer amount = chemicalCategoryInput.getAmount();
        switch (chemicalCategoryInput.getUnit()) {
            case "d": return Duration.ofDays(amount);
            case "w": return Duration.between(LocalDateTime.now(), LocalDateTime.now().plusWeeks(amount));
            case "m": return Duration.between(LocalDateTime.now(), LocalDateTime.now().plusMonths(amount));
            case "y": return Duration.between(LocalDateTime.now(), LocalDateTime.now().plusYears(amount));
        }
        throw new ValidationException("Invalid shelfLifeInput.unit. Must be 'd', 'w', 'm' or 'y'");
    }


    @Override
    public void throwExceptionIfNotUnique(ChemicalCategoryInput input, Long id) {
        String labKey = input.getLabKey();
        Optional<ChemicalCategory> optional = chemicalCategoryRepositoy.findByLabKeyAndName(labKey, input.getName());
        optional.ifPresent(category -> {
            if (!category.getId().equals(id)) {
                throw new ValidationException(
                        String.format(Lang.CHEMICAL_CATEGORY_ALREADY_EXISTS,
                                category.getName(),
                                labKey));
            }
        });
    }

    @Data
    @Builder
    public static class ChemicalCategoryValidatorWrapper {

        ChemicalCategoryInput chemicalCategoryInput;
        ChemicalCategory chemicalCategory;
        Long id;
        Principal principal;
    }

}
