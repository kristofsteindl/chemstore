package com.ksteindl.chemstore.domain.input;

import javax.validation.constraints.NotBlank;
import java.util.List;

public class RecipeInput {

    @NotBlank(message = "name is required")
    private String name;
    
    private Integer shelfLife;
    
    private List<IngredientInput> ingredients;
}
