package com.ksteindl.chemstore.security.role;

import com.ksteindl.chemstore.exceptions.ResourceNotFoundException;
import com.ksteindl.chemstore.util.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Role findByRole(String role) {
        return roleRepository.findByRole(role).orElseThrow(() -> new ResourceNotFoundException(Lang.ROLE_ENTITY_NAME, role));

    }
}
