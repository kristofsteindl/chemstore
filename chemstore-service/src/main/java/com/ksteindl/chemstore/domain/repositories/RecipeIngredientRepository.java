package com.ksteindl.chemstore.domain.repositories;

import com.ksteindl.chemstore.domain.entities.Recipe;
import com.ksteindl.chemstore.domain.entities.RecipeIngredient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecipeIngredientRepository extends CrudRepository<RecipeIngredient, Long> {

    Optional<RecipeIngredient> findByIngredientAndContainerRecipe(Recipe ingredient, Recipe containerRecipe);
    
}
