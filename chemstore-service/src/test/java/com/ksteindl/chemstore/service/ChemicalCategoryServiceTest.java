package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.BaseControllerTest;
import com.ksteindl.chemstore.domain.entities.Chemical;
import com.ksteindl.chemstore.domain.entities.ChemicalCategory;
import com.ksteindl.chemstore.domain.input.ChemicalCategoryInput;
import com.ksteindl.chemstore.exceptions.ForbiddenException;
import com.ksteindl.chemstore.exceptions.ResourceNotFoundException;
import com.ksteindl.chemstore.exceptions.ValidationException;
import com.ksteindl.chemstore.utils.AccountManagerTestUtils;
import com.ksteindl.chemstore.utils.LabAdminTestUtils;
import jakarta.transaction.Transactional;
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

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ChemicalCategoryServiceTest extends BaseControllerTest{

    private static final Logger logger = LogManager.getLogger(ChemicalCategoryServiceTest.class);

    @Autowired
    private ChemicalCategoryService chemicalCategoryService;
    @Autowired
    private ChemicalService chemicalService;

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
    public void testCreateCategory_whenLabIsDeleted_gotResourceNotFoundException() {
        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
        input.setLabKey(AccountManagerTestUtils.DELTA_LAB_KEY);
        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
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
        ChemicalCategory persisted = chemicalCategoryService.getByLabForUser(input.getLabKey(), admin).stream()
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
        ChemicalCategory persisted = chemicalCategoryService.getByLabForUser(input.getLabKey(), admin).stream()
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
        ChemicalCategory persisted = chemicalCategoryService.getByLabForUser(input.getLabKey(), admin).stream()
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
        ChemicalCategory persisted = chemicalCategoryService.getByLabForUser(input.getLabKey(), admin).stream()
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
        ChemicalCategory persisted = chemicalCategoryService.getByLabForUser(input.getLabKey(), admin).stream()
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
        ChemicalCategory persisted = chemicalCategoryService.getByLabForUser(input.getLabKey(), AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL).stream()
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
        ChemicalCategory persisted = chemicalCategoryService.getByLabForUser(input.getLabKey(), AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL).stream()
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
        ChemicalCategory persisted = chemicalCategoryService.getByLabForUser(input.getLabKey(), admin).stream()
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

    @Test
    @Rollback
    @Transactional
    public void testUpdateCategory_whenLabIsDeleted_gotFrobiddenException() {
        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
        ChemicalCategory persisted = chemicalCategoryService.getByLabForUser(input.getLabKey(), admin).stream()
                .filter(category -> category.getName().equals(input.getName()))
                .findAny().get();
        input.setLabKey(AccountManagerTestUtils.DELTA_LAB_KEY);
        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            chemicalCategoryService.updateCategory(input, persisted.getId(), admin);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateCategory_whenIdDoesNotExist_gotResourceNotFoundException() {
        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            chemicalCategoryService.updateCategory(input, (long)Integer.MAX_VALUE, admin);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testGetCategoryById_whenValid_gotNoException() {
        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
        ChemicalCategory persisted = chemicalCategoryService.getByLabForUser(input.getLabKey(), admin).stream()
                .filter(category -> category.getName().equals(input.getName()))
                .findAny().get();
        chemicalCategoryService.getById(persisted.getId());
    }

    @Test
    @Rollback
    @Transactional
    public void testGetCategoryById_whenValid_gotAttributesAsExpected() {
        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
        ChemicalCategory persisted = chemicalCategoryService.getByLabForUser(input.getLabKey(), admin).stream()
                .filter(category -> category.getName().equals(input.getName()))
                .findAny().get();
        ChemicalCategory fetched = chemicalCategoryService.getById(persisted.getId());
        Assertions.assertEquals(input.getLabKey(), fetched.getLab().getKey());
        Assertions.assertFalse(fetched.getDeleted());
        Assertions.assertEquals(input.getName(), fetched.getName());
        Assertions.assertEquals(Duration.between(
                        LocalDateTime.now(), LocalDateTime.now().plusYears(LabAdminTestUtils.ORGANIC_FOR_ALPHA_YEARS)).toHours(),
                fetched.getShelfLife().toHours(), 24);
    }

    @Test
    @Rollback
    @Transactional
    public void testGetCategoryById_whenIdDoesNotExist_gotResourceNotFoundException() {
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            chemicalCategoryService.getById((long)Integer.MAX_VALUE);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testGetCategoryById_whenCategoryAlreadyDeleted_gotResourceNotFoundException() {
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
        ChemicalCategory deleted = chemicalCategoryService.findByLabForUser(AccountManagerTestUtils.ALPHA_LAB_KEY, false, admin).stream()
                .filter(category -> category.getName().equals(LabAdminTestUtils.DELETED_CATEGORY))
                .findAny().get();
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            chemicalCategoryService.getById(deleted.getId());
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testFindCategoryById_whenValid_gotNoException() {
        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
        ChemicalCategory persisted = chemicalCategoryService.getByLabForUser(input.getLabKey(), admin).stream()
                .filter(category -> category.getName().equals(input.getName()))
                .findAny().get();
        chemicalCategoryService.findById(persisted.getId());
    }

    @Test
    @Rollback
    @Transactional
    public void testFindCategoryById_whenValid_gotAttributesAsExpected() {
        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
        ChemicalCategory persisted = chemicalCategoryService.getByLabForUser(input.getLabKey(), admin).stream()
                .filter(category -> category.getName().equals(input.getName()))
                .findAny().get();
        ChemicalCategory fetched = chemicalCategoryService.findById(persisted.getId());
        Assertions.assertEquals(input.getLabKey(), fetched.getLab().getKey());
        Assertions.assertFalse(fetched.getDeleted());
        Assertions.assertEquals(input.getName(), fetched.getName());
        Assertions.assertEquals(Duration.between(
                        LocalDateTime.now(), LocalDateTime.now().plusYears(LabAdminTestUtils.ORGANIC_FOR_ALPHA_YEARS)).toHours(),
                fetched.getShelfLife().toHours(), 24);
    }

    @Test
    @Rollback
    @Transactional
    public void testFindCategoryById_whenIdDoesNotExist_gotResourceNotFoundException() {
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            chemicalCategoryService.findById((long)Integer.MAX_VALUE);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testFindCategoryById_whenCategoryAlreadyDeleted_gotAttributesAsExpected() {
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
        ChemicalCategory deleted = chemicalCategoryService.findByLabForUser(AccountManagerTestUtils.ALPHA_LAB_KEY, false, admin).stream()
                .filter(category -> category.getName().equals(LabAdminTestUtils.DELETED_CATEGORY))
                .findAny().get();
        ChemicalCategory fetched = chemicalCategoryService.findById(deleted.getId());
        Assertions.assertEquals(LabAdminTestUtils.DELETED_CATEGORY, fetched.getName());
        Assertions.assertTrue(fetched.getDeleted());
    }

    @Test
    @Rollback
    @Transactional
    public void testGetCategoriesByLab_gotNoException() {
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL;
        chemicalCategoryService.getByLabForUser(AccountManagerTestUtils.ALPHA_LAB_KEY, admin);
    }

    @Test
    @Rollback
    @Transactional
    public void testGetCategoriesByLab_gotRightSize() {
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL;
        List<ChemicalCategory> categories = chemicalCategoryService.getByLabForUser(AccountManagerTestUtils.ALPHA_LAB_KEY, admin);
        Assertions.assertEquals(2, categories.size());
    }

    @Test
    @Rollback
    @Transactional
    public void testGetCategoriesByLab_gotOrganicAsExpected() {
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL;
        List<ChemicalCategory> categories = chemicalCategoryService.getByLabForUser(AccountManagerTestUtils.ALPHA_LAB_KEY, admin);
        categories.stream().filter(category -> category.getName().equals(LabAdminTestUtils.ORGANIC_CATEGORY)).findAny().get();
    }

    @Test
    @Rollback
    @Transactional
    public void testGetCategoriesByLab_gotNoDeleted() {
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL;
        List<ChemicalCategory> categories = chemicalCategoryService.getByLabForUser(AccountManagerTestUtils.ALPHA_LAB_KEY, admin);
        boolean deletedNotFound = true;
        for (ChemicalCategory category :categories) {
            deletedNotFound = deletedNotFound && !category.getDeleted();
        }
        Assertions.assertTrue(deletedNotFound);
    }

    @Test
    @Rollback
    @Transactional
    public void testGetCategoriesByLab_whenBlabAdmin_gotForbiddenException() {
        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            chemicalCategoryService.getByLabForUser(
                    AccountManagerTestUtils.ALPHA_LAB_KEY, 
                    AccountManagerTestUtils.BETA_LAB_ADMIN_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }
    

    @Test
    @Rollback
    @Transactional
    public void testFindCategoriesByLab_gotNoException() {
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
        chemicalCategoryService.findByLabForUser(AccountManagerTestUtils.ALPHA_LAB_KEY, false, admin);
    }

    @Test
    @Rollback
    @Transactional
    public void testFindCategoriesByLab_gotRightSize() {
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
        List<ChemicalCategory> categories = chemicalCategoryService.findByLabForUser(AccountManagerTestUtils.ALPHA_LAB_KEY, false, admin);
        Assertions.assertEquals(3, categories.size());
    }

    @Test
    @Rollback
    @Transactional
    public void testFindCategoriesByLab_gotDeletedCategory() {
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
        List<ChemicalCategory> categories = chemicalCategoryService.findByLabForUser(AccountManagerTestUtils.ALPHA_LAB_KEY, false, admin);
        categories.stream().filter(category -> category.getName().equals(LabAdminTestUtils.DELETED_CATEGORY)).findAny().get();
    }

    @Test
    @Rollback
    @Transactional
    public void testGetCategoriesByLab_gotDeletedAsWell() {
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
        List<ChemicalCategory> categories = chemicalCategoryService.findByLabForUser(AccountManagerTestUtils.ALPHA_LAB_KEY, false, admin);
        boolean deletedFound = false;
        for (ChemicalCategory category :categories) {
            deletedFound = deletedFound || category.getDeleted();
        }
        Assertions.assertTrue(deletedFound);
    }

    @Test
    @Rollback
    @Transactional
    public void testFindCategoriesByLab_whenBlabAdmin_gotForbiddenException() {
        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            chemicalCategoryService.findByLabForUser(
                    AccountManagerTestUtils.ALPHA_LAB_KEY,
                    false,
                    AccountManagerTestUtils.BETA_LAB_ADMIN_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }
    

    @Test
    @Rollback
    @Transactional
    public void testDeleteCategory_gotGotNoException() {
        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
        ChemicalCategory persisted = chemicalCategoryService.getByLabForUser(input.getLabKey(), admin).stream()
                .filter(category -> category.getName().equals(input.getName()))
                .findAny().get();
        chemicalCategoryService.deleteChemicalCategory(persisted.getId(), admin);
    }

    @Test
    @Rollback
    @Transactional
    public void testDeleteCategory_categoryIsDeleted() {
        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
        ChemicalCategory persisted = chemicalCategoryService.getByLabForUser(input.getLabKey(), admin).stream()
                .filter(category -> category.getName().equals(input.getName()))
                .findAny().get();
        chemicalCategoryService.deleteChemicalCategory(persisted.getId(), admin);
        ChemicalCategory deleted = chemicalCategoryService.findById(persisted.getId());
        Assertions.assertTrue(deleted.getDeleted());
    }

    @Test
    @Rollback
    @Transactional
    public void testDeleteCategory_whenIdNotExists_gotResourceNotFoundException() {
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            chemicalCategoryService.deleteChemicalCategory((long)Integer.MAX_VALUE, admin);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testDeleteCategory_whenBetaLabAdmin_gotForbiddenException() {
        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
        ChemicalCategory persisted = chemicalCategoryService.getByLabForUser(input.getLabKey(), AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL).stream()
                .filter(category -> category.getName().equals(input.getName()))
                .findAny().get();
        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            chemicalCategoryService.deleteChemicalCategory(persisted.getId(), AccountManagerTestUtils.BETA_LAB_ADMIN_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testDeleteCategory_whenAlphaLabUser_gotForbiddenException() {
        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
        ChemicalCategory persisted = chemicalCategoryService.getByLabForUser(input.getLabKey(), AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL).stream()
                .filter(category -> category.getName().equals(input.getName()))
                .findAny().get();
        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            chemicalCategoryService.deleteChemicalCategory(persisted.getId(), AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testDeleteCategory_whenAlreadyDeleted_gotResourceNotFoundException() {
        ChemicalCategory persisted = chemicalCategoryService.findByLabForUser(AccountManagerTestUtils.ALPHA_LAB_KEY, false, AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL).stream()
                .filter(category -> category.getName().equals(LabAdminTestUtils.DELETED_CATEGORY))
                .findAny().get();
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            chemicalCategoryService.deleteChemicalCategory(persisted.getId(), AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testDeleteCategory_categoriesAreDeletedFromChemicals() {
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
        ChemicalCategory persisted = chemicalCategoryService.getByLabForUser(AccountManagerTestUtils.ALPHA_LAB_KEY, admin).stream()
                .filter(category -> category.getName().equals(LabAdminTestUtils.ORGANIC_CATEGORY))
                .findAny().get();
        chemicalCategoryService.deleteChemicalCategory(persisted.getId(), admin);
        List<Chemical> chemicals = chemicalService.getChemicalsForUser(AccountManagerTestUtils.ALPHA_LAB_KEY, admin).stream()
                .filter(chemical -> chemical.getCategory() != null && chemical.getCategory().equals(persisted))
                .collect(Collectors.toList());
        Assertions.assertTrue(chemicals.isEmpty());
    }



}
