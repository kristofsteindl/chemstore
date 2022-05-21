package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.domain.input.AppUserInput;
import com.ksteindl.chemstore.domain.input.AppUserQuery;
import com.ksteindl.chemstore.domain.input.PasswordInput;
import com.ksteindl.chemstore.domain.repositories.LabRepository;
import com.ksteindl.chemstore.domain.repositories.appuser.AppUserRepository;
import com.ksteindl.chemstore.exceptions.ResourceNotFoundException;
import com.ksteindl.chemstore.exceptions.ValidationException;
import com.ksteindl.chemstore.security.UserDetailsImpl;
import com.ksteindl.chemstore.security.role.Role;
import com.ksteindl.chemstore.security.role.RoleService;
import com.ksteindl.chemstore.util.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AppUserService implements UniqueEntityService<AppUserInput>, UserDetailsService {

    private static final Logger logger
            = LoggerFactory.getLogger(AppUserService.class);

    private final static Sort SORT_BY_USERNAME = Sort.by(Sort.Direction.ASC, "username");

    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private LabRepository labRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private RoleService roleService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        //return appUser;
        return new UserDetailsImpl(appUser);
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        AppUser appUser = appUserRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        //return appUser;
        return new UserDetailsImpl(appUser);
    }

    public AppUser createUser(AppUserInput appUserInput) {
        AppUser appUser = new AppUser();
        throwExceptionIfNotUnique(appUserInput);
        validateAndSetAppUser(appUser, appUserInput);
        setDefaultPassword(appUser);
        return appUserRepository.save(appUser);
    }


    public AppUser updateUser(AppUserInput appUserInput, Long id) {
        AppUser appUser = findById(id);
        if (!appUser.getUsername().equals(appUserInput.getUsername())) {
            throw new ValidationException(Lang.APP_USER_USERNAME_ATTRIBUTE_NAME,
                    String.format(Lang.USERNAME_CANNOT_BE_CHANGED, appUser.getUsername(), appUserInput.getUsername()));
        }
        validateAndSetAppUser(appUser, appUserInput);
        return appUserRepository.save(appUser);
    }

    public AppUser restorePassword(Long id) {
        AppUser appUser = findById(id);
        setDefaultPassword(appUser);
        return appUserRepository.save(appUser);
    }

    public AppUser updatePassword(PasswordInput passwordInput, Principal principal) {
        AppUser appUser = getAppUser(principal.getName());
        validateAndSetPassword(appUser, passwordInput);
        return appUserRepository.save(appUser);
    }

    public List<AppUser> getAppUsers(AppUserQuery appUserQuery) {
        return appUserRepository.findAppUsers(appUserQuery);
    }

    public AppUser getAppUser(String username) {
        return findByUsername(username).stream().findAny()
                .orElseThrow(() -> new ResourceNotFoundException(Lang.APP_USER_ENTITY_NAME, username));
    }

    public Map<String, List<AppUser>> getUsersFromManagedLabs(Principal principal) {
        AppUser manager = getAppUser(principal.getName());
        return manager.getManagedLabs().stream()
                .collect(Collectors.toMap(lab -> lab.getKey(), lab -> appUserRepository.findByLabsAsUser(lab)));
    }

    public List<AppUser> getUsersFromManagedLab(Principal managerPrincipal, String labKey) {
        AppUser manager = getAppUser(managerPrincipal.getName());
        Lab managedLab = findLabByKey(labKey);
        if (!managedLab.getLabManagers().stream().anyMatch(appUser -> appUser.equals(manager))) {
            throw new ValidationException("authorization", "User " + manager.getUsername() + " is not manager of " + managedLab.getKey() + ", please the account admin");
        }
        return appUserRepository.findByLabsAsUser(managedLab);
    }

    public void deleteAppUser(Long id) {
        AppUser appUser = findById(id);
        appUser.setDeleted(true);
        appUser.setLabsAsAdmin(Collections.emptyList());
        appUser.setLabsAsUser(Collections.emptyList());
        removeUserFromLabs(appUser);
        appUserRepository.save(appUser);
    }

    public Optional<AppUser> findByUsername(String username) {
        return findByUsername(username, true);
    }

    public Optional<AppUser> findByUsername(String username, boolean onlyActive) {
        AppUserQuery appUserQuery = AppUserQuery.builder().username(username).onlyActive(onlyActive).build();
        return appUserRepository.findAppUsers(appUserQuery).stream().findAny();
    }

    public AppUser findById(Long id) {
        return findById(id, true);
    }

    public AppUser findById(Long id, Boolean onlyActive) {
        AppUser appUser = appUserRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Lang.APP_USER_ENTITY_NAME, id));
        if (onlyActive && appUser.getDeleted()) {
            throw new ResourceNotFoundException(Lang.APP_USER_ALREADY_DELETED, appUser.getUsername());
        }
        return appUser;
    }

    public void removeLabsFromAppUsers(Lab labToRemove) {
        appUserRepository.findByLabsAsAdmin(labToRemove).forEach(user -> {
            user.getLabsAsAdmin().remove(labToRemove);
            appUserRepository.save(user);
        });
        appUserRepository.findByLabsAsUser(labToRemove).forEach(admin -> {
            admin.getLabsAsUser().remove(labToRemove);
            appUserRepository.save(admin);
        });

    }

    private void removeUserFromLabs(AppUser labManager) {
        labRepository.findByLabManagers(labManager).forEach(lab -> {
            lab.getLabManagers().remove(labManager);
            labRepository.save(lab);
        });
    }

    private Lab findLabByKey(String key) {
        return labRepository.findByKey(key).orElseThrow(() -> new ResourceNotFoundException(Lang.LAB_ENTITY_NAME, key));
    }

    private void validateAndSetAppUser(AppUser appUser, AppUserInput appUserInput) {
        Set<Role> roles = appUserInput.getRoles()
                .stream()
                .map(role -> roleService.findByRole(role))
                .collect(Collectors.toSet());
        List<Lab> labsAsUser = appUserInput.getLabKeysAsUser()
                .stream()
                .map(labKey -> findLabByKey(labKey))
                .collect(Collectors.toList());
        List<Lab> labsAsAdmin = appUserInput.getLabKeysAsAdmin()
                .stream()
                .map(labKey -> findLabByKey(labKey))
                .collect(Collectors.toList());
        appUser.setUsername(appUserInput.getUsername());
        appUser.setFullName(appUserInput.getFullName());
        appUser.setRoles(roles);
        appUser.setLabsAsUser(labsAsUser);
        appUser.setLabsAsAdmin(labsAsAdmin);
    }

    private void setDefaultPassword(AppUser appUser) {
        String password = appUser.getUsername().split("@")[0];
        appUser.setPassword(bCryptPasswordEncoder.encode(password));
    }

    private void validateAndSetPassword(AppUser appUser, PasswordInput passwordInput) {
        if (!bCryptPasswordEncoder.matches(passwordInput.getOldPassword(), appUser.getPassword())) {
            throw new ValidationException(Lang.OLD_PASSWORD_INPUT_ATTR_NAME, Lang.WRONG_OLD_PASSWORD);
        }
        String password = passwordInput.getNewPassword();
        String password2 = passwordInput.getNewPassword2();
        Integer minPasswordLength = 6;
        if (password.length() < minPasswordLength) {
            throw new ValidationException(Lang.NEW_PASSWORD_INPUT_ATTR_NAME, String.format(Lang.PASSWORD_TOO_SHORT, minPasswordLength));
        }
        if (!password.equals(password2)) {
            Map<String, String> errors = Map.of(
                    Lang.NEW_PASSWORD_INPUT_ATTR_NAME, Lang.PASSWORDS_MUST_BE_THE_SAME,
                    Lang.NEW_PASSWORD2_INPUT_ATTR_NAME, Lang.PASSWORDS_MUST_BE_THE_SAME);
            throw new ValidationException(errors);
        }
        appUser.setPassword(bCryptPasswordEncoder.encode(password));
    }

    @Override
    public void throwExceptionIfNotUnique(AppUserInput input, Long id) {
        Optional<AppUser> foundUser = findByUsername(
                input.getUsername());
        foundUser.ifPresent(appUser -> {
            if (!appUser.getId().equals(id)) {
                throw new ValidationException(String.format(Lang.APP_USER_SAME_NAME_FOUND_TEMPLATE, appUser.getUsername()));
            }
        });
    }



    /*
    * https://www.baeldung.com/logback
    * https://www.baeldung.com/sql-logging-spring-boot
    *
    * */

}
