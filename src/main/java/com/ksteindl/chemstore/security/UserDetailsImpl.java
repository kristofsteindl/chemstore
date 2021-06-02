package com.ksteindl.chemstore.security;

import com.ksteindl.chemstore.domain.entities.AppUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserDetailsImpl implements UserDetails {

    private final AppUser appUser;
    private final List<SimpleGrantedAuthority> authorities;

    public UserDetailsImpl(AppUser appUser) {
        this.appUser = appUser;
        authorities = new ArrayList<>();
        if (appUser.getRoles().stream().anyMatch(role -> role.getRole().equals(Authority.ACCOUNT_MANAGER))) {
            authorities.add(new SimpleGrantedAuthority(Authority.ACCOUNT_MANAGER));
        }
        if (!appUser.getManagedLabs().isEmpty()) {
            authorities.add(new SimpleGrantedAuthority(Authority.LAB_MANAGER));
        }
        if (!appUser.getLabsAsAdmin().isEmpty()) {
            authorities.add(new SimpleGrantedAuthority(Authority.LAB_ADMIN));
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return appUser.getPassword();
    }

    @Override
    public String getUsername() {
        return appUser.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
