package com.ksteindl.chemstore.domain.input;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class RecipeInput {

    @NotBlank(message = "name is required")
    private String name;

    @NotNull(message = "project (projectId) is required")
    private Long projectId;
    
    private Integer shelfLife;
    
    private List<IngredientInput> ingredients;
}
