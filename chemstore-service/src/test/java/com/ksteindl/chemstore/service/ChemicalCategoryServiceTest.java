package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.BaseControllerTest;
import com.ksteindl.chemstore.domain.entities.ChemType;
import com.ksteindl.chemstore.domain.entities.ChemicalCategory;
import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.domain.input.ChemicalCategoryInput;
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
import java.time.Duration;
import java.time.LocalDateTime;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ChemicalCategoryServiceTest extends BaseControllerTest{

    private static final Logger logger = LogManager.getLogger(ChemicalCategoryServiceTest.class);

    @Autowired
    private ChemicalCategoryService chemicalCategoryService;
    @Autowired
    private LabService labService;

    // CREATE
    // TODO seq number check
    @Test
    @Rollback
    @Transactional
    public void testCreateCategory_whenAllValid_gotNoException() {
        ChemicalCategoryInput input = LabAdminTestUtils.getSolidForAlphaInput();
        chemicalCategoryService.createCategory(input, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateCategory_whenAllValid_savedValuesAsExpected() {
        ChemicalCategoryInput input = LabAdminTestUtils.getSolidForAlphaInput();
        ChemicalCategory returned = chemicalCategoryService.createCategory(input, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
        ChemicalCategory fetched = chemicalCategoryService.findById(returned.getId());
        Assertions.assertEquals(input.getLabKey(), fetched.getLab().getKey());
        Assertions.assertFalse(fetched.getDeleted());
        Assertions.assertEquals(input.getName(), fetched.getName());
        Assertions.assertEquals(Duration.between(
                LocalDateTime.now(), LocalDateTime.now().plusWeeks(LabAdminTestUtils.SOLID_FOR_ALPHA_WEEKS)).toHours(), 
                fetched.getShelfLife().toHours());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateCategory_whenEmptyInput_gotException() {
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            ChemicalCategoryInput input = ChemicalCategoryInput.builder().build();
            chemicalCategoryService.createCategory(input, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
   }

    @Test
    @Rollback
    @Transactional
    public void testCreateCategory_whenNameAlreadyExists_gotValidationExteption() {
        ChemicalCategoryInput input = LabAdminTestUtils.getSolidForAlphaInput();
        input.setName(LabAdminTestUtils.ORGANIC_CATEGORY);
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            chemicalCategoryService.createCategory(input, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateCategory_whenLabKeyDoesNotExist_gotResourceNotFoundException() {
        ChemicalCategoryInput input = LabAdminTestUtils.getSolidForAlphaInput();
        input.setLabKey("non-existing-key");
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            chemicalCategoryService.createCategory(input, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateCategory_whenUserNeitherAdminNorManager_gotForbiddenException() {
        ChemicalCategoryInput input = LabAdminTestUtils.getSolidForAlphaInput();
        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            chemicalCategoryService.createCategory(input, AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateCategory_whenBetaLabAdmin_gotForbiddenException() {
        ChemicalCategoryInput input = LabAdminTestUtils.getSolidForAlphaInput();
        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            chemicalCategoryService.createCategory(input, AccountManagerTestUtils.BETA_LAB_ADMIN_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateCategory_whenDurationUnitIsNotValid_gotValidationException() {
        ChemicalCategoryInput input = LabAdminTestUtils.getSolidForAlphaInput();
        input.setUnit("not-valid-unit");
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            chemicalCategoryService.createCategory(input, AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateCategory_whenAllValid_gotNoException() {
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
        ChemicalCategory persisted = chemicalCategoryService.getByLab(input.getLabKey(), admin).stream()
                .filter(category -> category.getName().equals(input.getName()))
                .findAny().get();
        input.setName("Changed organic for alpha");
        chemicalCategoryService.updateCategory(input, persisted.getId(), admin);
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateCategory_whenAllValid_savedValuesAsExpected() {
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
        ChemicalCategory persisted = chemicalCategoryService.getByLab(input.getLabKey(), admin).stream()
                .filter(category -> category.getName().equals(input.getName()))
                .findAny().get();
        String newName = "Changed organic for alpha";
        String newAmount = "d";
        Integer newUnit = 1;
        input.setName(newName);
        input.setUnit(newAmount);
        input.setAmount(newUnit);
        chemicalCategoryService.updateCategory(input, persisted.getId(), admin);
        ChemicalCategory changed = chemicalCategoryService.getById(persisted.getId());
        Assertions.assertFalse(changed.getDeleted());
        Assertions.assertEquals(newName, changed.getName());
        Assertions.assertEquals(Duration.between(LocalDateTime.now(), LocalDateTime.now().plusDays(newUnit)).toHours(), changed.getShelfLife().toHours());    
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateCategory_whenEmptyInput_gotException() {
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
        ChemicalCategory persisted = chemicalCategoryService.getByLab(input.getLabKey(), admin).stream()
                .filter(category -> category.getName().equals(input.getName()))
                .findAny().get();
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            ChemicalCategoryInput emptyInput = ChemicalCategoryInput.builder().build();
            chemicalCategoryService.updateCategory(emptyInput, persisted.getId(), admin);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateCategory_whenNameAlreadyExists_gotValidationExteption() {
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
        ChemicalCategory persisted = chemicalCategoryService.getByLab(input.getLabKey(), admin).stream()
                .filter(category -> category.getName().equals(input.getName()))
                .findAny().get();
        input.setName(LabAdminTestUtils.BUFFER_CATEGORY);
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            chemicalCategoryService.updateCategory(input, persisted.getId(), admin);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateCategory_whenLabKeyDoesntExists_gotResourceNotFoundExteption() {
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
        ChemicalCategory persisted = chemicalCategoryService.getByLab(input.getLabKey(), admin).stream()
                .filter(category -> category.getName().equals(input.getName()))
                .findAny().get();
        input.setLabKey("non-existing-key");
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            chemicalCategoryService.updateCategory(input, persisted.getId(), admin);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateCategory_whenUserNeitherAdminNorManager_gotForbiddenException() {
        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
        ChemicalCategory persisted = chemicalCategoryService.getByLab(input.getLabKey(), AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL).stream()
                .filter(category -> category.getName().equals(input.getName()))
                .findAny().get();
        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            chemicalCategoryService.updateCategory(input, persisted.getId(), AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateCategory_whenUserBetaLabAdmin_gotForbiddenException() {
        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
        ChemicalCategory persisted = chemicalCategoryService.getByLab(input.getLabKey(), AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL).stream()
                .filter(category -> category.getName().equals(input.getName()))
                .findAny().get();
        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            chemicalCategoryService.updateCategory(input, persisted.getId(), AccountManagerTestUtils.BETA_LAB_ADMIN_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateCategory_whenDurationUnitIsNotValid_gotValidationException() {
        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
        ChemicalCategory persisted = chemicalCategoryService.getByLab(input.getLabKey(), admin).stream()
                .filter(category -> category.getName().equals(input.getName()))
                .findAny().get();
        input.setUnit("not-valid-unit");
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            chemicalCategoryService.updateCategory(input, persisted.getId(), admin);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }
    
    
    



//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindSHelfLifeById_whenIdValid_gotNoException() {
//        ChemType buffer = chemTypeService.findByName(LabAdminTestUtils.BUFFER_CATEGORY).get();
//        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
//        ShelfLife bufferForAlpha = chemicalCategoryService.findByLabAndName(alphaLab, buffer).get();
//        ShelfLife foundById = chemicalCategoryService.findById(bufferForAlpha.getId());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindSHelfLifeById_whenIdValid_gotWhatExpected() {
//        ChemType buffer = chemTypeService.findByName(LabAdminTestUtils.BUFFER_CATEGORY).get();
//        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
//        ShelfLife bufferForAlpha = chemicalCategoryService.findByLabAndName(alphaLab, buffer).get();
//        ShelfLife foundById = chemicalCategoryService.findById(bufferForAlpha.getId());
//        Assertions.assertEquals(bufferForAlpha.getLab(), foundById.getLab());
//        Assertions.assertEquals(bufferForAlpha.getChemType(), foundById.getChemType());
//        Assertions.assertEquals(bufferForAlpha.getDuration().toHours(), foundById.getDuration().toHours());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindSHelfLifeById_whenIdNotValid_gotResourceNotFoundException() {
//        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
//            chemicalCategoryService.findById((long)Integer.MAX_VALUE);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindShelfLifeById_whenDeleted_gotResourceNotFoundException() {
//        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
//            Long deletedId = chemicalCategoryService.getCategories(false).stream().filter(ShelfLife::getDeleted).findAny().get().getId();
//            chemicalCategoryService.findById(deletedId);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindSHelfLifeByIdActiveTrue_whenIdValid_gotNoException() {
//        ChemType buffer = chemTypeService.findByName(LabAdminTestUtils.BUFFER_CATEGORY).get();
//        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
//        ShelfLife bufferForAlpha = chemicalCategoryService.findByLabAndName(alphaLab, buffer).get();
//        ShelfLife foundById = chemicalCategoryService.findById(bufferForAlpha.getId(), true);
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindSHelfLifeById_whenIdValidActiveTrue_gotWhatExpected() {
//        ChemType buffer = chemTypeService.findByName(LabAdminTestUtils.BUFFER_CATEGORY).get();
//        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
//        ShelfLife bufferForAlpha = chemicalCategoryService.findByLabAndName(alphaLab, buffer).get();
//        ShelfLife foundById = chemicalCategoryService.findById(bufferForAlpha.getId(), true);
//        Assertions.assertEquals(bufferForAlpha.getLab(), foundById.getLab());
//        Assertions.assertEquals(bufferForAlpha.getChemType(), foundById.getChemType());
//        Assertions.assertEquals(bufferForAlpha.getDuration().toHours(), foundById.getDuration().toHours());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindSHelfLifeByIdActiveTrue_whenIdNotValid_gotResourceNotFoundException() {
//        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
//            chemicalCategoryService.findById((long)Integer.MAX_VALUE, true);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindShelfLifeByIdActiveTrue_whenDeleted_gotResourceNotFoundException() {
//        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
//            Long deletedId = chemicalCategoryService.getCategories(false).stream().filter(ShelfLife::getDeleted).findAny().get().getId();
//            chemicalCategoryService.findById(deletedId, true);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindSHelfLifeByIdActiveFalse_whenIdValid_gotNoException() {
//        ChemType buffer = chemTypeService.findByName(LabAdminTestUtils.BUFFER_CATEGORY).get();
//        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
//        ShelfLife bufferForAlpha = chemicalCategoryService.findByLabAndName(alphaLab, buffer).get();
//        ShelfLife foundById = chemicalCategoryService.findById(bufferForAlpha.getId(), false);
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindSHelfLifeById_whenIdValidActiveFalse_gotWhatExpected() {
//        ChemType buffer = chemTypeService.findByName(LabAdminTestUtils.BUFFER_CATEGORY).get();
//        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
//        ShelfLife bufferForAlpha = chemicalCategoryService.findByLabAndName(alphaLab, buffer).get();
//        ShelfLife foundById = chemicalCategoryService.findById(bufferForAlpha.getId(), false);
//        Assertions.assertEquals(bufferForAlpha.getLab(), foundById.getLab());
//        Assertions.assertEquals(bufferForAlpha.getChemType(), foundById.getChemType());
//        Assertions.assertEquals(bufferForAlpha.getDuration().toHours(), foundById.getDuration().toHours());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindSHelfLifeByIdActiveFalse_whenIdNotValid_gotResourceNotFoundException() {
//        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
//            chemicalCategoryService.findById((long)Integer.MAX_VALUE, false);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindShelfLifeByIdActiveFalse_whenDeleted_gotWhatExpected() {
//        ShelfLife deletedShelfLife = chemicalCategoryService.getCategories(false).stream().filter(ShelfLife::getDeleted).findAny().get();
//        ShelfLife found = chemicalCategoryService.findById(deletedShelfLife.getId(), false);
//        Assertions.assertEquals(deletedShelfLife.getLab(), found.getLab());
//        Assertions.assertEquals(deletedShelfLife.getChemType(), found.getChemType());
//        Assertions.assertEquals(deletedShelfLife.getDuration().toHours(), found.getDuration().toHours());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindShelfLifeByLabAndChemType_whenIdValid_gotNoException() {
//        ChemType buffer = chemTypeService.findByName(LabAdminTestUtils.BUFFER_CATEGORY).get();
//        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
//        chemicalCategoryService.findByLabAndName(alphaLab, buffer).get();
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindShelfLifeByLabAndChemType_whenIdValid_gotWhatExpected() {
//        ChemType buffer = chemTypeService.findByName(LabAdminTestUtils.BUFFER_CATEGORY).get();
//        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
//        ShelfLife bufferForAlpha = chemicalCategoryService.findByLabAndName(alphaLab, buffer).get();
//        ShelfLife foundById = shelfLifeRepositoy.findById(bufferForAlpha.getId()).get();
//        Assertions.assertEquals(foundById.getLab(), bufferForAlpha.getLab());
//        Assertions.assertEquals(foundById.getChemType(), bufferForAlpha.getChemType());
//        Assertions.assertEquals(foundById.getDuration().toHours(), bufferForAlpha.getDuration().toHours());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindShelfLifeByLabAndChemType_whenChemTypeNotValid_gotResourceNotFoundException() {
//        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
//        Assertions.assertTrue(chemicalCategoryService.findByLabAndName(alphaLab, getNonexistingChemType()).isEmpty());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindShelfLifeByLabAndChemType_whenLabNotValid_gotResourceNotFoundException() {
//        Exception exception = Assertions.assertThrows(Exception.class, () -> {
//            ChemType buffer = chemTypeService.findByName(LabAdminTestUtils.BUFFER_CATEGORY).get();
//            Assertions.assertTrue(chemicalCategoryService.findByLabAndName(getNonexistingLab(), buffer).isEmpty());
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindShelfLifeByLabAndChemType_whenLabAmdChemTypeNotExisting_gotResourceNotFoundException() {
//        Exception exception = Assertions.assertThrows(Exception.class, () -> {
//            Assertions.assertTrue(chemicalCategoryService.findByLabAndName(getNonexistingLab(), getNonexistingChemType()).isEmpty());
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindShelfLifeByLabAndChemType_whenNoHit_gotEmptyOptional() {
//        ChemType waterChemType = chemTypeService.findByName(LabAdminTestUtils.WATER_CATEGORY).get();
//        Lab betaLab = labService.findLabByKey(AccountManagerTestUtils.BETA_LAB_KEY);
//        Assertions.assertTrue(chemicalCategoryService.findByLabAndName(betaLab, waterChemType).isEmpty());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindShelfLifeByLabAndChemType_whenDeleted_gotWhatExpected() {
//        ShelfLife deletedShelfLife = chemicalCategoryService.getCategories(false).stream().filter(ShelfLife::getDeleted).findAny().get();
//        ShelfLife found = chemicalCategoryService.findByLabAndName(deletedShelfLife.getLab(), deletedShelfLife.getChemType()).get();
//        Assertions.assertEquals(deletedShelfLife.getId(), found.getId());
//        Assertions.assertTrue(found.getDeleted());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindShelfLifeByLab_whenLabValid_gotNoException() {
//        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
//        chemicalCategoryService.findByLab(alphaLab.getKey(), AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
//
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindShelfLifeByLab_whenLabValid_gotWhatExpected() {
//        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
//        List<ShelfLife> shelfLifesForAlphaLab = chemicalCategoryService.findByLab(alphaLab.getKey(), AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
//        Assertions.assertTrue(!shelfLifesForAlphaLab.isEmpty());
//        shelfLifesForAlphaLab.forEach(shelfLife -> {
//            Assertions.assertTrue(!shelfLife.getDeleted());
//            Assertions.assertEquals(alphaLab, shelfLife.getLab());
//        });
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindShelfLifeByLab_whenLabNonValid_gotResourceNotFoundException() {
//        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
//            chemicalCategoryService.findByLab("non-existing-key", AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindShelfLifeByLabOATrue_whenLabValid_gotNoException() {
//        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
//        chemicalCategoryService.findByLab(alphaLab.getKey(), true, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
//
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindShelfLifeByLabOATrue_whenLabValid_gotWhatExpected() {
//        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
//        List<ShelfLife> shelfLifesForAlphaLab = chemicalCategoryService.findByLab(alphaLab.getKey(), true, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
//        Assertions.assertTrue(!shelfLifesForAlphaLab.isEmpty());
//        shelfLifesForAlphaLab.forEach(shelfLife -> {
//            Assertions.assertTrue(!shelfLife.getDeleted());
//            Assertions.assertEquals(alphaLab, shelfLife.getLab());
//        });
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindShelfLifeByLabOATrue_whenLabNonValid_gotResourceNotFoundException() {
//        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
//            chemicalCategoryService.findByLab("non-existing-key", true, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindShelfLifeByLabOAFalse_whenLabValid_gotNoException() {
//        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
//        chemicalCategoryService.findByLab(alphaLab.getKey(), false, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
//
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindShelfLifeByLabOAFalse_whenLabValid_gotWhatExpected() {
//        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
//        List<ShelfLife> shelfLifesForAlphaLab = chemicalCategoryService.findByLab(alphaLab.getKey(), false, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
//        Assertions.assertTrue(!shelfLifesForAlphaLab.isEmpty());
//        shelfLifesForAlphaLab.forEach(shelfLife -> {
//            Assertions.assertEquals(alphaLab, shelfLife.getLab());
//        });
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindShelfLifeByLabOAFalse_whenLabValid_gotDeletedItem() {
//        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
//        List<ShelfLife> shelfLifesForAlphaLab = chemicalCategoryService.findByLab(alphaLab.getKey(), false, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
//        Assertions.assertTrue(shelfLifesForAlphaLab.stream().anyMatch(ShelfLife::getDeleted));
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindShelfLifeByLabOAFalse_whenLabNonValid_gotResourceNotFoundException() {
//        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
//            chemicalCategoryService.findByLab("non-existing-key", false, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testGetShelfLifes_gotNoException() {
//        chemicalCategoryService.getCategories();
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testGetShelfLifes_gotWhatExpected() {
//        List<ShelfLife> shelfLifes = chemicalCategoryService.getCategories();
//        Assertions.assertTrue(!shelfLifes.isEmpty());
//        shelfLifes.forEach(shelfLife -> {
//            Assertions.assertTrue(!shelfLife.getDeleted());
//        });
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testGetShelfLifesOATrue_gotNoException() {
//        chemicalCategoryService.getCategories(true);
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testGetShelfLifesOATrue_gotWhatExpected() {
//        List<ShelfLife> shelfLifes = chemicalCategoryService.getCategories(true);
//        Assertions.assertTrue(!shelfLifes.isEmpty());
//        shelfLifes.forEach(shelfLife -> {
//            Assertions.assertTrue(!shelfLife.getDeleted());
//        });
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testGetShelfLifesOAFalse_gotNoException() {
//        chemicalCategoryService.getCategories(false);
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testGetShelfLifesOAFalse_gotDeletedItem() {
//        List<ShelfLife> shelfLifes = chemicalCategoryService.getCategories(false);
//        shelfLifes.forEach(shelfLife -> logger.info("lab key: " + shelfLife.getLab().getKey() + ", chem type: " + shelfLife.getChemType().getName()));
//        Assertions.assertTrue(shelfLifes.stream().anyMatch(ShelfLife::getDeleted));
//    }
//
//    //DELETE
//    @Test
//    @Rollback
//    @Transactional
//    public void testDeleteShelfLife_whenIdValid_gotNoException() {
//        ChemType buffer = chemTypeService.findByName(LabAdminTestUtils.BUFFER_CATEGORY).get();
//        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
//        ShelfLife bufferForAlpha = chemicalCategoryService.findByLabAndName(alphaLab, buffer).get();
//        chemicalCategoryService.deleteChemicalCategory(bufferForAlpha.getId(), AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testDeleteShelfLife_whenIdValid_notFoundAfterwards() {
//        ChemType buffer = chemTypeService.findByName(LabAdminTestUtils.BUFFER_CATEGORY).get();
//        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
//        ShelfLife bufferForAlpha = chemicalCategoryService.findByLabAndName(alphaLab, buffer).get();
//        chemicalCategoryService.deleteChemicalCategory(bufferForAlpha.getId(), AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
//        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
//            chemicalCategoryService.findById(bufferForAlpha.getId());
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testDeleteShelfLife_whenIdDoesNotExist_gotResourceNotFoudnException() {
//        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
//            chemicalCategoryService.deleteChemicalCategory((long)Integer.MAX_VALUE, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testDeleteShelfLife_whenAdminHasNoRightToSHelfLife_gotValidationException() {
//        ChemType buffer = chemTypeService.findByName(LabAdminTestUtils.BUFFER_CATEGORY).get();
//        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
//        ShelfLife bufferForAlpha = chemicalCategoryService.findByLabAndName(alphaLab, buffer).get();
//        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
//            chemicalCategoryService.deleteChemicalCategory(bufferForAlpha.getId(), AccountManagerTestUtils.BETA_LAB_MANAGER_PRINCIPAL);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }






    private ChemType getNonexistingChemType() {
        ChemType nonexistingChemType = new ChemType();
        nonexistingChemType.setName("non-existing-chem-type");
        nonexistingChemType.setId((long)Integer.MAX_VALUE);
        return nonexistingChemType;
    }

    private Lab getNonexistingLab() {
        Lab lab = new Lab();
        lab.setKey("non-existing-lab");
        lab.setName("Non Existing Lab");
        return lab;
    }



}
