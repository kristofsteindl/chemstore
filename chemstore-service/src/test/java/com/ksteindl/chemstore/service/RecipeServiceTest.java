package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.BaseControllerTest;
import com.ksteindl.chemstore.domain.entities.ChemicalIngredient;
import com.ksteindl.chemstore.domain.entities.Recipe;
import com.ksteindl.chemstore.domain.entities.RecipeIngredient;
import com.ksteindl.chemstore.domain.input.IngredientInput;
import com.ksteindl.chemstore.domain.input.RecipeInput;
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
                .filter(ingredient -> ingredient.getIngredient().equals(alphaMeOh))
                .findAny().get();
        Assertions.assertEquals(acnInput.getUnit(), acnIngredient.getUnit());
        Assertions.assertEquals(acnInput.getAmount(), acnIngredient.getAmount());
        Assertions.assertEquals(alphaAcn, acnIngredient.getIngredient());
        Assertions.assertEquals(alphaMeOh, meOhIngredient.getIngredient());
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
    
    private RecipeInput getDegrAForLisoInput() {
        RecipeInput input = LabAdminTestUtils.getDegrAForLisoInput();
        input.setProjectId(alphaLisoProject.getId());
        List<IngredientInput> ingredientInputs = input.getIngredients();
        ingredientInputs.get(0).setIngredientId(alphaAcn.getId());
        ingredientInputs.get(1).setIngredientId(alphaMeOh.getId());
        ingredientInputs.get(2).setIngredientId(alphaLisoBuffer.getId());
        return input;
    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testCreateProject_whenAllValid_savedValuesAsExpected() {
//        ProjectInput input = LabAdminTestUtils.getRosuForAlphaInput();
//        Project returned = projectService.createProject(input, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
//        Project fetched = projectRepository.findById(returned.getId()).get();
//        Assertions.assertEquals(input.getName(), fetched.getName());
//        Assertions.assertEquals(input.getLabKey(), fetched.getLab().getKey());
//        Assertions.assertFalse(fetched.getDeleted());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testCreateProject_whenAlabAdmin_gotForbiddenException() {
//        ProjectInput input = LabAdminTestUtils.getRosuForAlphaInput();
//        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
//            projectService.createProject(input, AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testCreateProject_whenBlabManager_gotForbiddenException() {
//        ProjectInput input = LabAdminTestUtils.getRosuForAlphaInput();
//        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
//            projectService.createProject(input, AccountManagerTestUtils.BETA_LAB_MANAGER_PRINCIPAL);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testCreateProject_whenEmptyInput_gotException() {
//        ProjectInput input = new ProjectInput();
//        Exception exception = Assertions.assertThrows(Exception.class, () -> {
//            projectService.createProject(input, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testCreateProject_whenShortNameAlreadyExists_gotValidationExteption() {
//        ProjectInput input = LabAdminTestUtils.getRosuForAlphaInput();
//        input.setName(LabAdminTestUtils.AMLO_NAME);
//        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
//            projectService.createProject(input, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testCreateProject_whenLabKeyDoesNotExist_gotResourceNotFoundException() {
//        ProjectInput input = LabAdminTestUtils.getRosuForAlphaInput();
//        input.setLabKey("not-existing");
//        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
//            projectService.createProject(input, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testCreateProject_whenLabIsDeleted_gotResourceNotFoundException() {
//        ProjectInput input = LabAdminTestUtils.getRosuForAlphaInput();
//        input.setLabKey(AccountManagerTestUtils.DELTA_LAB_KEY);
//        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
//            projectService.createProject(input, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//    
//
//
//    // UPDATE
//    @Test
//    @Rollback
//    @Transactional
//    public void testUpdateProject_whenAllValid_gotNoException() {
//        Principal manager = AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL;
//        Project project = projectService.getProjects(AccountManagerTestUtils.ALPHA_LAB_KEY, manager).stream()
//                .filter(gotProject -> gotProject.getName().equals(LabAdminTestUtils.AMLO_NAME))
//                .findAny()
//                .get();
//        
//        ProjectInput input = LabAdminTestUtils.getAmloForAlphaInput();
//        input.setName("Updated Amlo");
//        projectService.updateProject(input, project.getId(), manager);
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testUpdateProject_whenAllValid_savedValuesAsExpected() {
//        Principal manager = AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL;
//        Project project = projectService.getProjects(AccountManagerTestUtils.ALPHA_LAB_KEY, manager).stream()
//                .filter(gotProject -> gotProject.getName().equals(LabAdminTestUtils.AMLO_NAME))
//                .findAny()
//                .get();
//
//        ProjectInput input = LabAdminTestUtils.getAmloForAlphaInput();
//        input.setName("Updated Amlo");
//        Project returned = projectService.updateProject(input, project.getId(), manager);
//        
//        Project fetched = projectRepository.findById(returned.getId()).get();
//        Assertions.assertEquals(input.getName(), fetched.getName());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testUpdateProject_whenAlabAdmin_gotForbiddenException() {
//        Project project = projectService.getProjects(AccountManagerTestUtils.ALPHA_LAB_KEY, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL).stream()
//                .filter(gotProject -> gotProject.getName().equals(LabAdminTestUtils.AMLO_NAME))
//                .findAny()
//                .get();
//
//        ProjectInput input = LabAdminTestUtils.getAmloForAlphaInput();
//        input.setName("Updated Amlo");
//        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
//            projectService.updateProject(input, project.getId(), AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testUpdateProject_whenBlabManager_gotForbiddenException() {
//        Project project = projectService.getProjects(AccountManagerTestUtils.ALPHA_LAB_KEY, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL).stream()
//                .filter(gotProject -> gotProject.getName().equals(LabAdminTestUtils.AMLO_NAME))
//                .findAny()
//                .get();
//
//        ProjectInput input = LabAdminTestUtils.getAmloForAlphaInput();
//        input.setName("Updated Amlo");
//        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
//            projectService.updateProject(input, project.getId(), AccountManagerTestUtils.BETA_LAB_MANAGER_PRINCIPAL);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testUpdateProject_whenProjectNameAlreadyExists_gotValidationException() {
//        Principal manager = AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL;
//        Project project = projectService.getProjects(AccountManagerTestUtils.ALPHA_LAB_KEY, manager).stream()
//                .filter(gotProject -> gotProject.getName().equals(LabAdminTestUtils.AMLO_NAME))
//                .findAny()
//                .get();
//
//        ProjectInput input = LabAdminTestUtils.getAmloForAlphaInput();
//        input.setName("Lisinopril");
//        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
//            projectService.updateProject(input, project.getId(), manager);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testUpdateProject_whenLabKeyChanges_labStaysTheSame() {
//        Principal manager = AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL;
//        Project project = projectService.getProjects(AccountManagerTestUtils.ALPHA_LAB_KEY, manager).stream()
//                .filter(gotProject -> gotProject.getName().equals(LabAdminTestUtils.AMLO_NAME))
//                .findAny()
//                .get();
//
//        ProjectInput input = LabAdminTestUtils.getAmloForAlphaInput();
//        input.setLabKey(AccountManagerTestUtils.BETA_LAB_KEY);
//        Project updated = projectService.updateProject(input, project.getId(), manager);
//        
//        Assertions.assertFalse(updated.getLab().getKey().equals(AccountManagerTestUtils.BETA_LAB_KEY));
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testUpdateProject_whenProjectExistsWithSameNameWithAnotherLab_updated() {
//        Principal manager = AccountManagerTestUtils.ALPHA_BETA_LAB_MANAGER_PRINCIPAL;
//        Project project = projectService.getProjects(AccountManagerTestUtils.ALPHA_LAB_KEY, manager).stream()
//                .filter(gotProject -> gotProject.getName().equals(LabAdminTestUtils.AMLO_NAME))
//                .findAny()
//                .get();
//
//        ProjectInput input = LabAdminTestUtils.getAmloForAlphaInput();
//        String updatedName = "Indapamide";
//        input.setName(updatedName);
//        Project updated = projectService.updateProject(input, project.getId(), manager);
//
//        Assertions.assertEquals(updated.getName(), updatedName);
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testUpdateProject_whenIdDoesNotExist_gotResourceNotFoundException() {
//        Principal manager = AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL;
//        ProjectInput input = LabAdminTestUtils.getAmloForAlphaInput();
//        input.setName("Updated name");
//        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
//            projectService.updateProject(input, (long) Integer.MAX_VALUE, manager);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testUpdateProject_whenProjectAlreadyDeleted_gotResourceNotFoundException() {
//        Principal manager = AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL;
//        Project project = projectService.getProjects(AccountManagerTestUtils.ALPHA_LAB_KEY, manager, false).stream()
//                .filter(gotProject -> gotProject.getDeleted() && gotProject.getLab().getKey().equals(AccountManagerTestUtils.ALPHA_LAB_KEY))
//                .findAny()
//                .get();
//        ProjectInput input = LabAdminTestUtils.getAmloForAlphaInput();
//        input.setName("Updated name");
//        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
//            projectService.updateProject(input, project.getId(), manager);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    
//    // DELETE
//    @Test
//    @Rollback
//    @Transactional
//    public void testDeleteProject_whenAllValid_gotNoException() {
//        Principal manager = AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL;
//        Project project = projectService.getProjects(AccountManagerTestUtils.ALPHA_LAB_KEY, manager).stream()
//                .filter(gotProject ->
//                        gotProject.getLab().getKey().equals(AccountManagerTestUtils.ALPHA_LAB_KEY) &&
//                                gotProject.getName().equals(LabAdminTestUtils.AMLO_NAME))
//                .findAny()
//                .get();
//        projectService.deleteProject(project.getId(), manager);
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testDeleteProject_whenAllValid_gotFetchedDeleted() {
//        Principal manager = AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL;
//        Project project = projectService.getProjects(AccountManagerTestUtils.ALPHA_LAB_KEY, manager).stream()
//                .filter(gotProject ->
//                        gotProject.getLab().getKey().equals(AccountManagerTestUtils.ALPHA_LAB_KEY) &&
//                                gotProject.getName().equals(LabAdminTestUtils.AMLO_NAME))
//                .findAny()
//                .get();
//        projectService.deleteProject(project.getId(), manager);
//        Project deleted = projectRepository.findById(project.getId()).get();
//        Assertions.assertTrue(deleted.getDeleted());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testDeleteProject_whenAlphaLabAdmin_gotForbiddenException() {
//        Principal manager = AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL;
//        Project project = projectService.getProjects(AccountManagerTestUtils.ALPHA_LAB_KEY, manager).stream()
//                .filter(gotProject ->
//                        gotProject.getLab().getKey().equals(AccountManagerTestUtils.ALPHA_LAB_KEY) &&
//                                gotProject.getName().equals(LabAdminTestUtils.AMLO_NAME))
//                .findAny()
//                .get();
//        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
//            projectService.deleteProject(project.getId(), AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testDeleteProject_whenBetaLabManager_gotForbiddenEception() {
//        Principal manager = AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL;
//        Project project = projectService.getProjects(AccountManagerTestUtils.ALPHA_LAB_KEY, manager).stream()
//                .filter(gotProject ->
//                        gotProject.getLab().getKey().equals(AccountManagerTestUtils.ALPHA_LAB_KEY) &&
//                                gotProject.getName().equals(LabAdminTestUtils.AMLO_NAME))
//                .findAny()
//                .get();
//        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
//            projectService.deleteProject(project.getId(), AccountManagerTestUtils.BETA_LAB_MANAGER_PRINCIPAL);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testDeleteProject_whenIdDoesNotExist_gotResourceNotFoundException() {
//        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
//            projectService.deleteProject((long) Integer.MAX_VALUE, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testDeleteProject_whenAlreadyDeleted_gotResourceNotFoundException() {
//        Principal manager = AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL;
//        Project project = projectService.getProjects(AccountManagerTestUtils.ALPHA_LAB_KEY, manager, false).stream()
//                .filter(gotProject ->
//                        gotProject.getLab().getKey().equals(AccountManagerTestUtils.ALPHA_LAB_KEY) && 
//                        gotProject.getDeleted())
//                .findAny()
//                .get();
//        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
//            projectService.deleteProject(project.getId(), AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    // READ
//    @Test
//    @Rollback
//    @Transactional
//    public void testGetProjects_gotNoException() {
//        projectService.getProjects(alphaLabKey, alphaLabUser);
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testGetProjects_ReturnedListSizeIsGreaterThenOne() {
//        List<Project> projects = projectService.getProjects(alphaLabKey, alphaLabUser);
//        Assertions.assertTrue(projects.size() > 1);
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testGetProjects_eachProjectIsForAlpha() {
//        List<Project> projects = projectService.getProjects(alphaLabKey, alphaLabUser);
//        Assertions.assertFalse(projects.stream().anyMatch(project -> !project.getLab().getKey().equals(alphaLabKey)));
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testGetProjects_neitherProjectIsNotDeleted() {
//        List<Project> projects = projectService.getProjects(alphaLabKey, alphaLabUser);
//        Assertions.assertFalse(projects.stream().anyMatch(project -> project.getDeleted()));
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testGetProjects_whenLabKeyIsInvalid_gotResourceNotFoundException() {
//        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
//            projectService.getProjects("non-existing", alphaLabUser);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testGetProjects_whenBetaLabUser_gotForbiddenFoundException() {
//        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
//            projectService.getProjects(alphaLabKey, AccountManagerTestUtils.BETA_LAB_USER_PRINCIPAL);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testGetProjectsWithDeleted_gotNoException() {
//        projectService.getProjects(alphaLabKey, alphaLabUser, false);
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testGetProjectsWithDeleted_eachProjectIsForAlpha() {
//        List<Project> projects = projectService.getProjects(alphaLabKey, alphaLabUser, false);
//        Assertions.assertFalse(projects.stream().anyMatch(project -> !project.getLab().getKey().equals(alphaLabKey)));
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testGetProjectsWithDeleted_thereIsBothDeletedAndNotDeleted() {
//        List<Project> projects = projectService.getProjects(alphaLabKey, alphaLabUser, false);
//        Assertions.assertTrue(projects.stream().anyMatch(project -> project.getDeleted()));
//        Assertions.assertTrue(projects.stream().anyMatch(project -> !project.getDeleted()));
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testGetProjectsWithDeleted_whenLabKeyIsInvalid_gotResourceNotFoundException() {
//        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
//            projectService.getProjects("non-existing", alphaLabUser, false);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testGetProjectsWithDeleted_whenBetaLabUser_gotForbiddenFoundException() {
//        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
//            projectService.getProjects(alphaLabKey, AccountManagerTestUtils.BETA_LAB_USER_PRINCIPAL, false);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testGetProjectsOnlyAvailable_gotNoException() {
//        projectService.getProjects(alphaLabKey, alphaLabUser, true);
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testGetProjectsOnlyAvailable_ReturnedListSizeIsGreaterThenOne() {
//        List<Project> projects = projectService.getProjects(alphaLabKey, alphaLabUser, true);
//        Assertions.assertTrue(projects.size() > 1);
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testGetProjectsOnlyAvailable_eachProjectIsForAlpha() {
//        List<Project> projects = projectService.getProjects(alphaLabKey, alphaLabUser, true);
//        Assertions.assertFalse(projects.stream().anyMatch(project -> !project.getLab().getKey().equals(alphaLabKey)));
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testGetProjectsOnlyAvailable_neitherProjectIsNotDeleted() {
//        List<Project> projects = projectService.getProjects(alphaLabKey, alphaLabUser, true);
//        Assertions.assertFalse(projects.stream().anyMatch(project -> project.getDeleted()));
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testGetProjectsOnlyAvailable_whenLabKeyIsInvalid_gotResourceNotFoundException() {
//        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
//            projectService.getProjects("non-existing", alphaLabUser, true);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testGetProjects_whenBetaLabUserOnlyAvailable_gotForbiddenFoundException() {
//        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
//            projectService.getProjects(alphaLabKey, AccountManagerTestUtils.BETA_LAB_USER_PRINCIPAL, true);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindProjectById_gotNoException() {
//        Project target = projectService.getProjects(alphaLabKey, alphaLabUser).stream()
//                .filter(found -> found.getName().equals(LabAdminTestUtils.AMLO_NAME) && found.getLab().getKey().equals(alphaLabKey))
//                .findAny().get();
//        projectService.findById(target.getId());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindProjectById_gotExpectedProject() {
//        Project target = projectService.getProjects(alphaLabKey, alphaLabUser).stream()
//                .filter(found -> found.getName().equals(LabAdminTestUtils.AMLO_NAME) && found.getLab().getKey().equals(alphaLabKey))
//                .findAny().get();
//        Project project = projectService.findById(target.getId());
//        Assertions.assertEquals(LabAdminTestUtils.AMLO_NAME, project.getName());
//        Assertions.assertEquals(alphaLabKey, project.getLab().getKey());
//        Assertions.assertFalse(project.getDeleted());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindProjectById_whenIdDoesNotExist_gotResourceNotFoundException() {
//        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
//            projectService.findById((long) Integer.MAX_VALUE);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindProjectById_whenAlreadyDeleted_gotResourceNotFoundException() {
//        Project target = projectService.getProjects(alphaLabKey, alphaLabUser, false).stream()
//                .filter(found -> found.getDeleted())
//                .findAny().get();
//
//        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
//            projectService.findById(target.getId());
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindProjectByIdOnlyActive_gotNoException() {
//        Project target = projectService.getProjects(alphaLabKey, alphaLabUser).stream()
//                .filter(found -> found.getName().equals(LabAdminTestUtils.AMLO_NAME) && found.getLab().getKey().equals(alphaLabKey))
//                .findAny().get();
//        projectService.findById(target.getId(), true);
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindProjectByIdOnlyActive_gotExpectedProject() {
//        Project target = projectService.getProjects(alphaLabKey, alphaLabUser).stream()
//                .filter(found -> found.getName().equals(LabAdminTestUtils.AMLO_NAME) && found.getLab().getKey().equals(alphaLabKey))
//                .findAny().get();
//        Project project = projectService.findById(target.getId(), true);
//        Assertions.assertEquals(LabAdminTestUtils.AMLO_NAME, project.getName());
//        Assertions.assertEquals(alphaLabKey, project.getLab().getKey());
//        Assertions.assertFalse(project.getDeleted());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindProjectByIdOnlyActive_whenIdDoesNotExist_gotResourceNotFoundException() {
//        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
//            projectService.findById((long) Integer.MAX_VALUE, true);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindProjectByIdOnlyActive_whenAlreadyDeleted_gotResourceNotFoundException() {
//        Project target = projectService.getProjects(alphaLabKey, alphaLabUser, false).stream()
//                .filter(found -> found.getDeleted())
//                .findAny().get();
//
//        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
//            projectService.findById(target.getId(), true);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindProjectByIdOnlyActiveFalse_gotNoException() {
//        Project target = projectService.getProjects(alphaLabKey, alphaLabUser, false).stream()
//                .filter(found -> found.getDeleted())
//                .findAny().get();
//        projectService.findById(target.getId(), false);
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindProjectByIdOnlyActiveFalse_gotExpectedProject() {
//        Project target = projectService.getProjects(alphaLabKey, alphaLabUser, false).stream()
//                .filter(found -> found.getDeleted())
//                .findAny().get();
//        Project project = projectService.findById(target.getId(), false);
//        Assertions.assertEquals("Pantoprazole", project.getName());
//        Assertions.assertEquals(alphaLabKey, project.getLab().getKey());
//        Assertions.assertTrue(project.getDeleted());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindProjectByIdOnlyActiveFalse_whenIdDoesNotExist_gotResourceNotFoundException() {
//        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
//            projectService.findById((long) Integer.MAX_VALUE, false);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindProjectByIdOnlyActiveFalse_whenAlreadyDeleted_gotDeletedProject() {
//        Project target = projectService.getProjects(alphaLabKey, alphaLabUser, false).stream()
//                .filter(found -> found.getDeleted())
//                .findAny().get();
//
//        Project project = projectService.findById(target.getId(), false);
//        Assertions.assertTrue(project.getDeleted());
//    }

    

}
