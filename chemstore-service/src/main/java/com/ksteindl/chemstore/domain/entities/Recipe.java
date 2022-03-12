package com.ksteindl.chemstore.domain.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Getter
@Setter
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    private Integer shelfLife;
    
    @OneToMany(mappedBy="containerRecipe")
    private List<ChemicalIngredient> chemicalIngredients;

    @OneToMany(mappedBy="containerRecipe")
    private List<RecipeIngredient> recipeIngredients;
}
