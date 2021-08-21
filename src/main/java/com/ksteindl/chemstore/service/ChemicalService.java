package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.exceptions.ResourceNotFoundException;
import com.ksteindl.chemstore.exceptions.ValidationException;
import com.ksteindl.chemstore.domain.entities.Chemical;
import com.ksteindl.chemstore.domain.input.ChemicalInput;
import com.ksteindl.chemstore.domain.repositories.ChemicalRepository;
import com.ksteindl.chemstore.util.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChemicalService implements UniqueEntityInput<ChemicalInput> {


    @Autowired
    private ChemicalRepository chemicalRepository;

    public Chemical getChemicalById(Long id) {
        return chemicalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Lang.CHEMICAL_ENTITY_NAME, id));
    }

    public Chemical createChemical(ChemicalInput chemicalInput) {
        throwExceptionIfNotUnique(chemicalInput);
        Chemical chemical = new Chemical();
        copyAttributes(chemical, chemicalInput);
        return chemicalRepository.save(chemical);
    }


    public Chemical updateChemical(ChemicalInput chemicalInput, Long id) {
        Chemical chemical = chemicalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Lang.CHEMICAL_ENTITY_NAME, id));
        throwExceptionIfNotUnique(chemicalInput, id);
        copyAttributes(chemical, chemicalInput);
        return chemicalRepository.save(chemical);
    }

    private void copyAttributes(Chemical chemical, ChemicalInput chemicalInput) {
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

    public List<Chemical> getChemicals() {
        return chemicalRepository.findAllByOrderByShortNameAsc();
    }

    public void deleteChemical(Long id) {
        Chemical chemical = getChemicalById(id);
        chemical.setDeleted(true);
        chemicalRepository.save(chemical);
    }
}
