package com.ksteindl.chemstore.domain.input;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ChemicalInput implements Input{

    @NotBlank(message = "Short name of chemical cannot be blank")
    private String shortName;

    @NotBlank(message = "Exact name of chemical cannot be blank")
    private String exactName;

    @NotBlank(message = "Chemical type cannot be blank (chemicalTypeId)")
    private Long chemicalTypeId;
}
