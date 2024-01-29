package com.ksteindl.chemstore.domain.input;

import lombok.Data;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Data
public class IngredientInput {

    @Pattern(regexp = "^CHEMICAL$|^RECIPE$", message = "type of the ingredient must be either CHEMICAL or RECIPE")
    private String type;

    @NotNull(message = "Ingredient id is required (ingredientId)")
    private Long ingredientId;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.000001", message = "amount of the ingredient (amount) must be a greater or equal then 0.000001")
    private Double amount;

    @NotBlank(message = "unit is required")
    private String unit;
    
    
}
