package com.ksteindl.chemstore.domain.repositories;

import com.ksteindl.chemstore.domain.entities.Chemical;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChemicalRepository extends CrudRepository<Chemical, Long> {

    Optional<Chemical> findByShortNameOrExactName(String shortName, String exactName);

    List<Chemical> findAllByOrderByShortNameAsc();

}
