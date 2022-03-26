package com.ksteindl.chemstore.service.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksteindl.chemstore.domain.entities.ChemItem;
import com.ksteindl.chemstore.domain.entities.ChemicalIngredient;
import com.ksteindl.chemstore.domain.entities.Manufacturer;
import com.ksteindl.chemstore.domain.entities.Mixture;

public class ChemItemForMixture {
    
    
    private final ChemItem chemItem;
    private final ChemicalIngredient chemicalIngredient;
    @JsonProperty
    private final Double amount;
    @JsonProperty
    private final String unit;

    public ChemItemForMixture(ChemItem chemItem, Mixture mixture) {
        this.chemItem = chemItem;
        this.chemicalIngredient = mixture.getRecipe().getChemicalIngredients().stream()
                .filter(ci -> ci.getIngredient().equals(chemItem.getChemical()))
                .findAny().get();
        this.amount = chemicalIngredient.getAmount() * mixture.getAmount() / mixture.getRecipe().getAmount();
        this.unit = chemicalIngredient.getUnit();
    }

    @JsonProperty("id")
    public Long getId() {
        return chemItem.getId();
    }

    @JsonProperty("chemical")
    public ChemicalCard getChemicalCard() {
        return new ChemicalCard(chemItem.getChemical());
    }

    @JsonProperty("manufacturer")
    public Manufacturer getManufacturer() {
        return chemItem.getManufacturer();
    }

    @JsonProperty("batchNumber")
    public String getBatchNumber() {
        return chemItem.getBatchNumber();
    }

    @JsonProperty("seqNumber")
    public Integer getSeqNumber() {
        return chemItem.getSeqNumber();
    }

}
