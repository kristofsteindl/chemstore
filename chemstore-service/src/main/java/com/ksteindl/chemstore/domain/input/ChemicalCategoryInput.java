package com.ksteindl.chemstore.domain.input;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Getter
@Setter
@Builder
public class ChemicalCategoryInput implements Input{

    @NotNull(message = "Lab (labKey) is required")
    private String labKey;

    @NotBlank(message = "Chemical category name (name) cannot be blank")
    private String name;

    @NotNull(message = "amount is required")
    @Min(value = 1L, message = "amount of shelf life duration must be a positive integer (amount)")
    private Integer amount;

    @Pattern(regexp = "d|w|m|y")
    private String unit;

}
