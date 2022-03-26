package com.ksteindl.chemstore.domain.repositories;

import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.domain.entities.Mixture;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MixtureRepository extends CrudRepository<Mixture, Long> {

    @Query("SELECT m FROM Mixture m WHERE m.recipe.project.lab = ?1")
    List<Mixture> findByLab(Lab lab);
    
}
