package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.audittrail.AuditTrailService;
import com.ksteindl.chemstore.audittrail.EntityLogTemplate;
import com.ksteindl.chemstore.audittrail.StartingEntry;
import com.ksteindl.chemstore.domain.entities.Chemical;
import com.ksteindl.chemstore.domain.entities.ChemicalCategory;
import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.domain.input.ChemicalInput;
import com.ksteindl.chemstore.domain.repositories.ChemicalRepository;
import com.ksteindl.chemstore.exceptions.ResourceNotFoundException;
import com.ksteindl.chemstore.exceptions.ValidationException;
import com.ksteindl.chemstore.util.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
public class ChemicalService implements UniqueEntityService<ChemicalInput> {

    private final static EntityLogTemplate<Chemical> template = LogTemplates.CHEM_TEMPLATE;

    @Autowired
    private ChemicalRepository chemicalRepository;
    @Autowired
    private ChemicalCategoryService chemicalCategoryService;
    @Autowired
    private LabService labService;
    @Autowired
    private AuditTrailService auditTrailService;

    public Chemical createChemical(ChemicalInput chemicalInput, Principal admin) {
        Chemical chemical = new Chemical();
        Lab lab = labService.findLabForAdmin(chemicalInput.getLabKey(), admin);
        validateAndCopyAttributes(chemicalInput, chemical, lab);
        chemical.setLab(lab);
        Chemical created = chemicalRepository.save(chemical);
        auditTrailService.createEntry(created, admin, template);
        return created;
    }

    public Chemical updateChemical(ChemicalInput chemicalInput, Long id, Principal admin) {
        Chemical chemical = chemicalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Lang.CHEMICAL_ENTITY_NAME, id));
        StartingEntry startingEntry = StartingEntry.of(chemical, admin, template);
        Lab lab = chemical.getLab();
        String labKey = lab.getKey();
        labService.validateLabForAdmin(lab, admin);
        if (!labKey.equals(chemicalInput.getLabKey())) {
            throw new ValidationException(String.format(Lang.LAB_OF_CHEMICAL_CANNOT_CHANGED, labKey));
        }
        validateAndCopyAttributes(chemicalInput, chemical, lab);
        Chemical updated = chemicalRepository.save(chemical);
        auditTrailService.updateEntry(startingEntry, updated);
        return updated;
    }

    private void validateAndCopyAttributes(ChemicalInput chemicalInput, Chemical chemical, Lab lab) {
        throwExceptionIfNotUnique(chemicalInput, chemical.getId());
        Long categoryId = chemicalInput.getCategoryId();
        if (categoryId != null && categoryId > 0) {
            ChemicalCategory category = chemicalCategoryService.getById(categoryId);
            if (!category.getLab().getKey().equals(lab.getKey())) {
                throw new ValidationException(String.format(
                        Lang.CHEMICAL_CATEGORY_LAB_NOT_THE_SAME,
                        category.getName(),
                        category.getLab().getName(),
                        lab.getName()));
            }
            chemical.setCategory(category);
        } else {
            chemical.setCategory(null);
        }
        chemical.setShortName(chemicalInput.getShortName());
        chemical.setExactName(chemicalInput.getExactName());
    }

    public Chemical getByShortName(String shortName, Lab lab) {
        Chemical chemical = chemicalRepository.findByShortNameAndLab(shortName, lab).orElseThrow(() -> new ResourceNotFoundException(Lang.CHEMICAL_ENTITY_NAME, shortName));
        if (chemical.getDeleted()) {
            throw new ResourceNotFoundException(Lang.CHEMICAL_ENTITY_NAME, chemical.getShortName());
        }
        return chemical;
    }

    public List<Chemical> getChemicalsForUser(String labKey, Principal user) {
        Lab lab = labService.findLabForUser(labKey, user);
        return chemicalRepository.findAllActive(lab);
    }
    

    public Chemical findById(Long id) {
        return findById(id, true);
    }

    public Chemical findById(Long id, Boolean onlyActive) {
        Chemical chemical = chemicalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Lang.CHEMICAL_ENTITY_NAME, id));
        if (onlyActive && chemical.getDeleted()) {
            throw new ResourceNotFoundException(String.format(Lang.CHEMICAL_ALREADY_DELETED, chemical.getExactName()));
        }
        return chemical;
    }

    public void deleteChemical(Long id, Principal admin) {
        Chemical chemical = findById(id);
        labService.validateLabForAdmin(chemical.getLab(), admin);
        chemical.setDeleted(true);
        chemicalRepository.save(chemical);
        auditTrailService.archiveEntry(StartingEntry.of(chemical, admin, template), chemical);
    }

    @Override
    public void throwExceptionIfNotUnique(ChemicalInput chemicalInput, Long id) {
        Lab lab = labService.findLabByKey(chemicalInput.getLabKey());
        List<Chemical> foundChemical = chemicalRepository.findDuplicate(
                lab,
                chemicalInput.getShortName(),
                chemicalInput.getExactName());
        foundChemical.stream()
                .filter(chemical -> !chemical.getId().equals(id))
                .findAny()
                .ifPresent(chemical -> {
                    throw new ValidationException(String.format(
                            Lang.CHEMICAL_SAME_NAME_FOUND_TEMPLATE,
                            chemical.getShortName(),
                            chemical.getExactName(),
                            lab.getName())
                    );});
    }
}
