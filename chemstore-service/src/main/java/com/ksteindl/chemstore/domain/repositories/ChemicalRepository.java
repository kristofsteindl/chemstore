package com.ksteindl.chemstore.domain.repositories;

import com.ksteindl.chemstore.domain.entities.Chemical;
import com.ksteindl.chemstore.domain.entities.Manufacturer;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChemicalRepository extends CrudRepository<Chemical, Long> {

    Optional<Chemical> findByShortName(String shortName);

    List<Chemical> findByShortNameOrExactName(String shortName, String exactName);

    List<Chemical> findAllByOrderByShortNameAsc();

    List<Chemical> findAll(Sort sort);

    @Query("SELECT u FROM Chemical u WHERE u.deleted = false")
    List<Chemical> findAllActive(Sort sort);

}
