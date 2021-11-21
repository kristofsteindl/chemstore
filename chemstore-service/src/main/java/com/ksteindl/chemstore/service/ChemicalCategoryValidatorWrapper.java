package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.domain.entities.ChemicalCategory;
import com.ksteindl.chemstore.domain.input.ChemicalCategoryInput;
import lombok.Builder;
import lombok.Data;

import java.security.Principal;

@Data
@Builder
public class ChemicalCategoryValidatorWrapper {

    ChemicalCategoryInput chemicalCategoryInput;
    ChemicalCategory chemicalCategory;
    Long id;
    Principal principal;
}
