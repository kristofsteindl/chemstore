package com.ksteindl.chemstore.domain.repositories;

import com.ksteindl.chemstore.domain.entities.ChemType;
import com.ksteindl.chemstore.domain.entities.Manufacturer;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ChemTypeRepository extends CrudRepository<ChemType, Long> {

    List<ChemType> findAll(Sort sort);

    @Query("SELECT u FROM ChemType u WHERE u.deleted = false")
    List<ChemType> findAllActive(Sort sort);

    Optional<ChemType> findByName(String name);
}
