package com.ksteindl.chemstore.domain.repositories.appuser;

import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.input.AppUserQuery;

import java.util.List;

public interface AppUserRepositoryCustom {
    
    List<AppUser> findAppUsers(AppUserQuery appUserQuery);
    
}
