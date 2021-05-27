package com.ksteindl.chemstore.domain.repositories;

import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.entities.Lab;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppUserRepository extends CrudRepository<AppUser, Long> {

    Optional<AppUser> findByUsername(String username);

    List<AppUser> findAll(Sort sort);

    @Query("SELECT u FROM AppUser u WHERE u.deleted = false")
    List<AppUser> findAllActive(Sort sort);

    List<AppUser> findByLabsAsAdmin(Lab lab);

    List<AppUser> findByLabsAsUser(Lab lab);


    /*
    * https://www.baeldung.com/spring-data-jpa-query
    * https://stackoverflow.com/questions/19178820/spring-requestparam-mapping-boolean-based-on-1-or-0-instead-of-true-or-false
    * https://stackoverflow.com/questions/24441411/spring-data-jpa-find-by-embedded-object-property
    * */

}
