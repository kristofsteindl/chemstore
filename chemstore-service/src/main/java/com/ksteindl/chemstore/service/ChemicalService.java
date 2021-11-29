package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.domain.entities.Chemical;
import com.ksteindl.chemstore.domain.entities.ChemicalCategory;
import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.domain.input.ChemicalInput;
import com.ksteindl.chemstore.domain.repositories.ChemicalRepository;
import com.ksteindl.chemstore.exceptions.ResourceNotFoundException;
import com.ksteindl.chemstore.exceptions.ValidationException;
import com.ksteindl.chemstore.util.HibernateProxyUtil;
import com.ksteindl.chemstore.util.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
public class ChemicalService implements UniqueEntityService<ChemicalInput> {

    private final static Sort SORT_BY_SHORT_NAME = Sort.by(Sort.Direction.ASC, "shortName");

    @Autowired
    private ChemicalRepository chemicalRepository;
    @Autowired
    private ChemicalCategoryService chemicalCategoryService;
    @Autowired
    private LabService labService;

    public Chemical createChemical(ChemicalInput chemicalInput, Principal admin) {
        Chemical chemical = new Chemical();
        Lab lab = labService.findLabForAdmin(chemicalInput.getLabKey(), admin);
        validateAndCopyAttributes(chemicalInput, chemical, lab);
        chemical.setLab(lab);
        return chemicalRepository.save(chemical);
    }

    public Chemical updateChemical(ChemicalInput chemicalInput, Long id, Principal admin) {
        Chemical chemical = chemicalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Lang.CHEMICAL_ENTITY_NAME, id));
        Lab lab = chemical.getLab();
        String labKey = lab.getKey();
        labService.validateLabForAdmin(lab, admin);
        if (!labKey.equals(chemicalInput.getLabKey())) {
            throw new ValidationException(String.format(Lang.LAB_OF_CHEMICAL_CANNOT_CHANGED, labKey));
        }
        validateAndCopyAttributes(chemicalInput, chemical, lab);
        return chemicalRepository.save(chemical);
    }

    private void validateAndCopyAttributes(ChemicalInput chemicalInput, Chemical chemical, Lab lab) {
        throwExceptionIfNotUnique(chemicalInput, chemical.getId());
        Long categoryId = chemicalInput.getCategoryId();
        if (categoryId != null && categoryId > 0) {
            ChemicalCategory category = chemicalCategoryService.getById(categoryId);
            if (!category.getLab().equals(lab)) {
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

    public Chemical getForChemItem(String shortName, Lab lab) {
        Chemical chemical = chemicalRepository.findByShortNameAndLab(shortName, lab).orElseThrow(() -> new ResourceNotFoundException(Lang.CHEMICAL_ENTITY_NAME, shortName));
        if (chemical.getDeleted()) {
            throw new ResourceNotFoundException(Lang.CHEMICAL_ENTITY_NAME, chemical.getShortName());

        }
        return chemical;
    }

    public List<Chemical> getChemicalsForAdmin(String labKey, Principal admin, Boolean onlyActive) {
        Lab lab = labService.findLabForAdmin(labKey, admin);
        return onlyActive ?
                chemicalRepository.findAllActive(lab, SORT_BY_SHORT_NAME) :
                chemicalRepository.findByLab(lab, SORT_BY_SHORT_NAME);
    }

    public List<Chemical> getChemicalsForUser(String labKey, Principal user) {
        Lab lab = labService.findLabForUser(labKey, user);
        return chemicalRepository.findAllActive(lab, SORT_BY_SHORT_NAME);
    }

    public void deleteChemical(Long id, Principal admin) {
        Chemical chemical = findById(id, admin);
        Lab lab = HibernateProxyUtil.unproxy(chemical.getLab());
        labService.validateLabForAdmin(lab, admin);
        chemical.setDeleted(true);
        chemicalRepository.save(chemical);
    }

    public Chemical findById(Long id, Principal user) {
        return findById(id, user, true);
    }

    public Chemical findById(Long id, Principal user, Boolean onlyActive) {
        Chemical chemical = chemicalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Lang.CHEMICAL_ENTITY_NAME, id));
        Lab lab = HibernateProxyUtil.unproxy(chemical.getLab());
        labService.validateLabForUser(lab, user);
        if (onlyActive && chemical.getDeleted()) {
            throw new ResourceNotFoundException(String.format(Lang.CHEMICAL_ALREADY_DELETED, chemical.getExactName(), lab.getName()));
        }
        return chemical;
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
