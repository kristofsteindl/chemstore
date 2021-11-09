package com.ksteindl.chemstore.security.role;

import com.ksteindl.chemstore.exceptions.ResourceNotFoundException;
import com.ksteindl.chemstore.security.Authority;
import com.ksteindl.chemstore.util.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public List<Role> getDefaultRoles() {
        return List.of(
                new Role(Authority.ACCOUNT_MANAGER, "Account Manager"));
    }

    public Role findByRole(String key) {
        return roleRepository.findByKey(key).orElseThrow(() -> new ResourceNotFoundException(Lang.ROLE_ENTITY_NAME, key));
    }

    public List<Role> getRoles() {
        return StreamSupport.stream(roleRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }
}
