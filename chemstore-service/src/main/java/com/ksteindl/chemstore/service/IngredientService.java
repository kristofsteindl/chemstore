package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.domain.entities.Chemical;
import com.ksteindl.chemstore.domain.entities.ChemicalIngredient;
import com.ksteindl.chemstore.domain.entities.HasLab;
import com.ksteindl.chemstore.domain.entities.Ingredient;
import com.ksteindl.chemstore.domain.entities.Recipe;
import com.ksteindl.chemstore.domain.entities.RecipeIngredient;
import com.ksteindl.chemstore.domain.input.IngredientInput;
import com.ksteindl.chemstore.domain.input.RecipeInput;
import com.ksteindl.chemstore.domain.repositories.ChemicalIngredientRepository;
import com.ksteindl.chemstore.domain.repositories.RecipeIngredientRepository;
import com.ksteindl.chemstore.domain.repositories.RecipeRepository;
import com.ksteindl.chemstore.exceptions.ResourceNotFoundException;
import com.ksteindl.chemstore.exceptions.ValidationException;
import com.ksteindl.chemstore.util.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class IngredientService {
    
    public static final String CHEMICAL = "CHEMICAL";
    public static final String RECIPE = "RECIPE";
    
    @Autowired
    private ChemicalService chemicalService;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private ChemicalIngredientRepository chemicalIngredientRepo;
    @Autowired
    private RecipeIngredientRepository recipeIngredientRepo;
    @Autowired
    private UnitService unitService;

    public void setIngredientAttributes(Recipe recipe, RecipeInput recipeInput) {
        List<IngredientInput> ingredientInputs = recipeInput.getIngredients();
        if (ingredientInputs.isEmpty()) {
            throw new ValidationException(Lang.NO_INGREDIENT_INPUTS);
        } 
        ingredientInputs.forEach(ingredientInput -> setIngredient(recipe, ingredientInput));
        deleteIngredients(recipe, recipeInput);
    }
    
    private void deleteIngredients(Recipe recipe, RecipeInput recipeInput) {
        deleteChemicalIngredients(recipe, recipeInput);
        deleteRecipeIngredients(recipe, recipeInput);
    }
    
    private void deleteChemicalIngredients(Recipe recipe, RecipeInput recipeInput) {
        Set<Long> chemicalIngrSet =  recipeInput.getIngredients().stream()
                .filter(ingredientInput -> ingredientInput.getType().equals(CHEMICAL))
                .map(ingredientInput -> ingredientInput.getIngredientId())
                .collect(Collectors.toSet());
        List<ChemicalIngredient> ingList = new ArrayList<>(recipe.getChemicalIngredients());
        ingList.forEach(chemicalIngredient -> {
            if (!chemicalIngrSet.contains(chemicalIngredient.getIngredient().getId())) {
                recipe.getChemicalIngredients().remove(chemicalIngredient);
            }
        });
    }

    private void deleteRecipeIngredients(Recipe recipe, RecipeInput recipeInput) {
        Set<Long> recipeIngrSet =  recipeInput.getIngredients().stream()
                .filter(ingredientInput -> ingredientInput.getType().equals(RECIPE))
                .map(ingredientInput -> ingredientInput.getIngredientId())
                .collect(Collectors.toSet());
        List<RecipeIngredient> ingList = new ArrayList<>(recipe.getRecipeIngredients());
        ingList.forEach(recipeIngredient -> {
            if (!recipeIngrSet.contains(recipeIngredient.getIngredient().getId())) {
                recipe.getRecipeIngredients().remove(recipeIngredient);
            }
        });
    }
    
    private void setIngredient(Recipe recipe, IngredientInput ingredientInput) {
        String type = ingredientInput.getType();
        if (type.equals(CHEMICAL)) {
            createOrUpdateChemicalIngredient(recipe, ingredientInput);
        } else if (type.equals(RECIPE)) {
            createOrUpdateRecipeIngredient(recipe, ingredientInput);
        } else {
            throw new ValidationException(String.format(Lang.RECIPE_WRONG_INGREDIENT_TYPE, type));
        }
    }

    private void createOrUpdateRecipeIngredient(Recipe containerRecipe, IngredientInput ingredientInput) {
        Recipe recipe = findRecipeById(ingredientInput.getIngredientId());
        assertHaveSameLab(recipe, containerRecipe, Lang.INGREDIENT_LAB_AND_PROJECT_LAB_DIFFERS);
        RecipeIngredient recipeIngredient = getLinkedRecipeIngredient(recipe, containerRecipe);
        setIngredientAttributes(recipeIngredient, ingredientInput);
    }

    private RecipeIngredient getLinkedRecipeIngredient(Recipe ingredient, Recipe containerRecipe) {
        if (containerRecipe.getId() == null) {
            return createAndLinkNewRecipeIngredient(ingredient, containerRecipe);
        }
        return recipeIngredientRepo
                .findByIngredientAndContainerRecipe(ingredient, containerRecipe)
                .orElseGet(() -> createAndLinkNewRecipeIngredient(ingredient, containerRecipe));
    }
    
    private RecipeIngredient createAndLinkNewRecipeIngredient(Recipe ingredient, Recipe containerRecipe) {
        RecipeIngredient recipeIngredient = new RecipeIngredient();
        recipeIngredient.setIngredient(ingredient);
        containerRecipe.getRecipeIngredients().add(recipeIngredient);
        return  recipeIngredient;
    }

    private void createOrUpdateChemicalIngredient(Recipe containerRecipe, IngredientInput ingredientInput) {
        Chemical chemical = chemicalService.findById(ingredientInput.getIngredientId());
        assertHaveSameLab(chemical, containerRecipe, Lang.INGREDIENT_LAB_AND_PROJECT_LAB_DIFFERS);
        ChemicalIngredient chemicalIngredient = getLinkedChemicalIngredient(chemical, containerRecipe);
        setIngredientAttributes(chemicalIngredient, ingredientInput);
    }
    
    private ChemicalIngredient getLinkedChemicalIngredient(Chemical ingredient, Recipe containerRecipe) {
        if (containerRecipe.getId() == null) {
            return createAndLinkNewChemicalIngredient(ingredient, containerRecipe);
        }
        return chemicalIngredientRepo
                .findByIngredientAndContainerRecipe(ingredient, containerRecipe)
                .orElseGet(() -> createAndLinkNewChemicalIngredient(ingredient, containerRecipe));
    }
    
    private ChemicalIngredient createAndLinkNewChemicalIngredient(Chemical ingredient, Recipe containerRecipe) {
        ChemicalIngredient chemicalIngredient = new ChemicalIngredient();
        chemicalIngredient.setIngredient(ingredient);
        containerRecipe.getChemicalIngredients().add(chemicalIngredient);
        return chemicalIngredient;
    }

    private void setIngredientAttributes(Ingredient ingredient, IngredientInput input) {
        String unit = input.getUnit();
        unitService.validate(unit);
        ingredient.setUnit(input.getUnit());
        ingredient.setAmount(input.getAmount());
    }
    
    private void assertHaveSameLab(HasLab hasLab1, HasLab hasLab2, String msgTemlpate) {
        if (!hasLab1.getLab().getKey().equals(hasLab2.getLab().getKey())) {
            throw new ValidationException(
                    String.format(msgTemlpate,
                            hasLab1.getLab().getKey(),
                            hasLab2.getLab().getKey()));
        }
    }
    
    public Recipe findRecipeById(Long id) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Lang.RECIPE_ENTITY_NAME, id));
        if (recipe.getDeleted()) {
            throw new ResourceNotFoundException(String.format(Lang.RECIPE_ALREADY_DELETED, recipe.getName()));
        }
        return recipe;
    }

}
