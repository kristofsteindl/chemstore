package com.ksteindl.chemstore.domain.repositories;

import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.entities.Manufacturer;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ManufacturerRepository extends CrudRepository<Manufacturer, Long> {

    Optional<Manufacturer> findByName(String name);

    List<Manufacturer> findAllByOrderByName();

    List<Manufacturer> findAll(Sort sort);

    @Query("SELECT u FROM Manufacturer u WHERE u.deleted = false")
    List<Manufacturer> findAllActive(Sort sort);



}
