package com.ksteindl.chemstore.domain.input;

import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
public class RecipeInput {

    @NotBlank(message = "name is required")
    private String name;

    @NotNull(message = "project (projectId) is required")
    private Long projectId;

    @NotNull(message = "shelf life (shelfLifeInDays) is required")
    @Min(value = 0, message = "shelf life (shelfLifeInDays) must be greater or equals then 0")
    private Integer shelfLifeInDays;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.000001", message = "amount of the ingredient (amount) must be a greater or equal then 0.000001")
    private Double amount;

    @NotBlank(message = "unit is required")
    private String unit;

    @Size(min=1, message = "ingredients must not be empty")
    @Valid
    private List<IngredientInput> ingredients = new ArrayList<>();
}
