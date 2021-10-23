package com.ksteindl.chemstore.domain.input;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class ChemicalInput implements Input{

    @NotBlank(message = "Short name of chemical cannot be blank")
    private String shortName;

    @NotBlank(message = "Exact name of chemical cannot be blank")
    private String exactName;

    @Min(value = 1, message = "Chemical type id must be a valid id (chemicalTypeId)")
    private Long chemTypeId;
}
