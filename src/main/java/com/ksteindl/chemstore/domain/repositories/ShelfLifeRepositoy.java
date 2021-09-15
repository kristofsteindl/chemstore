package com.ksteindl.chemstore.domain.repositories;

import com.ksteindl.chemstore.domain.entities.ChemType;
import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.domain.entities.ShelfLife;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShelfLifeRepositoy extends CrudRepository<ShelfLife, Long> {

    List<ShelfLife> findAll();

    @Query("SELECT s FROM ShelfLife s WHERE s.deleted = false")
    List<ShelfLife> findAllActive();

    Optional<ShelfLife> findByLabAndChemType(Lab lab, ChemType chemType);

    List<ShelfLife> findByLab(Lab lab);

    @Query("SELECT s FROM ShelfLife s WHERE s.deleted = false and s.lab = :lab")
    List<ShelfLife> findByLabOnlyActive(@Param("lab") Lab lab);



}
