package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.exceptions.ResourceNotFoundException;
import com.ksteindl.chemstore.exceptions.ValidationException;
import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.domain.input.LabInput;
import com.ksteindl.chemstore.domain.repositories.LabRepository;
import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.repositories.AppUserRepository;
import com.ksteindl.chemstore.util.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class LabService implements UniqueEntityInput<LabInput> {

    @Autowired
    private LabRepository labRepository;
    @Autowired
    private AppUserRepository appUserRepository;

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


    public List<Lab> getEveryLab() {
        Iterable<Lab> iterator = labRepository.findAllByOrderByNameAsc();
        return StreamSupport.stream(iterator.spliterator(), false).collect(Collectors.toList());
    }

    private void updateAttributes(Lab lab, LabInput labInput) {
        lab.setName(labInput.getName());
        Long managerId = labInput.getLabManagerId();
        lab.setLabManager(appUserRepository.findById(managerId).orElseThrow(() -> new ResourceNotFoundException(Lang.APP_USER_ENTITY_NAME, managerId)));
        List<AppUser> labAdmins = labInput.getLabAdminIds()
                .stream()
                .map(labAdminId -> appUserRepository.findById(labAdminId).orElseThrow(() -> new ResourceNotFoundException(Lang.APP_USER_ENTITY_NAME, labAdminId)))
                .collect(Collectors.toList());
        lab.setLabAdmins(labAdmins);
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
