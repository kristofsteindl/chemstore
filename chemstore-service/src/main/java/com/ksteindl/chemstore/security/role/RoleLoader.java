package com.ksteindl.chemstore.security.role;

import com.ksteindl.chemstore.domain.input.AppUserInput;
import com.ksteindl.chemstore.security.Authority;
import com.ksteindl.chemstore.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class RoleLoader implements CommandLineRunner {

    private final String SUPERADMIN = "superadmin";

    @Autowired
    private AppUserService appUserService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleService roleService;

    // TODO logging is needed
    @Override
    public void run(String... args) throws Exception {
        roleService.getDefaultRoles().stream()
                .filter(role -> roleRepository.findByKey(role.getKey()).isEmpty())
                .forEach(role -> roleRepository.save(role));
        if (!appUserService.findByUsername(SUPERADMIN).isPresent()) {
            AppUserInput superAdminInput = AppUserInput.builder()
                    .fullName(SUPERADMIN)
                    .username(SUPERADMIN)
                    .roles(List.of(Authority.ACCOUNT_MANAGER))
                    .build();
            appUserService.createUser(superAdminInput);
        }
    }

}
