package com.ksteindl.chemstore.domain.input;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class ManufacturerInput implements Input{

    @NotBlank(message = "Manufacturer name cannot be blank")
    private String name;

}
