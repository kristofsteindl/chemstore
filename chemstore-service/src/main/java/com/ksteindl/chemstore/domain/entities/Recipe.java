package com.ksteindl.chemstore.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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
    
    private Boolean deleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Project project;
    
    @OneToMany(mappedBy="containerRecipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChemicalIngredient> chemicalIngredients;

    @OneToMany(mappedBy="containerRecipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeIngredient> recipeIngredients;
    
    @JsonProperty("labKey")
    private String getLabKey() {
        return project.getLab().getKey();
    }

    @JsonProperty("projectName")
    private String getProjectName() {
        return project.getName();
    }
}
