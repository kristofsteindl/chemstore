package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.exceptions.ResourceNotFoundException;
import com.ksteindl.chemstore.exceptions.ValidationException;
import com.ksteindl.chemstore.domain.entities.Chemical;
import com.ksteindl.chemstore.domain.input.ChemicalInput;
import com.ksteindl.chemstore.domain.repositories.ChemicalRepository;
import com.ksteindl.chemstore.util.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChemicalService implements UniqueEntityInput<ChemicalInput> {

    private final static Sort SORT_BY_SHORT_NAME = Sort.by(Sort.Direction.ASC, "shortName");

    @Autowired
    private ChemicalRepository chemicalRepository;
    @Autowired
    private ChemTypeService chemTypeService;

    public Chemical createChemical(ChemicalInput chemicalInput) {
        throwExceptionIfNotUnique(chemicalInput);
        Chemical chemical = new Chemical();
        validateAndCopyAttributes(chemical, chemicalInput);
        return chemicalRepository.save(chemical);
    }


    public Chemical updateChemical(ChemicalInput chemicalInput, Long id) {
        Chemical chemical = chemicalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Lang.CHEMICAL_ENTITY_NAME, id));
        throwExceptionIfNotUnique(chemicalInput, id);
        validateAndCopyAttributes(chemical, chemicalInput);
        return chemicalRepository.save(chemical);
    }


    public Chemical getChemicalByShortName(String shortName) {
        return chemicalRepository.findByShortName(shortName).orElseThrow(() -> new ResourceNotFoundException(Lang.CHEMICAL_ENTITY_NAME, shortName));
    }

    public List<Chemical> getChemicals(Boolean onlyActive) {
        return onlyActive ?
                chemicalRepository.findAllActive(SORT_BY_SHORT_NAME) :
                chemicalRepository.findAll(SORT_BY_SHORT_NAME);
    }

    public List<Chemical> getChemicals() {
        return getChemicals(true);

    }

    public void deleteChemical(Long id) {
        Chemical chemical = findById(id);
        chemical.setDeleted(true);
        chemicalRepository.save(chemical);
    }

    public Chemical findById(Long id) {
        return findById(id, true);
    }

    public Chemical findById(Long id, Boolean onlyActive) {
        Chemical chemical = chemicalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Lang.CHEMICAL_ENTITY_NAME, id));
        if (onlyActive && chemical.getDeleted()) {
            ValidationException.throwEntityIsDeletedException(Lang.CHEMICAL_ENTITY_NAME, chemical.getExactName());
        }
        return chemical;
    }

    private void validateAndCopyAttributes(Chemical chemical, ChemicalInput chemicalInput) {
        Long chemTypeId = chemicalInput.getChemTypeId();
        if (chemTypeId == null) {
            chemical.setChemType(null);
        } else {
            chemical.setChemType(chemTypeService.findById(chemTypeId));
        }
        chemical.setShortName(chemicalInput.getShortName());
        chemical.setExactName(chemicalInput.getExactName());
    }

    @Override
    public void throwExceptionIfNotUnique(ChemicalInput chemicalInput, Long id) {
        List<Chemical> foundChemical = chemicalRepository.findByShortNameOrExactName(
                chemicalInput.getShortName(),
                chemicalInput.getExactName());
        foundChemical.stream()
                .filter(chemical -> !chemical.getId().equals(id))
                .findAny()
                .ifPresent(chemical -> {
                    throw new ValidationException(String.format(
                            Lang.CHEMICAL_SAME_NAME_FOUND_TEMPLATE,
                            chemical.getShortName(),
                            chemical.getExactName()));});
    }
}
