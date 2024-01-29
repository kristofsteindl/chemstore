package com.ksteindl.chemstore.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationFacade {

    Authentication getAuthentication();

    UserDetails getLoggedInUser();
}
