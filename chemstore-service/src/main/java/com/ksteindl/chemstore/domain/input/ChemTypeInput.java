package com.ksteindl.chemstore.domain.input;

import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Data
public class ChemTypeInput implements Input{

    @NotBlank(message = "Chemical type name (name) cannot be blank")
    private String name;
}
