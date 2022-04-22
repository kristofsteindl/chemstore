package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.domain.entities.Project;
import com.ksteindl.chemstore.domain.entities.Recipe;
import com.ksteindl.chemstore.domain.input.RecipeInput;
import com.ksteindl.chemstore.domain.repositories.RecipeRepository;
import com.ksteindl.chemstore.exceptions.ResourceNotFoundException;
import com.ksteindl.chemstore.exceptions.ValidationException;
import com.ksteindl.chemstore.util.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

@Service
public class RecipeService {
    
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private LabService labService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private IngredientService ingredientService;
    @Autowired
    private UnitService unitService;
    
    
    public Recipe createRecipe(RecipeInput recipeInput, Principal managerPrincipal) {
        Recipe recipe = new Recipe();
        return createOrUpdateRecipe(recipe, recipeInput, managerPrincipal, ingredientService::createIngredientAttributes);
    }

    public Recipe updateRecipe(RecipeInput recipeInput, Long id, Principal managerPrincipal) {
        Recipe recipe = findById(id);
        Project oldProject = recipe.getProject();
        labService.validateLabForManager(oldProject.getLab(), managerPrincipal);
        Project newProject = projectService.findById(recipeInput.getProjectId());
        if (!newProject.getLab().getKey().equals(oldProject.getLab().getKey())) {
            throw new ValidationException(String.format(
                    Lang.RECIPE_UPDATE_PROJECT_LAB_NOT_THE_SAME,
                    oldProject.getName(),
                    oldProject.getLab().getKey(),
                    newProject.getName(),
                    newProject.getLab().getKey()));
        }
        return createOrUpdateRecipe(recipe, recipeInput, managerPrincipal, ingredientService::updateIngredientAttributes);
    }
    
    private Recipe createOrUpdateRecipe(Recipe recipe, RecipeInput recipeInput, Principal managerPrincipal, BiConsumer<Recipe, RecipeInput> consumer) {
        Project project = projectService.findById(recipeInput.getProjectId());
        labService.validateLabForManager(project.getLab(), managerPrincipal);
        throwExceptionIfNotUnique(recipeInput.getName(), project, recipe.getId());
        recipe.setProject(project);
        validateAndSetAttributes(recipe, recipeInput);
        consumer.accept(recipe, recipeInput);
        return recipeRepository.save(recipe);
    }
    
    public void deleteRecipe(Long id, Principal managerPrincipal) {
        Recipe recipe = findById(id);
        Project oldProject = recipe.getProject();
        labService.validateLabForManager(oldProject.getLab(), managerPrincipal);
        recipe.setDeleted(true);
        recipeRepository.save(recipe);
    }
    
    public List<Recipe> getRecipes(Long projectId, Principal user, boolean onlyActive) {
        Project project = projectService.findById(projectId, onlyActive);
        labService.validateLabForUser(project.getLab(), user);
        return onlyActive ? 
                recipeRepository.findAllActive(project) :
                recipeRepository.findAllByProject(project);
    }

    public Recipe getRecipe(Long id, Principal labManager) {
        Recipe recipe = findById(id, false);
        labService.validateLabForManager(recipe.getLab(), labManager);
        return recipe;
    }

    public Recipe findById(Long id) {
        return findById(id, true);
    }
    
    public Recipe findById(Long id, boolean onlyActive) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Lang.RECIPE_ENTITY_NAME, id));
        if (recipe.getDeleted() && onlyActive) {
            throw new ResourceNotFoundException(String.format(Lang.RECIPE_ALREADY_DELETED, recipe.getName()));
        }
        return recipe;
    }
    
    private void validateAndSetAttributes(Recipe recipe, RecipeInput recipeInput) {
        String unit = recipeInput.getUnit();
        unitService.validate(recipeInput.getUnit());
        recipe.setAmount(recipeInput.getAmount());
        recipe.setUnit(unit);
        recipe.setShelfLifeInDays(recipeInput.getShelfLifeInDays());
        recipe.setName(recipeInput.getName());
    }

    private void throwExceptionIfNotUnique(String name, Project project, Long id) {
        Optional<Recipe> optional = recipeRepository.findByNameAndProject(name, project);
        optional.ifPresent(recipe -> {
            if (!recipe.getId().equals(id)) {
                throw new ValidationException(String.format(Lang.RECIPE_WITH_SAME_NAME_FOUND, name, project.getName()));
            }
        });
    }
    
    
}
