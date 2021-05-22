package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.exceptions.ResourceNotFoundException;
import com.ksteindl.chemstore.exceptions.ValidationException;
import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.input.RegUserInput;
import com.ksteindl.chemstore.domain.input.UpdateUserInput;
import com.ksteindl.chemstore.domain.repositories.AppUserRepository;
import com.ksteindl.chemstore.util.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AppUserService implements UniqueEntityInput<UpdateUserInput> {

    @Autowired
    private AppUserRepository appUserRepository;

    public AppUser crateUser(RegUserInput regUserInput) {
        AppUser appUser = new AppUser();
        validateAndSetAppUser(appUser, regUserInput);
        return appUserRepository.save(appUser);
    }


    public AppUser updateUser(UpdateUserInput updateUserInput, Long id) {
        AppUser appUser = appUserRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Lang.APP_USER_ENTITY_NAME, id));
        throwExceptionIfNotUnique(updateUserInput, id);
        appUser.setUsername(updateUserInput.getUsername());
        appUser.setFullName(updateUserInput.getFullName());
        return appUserRepository.save(appUser);
    }


    public List<AppUser> getAllAppUser() {
        return appUserRepository.findAllByOrderByUsernameAsc();
    }

    private void validateAndSetAppUser(AppUser appUser, RegUserInput regUserInput) {
        throwExceptionIfNotUnique(regUserInput);
        appUser.setUsername(regUserInput.getUsername());
        appUser.setFullName(regUserInput.getFullName());
        validateAndSetPassword(appUser, regUserInput);
    }

    private void validateAndSetPassword(AppUser appUser, RegUserInput regUserInput) {
        String password = regUserInput.getPassword();
        String password2 = regUserInput.getPassword2();
        if (!password.equals(password2)) {
            Map<String, String> errors = Map.of(
                    Lang.APP_USER_PASSWORD_ATTRIBUTE_NAME, Lang.PASSWORDS_MUST_BE_THE_SAME,
                    Lang.APP_USER_PASSWORD2_ATTRIBUTE_NAME, Lang.PASSWORDS_MUST_BE_THE_SAME);
            throw new ValidationException(errors);
        }
        appUser.setPassword(password);
    }

    @Override
    public void throwExceptionIfNotUnique(UpdateUserInput input, Long id) {
        Optional<AppUser> foundUser = appUserRepository.findByUsername(
                input.getUsername());
        foundUser.ifPresent(appUser -> {
            if (!appUser.getId().equals(id)) {
                throw new ValidationException(String.format(Lang.APP_USER_SAME_NAME_FOUND_TEMPLATE, appUser.getUsername()));
            }
        });
    }

}
