package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.exceptions.ResourceNotFoundException;
import com.ksteindl.chemstore.exceptions.ValidationException;
import com.ksteindl.chemstore.domain.entities.Manufacturer;
import com.ksteindl.chemstore.domain.input.ManufacturerInput;
import com.ksteindl.chemstore.domain.repositories.ManufacturerRepository;
import com.ksteindl.chemstore.util.Lang;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ManufacturerService implements UniqueEntityService<ManufacturerInput> {

    private static final Logger logger = LogManager.getLogger(ManufacturerService.class);
    private final static Sort SORT_BY_NAME = Sort.by(Sort.Direction.ASC, "name");

    @Autowired
    ManufacturerRepository manufacturerRepository;

    public Manufacturer createManufacturer(ManufacturerInput manufacturerInput) {
        throwExceptionIfNotUnique(manufacturerInput);
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName(manufacturerInput.getName());
        return manufacturerRepository.save(manufacturer);
    }

    public Manufacturer updateManufacturer(ManufacturerInput manufacturerInput, Long id) {
        Manufacturer manufacturer = manufacturerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Lang.MANUFACTURER_ENTITY_NAME, id));
        throwExceptionIfNotUnique(manufacturerInput, id);
        manufacturer.setName(manufacturerInput.getName());
        return manufacturerRepository.save(manufacturer);
    }

    public List<Manufacturer> getManufacturers() {
        return getManufacturers(true);
    }

    public List<Manufacturer> getManufacturers(boolean onlyActive) {
        return onlyActive ?
                manufacturerRepository.findAllActive(SORT_BY_NAME) :
                manufacturerRepository.findAll(SORT_BY_NAME);
    }

    public void deleteManufacturer(Long id) {
        Manufacturer manufacturer = findById(id);
        manufacturer.setDeleted(true);
        manufacturerRepository.save(manufacturer);
    }

    public Manufacturer findById(Long id) {
        return findById(id, true);
    }

    public Manufacturer findById(Long id, Boolean onlyActive) {
        Manufacturer manufacturer = manufacturerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Lang.MANUFACTURER_ENTITY_NAME, id));
        if (onlyActive && manufacturer.getDeleted()) {
            ValidationException.throwEntityIsDeletedException(Lang.MANUFACTURER_ENTITY_NAME, manufacturer.getName());
        }
        return manufacturer;
    }

    @Override
    public void throwExceptionIfNotUnique(ManufacturerInput input, Long id) {
        Optional<Manufacturer> optional = manufacturerRepository.findByName(input.getName());
        optional.ifPresent(manufacturer -> {
            if (!manufacturer.getId().equals(id)) {
                throw new ValidationException(String.format(Lang.MANUFACTURER_WITH_SAME_NAME_FOUND_TEMPLATE, input.getName()));
            }
        });
    }
}
