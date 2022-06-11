package com.ksteindl.chemstore.audittrail;

import com.ksteindl.chemstore.domain.entities.AppUser;
import lombok.Builder;

import java.util.Optional;

@Builder
public class LoginLogInput {
    
    Optional<AppUser> appUserOpt;
    String username;
    boolean success;
    
}
