package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.audittrail.AuditTrailService;
import com.ksteindl.chemstore.audittrail.EntityLogTemplate;
import com.ksteindl.chemstore.audittrail.StartingEntry;
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
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LabService implements UniqueEntityService<LabInput> {

    private final static Sort SORT_BY_NAME = Sort.by(Sort.Direction.ASC, "name");
    private final static EntityLogTemplate<Lab> template = LogTemplates.LAB_LOG_TEMPLATE;

    @Autowired
    private LabRepository labRepository;
    @Autowired
    private AppUserService appUserService;
    @Autowired
    private AuditTrailService auditTrailService;

    public Lab createLab(LabInput labInput, Principal accountManagerPrincipal) {
        throwExceptionIfNotUnique(labInput);
        Lab lab = new Lab();
        lab.setKey(labInput.getKey());
        updateAttributes(lab, labInput);
        Lab created = labRepository.save(lab);
        auditTrailService.createEntry(lab, accountManagerPrincipal, template);
        return created;
    }

    public Lab updateLab(LabInput labInput, Long id, Principal accountManagerPrincipal) {
        Lab lab = labRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Lang.LAB_ENTITY_NAME, id));
        StartingEntry<Lab> startingEntry = StartingEntry.of(lab, accountManagerPrincipal, template);
        if (!lab.getKey().equals(labInput.getKey())) {
            throw new ValidationException(Lang.LAB_KEY_ATTRIBUTE_NAME, String.format(Lang.LAB_KEY_CANNOT_BE_CHANGED, lab.getKey(), labInput.getKey()));
        }
        throwExceptionIfNotUnique(labInput, id);
        updateAttributes(lab, labInput);
        Lab updated = labRepository.save(lab);
        
        auditTrailService.updateEntry(startingEntry, updated);
        return updated;
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
        AppUser user = appUserService.getAppUser(principal.getName());
        Set<Lab> labsForUser = new HashSet<>();
        labsForUser.addAll(user.getLabsAsUser());
        labsForUser.addAll(user.getLabsAsAdmin());
        labsForUser.addAll(labRepository.findByLabManagers(user));
        return labsForUser.stream().sorted(Comparator.comparing(Lab::getName)).collect(Collectors.toList());
    }

    public Lab findLabByKey(String key) {
        return labRepository.findByKey(key).orElseThrow(() -> new ResourceNotFoundException(Lang.LAB_ENTITY_NAME, key));
    }

    public void deleteLab(Long id, Principal accountManagerPrincipal) {
        Lab lab = findById(id);
        StartingEntry<Lab> startingEntry = StartingEntry.of(lab, accountManagerPrincipal, template);
        lab.setDeleted(true);
        lab.getLabManagers().clear();
        appUserService.removeLabsFromAppUsers(lab);
        Lab deleted = labRepository.save(lab);
        auditTrailService.archiveEntry(startingEntry, deleted);
    }

    public Lab findLabForManager(String labKey, Principal admin) {
        Lab lab = findLabByKey(labKey);
        validateLabForManager(lab, admin);
        return lab;
    }

    public Lab findLabForAdmin(String labKey, Principal admin) {
        Lab lab = findLabByKey(labKey);
        validateLabForAdmin(lab, admin);
        return lab;
    }

    public Lab findLabForUser(String labKey, Principal admin) {
        Lab lab = findLabByKey(labKey);
        validateLabForUser(lab, admin.getName());
        return lab;
    }
    

    public void validateLabForUser(Lab lab, String username) {
        AppUser user = appUserService.getAppUser(username);
        if (!user.getLabsAsUser().stream().anyMatch(labAsUser -> labAsUser.getKey().equals(lab.getKey())) &&
                !lab.getLabManagers().stream().anyMatch(manager -> manager.getUsername().equals(user.getUsername())) &&
                !user.getLabsAsAdmin().stream().anyMatch(labAsAdmin -> labAsAdmin.getKey().equals(lab.getKey()))) {
            throw new ForbiddenException(String.format(Lang.LAB_USER_FORBIDDEN, lab.getName(), username));
        }
    }

    public void validateLabForManager(Lab lab, Principal managerPrincipal) {
        AppUser user = appUserService.getAppUser(managerPrincipal.getName());
        boolean userIsManagerOfLab = user.getManagedLabs().stream()
                .anyMatch(managedLab -> managedLab.getKey().equals(lab.getKey()));
        if (!userIsManagerOfLab) {
            throw new ForbiddenException(String.format(Lang.LAB_MANAGER_FORBIDDEN, lab.getName(), managerPrincipal.getName())); 
        }
    }

    public void validateLabForAdmin(Lab lab, Principal adminPrincipal) {
        Lab unproxiedLab = HibernateProxyUtil.unproxy(lab);
        AppUser admin = appUserService.getAppUser(adminPrincipal.getName());
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
