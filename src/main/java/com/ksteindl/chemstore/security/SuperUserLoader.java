package com.ksteindl.chemstore.security;

import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.input.AppUserInput;
import com.ksteindl.chemstore.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SuperUserLoader implements CommandLineRunner {

    private final String SUPERADMIN = "superadmin";

    @Autowired
    private AppUserService appUserService;

    @Override
    public void run(String... args) throws Exception {
        if (!appUserService.findByUsername(SUPERADMIN).isPresent()) {
            AppUserInput superAdminInput = AppUserInput.builder()
                    .fullName(SUPERADMIN)
                    .username(SUPERADMIN)
                    .password(SUPERADMIN)
                    .password2(SUPERADMIN)
                    .roles(List.of(Authority.ACCOUNT_MANAGER))
                    .build();
            appUserService.crateUser(superAdminInput);
        }

    }

}
