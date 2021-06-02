package com.ksteindl.chemstore.security.role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class RoleLoader implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    // TODO logging is needed
    @Override
    public void run(String... args) throws Exception {
        RoleService.ROLES.stream()
                .filter(role -> roleRepository.findByRole(role).isEmpty())
                .forEach(role -> roleRepository.save(new Role(role)));
    }

}
