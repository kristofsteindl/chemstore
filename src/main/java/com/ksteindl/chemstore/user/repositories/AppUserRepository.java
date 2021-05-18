package com.ksteindl.chemstore.user.repositories;

import com.ksteindl.chemstore.user.domain.AppUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepository extends CrudRepository<AppUser, Long> {
}
