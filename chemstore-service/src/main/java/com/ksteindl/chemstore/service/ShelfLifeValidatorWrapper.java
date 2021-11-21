package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.domain.entities.ShelfLife;
import com.ksteindl.chemstore.domain.input.ChemicalCategoryInput;
import lombok.Builder;
import lombok.Data;

import java.security.Principal;

@Data
@Builder
public class ShelfLifeValidatorWrapper {

    ChemicalCategoryInput chemicalCategoryInput;
    ShelfLife shelfLife;
    Long id;
    Principal principal;
}
