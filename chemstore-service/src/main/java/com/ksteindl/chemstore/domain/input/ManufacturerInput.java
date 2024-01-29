package com.ksteindl.chemstore.domain.input;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

@Data
public class ManufacturerInput implements Input{

    @NotBlank(message = "Manufacturer name cannot be blank")
    private String name;

}
