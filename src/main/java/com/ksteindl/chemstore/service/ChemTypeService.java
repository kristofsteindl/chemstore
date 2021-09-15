package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.domain.entities.ChemType;
import com.ksteindl.chemstore.domain.input.ChemTypeInput;
import com.ksteindl.chemstore.domain.repositories.ChemTypeRepository;
import com.ksteindl.chemstore.exceptions.ResourceNotFoundException;
import com.ksteindl.chemstore.exceptions.ValidationException;
import com.ksteindl.chemstore.util.Lang;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChemTypeService implements UniqueEntityService<ChemTypeInput> {

    @Autowired
    private ChemTypeRepository chemTypeRepository;

    private static final Logger logger = LogManager.getLogger(ChemTypeService.class);
    private final static Sort SORT_BY_NAME = Sort.by(Sort.Direction.ASC, "name");

    public ChemType createChemType(ChemTypeInput chemTypeInput) {
        throwExceptionIfNotUnique(chemTypeInput);
        ChemType chemType = new ChemType();
        chemType.setName(chemTypeInput.getName());
        return chemTypeRepository.save(chemType);
    }

    public ChemType updateChemType(ChemTypeInput chemTypeInput, Long id) {
        throwExceptionIfNotUnique(chemTypeInput, id);
        ChemType chemType = chemTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Lang.CHEM_TYPE_ENTITY_NAME, id));
        chemType.setName(chemTypeInput.getName());
        return chemTypeRepository.save(chemType);
    }

    public List<ChemType> getChemTypes() {
        return getChemTypes(true);
    }

    public List<ChemType> getChemTypes(boolean onlyActive) {
        return onlyActive ?
                chemTypeRepository.findAllActive(SORT_BY_NAME) :
                chemTypeRepository.findAll(SORT_BY_NAME);
    }

    public void deleteChemType(Long id) {
        ChemType chemType = findById(id);
        chemType.setDeleted(true);
        chemTypeRepository.save(chemType);
    }

    public ChemType findById(Long id) {
        return findById(id, true);
    }

    public ChemType findById(Long id, Boolean onlyActive) {
        ChemType chemType = chemTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Lang.CHEM_TYPE_ENTITY_NAME, id));
        if (onlyActive && chemType.getDeleted()) {
            ValidationException.throwEntityIsDeletedException(Lang.CHEM_TYPE_ENTITY_NAME, chemType.getName());
        }
        return chemType;
    }

    @Override
    public void throwExceptionIfNotUnique(ChemTypeInput input, Long id) {
        Optional<ChemType> optional = chemTypeRepository.findByName(input.getName());
        optional.ifPresent(chemType -> {
            if (!chemType.getId().equals(id)) {
                throw new ValidationException(String.format(Lang.CHEM_TYPE_WITH_SAME_NAME_FOUND_TEMPLATE, input.getName()));
            }
        });
    }

}
