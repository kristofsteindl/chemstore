package com.ksteindl.chemstore.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksteindl.chemstore.service.wrapper.AppUserCard;
import com.ksteindl.chemstore.service.wrapper.ChemItemForMixture;
import com.ksteindl.chemstore.service.wrapper.MixtureItemForMixture;
import com.ksteindl.chemstore.service.wrapper.RecipeCard;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
public class Mixture {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private AppUser creator;
    
    private LocalDate creationDate;
    
    private Double amount;
    
    private LocalDate expirationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Recipe recipe;
    
    @ManyToMany(cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ChemItem> chemItems = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Mixture> mixtureItems = new ArrayList<>();

    @JsonProperty("recipe")
    public RecipeCard getRecipeCard() {
        return new RecipeCard(recipe);
    }
    
    @JsonProperty("creator")
    public AppUserCard getCreatorCard() {
        return new AppUserCard(creator);
    }

    @JsonProperty("chemItems")
    public List<ChemItemForMixture> getChemItemsForMixtures() {
        return chemItems.stream().map(chemItem -> new ChemItemForMixture(chemItem, this))
                .collect(Collectors.toList());
    }

    @JsonProperty("mixtureItems")
    public List<MixtureItemForMixture> getMixtureItemsForMixtures() {
        return mixtureItems.stream().map(mixture -> new MixtureItemForMixture(mixture, this))
                .collect(Collectors.toList());
    }

    public void addChemItem(ChemItem chemItem) {
        chemItems.add(chemItem);
    }

    public void addMixtureItem(Mixture mixtureItem) {
        mixtureItems.add(mixtureItem);
    }

}
