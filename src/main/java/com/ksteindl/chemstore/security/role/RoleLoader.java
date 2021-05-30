package com.ksteindl.chemstore.security.role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Component
public class RoleLoader implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    // TODO logging is needed
    @Override
    public void run(String... args) throws Exception {
        Role.ROLES.stream()
                .filter(role -> roleRepository.findByRole(role).isEmpty())
                .forEach(role -> roleRepository.save(new Role(role)));
    }

}
