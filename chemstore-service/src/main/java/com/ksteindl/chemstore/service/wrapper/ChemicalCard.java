package com.ksteindl.chemstore.service.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksteindl.chemstore.domain.entities.Chemical;

public class ChemicalCard {
    
    private final Chemical chemical;
    
    public ChemicalCard(Chemical chemical) {
        this.chemical = chemical;
    }
    @JsonProperty("id")
    public Long getId() {
        return chemical.getId();
    }

    @JsonProperty("shortName")
    public String getShortName() {
        return chemical.getShortName();
    }

    @JsonProperty("exactName")
    public String getExactName() {
        return chemical.getExactName();
    }

    @JsonProperty("deleted")
    public Boolean getDeleted() {
        return chemical.getDeleted();
    }
}
