package com.ksteindl.chemstore.service.wrapper;

import com.ksteindl.chemstore.domain.entities.Recipe;

/**
 * RecipeCard is a wrapper of Recipe for returning to client a light-weight data representation,
 */
public class RecipeCard {

    private final Recipe recipe;

    public RecipeCard(Recipe recipe) {
        this.recipe = recipe;
    }

    public Long getId() {
        return recipe.getId();
    }

    public String getName() {
        return recipe.getName();
    }
}
