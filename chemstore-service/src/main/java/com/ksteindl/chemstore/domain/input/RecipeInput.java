package com.ksteindl.chemstore.domain.input;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
public class RecipeInput {

    @NotBlank(message = "name is required")
    private String name;

    @NotNull(message = "project (projectId) is required")
    private Long projectId;
    
    @Min(value = 0, message = "shelf life (shelfLifeInDays) must be greater or equals then 0")
    private Integer shelfLifeInDays;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.000001", message = "amount of the ingredient (amount) must be a greater or equal then 0.000001")
    private Double amount;

    @NotBlank(message = "unit is required")
    private String unit;

    @Size(min=1)
    private List<IngredientInput> ingredients = new ArrayList<>();
}
