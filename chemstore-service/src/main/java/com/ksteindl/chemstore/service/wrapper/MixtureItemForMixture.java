package com.ksteindl.chemstore.service.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksteindl.chemstore.domain.entities.Mixture;
import com.ksteindl.chemstore.domain.entities.RecipeIngredient;

import java.time.LocalDate;

public class MixtureItemForMixture {
    
    private final Mixture mixtureItem;
    private final RecipeIngredient recipeIngredient;
    @JsonProperty
    private final Double amount;
    @JsonProperty
    private final String unit;

    public MixtureItemForMixture(Mixture mixtureItem, Mixture containerMixture) {
        this.mixtureItem = mixtureItem;
        this.recipeIngredient = containerMixture.getRecipe().getRecipeIngredients().stream()
                .filter(ri -> ri.getIngredient().getId().equals(mixtureItem.getRecipe().getId()))
                .findAny().get();
        this.amount = recipeIngredient.getAmount() * containerMixture.getAmount() / containerMixture.getRecipe().getAmount();
        this.unit = recipeIngredient.getUnit();
    }
    
    public Long getId() {
        return mixtureItem.getId();
    }
    
    public LocalDate getExpirationDate() {
        return mixtureItem.getExpirationDate();
    }
    
    public RecipeCard getRecipe() {
        return new RecipeCard(mixtureItem.getRecipe());
    }
}
