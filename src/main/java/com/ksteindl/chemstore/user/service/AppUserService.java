package com.ksteindl.chemstore.user.service;

import com.ksteindl.chemstore.user.domain.AppUser;
import com.ksteindl.chemstore.user.domain.AppUserInput;
import com.ksteindl.chemstore.user.repositories.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppUserService {

    @Autowired
    private AppUserRepository appUserRepository;

    public AppUser crateUser(AppUserInput appUserInput) {

    }

}
