package com.ksteindl.chemstore.domain.input;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class IngredientInput {

    @Pattern(regexp = "^CHEMICAL$|^RECIPE$", message = "type of the ingedient must be either CHEMICAL or RECIPE")
    private String type;

    @NotNull(message = "Ingredient id is required (ingredientId)")
    private Long ingredientId;

    @NotNull(message = "amount is required")
    @Min(value = 1L, message = "amount of the ingredient (amount) must be a positive integer number")
    private Long amount;

    @NotBlank(message = "unit is required")
    private String unit;
    
    
}
