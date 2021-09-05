package com.ksteindl.chemstore.service.wrapper;

import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.entities.Lab;

/**
 * AppUserCard is a wrapper of AppUser for returning to client a light-weight data representation,
 * containing only publicly available (non confidential) information about user
 * Feel free to extend with NON confidential data, if the necessary.
 */
public class LabCard {

    private final Lab lab;

    public LabCard(Lab lab) {
        this.lab = lab;
    }

    public Long getId() {
        return lab.getId();
    }

    public String getKey() {
        return lab.getKey();
    }

    public String getName() {
        return lab.getName();
    }
}
