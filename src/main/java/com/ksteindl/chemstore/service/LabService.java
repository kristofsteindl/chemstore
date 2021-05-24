package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.exceptions.ResourceNotFoundException;
import com.ksteindl.chemstore.exceptions.ValidationException;
import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.domain.input.LabInput;
import com.ksteindl.chemstore.domain.repositories.LabRepository;
import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.util.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabService implements UniqueEntityInput<LabInput> {

    private final static Sort SORT_BY_NAME = Sort.by(Sort.Direction.ASC, "name");

    @Autowired
    private LabRepository labRepository;
    @Autowired
    private AppUserService appUserService;

    public Lab createLab(LabInput labInput) {
        throwExceptionIfNotUnique(labInput);
        Lab lab = new Lab();
        lab.setKey(labInput.getKey());
        updateAttributes(lab, labInput);
        return labRepository.save(lab);
    }

    public Lab updateLab(LabInput labInput, Long id) {
        Lab lab = labRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Lang.LAB_ENTITY_NAME, id));
        if (!lab.getKey().equals(labInput.getKey())) {
            throw new ValidationException(Lang.LAB_KEY_ATTRIBUTE_NAME, String.format(Lang.LAB_KEY_CANOT_BE_CHANGED, lab.getKey(), labInput.getKey()));
        }
        throwExceptionIfNotUnique(labInput, id);
        updateAttributes(lab, labInput);
        return labRepository.save(lab);
    }

    public List<Lab> getLabs() {
        return getLabs(true);
    }

    public List<Lab> getLabs(Boolean onlyActive) {
        return onlyActive ?
                labRepository.findAllActive(SORT_BY_NAME) :
                labRepository.findAll(SORT_BY_NAME);
    }

    public void deleteLab(Long id) {
        Lab lab = labRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Lang.LAB_ENTITY_NAME, id));
        lab.setDeleted(true);
        lab.setLabManager(null);
        appUserService.removeLabsFromAppUsers(lab);
        labRepository.save(lab);
    }

    public void removeUserFromLabs(AppUser labManager) {
        labRepository.findByLabManager(labManager).ifPresent(lab -> {
            lab.setLabManager(null);
            labRepository.save(lab);
        });
    }
    public Lab findById(Long id) {
        return findById(id, false);
    }

    public Lab findById(Long id, Boolean onlyActive) {
        Lab lab = labRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Lang.LAB_ENTITY_NAME, id));
        if (onlyActive && lab.getDeleted()) {
            throw new ValidationException(Lang.LAB_ENTITY_NAME, String.format(Lang.LAB_IS_DELETED, lab.getName()));
        }
        return lab;
    }

    private void updateAttributes(Lab lab, LabInput labInput) {
        lab.setName(labInput.getName());
        lab.setLabManager(appUserService.findById(labInput.getLabManagerId()));
    }

    @Override
    public void throwExceptionIfNotUnique(LabInput input, Long id) {
        List<Lab> foundLabs = labRepository.findByKeyOrName(
                input.getKey(),
                input.getName());
        foundLabs.stream().filter(lab -> (!lab.getId().equals(id)))
                .forEach(lab -> {
                    throw new ValidationException(String.format(
                            Lang.LAB_SAME_NAME_FOUND_TEMPLATE,
                            lab.getKey(),
                            lab.getName()));
                });
    }


}
