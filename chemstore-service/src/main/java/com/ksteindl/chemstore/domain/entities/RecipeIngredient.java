package com.ksteindl.chemstore.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksteindl.chemstore.service.wrapper.RecipeCard;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
public class RecipeIngredient implements Ingredient{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    private Recipe ingredient;

    @ManyToOne
    @JsonIgnore
    private Recipe containerRecipe;
    
    private Double amount;
    
    private String unit;

    @JsonProperty("containerRecipe")
    public RecipeCard getContainerRecipeCard() {
        return new RecipeCard(containerRecipe);
    }

    @JsonProperty("ingredient")
    public RecipeCard getIngredientCard() {
        return new RecipeCard(ingredient);
    }
}
