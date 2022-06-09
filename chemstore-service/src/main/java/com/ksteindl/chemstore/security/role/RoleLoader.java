package com.ksteindl.chemstore.security.role;

import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.input.AppUserQuery;
import com.ksteindl.chemstore.domain.repositories.appuser.AppUserRepository;
import com.ksteindl.chemstore.security.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;


@Component
public class RoleLoader implements CommandLineRunner {

    private final String SUPERADMIN = "superadmin";

    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    // TODO logging is needed
    @Override
    public void run(String... args) throws Exception {
        roleService.getDefaultRoles().stream()
                .filter(role -> roleRepository.findByKey(role.getKey()).isEmpty())
                .forEach(role -> roleRepository.save(role));
        Role accountManagerRole = roleRepository.findByKey(Authority.ACCOUNT_MANAGER).get();
        AppUserQuery query = AppUserQuery.builder().username(SUPERADMIN).build();
        if (appUserRepository.findAppUsers(query).isEmpty()) {
            AppUser superadmin = new AppUser();
            superadmin.setUsername(SUPERADMIN);
            superadmin.setFullName(SUPERADMIN);
            superadmin.setPassword(bCryptPasswordEncoder.encode(SUPERADMIN));
            superadmin.setRoles(Set.of(accountManagerRole));
            appUserRepository.save(superadmin);
        }
    }

}
