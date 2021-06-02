package com.ksteindl.chemstore.security.role;

import com.ksteindl.chemstore.exceptions.ResourceNotFoundException;
import com.ksteindl.chemstore.security.Authority;
import com.ksteindl.chemstore.util.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    public static final List<String> ROLES = List.of(
            Authority.ACCOUNT_MANAGER);


    @Autowired
    private RoleRepository roleRepository;

    public Role findByRole(String role) {
        return roleRepository.findByRole(role).orElseThrow(() -> new ResourceNotFoundException(Lang.ROLE_ENTITY_NAME, role));

    }
}
