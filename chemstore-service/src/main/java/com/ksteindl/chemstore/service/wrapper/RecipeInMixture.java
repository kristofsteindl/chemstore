package com.ksteindl.chemstore.service.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksteindl.chemstore.domain.entities.ChemicalIngredient;
import com.ksteindl.chemstore.domain.entities.Recipe;
import com.ksteindl.chemstore.domain.entities.RecipeIngredient;

import java.util.List;

/**
 * RecipeCard is a wrapper of Recipe for returning to client a light-weight data representation,
 */
public class RecipeInMixture {

    private final Recipe recipe;

    public RecipeInMixture(Recipe recipe) {
        this.recipe = recipe;
    }

    public Long getId() {
        return recipe.getId();
    }

    public String getName() {
        return recipe.getName();
    }

    public Integer getShelfLifeInDays() {
        return recipe.getShelfLifeInDays();
    }

    @JsonProperty("project")
    public ProjectCard getProjectCard() {
        return new ProjectCard(recipe.getProject());
    }


    public Double getAmount() {
        return recipe.getAmount();
    }

    public String getUnit() {
        return recipe.getUnit();
    }

    public Boolean getDeleted() {
        return recipe.getDeleted();
    }

    public List<ChemicalIngredient> getChemicalIngredients() {
        return recipe.getChemicalIngredients();
    }

    public List<RecipeIngredient> getRecipeIngredients() {
        return recipe.getRecipeIngredients();
    }
}
