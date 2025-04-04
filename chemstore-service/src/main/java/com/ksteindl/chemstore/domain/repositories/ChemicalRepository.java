package com.ksteindl.chemstore.domain.repositories;

import com.ksteindl.chemstore.domain.entities.Chemical;
import com.ksteindl.chemstore.domain.entities.ChemicalCategory;
import com.ksteindl.chemstore.domain.entities.Lab;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChemicalRepository extends CrudRepository<Chemical, Long> {

    Optional<Chemical> findByShortNameAndLab(String shortName, Lab lab);

    @Query("SELECT c FROM Chemical c WHERE c.lab = ?1 and (c.shortName = ?2 or c.exactName = ?3)")
    List<Chemical> findDuplicate(Lab lab, String shortName, String exactName);

    List<Chemical> findByCategory(ChemicalCategory category);

    @Query("SELECT c FROM Chemical c WHERE c.lab = ?1 and c.deleted = false ORDER BY c.shortName desc ")
    List<Chemical> findAllActive(Lab lab);

}
