package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.exceptions.ResourceNotFoundException;
import com.ksteindl.chemstore.exceptions.ValidationException;
import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.input.AppUserInput;
import com.ksteindl.chemstore.domain.repositories.AppUserRepository;
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

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AppUserService implements UniqueEntityInput<AppUserInput>, UserDetailsService {

    private static final Logger logger
            = LoggerFactory.getLogger(AppUserService.class);

    private final static Sort SORT_BY_USERNAME = Sort.by(Sort.Direction.ASC, "username");

    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private LabService labService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return appUserRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Transactional
    public AppUser loadUserById(Long id) {
        return appUserRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
   }

    public AppUser crateUser(AppUserInput appUserInput) {
        AppUser appUser = new AppUser();
        throwExceptionIfNotUnique(appUserInput);
        validateAndSetAppUser(appUser, appUserInput);
        return appUserRepository.save(appUser);
    }


    public AppUser updateUser(AppUserInput appUserInput, Long id) {
        AppUser appUser = appUserRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Lang.APP_USER_ENTITY_NAME, id));
        throwExceptionIfNotUnique(appUserInput, id);
        validateAndSetAppUser(appUser, appUserInput);
        return appUserRepository.save(appUser);
    }


    public List<AppUser> getAppUsers() {
        return getAppUsers(true);
    }

    public List<AppUser> getAppUsers(Boolean onlyActive) {
        return onlyActive ?
                appUserRepository.findAllActive(SORT_BY_USERNAME) :
                appUserRepository.findAll(SORT_BY_USERNAME);
    }

    public void deleteAppUser(Long id) {
        AppUser appUser = appUserRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Lang.APP_USER_ENTITY_NAME, id));
        appUser.setDeleted(true);
        appUser.setLabsAsAdmin(Collections.emptyList());
        appUser.setLabsAsUser(Collections.emptyList());
        labService.removeUserFromLabs(appUser);
        appUserRepository.save(appUser);
    }

    public AppUser findById(Long id) {
        return findById(id, false);
    }

    public AppUser findById(Long id, Boolean onlyActive) {
        AppUser appUser = appUserRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Lang.APP_USER_ENTITY_NAME, id));
        if (onlyActive && appUser.getDeleted()) {
            throw new ValidationException(Lang.APP_USER_ENTITY_NAME, String.format(Lang.APP_USER_IS_DELETED, appUser.getUsername()));
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

    private void validateAndSetAppUser(AppUser appUser, AppUserInput appUserInput) {
        List<Lab> labsAsUser = appUserInput.getLabIdsAsUser()
                .stream()
                .map(labId -> labService.findById(labId))
                .collect(Collectors.toList());
        appUser.setLabsAsUser(labsAsUser);
        List<Lab> labsAsAdmin = appUserInput.getLabIdsAsAdmin()
                .stream()
                .map(labId -> labService.findById(labId))
                .collect(Collectors.toList());
        appUser.setLabsAsAdmin(labsAsAdmin);
        appUser.setUsername(appUserInput.getUsername());
        appUser.setFullName(appUserInput.getFullName());
        validateAndSetPassword(appUser, appUserInput);
    }

    private void validateAndSetPassword(AppUser appUser, AppUserInput appUserInput) {
        String password = appUserInput.getPassword();
        String password2 = appUserInput.getPassword2();
        if (!password.equals(password2)) {
            Map<String, String> errors = Map.of(
                    Lang.APP_USER_PASSWORD_ATTRIBUTE_NAME, Lang.PASSWORDS_MUST_BE_THE_SAME,
                    Lang.APP_USER_PASSWORD2_ATTRIBUTE_NAME, Lang.PASSWORDS_MUST_BE_THE_SAME);
            throw new ValidationException(errors);
        }
        appUser.setPassword(bCryptPasswordEncoder.encode(password));
    }

    @Override
    public void throwExceptionIfNotUnique(AppUserInput input, Long id) {
        Optional<AppUser> foundUser = appUserRepository.findByUsername(
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
