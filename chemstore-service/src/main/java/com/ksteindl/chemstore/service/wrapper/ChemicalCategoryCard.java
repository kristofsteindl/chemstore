package com.ksteindl.chemstore.service.wrapper;

import com.ksteindl.chemstore.domain.entities.ChemicalCategory;

public class ChemicalCategoryCard {

    private final ChemicalCategory chemicalCategory;

    public ChemicalCategoryCard(ChemicalCategory chemicalCategory) {
        this.chemicalCategory = chemicalCategory;
    }

    public Long getId() {
        return chemicalCategory.getId();
    }

    public String getName() {
        return chemicalCategory.getName();
    }
}
