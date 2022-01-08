package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.domain.input.LabInput;
import com.ksteindl.chemstore.domain.repositories.LabRepository;
import com.ksteindl.chemstore.exceptions.ForbiddenException;
import com.ksteindl.chemstore.exceptions.ResourceNotFoundException;
import com.ksteindl.chemstore.exceptions.ValidationException;
import com.ksteindl.chemstore.util.HibernateProxyUtil;
import com.ksteindl.chemstore.util.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LabService implements UniqueEntityService<LabInput> {

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
            throw new ValidationException(Lang.LAB_KEY_ATTRIBUTE_NAME, String.format(Lang.LAB_KEY_CANNOT_BE_CHANGED, lab.getKey(), labInput.getKey()));
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

    public List<Lab> getLabsForUser(Principal principal) {
        AppUser user = appUserService.getMyAppUser(principal);
        List<Lab> labsForUser = new ArrayList<>();
        labsForUser.addAll(user.getLabsAsUser());
        labsForUser.addAll(user.getLabsAsAdmin());
        labsForUser.addAll(labRepository.findByLabManagers(user));
        return labsForUser;
    }

    public Lab findLabByKey(String key) {
        return labRepository.findByKey(key).orElseThrow(() -> new ResourceNotFoundException(Lang.LAB_ENTITY_NAME, key));
    }

    public void deleteLab(Long id) {
        Lab lab = findById(id);
        lab.setDeleted(true);
        lab.getLabManagers().clear();
        appUserService.removeLabsFromAppUsers(lab);
        labRepository.save(lab);
    }

    public Lab findLabForAdmin(String labKey, Principal admin) {
        Lab lab = findLabByKey(labKey);
        validateLabForAdmin(lab, admin);
        return lab;
    }

    public Lab findLabForUser(String labKey, Principal admin) {
        Lab lab = findLabByKey(labKey);
        validateLabForUser(lab, admin);
        return lab;
    }

    public void validateLabForUser(Lab lab, Principal userPrincipal) {
        Lab unproxiedLab = HibernateProxyUtil.unproxy(lab);
        AppUser user = appUserService.getMyAppUser(userPrincipal);
        if (!user.getLabsAsUser().stream().anyMatch(labAsUser -> labAsUser.equals(unproxiedLab)) &&
                !unproxiedLab.getLabManagers().stream().anyMatch(manager -> manager.equals(user)) &&
                !user.getLabsAsAdmin().stream().anyMatch(labAsAdmin -> labAsAdmin.equals(unproxiedLab))) {
            throw new ForbiddenException(String.format(Lang.LAB_USER_FORBIDDEN, unproxiedLab.getName(), userPrincipal.getName()));
        }
    }

    public void validateLabForAdmin(Lab lab, Principal adminPrincipal) {
        Lab unproxiedLab = HibernateProxyUtil.unproxy(lab);
        AppUser admin = appUserService.getMyAppUser(adminPrincipal);
        if (!unproxiedLab.getLabManagers().stream().anyMatch(manager -> manager.equals(admin))
                && !admin.getLabsAsAdmin().stream().anyMatch(labAsAdmin -> labAsAdmin.equals(unproxiedLab))) {
            throw new ForbiddenException(String.format(Lang.LAB_ADMIN_FORBIDDEN, unproxiedLab.getName(), adminPrincipal.getName()));
        }
    }

    public Lab findById(Long id) {
        return findById(id, true);
    }

    public Lab findById(Long id, Boolean onlyActive) {
        Lab lab = labRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Lang.LAB_ENTITY_NAME, id));
        if (onlyActive && lab.getDeleted()) {
            throw new ResourceNotFoundException(Lang.LAB_ALREADY_DELETED, lab.getKey());
        }
        return lab;
    }

    private void updateAttributes(Lab lab, LabInput labInput) {
        lab.setName(labInput.getName());
        List<AppUser> managers = labInput.getLabManagerUsernames().stream()
                .map(username -> appUserService
                        .findByUsername(username)
                        .orElseThrow(() -> new ResourceNotFoundException(Lang.APP_USER_ENTITY_NAME, username)))
                .collect(Collectors.toList());
        lab.setLabManagers(managers);
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
