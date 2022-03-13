package com.ksteindl.chemstore.domain.repositories;

import com.ksteindl.chemstore.domain.entities.Chemical;
import com.ksteindl.chemstore.domain.entities.ChemicalIngredient;
import com.ksteindl.chemstore.domain.entities.Recipe;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChemicalIngredientRepository extends CrudRepository<ChemicalIngredient, Long> {

    Optional<ChemicalIngredient> findByIngredientAndContainerRecipe(Chemical chemical, Recipe containerRecipe);
    
}
