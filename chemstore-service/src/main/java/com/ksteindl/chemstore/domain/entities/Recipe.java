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
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Recipe implements HasLab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    private Integer shelfLifeInDays;
    
    private Double amount;
    
    private String unit;
    
    private Boolean deleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Project project;
    
    @OneToMany(mappedBy="containerRecipe", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<ChemicalIngredient> chemicalIngredients = new ArrayList<>();

    @OneToMany(mappedBy="containerRecipe", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<RecipeIngredient> recipeIngredients = new ArrayList<>();
    
    @JsonProperty("labKey")
    private String getLabKey() {
        return project.getLab().getKey();
    }

    @JsonProperty("projectName")
    private String getProjectName() {
        return project.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Recipe recipe = (Recipe) o;

        return id.equals(recipe.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public Lab getLab() {
        return project.getLab();
    }
}
