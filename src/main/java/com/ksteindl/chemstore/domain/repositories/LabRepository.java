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
public interface LabRepository extends CrudRepository<Lab, Long> {

    List<Lab> findByKeyOrName(String key, String name);

    Optional<Lab> findByKey(String key);

    @Query("SELECT l FROM Lab l WHERE l.deleted = false")
    List<Lab> findAllActive(Sort sort);

    List<Lab> findAll(Sort sort);

    List<Lab> findByLabManagers(AppUser appUser);

    /*
    * https://prog.hu/tudastar/164576/kereses-kapcsolotabla-hasznalataval-mysql-ben
    *
    * @Query(value = "select * from lab l inner join user_of_lab_table u ON l.id = u.lab_id where u.app_user_id = ?;", nativeQuery = true)
    List<Lab> findLabsWhereUserAccess(AppUser appUser);

    @Query(value = "select * from lab l inner join admin_of_lab_table u ON l.id = u.lab_id where u.app_user_id = ?;", nativeQuery = true)
    List<Lab> findLabsWhereUserAdmin(AppUser appUser);
    *
    * */

}
