package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.BaseControllerTest;
import com.ksteindl.chemstore.domain.entities.Chemical;
import com.ksteindl.chemstore.domain.entities.ChemicalIngredient;
import com.ksteindl.chemstore.domain.entities.Recipe;
import com.ksteindl.chemstore.domain.entities.RecipeIngredient;
import com.ksteindl.chemstore.domain.input.IngredientInput;
import com.ksteindl.chemstore.domain.input.RecipeInput;
import com.ksteindl.chemstore.domain.repositories.ChemicalRepository;
import com.ksteindl.chemstore.domain.repositories.RecipeRepository;
import com.ksteindl.chemstore.exceptions.ForbiddenException;
import com.ksteindl.chemstore.exceptions.ResourceNotFoundException;
import com.ksteindl.chemstore.exceptions.ValidationException;
import com.ksteindl.chemstore.utils.AccountManagerTestUtils;
import com.ksteindl.chemstore.utils.LabAdminTestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class RecipeServiceTest extends BaseControllerTest{

    private static final Logger logger = LogManager.getLogger(RecipeServiceTest.class);
    
    private Principal alphaManager = AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL;
    private Principal alphaLabUser = AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL;
    
    private String alphaLabKey = AccountManagerTestUtils.ALPHA_LAB_KEY;
    
    @Autowired
    private RecipeService recipeService;
    @Autowired
    private ChemicalRepository chemicalRepository;
    @Autowired
    private ChemicalService chemicalService;
    

    // CREATE
    @Test
    @Rollback
    @Transactional
    public void testCreateRecipe_whenAllValid_gotNoException() {
        RecipeInput input = getDegrAForLisoInput();
        recipeService.createRecipe(input, alphaManager);
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateRecipe_whenAllValid_savedRecipeIsExpected() {
        RecipeInput input = getDegrAForLisoInput();
        Recipe returned = recipeService.createRecipe(input, alphaManager);
        Recipe fetched = recipeService.findById(returned.getId());
        Assertions.assertEquals(input.getName(), fetched.getName());
        Assertions.assertEquals(input.getAmount(), fetched.getAmount());
        Assertions.assertEquals(input.getShelfLifeInDays(), fetched.getShelfLifeInDays());
        Assertions.assertEquals(input.getUnit(), fetched.getUnit());
        Assertions.assertEquals(input.getProjectId(), fetched.getProject().getId());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateRecipe_whenAllValid_chemicalIngredientsAreExpected() {
        RecipeInput input = getDegrAForLisoInput();
        Recipe returned = recipeService.createRecipe(input, alphaManager);
        Recipe fetched = recipeService.findById(returned.getId());
        List<ChemicalIngredient> chemIngredients = fetched.getChemicalIngredients();
        IngredientInput acnInput = input.getIngredients().get(0);
        
        Assertions.assertEquals(2, chemIngredients.size());
        ChemicalIngredient acnIngredient = fetched.getChemicalIngredients().stream()
                .filter(ingredient -> ingredient.getIngredient().equals(alphaAcn))
                .findAny().get();
        ChemicalIngredient meOhIngredient = fetched.getChemicalIngredients().stream()
                .filter(ingredient -> ingredient.getIngredient().equals(alphaMeOH))
                .findAny().get();
        Assertions.assertEquals(acnInput.getUnit(), acnIngredient.getUnit());
        Assertions.assertEquals(acnInput.getAmount(), acnIngredient.getAmount());
        Assertions.assertEquals(alphaAcn, acnIngredient.getIngredient());
        Assertions.assertEquals(alphaMeOH, meOhIngredient.getIngredient());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateRecipe_whenAllValid_recipeIngredientIsExpected() {
        RecipeInput input = getDegrAForLisoInput();
        Recipe returned = recipeService.createRecipe(input, alphaManager);
        Recipe fetched = recipeService.findById(returned.getId());
        List<RecipeIngredient> recipeIngredients = fetched.getRecipeIngredients();
        IngredientInput bufferInput = input.getIngredients().get(2);
        RecipeIngredient bufferIngredient = fetched.getRecipeIngredients().get(0);

        Assertions.assertEquals(1, recipeIngredients.size());
        Assertions.assertEquals(bufferInput.getUnit(), bufferIngredient.getUnit());
        Assertions.assertEquals(bufferInput.getAmount(), bufferIngredient.getAmount());
        Assertions.assertEquals(alphaLisoBuffer, bufferIngredient.getIngredient());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateRecipe_whenAlabAdmin_gotForbiddenException() {
        RecipeInput input = getDegrAForLisoInput();
        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            recipeService.createRecipe(input, AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateRecipe_whenAlabUser_gotForbiddenException() {
        RecipeInput input = getDegrAForLisoInput();
        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            recipeService.createRecipe(input, AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateRecipe_whenBlabManager_gotForbiddenException() {
        RecipeInput input = getDegrAForLisoInput();
        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            recipeService.createRecipe(input, AccountManagerTestUtils.BETA_LAB_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateRecipe_whenAccountManager_gotForbiddenException() {
        RecipeInput input = getDegrAForLisoInput();
        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            recipeService.createRecipe(input, AccountManagerTestUtils.ACCOUNT_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateRecipe_whenEmptyInput_gotException() {
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            recipeService.createRecipe(new RecipeInput(), alphaManager);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateRecipe_whenRecipeNameAlreadyExist_gotValidationException() {
        RecipeInput input = getDegrAForLisoInput();
        input.setName(LabAdminTestUtils.BUFFER_NAME);
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            recipeService.createRecipe(input, alphaManager);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateRecipe_whenProjectIdDoesNotExist_gotResourceNotFoundException() {
        RecipeInput input = getDegrAForLisoInput();
        input.setProjectId((long)Integer.MAX_VALUE);
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            recipeService.createRecipe(input, alphaManager);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateRecipe_whenProjectDeleted_gotResourceNotFoundException() {
        RecipeInput input = getDegrAForLisoInput();
        input.setProjectId(alphaDeletedProject.getId());
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            recipeService.createRecipe(input, alphaManager);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateRecipe_whenUnitDoesNotMatch_gotValidationException() {
        RecipeInput input = getDegrAForLisoInput();
        input.setUnit("dummy unit");
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            recipeService.createRecipe(input, alphaManager);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateRecipe_whenEmptyIngredientInputs_gotValidationException() {
        RecipeInput input = getDegrAForLisoInput();
        input.setIngredients(new ArrayList<>());
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            recipeService.createRecipe(input, alphaManager);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateRecipe_whenIngredientHasUnknowntType_gotValidationException() {
        RecipeInput input = getDegrAForLisoInput();
        input.getIngredients().get(0).setType("UNKNOWN");
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            recipeService.createRecipe(input, alphaManager);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateRecipe_whenChemicalIngredientIdDoesNotExist_gotResourceNotFoundException() {
        RecipeInput input = getDegrAForLisoInput();
        input.getIngredients().get(0).setIngredientId((long) Integer.MAX_VALUE);
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            recipeService.createRecipe(input, alphaManager);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateRecipe_whenChemicalIngredientDeleted_gotResourceNotFoundException() {
        RecipeInput input = getDegrAForLisoInput();
        Chemical deletedChem = null;
        for (Chemical chemical : chemicalRepository.findAll()) {
            if (chemical.getLab().equals(alphaLab) && chemical.getDeleted()) {
                deletedChem = chemical;
            }
        };
        input.getIngredients().get(0).setIngredientId(deletedChem.getId());
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            recipeService.createRecipe(input, alphaManager);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateRecipe_whenChemicalIngredientIsForOtherLab_gotValidationException() {
        RecipeInput input = getDegrAForLisoInput();
        Chemical betaChem = chemicalService.
                getChemicalsForUser(AccountManagerTestUtils.BETA_LAB_KEY, AccountManagerTestUtils.BETA_LAB_MANAGER_PRINCIPAL).get(0);
        input.getIngredients().get(0).setIngredientId(betaChem.getId());
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            recipeService.createRecipe(input, alphaManager);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateRecipe_whenUnitIsInvalidInIngredient_gotValidationException() {
        RecipeInput input = getDegrAForLisoInput();
        input.getIngredients().get(0).setUnit("dummy unit");
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            recipeService.createRecipe(input, alphaManager);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }
    
    

    @Test
    @Rollback
    @Transactional
    public void testCreateRecipe_whenRecipeIdDoesNotExist_gotResourceNotFoundException() {
        RecipeInput input = getDegrAForLisoInput();
        input.getIngredients().get(2).setIngredientId((long) Integer.MAX_VALUE);
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            recipeService.createRecipe(input, alphaManager);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateRecipe_whenRecipeIngredientIsDeleted_gotResourceNotFoundException(@Autowired RecipeRepository recipeRepository) {
        RecipeInput input = getDegrAForLisoInput();
        Recipe deletedRecipe = null;
        for (Recipe recipe : recipeRepository.findAll()) {
            if (recipe.getLab().equals(alphaLab) && recipe.getDeleted()) {
                deletedRecipe = recipe;
            }
        };
        input.getIngredients().get(2).setIngredientId(deletedRecipe.getId());
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            recipeService.createRecipe(input, alphaManager);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateRecipe_whenRecipeIngredientIsForOtherLab_gotValidationException(@Autowired RecipeRepository recipeRepository) {
        RecipeInput input = getDegrAForLisoInput();
        Recipe betaRecipe = null;
        for (Recipe recipe : recipeRepository.findAll()) {
            if (!recipe.getLab().equals(alphaLab) &&!recipe.getDeleted()) {
                betaRecipe = recipe;
            }
        }
        input.getIngredients().get(0).setIngredientId(betaRecipe.getId());
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            recipeService.createRecipe(input, alphaManager);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }
    
    //UPDATE
    
    @Test
    @Rollback
    @Transactional
    public void testUpdateRecipe_whenAllValid_gotNoException() {
        RecipeInput input = getUpdatedContentEluentBLisoInput();
        Recipe eluB = recipeService.getRecipes(alphaLisoProject.getId(), alphaManager, true).stream()
                .filter(recipe -> recipe.getName().equals(LabAdminTestUtils.CONTENT_ELUENT_B_NAME))
                .findAny().get();
        recipeService.updateRecipe(input, eluB.getId(), alphaManager);
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateRecipe_whenAllValid_savedRecipeIsExpected() {
        RecipeInput input = getUpdatedContentEluentBLisoInput();
        Recipe eluB = recipeService.getRecipes(alphaLisoProject.getId(), alphaManager, true).stream()
                .filter(recipe -> recipe.getName().equals(LabAdminTestUtils.CONTENT_ELUENT_B_NAME))
                .findAny().get();
        Recipe fetched = recipeService.updateRecipe(input, eluB.getId(), alphaManager);
        Assertions.assertEquals(input.getName(), fetched.getName());
        Assertions.assertEquals(input.getAmount(), fetched.getAmount());
        Assertions.assertEquals(input.getShelfLifeInDays(), fetched.getShelfLifeInDays());
        Assertions.assertEquals(input.getUnit(), fetched.getUnit());
        Assertions.assertEquals(input.getProjectId(), fetched.getProject().getId());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateRecipe_whenAllValid_chemicalIngredientsAreExpected() {
        RecipeInput input = getUpdatedContentEluentBLisoInput();
        Recipe eluB = recipeService.getRecipes(alphaLisoProject.getId(), alphaManager, true).stream()
                .filter(recipe -> recipe.getName().equals(LabAdminTestUtils.CONTENT_ELUENT_B_NAME))
                .findAny().get();
        Recipe fetched = recipeService.updateRecipe(input, eluB.getId(), alphaManager);
        
        List<ChemicalIngredient> chemIngredients = fetched.getChemicalIngredients();
        IngredientInput acnInput = input.getIngredients().get(0);
        IngredientInput meOHInput = input.getIngredients().get(1);

        Assertions.assertEquals(2, chemIngredients.size());
        ChemicalIngredient acnIngredient = fetched.getChemicalIngredients().stream()
                .filter(ingredient -> ingredient.getIngredient().equals(alphaAcn))
                .findAny().get();
        ChemicalIngredient meOhIngredient = fetched.getChemicalIngredients().stream()
                .filter(ingredient -> ingredient.getIngredient().equals(alphaMeOH))
                .findAny().get();
        Assertions.assertEquals(acnInput.getUnit(), acnIngredient.getUnit());
        Assertions.assertEquals(acnInput.getAmount(), acnIngredient.getAmount());
        Assertions.assertEquals(meOHInput.getUnit(), meOhIngredient.getUnit());
        Assertions.assertEquals(meOHInput.getAmount(), meOhIngredient.getAmount());
        Assertions.assertEquals(alphaAcn, acnIngredient.getIngredient());
        Assertions.assertEquals(alphaMeOH, meOhIngredient.getIngredient());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateRecipe_whenAllValid_chemicalIngredientIdDoesNotChange() {
        RecipeInput input = getUpdatedContentEluentBLisoInput();
        Recipe eluB = recipeService.getRecipes(alphaLisoProject.getId(), alphaManager, true).stream()
                .filter(recipe -> recipe.getName().equals(LabAdminTestUtils.CONTENT_ELUENT_B_NAME))
                .findAny().get();
        Long oldMeOHIngId = eluB.getChemicalIngredients().stream().filter(ingr -> ingr.getIngredient().equals(alphaMeOH)).findAny().get().getId();
        
        Recipe fetched = recipeService.updateRecipe(input, eluB.getId(), alphaManager);

        Long newMeOHIngId = fetched.getChemicalIngredients().stream().filter(ingr -> ingr.getIngredient().equals(alphaMeOH)).findAny().get().getId();

        Assertions.assertEquals(oldMeOHIngId, newMeOHIngId);
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateRecipe_whenAllValid_recipeIngredientsAreExpected() {
        RecipeInput input = getUpdatedContentEluentBLisoInput();
        Recipe eluB = recipeService.getRecipes(alphaLisoProject.getId(), alphaManager, true).stream()
                .filter(recipe -> recipe.getName().equals(LabAdminTestUtils.CONTENT_ELUENT_B_NAME))
                .findAny().get();
        Recipe fetched = recipeService.updateRecipe(input, eluB.getId(), alphaManager);

        List<RecipeIngredient> recipeIngredients = fetched.getRecipeIngredients();
        
        Assertions.assertEquals(1, recipeIngredients.size());
        RecipeIngredient contAEluIngredient = recipeIngredients.get(0);

        IngredientInput recipeInput = input.getIngredients().get(2);
        Assertions.assertEquals(recipeInput.getUnit(), contAEluIngredient.getUnit());
        Assertions.assertEquals(recipeInput.getAmount(), contAEluIngredient.getAmount());
        Assertions.assertEquals(alphaLisoContAElu, contAEluIngredient.getIngredient());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateRecipe_whenAllValid_recipeIngredientIdDoesNotChange() {
        RecipeInput input = getUpdatedContentEluentBLisoInput();
        input.getIngredients().get(2).setIngredientId(alphaLisoBuffer.getId());
        Recipe eluB = recipeService.getRecipes(alphaLisoProject.getId(), alphaManager, true).stream()
                .filter(recipe -> recipe.getName().equals(LabAdminTestUtils.CONTENT_ELUENT_B_NAME))
                .findAny().get();
        Long oldBufferIngId = eluB.getRecipeIngredients().get(0).getId();

        Recipe fetched = recipeService.updateRecipe(input, eluB.getId(), alphaManager);

        Long newBufferIngId = fetched.getRecipeIngredients().get(0).getId();

        Assertions.assertEquals(oldBufferIngId, newBufferIngId);
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateRecipe_whenAlabAdmin_gotForbiddenException() {
        RecipeInput input = getUpdatedContentEluentBLisoInput();
        Recipe eluB = recipeService.getRecipes(alphaLisoProject.getId(), alphaManager, true).stream()
                .filter(recipe -> recipe.getName().equals(LabAdminTestUtils.CONTENT_ELUENT_B_NAME))
                .findAny().get();
        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            recipeService.updateRecipe(input, eluB.getId(), AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateRecipe_whenAlabUser_gotForbiddenException() {
        RecipeInput input = getUpdatedContentEluentBLisoInput();
        Recipe eluB = recipeService.getRecipes(alphaLisoProject.getId(), alphaManager, true).stream()
                .filter(recipe -> recipe.getName().equals(LabAdminTestUtils.CONTENT_ELUENT_B_NAME))
                .findAny().get();
        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            recipeService.updateRecipe(input, eluB.getId(), AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateRecipe_whenBlabManager_gotForbiddenException() {
        RecipeInput input = getUpdatedContentEluentBLisoInput();
        Recipe eluB = recipeService.getRecipes(alphaLisoProject.getId(), alphaManager, true).stream()
                .filter(recipe -> recipe.getName().equals(LabAdminTestUtils.CONTENT_ELUENT_B_NAME))
                .findAny().get();
        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            recipeService.updateRecipe(input, eluB.getId(), AccountManagerTestUtils.BETA_LAB_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateRecipe_whenAccountManager_gotForbiddenException() {
        RecipeInput input = getUpdatedContentEluentBLisoInput();
        Recipe eluB = recipeService.getRecipes(alphaLisoProject.getId(), alphaManager, true).stream()
                .filter(recipe -> recipe.getName().equals(LabAdminTestUtils.CONTENT_ELUENT_B_NAME))
                .findAny().get();
        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            recipeService.updateRecipe(input, eluB.getId(), AccountManagerTestUtils.ACCOUNT_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateRecipe_whenEmptyInput_gotException() {
        Recipe eluB = recipeService.getRecipes(alphaLisoProject.getId(), alphaManager, true).stream()
                .filter(recipe -> recipe.getName().equals(LabAdminTestUtils.CONTENT_ELUENT_B_NAME))
                .findAny().get();
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            recipeService.updateRecipe(new RecipeInput(), eluB.getId(), alphaManager);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateRecipe_whenRecipeNameAlreadyExist_gotValidationException() {
        RecipeInput input = getUpdatedContentEluentBLisoInput();
        input.setName(LabAdminTestUtils.BUFFER_NAME);
        Recipe eluB = recipeService.getRecipes(alphaLisoProject.getId(), alphaManager, true).stream()
                .filter(recipe -> recipe.getName().equals(LabAdminTestUtils.CONTENT_ELUENT_B_NAME))
                .findAny().get();
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            recipeService.updateRecipe(input, eluB.getId(), alphaManager);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateRecipe_whenProjectIdDiffers_gotValidationException() {
        RecipeInput input = getUpdatedContentEluentBLisoInput();
        input.setProjectId(betaLisoProject.getId());
        Recipe eluB = recipeService.getRecipes(alphaLisoProject.getId(), alphaManager, true).stream()
                .filter(recipe -> recipe.getName().equals(LabAdminTestUtils.CONTENT_ELUENT_B_NAME))
                .findAny().get();
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            recipeService.updateRecipe(input, eluB.getId(), alphaManager);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateRecipe_whenUnitDoesNotMatch_gotValidationException() {
        RecipeInput input = getUpdatedContentEluentBLisoInput();
        input.setUnit("dummy unit");
        Recipe eluB = recipeService.getRecipes(alphaLisoProject.getId(), alphaManager, true).stream()
                .filter(recipe -> recipe.getName().equals(LabAdminTestUtils.CONTENT_ELUENT_B_NAME))
                .findAny().get();
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            recipeService.updateRecipe(input, eluB.getId(), alphaManager);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateRecipe_whenEmptyIngredientInputs_gotValidationException() {
        RecipeInput input = getUpdatedContentEluentBLisoInput();
        input.setIngredients(new ArrayList<>());
        Recipe eluB = recipeService.getRecipes(alphaLisoProject.getId(), alphaManager, true).stream()
                .filter(recipe -> recipe.getName().equals(LabAdminTestUtils.CONTENT_ELUENT_B_NAME))
                .findAny().get();
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            recipeService.updateRecipe(input, eluB.getId(), alphaManager);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateRecipe_whenIngredientHasUnknownType_gotValidationException() {
        RecipeInput input = getUpdatedContentEluentBLisoInput();
        input.getIngredients().get(0).setType("DUMMY");
        Recipe eluB = recipeService.getRecipes(alphaLisoProject.getId(), alphaManager, true).stream()
                .filter(recipe -> recipe.getName().equals(LabAdminTestUtils.CONTENT_ELUENT_B_NAME))
                .findAny().get();
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            recipeService.updateRecipe(input, eluB.getId(), alphaManager);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateRecipe_whenUnitIsInvalidInIngredient_gotValidationException() {
        RecipeInput input = getUpdatedContentEluentBLisoInput();
        input.getIngredients().get(0).setUnit("dummy unit");
        Recipe eluB = recipeService.getRecipes(alphaLisoProject.getId(), alphaManager, true).stream()
                .filter(recipe -> recipe.getName().equals(LabAdminTestUtils.CONTENT_ELUENT_B_NAME))
                .findAny().get();
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            recipeService.updateRecipe(input, eluB.getId(), alphaManager);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateRecipe_whenChemicalIngredientIdDoesNotExist_gotResourceNotFoundException() {
        RecipeInput input = getUpdatedContentEluentBLisoInput();
        input.getIngredients().get(0).setIngredientId((long) Integer.MAX_VALUE);
        Recipe eluB = recipeService.getRecipes(alphaLisoProject.getId(), alphaManager, true).stream()
                .filter(recipe -> recipe.getName().equals(LabAdminTestUtils.CONTENT_ELUENT_B_NAME))
                .findAny().get();
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            recipeService.updateRecipe(input, eluB.getId(), alphaManager);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }


    @Test
    @Rollback
    @Transactional
    public void testUpdateRecipe_whenChemicalIngredientDeleted_gotResourceNotFoundException() {
        RecipeInput input = getUpdatedContentEluentBLisoInput();
        Chemical deletedChem = null;
        for (Chemical chemical : chemicalRepository.findAll()) {
            if (chemical.getLab().equals(alphaLab) && chemical.getDeleted()) {
                deletedChem = chemical;
            }
        };
        input.getIngredients().get(0).setIngredientId(deletedChem.getId());
        Recipe eluB = recipeService.getRecipes(alphaLisoProject.getId(), alphaManager, true).stream()
                .filter(recipe -> recipe.getName().equals(LabAdminTestUtils.CONTENT_ELUENT_B_NAME))
                .findAny().get();
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            recipeService.updateRecipe(input, eluB.getId(), alphaManager);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateRecipe_whenChemicalIngredientIsForOtherLab_gotValidationException() {
        RecipeInput input = getUpdatedContentEluentBLisoInput();
        Chemical betaChem = chemicalService.
                getChemicalsForUser(AccountManagerTestUtils.BETA_LAB_KEY, AccountManagerTestUtils.BETA_LAB_MANAGER_PRINCIPAL).get(0);
        input.getIngredients().get(0).setIngredientId(betaChem.getId());
        Recipe eluB = recipeService.getRecipes(alphaLisoProject.getId(), alphaManager, true).stream()
                .filter(recipe -> recipe.getName().equals(LabAdminTestUtils.CONTENT_ELUENT_B_NAME))
                .findAny().get();
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            recipeService.updateRecipe(input, eluB.getId(), alphaManager);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateRecipe_whenRecipeIngredientIdDoesNotExist_gotResourceNotFoundException() {
        RecipeInput input = getUpdatedContentEluentBLisoInput();
        input.getIngredients().get(2).setIngredientId((long) Integer.MAX_VALUE);
        Recipe eluB = recipeService.getRecipes(alphaLisoProject.getId(), alphaManager, true).stream()
                .filter(recipe -> recipe.getName().equals(LabAdminTestUtils.CONTENT_ELUENT_B_NAME))
                .findAny().get();
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            recipeService.updateRecipe(input, eluB.getId(), alphaManager);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateRecipe_whenRecipeIngredientIsDeletedt_gotResourceNotFoundException(@Autowired RecipeRepository recipeRepository) {
        RecipeInput input = getUpdatedContentEluentBLisoInput();
        Recipe deletedRecipe = null;
        for (Recipe recipe : recipeRepository.findAll()) {
            if (recipe.getLab().equals(alphaLab) && recipe.getDeleted()) {
                deletedRecipe = recipe;
            }
        };
        input.getIngredients().get(2).setIngredientId(deletedRecipe.getId());
        Recipe eluB = recipeService.getRecipes(alphaLisoProject.getId(), alphaManager, true).stream()
                .filter(recipe -> recipe.getName().equals(LabAdminTestUtils.CONTENT_ELUENT_B_NAME))
                .findAny().get();
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            recipeService.updateRecipe(input, eluB.getId(), alphaManager);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateRecipe__whenRecipeIngredientIsForOtherLab_gotValidationException(@Autowired RecipeRepository recipeRepository) {
        RecipeInput input = getUpdatedContentEluentBLisoInput();
        Recipe betaRecipe = null;
        for (Recipe recipe : recipeRepository.findAll()) {
            if (!recipe.getLab().equals(alphaLab) &&!recipe.getDeleted()) {
                betaRecipe = recipe;
            }
        }
        input.getIngredients().get(2).setIngredientId(betaRecipe.getId());
        Recipe eluB = recipeService.getRecipes(alphaLisoProject.getId(), alphaManager, true).stream()
                .filter(recipe -> recipe.getName().equals(LabAdminTestUtils.CONTENT_ELUENT_B_NAME))
                .findAny().get();
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            recipeService.updateRecipe(input, eluB.getId(), alphaManager);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }
    
    
    private RecipeInput getDegrAForLisoInput() {
        RecipeInput input = LabAdminTestUtils.getDegrAForLisoInput();
        input.setProjectId(alphaLisoProject.getId());
        List<IngredientInput> ingredientInputs = input.getIngredients();
        ingredientInputs.get(0).setIngredientId(alphaAcn.getId());
        ingredientInputs.get(1).setIngredientId(alphaMeOH.getId());
        ingredientInputs.get(2).setIngredientId(alphaLisoBuffer.getId());
        return input;
    }

    private RecipeInput getContentEluentBLisoInput() {
        RecipeInput input = LabAdminTestUtils.getContentEluentBLisoInput();
        input.setProjectId(alphaLisoProject.getId());
        List<IngredientInput> ingredientInputs = input.getIngredients();
        ingredientInputs.get(0).setIngredientId(alphaAcn.getId());
        ingredientInputs.get(1).setIngredientId(alphaEtOH.getId());
        ingredientInputs.get(2).setIngredientId(alphaLisoBuffer.getId());
        return input;
    }

    private RecipeInput getUpdatedContentEluentBLisoInput() {
        RecipeInput input = LabAdminTestUtils.getContentEluentBLisoInput();
        input.setProjectId(alphaLisoProject.getId());
        List<IngredientInput> ingredientInputs = input.getIngredients();
        ingredientInputs.get(0).setIngredientId(alphaAcn.getId());
        ingredientInputs.get(1).setIngredientId(alphaMeOH.getId());
        ingredientInputs.get(2).setIngredientId(alphaLisoContAElu.getId());
        return input;
    }

    

}
