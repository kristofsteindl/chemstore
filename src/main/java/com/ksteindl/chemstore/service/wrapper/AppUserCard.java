package com.ksteindl.chemstore.service.wrapper;

import com.ksteindl.chemstore.domain.entities.AppUser;
import lombok.Data;

/**
 * AppUserCard is a wrapper of AppUser for returning to client only public information (non confidential data about user)
 * Feel free to extend with NON confidential data, if the necessary.
 */
public class AppUserCard {

    private final AppUser appUser;

    public AppUserCard(AppUser appUser) {
        this.appUser = appUser;
    }

    public Long getId() {
        return appUser.getId();
    }

    public String getUsername() {
        return appUser.getUsername();
    }

    public String getFullName() {
        return appUser.getFullName();
    }
}
