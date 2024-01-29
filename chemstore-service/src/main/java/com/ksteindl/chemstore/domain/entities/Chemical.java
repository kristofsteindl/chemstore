package com.ksteindl.chemstore.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksteindl.chemstore.service.wrapper.ChemicalCategoryCard;
import com.ksteindl.chemstore.service.wrapper.LabCard;
import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
@Data
public class Chemical implements HasLab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String shortName;

    private String exactName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Lab lab;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private ChemicalCategory category;

    private Boolean deleted = false;

    @JsonProperty("category")
    private ChemicalCategoryCard getChemicalCategoryCard() {
        return this.category == null ? null : new ChemicalCategoryCard(this.category);
    }

    @JsonProperty("lab")
    private LabCard getLabCard() {
        return new LabCard(this.lab);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Chemical chemical = (Chemical) o;

        return id.equals(chemical.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
