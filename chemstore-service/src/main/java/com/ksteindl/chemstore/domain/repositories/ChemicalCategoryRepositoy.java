package com.ksteindl.chemstore.domain.repositories;

import com.ksteindl.chemstore.domain.entities.ChemicalCategory;
import com.ksteindl.chemstore.domain.entities.Lab;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChemicalCategoryRepositoy extends CrudRepository<ChemicalCategory, Long> {

    @Query("SELECT c FROM ChemicalCategory c WHERE c.lab.key = ?1 and c.name = ?2")
    Optional<ChemicalCategory> findByLabKeyAndName(String labKey, String name);

    List<ChemicalCategory> findByLab(Lab lab);

    @Query("SELECT c FROM ChemicalCategory c WHERE c.deleted = false and c.lab = :lab")
    List<ChemicalCategory> findByLabOnlyActive(@Param("lab") Lab lab);



}
