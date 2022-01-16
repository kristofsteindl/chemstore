package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.BaseControllerTest;
import com.ksteindl.chemstore.domain.entities.Chemical;
import com.ksteindl.chemstore.domain.entities.ChemicalCategory;
import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.domain.input.ChemicalInput;
import com.ksteindl.chemstore.domain.repositories.ChemicalCategoryRepository;
import com.ksteindl.chemstore.domain.repositories.ChemicalRepository;
import com.ksteindl.chemstore.exceptions.ForbiddenException;
import com.ksteindl.chemstore.exceptions.ResourceNotFoundException;
import com.ksteindl.chemstore.exceptions.ValidationException;
import com.ksteindl.chemstore.utils.AccountManagerTestUtils;
import com.ksteindl.chemstore.utils.LabAdminTestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
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

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ChemicalServiceTest extends BaseControllerTest{

    private static final Logger logger = LogManager.getLogger(ChemicalServiceTest.class);

    @Autowired
    private ChemicalCategoryService chemicalCategoryService;
    @Autowired
    private LabService labService;
    @Autowired
    private ChemicalService chemicalService;
    @Autowired
    private ChemicalRepository chemicalRepository;
    private static ChemicalCategory organicForAlpha;
    private static ChemicalCategory bufferForAlpha;
    private static Lab alphaLab;
    
    @BeforeAll
    public static void init(
            @Autowired LabService labService, 
            @Autowired ChemicalCategoryRepository chemicalCategoryRepository) {
        alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        organicForAlpha = chemicalCategoryRepository
                .findByLab(alphaLab)
                .stream()
                .filter(category -> category.getName().equals(LabAdminTestUtils.ORGANIC_CATEGORY))
                .findAny()
                .get();
        bufferForAlpha = chemicalCategoryRepository
                .findByLab(alphaLab)
                .stream()
                .filter(category -> category.getName().equals(LabAdminTestUtils.BUFFER_CATEGORY))
                .findAny()
                .get();
    }

    // CREATE
    @Test
    @Rollback
    @Transactional
    public void testCreateChemical_whenAllValid_gotNoException() {
        ChemicalInput input = LabAdminTestUtils.getFaForAlphaInput();
        input.setCategoryId(organicForAlpha.getId());
        chemicalService.createChemical(input, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateChemical_whenAllValid_savedValuesAsExpected() {
        ChemicalInput input = LabAdminTestUtils.getFaForAlphaInput();
        input.setCategoryId(organicForAlpha.getId());
        Chemical returned = chemicalService.createChemical(input, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
        Chemical fetched = chemicalRepository.findById(returned.getId()).get();
        Assertions.assertEquals(input.getShortName(), fetched.getShortName());
        Assertions.assertEquals(input.getExactName(), fetched.getExactName());
        Assertions.assertEquals(input.getLabKey(), fetched.getLab().getKey());
        Assertions.assertEquals(input.getCategoryId(), fetched.getCategory().getId());
        Assertions.assertFalse(fetched.getDeleted());
    }
    

    @Test
    @Rollback
    @Transactional
    public void testCreateChemical_whenUserNeitherAdminNorManager_gotForbiddenException() {
        ChemicalInput input = LabAdminTestUtils.getFaForAlphaInput();
        input.setCategoryId(organicForAlpha.getId());
        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            chemicalService.createChemical(input, AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateChemical_whenBetaLabAdmin_gotForbiddenException() {
        ChemicalInput input = LabAdminTestUtils.getFaForAlphaInput();
        input.setCategoryId(organicForAlpha.getId());
        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            chemicalService.createChemical(input, AccountManagerTestUtils.BETA_LAB_ADMIN_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateChemical_whenEmptyInput_gotException() {
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            ChemicalInput input = new ChemicalInput();
            chemicalService.createChemical(input, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateChemical_whenShortNameAlreadyExists_gotValidationExteption() {
        ChemicalInput input = LabAdminTestUtils.getFaForAlphaInput();
        input.setCategoryId(organicForAlpha.getId());
        input.setShortName(LabAdminTestUtils.ACETONITRIL_SHORT_NAME);
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            chemicalService.createChemical(input, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateChemical_whenExactNameAlreadyExists_gotValidationExteption() {
        ChemicalInput input = LabAdminTestUtils.getFaForAlphaInput();
        input.setCategoryId(organicForAlpha.getId());
        input.setExactName(LabAdminTestUtils.NH4_ACETATE_EXACT_NAME);
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            chemicalService.createChemical(input, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateChemical_whenLabKeyDoesNotExist_gotResourceNotFoundException() {
        ChemicalInput input = LabAdminTestUtils.getFaForAlphaInput();
        input.setCategoryId(organicForAlpha.getId());
        input.setLabKey("not-existing");
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            chemicalService.createChemical(input, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateChemical_whenCategoryAndChemicalLabDoesNotMatch_gotValidation() {
        ChemicalInput input = LabAdminTestUtils.getFaForAlphaInput();
        input.setCategoryId(organicForAlpha.getId());
        input.setLabKey(AccountManagerTestUtils.BETA_LAB_KEY);
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            chemicalService.createChemical(input, AccountManagerTestUtils.ALPHA_BETA_LAB_ADMIN_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateChemical_whenCategoryIdDoesNotExist_gotResourceNotFoundException() {
        ChemicalInput input = LabAdminTestUtils.getFaForAlphaInput();
        input.setCategoryId((long)Integer.MAX_VALUE);
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            chemicalService.createChemical(input, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateChemical_whenLabIsDeleted_gotResourceNotFoundException() {
        ChemicalInput input = LabAdminTestUtils.getFaForAlphaInput();
        input.setCategoryId(organicForAlpha.getId());
        input.setLabKey(AccountManagerTestUtils.DELTA_LAB_KEY);
        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            chemicalService.createChemical(input, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    
    // UPDATE
    @Test
    @Rollback
    @Transactional
    public void testUpdateChemical_whenAllValid_gotNoException() {
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
        ChemicalInput input = LabAdminTestUtils.getAcnForAlphaInput();
        Chemical persisted = chemicalService.getByShortName(input.getShortName(), alphaLab);
        
        input.setCategoryId(organicForAlpha.getId());
        input.setExactName("Changed name");
        
        chemicalService.updateChemical(input,  persisted.getId(), admin);
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateChemical_whenAllValid_savedValuesAsExpected() {
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
        ChemicalInput input = LabAdminTestUtils.getAcnForAlphaInput();
        Chemical persisted = chemicalService.getByShortName(input.getShortName(), alphaLab);
        
        String newExactName = "Changed exact name";
        String newShortName = "Changed short";

        input.setShortName(newShortName);
        input.setExactName(newExactName);
        input.setCategoryId(bufferForAlpha.getId());
        chemicalService.updateChemical(input,  persisted.getId(), admin);
        
        Chemical fetched = chemicalRepository.findById(persisted.getId()).get();
        Assertions.assertEquals(newShortName, fetched.getShortName());
        Assertions.assertEquals(newExactName, fetched.getExactName());
        Assertions.assertEquals(AccountManagerTestUtils.ALPHA_LAB_KEY, fetched.getLab().getKey());
        Assertions.assertEquals(bufferForAlpha.getId(), fetched.getCategory().getId());
        Assertions.assertFalse(fetched.getDeleted());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateChemical_whenUserNeitherAdminNorManager_gotForbiddenException() {
        ChemicalInput input = LabAdminTestUtils.getAcnForAlphaInput();
        Chemical persisted = chemicalService.getByShortName(input.getShortName(), alphaLab);

        input.setCategoryId(organicForAlpha.getId());
        input.setExactName("Changed name");

        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            chemicalService.updateChemical(input,  persisted.getId(), AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateChemical_whenBetaLabAdmin_gotForbiddenException() {
        ChemicalInput input = LabAdminTestUtils.getAcnForAlphaInput();
        Chemical persisted = chemicalService.getByShortName(input.getShortName(), alphaLab);

        input.setCategoryId(organicForAlpha.getId());
        input.setExactName("Changed name");

        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            chemicalService.updateChemical(input,  persisted.getId(), AccountManagerTestUtils.BETA_LAB_ADMIN_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateChemical_whenEmptyInput_gotException() {
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
        ChemicalInput input = LabAdminTestUtils.getAcnForAlphaInput();
        Chemical persisted = chemicalService.getByShortName(input.getShortName(), alphaLab);
        
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            chemicalService.updateChemical(new ChemicalInput(),  persisted.getId(), admin);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateChemical_whenExactNameAlreadyExists_gotValidationExteption() {
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
        ChemicalInput input = LabAdminTestUtils.getAcnForAlphaInput();
        Chemical persisted = chemicalService.getByShortName(input.getShortName(), alphaLab);

        input.setCategoryId(organicForAlpha.getId());
        input.setExactName(LabAdminTestUtils.NH4_ACETATE_EXACT_NAME);
        
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            chemicalService.updateChemical(input,  persisted.getId(), admin);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateChemical_whenShortNameAlreadyExists_gotValidationExteption() {
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
        ChemicalInput input = LabAdminTestUtils.getAcnForAlphaInput();
        Chemical persisted = chemicalService.getByShortName(input.getShortName(), alphaLab);

        input.setCategoryId(organicForAlpha.getId());
        input.setShortName(LabAdminTestUtils.NH4_ACETATE_SHORT_NAME);

        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            chemicalService.updateChemical(input,  persisted.getId(), admin);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateChemical_whenLabKeyDoesNotExist_gotResourceNotFoundException() {
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
        ChemicalInput input = LabAdminTestUtils.getAcnForAlphaInput();
        Chemical persisted = chemicalService.getByShortName(input.getShortName(), alphaLab);

        input.setCategoryId(organicForAlpha.getId());
        input.setExactName("changed name");
        input.setLabKey("non-existing");

        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            chemicalService.updateChemical(input,  persisted.getId(), admin);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
        
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateChemical_whenCategoryAndChemicalLabDoesNotMatch_gotValidation() {
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
        ChemicalInput input = LabAdminTestUtils.getAcnForAlphaInput();
        Chemical persisted = chemicalService.getByShortName(input.getShortName(), alphaLab);

        input.setCategoryId(organicForAlpha.getId());
        input.setExactName("changed name");
        input.setLabKey(AccountManagerTestUtils.BETA_LAB_KEY);

        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            chemicalService.updateChemical(input,  persisted.getId(), admin);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
        
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateChemical_whenCategoryIdDoesNotExist_gotResourceNotFoundException() {
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
        ChemicalInput input = LabAdminTestUtils.getAcnForAlphaInput();
        Chemical persisted = chemicalService.getByShortName(input.getShortName(), alphaLab);

        input.setCategoryId((long)Integer.MAX_VALUE);
        input.setExactName("changed name");

        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            chemicalService.updateChemical(input,  persisted.getId(), admin);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateChemical_whenLabIsDeleted_gotResourceNotFoundException() {
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
        ChemicalInput input = LabAdminTestUtils.getAcnForAlphaInput();
        Chemical persisted = chemicalService.getByShortName(input.getShortName(), alphaLab);

        input.setCategoryId(organicForAlpha.getId());
        input.setExactName("changed name");
        input.setLabKey(AccountManagerTestUtils.DELTA_LAB_KEY);

        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            chemicalService.updateChemical(input,  persisted.getId(), admin);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateChemical_whenIdDoesNotExist_gotResourceNotFoundException() {
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
        ChemicalInput input = LabAdminTestUtils.getAcnForAlphaInput();

        input.setCategoryId(organicForAlpha.getId());
        input.setExactName("changed name");

        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            chemicalService.updateChemical(input, (long)Integer.MAX_VALUE, admin);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateChemical_whenCategoryIsForAnotherLab_gotResourceNotFoundException() {
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
        ChemicalInput input = LabAdminTestUtils.getAcnForBetaInput();
        Lab betaLab = labService.findLabByKey(AccountManagerTestUtils.BETA_LAB_KEY);
        Chemical persisted = chemicalService.getByShortName(input.getShortName(), betaLab);

        input.setCategoryId(organicForAlpha.getId());
        input.setExactName("changed name");

        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            chemicalService.updateChemical(input,  persisted.getId(), admin);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }
    
    
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testGetCategoryById_whenValid_gotNoException() {
//        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
//        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
//        ChemicalCategory persisted = chemicalCategoryService.getByLabForUser(input.getLabKey(), admin).stream()
//                .filter(category -> category.getName().equals(input.getName()))
//                .findAny().get();
//        chemicalCategoryService.getById(persisted.getId());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testGetCategoryById_whenValid_gotAttributesAsExpected() {
//        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
//        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
//        ChemicalCategory persisted = chemicalCategoryService.getByLabForUser(input.getLabKey(), admin).stream()
//                .filter(category -> category.getName().equals(input.getName()))
//                .findAny().get();
//        ChemicalCategory fetched = chemicalCategoryService.getById(persisted.getId());
//        Assertions.assertEquals(input.getLabKey(), fetched.getLab().getKey());
//        Assertions.assertFalse(fetched.getDeleted());
//        Assertions.assertEquals(input.getName(), fetched.getName());
//        Assertions.assertEquals(Duration.between(
//                        LocalDateTime.now(), LocalDateTime.now().plusYears(LabAdminTestUtils.ORGANIC_FOR_ALPHA_YEARS)).toHours(),
//                fetched.getShelfLife().toHours());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testGetCategoryById_whenIdDoesNotExist_gotResourceNotFoundException() {
//        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
//            chemicalCategoryService.getById((long)Integer.MAX_VALUE);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testGetCategoryById_whenCategoryAlreadyDeleted_gotResourceNotFoundException() {
//        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
//        ChemicalCategory deleted = chemicalCategoryService.findByLabForUser(AccountManagerTestUtils.ALPHA_LAB_KEY, false, admin).stream()
//                .filter(category -> category.getName().equals(LabAdminTestUtils.DELETED_CATEGORY))
//                .findAny().get();
//        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
//            chemicalCategoryService.getById(deleted.getId());
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindCategoryById_whenValid_gotNoException() {
//        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
//        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
//        ChemicalCategory persisted = chemicalCategoryService.getByLabForUser(input.getLabKey(), admin).stream()
//                .filter(category -> category.getName().equals(input.getName()))
//                .findAny().get();
//        chemicalCategoryService.findById(persisted.getId());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindCategoryById_whenValid_gotAttributesAsExpected() {
//        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
//        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
//        ChemicalCategory persisted = chemicalCategoryService.getByLabForUser(input.getLabKey(), admin).stream()
//                .filter(category -> category.getName().equals(input.getName()))
//                .findAny().get();
//        ChemicalCategory fetched = chemicalCategoryService.findById(persisted.getId());
//        Assertions.assertEquals(input.getLabKey(), fetched.getLab().getKey());
//        Assertions.assertFalse(fetched.getDeleted());
//        Assertions.assertEquals(input.getName(), fetched.getName());
//        Assertions.assertEquals(Duration.between(
//                        LocalDateTime.now(), LocalDateTime.now().plusYears(LabAdminTestUtils.ORGANIC_FOR_ALPHA_YEARS)).toHours(),
//                fetched.getShelfLife().toHours());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindCategoryById_whenIdDoesNotExist_gotResourceNotFoundException() {
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
//    public void testFindCategoryById_whenCategoryAlreadyDeleted_gotAttributesAsExpected() {
//        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
//        ChemicalCategory deleted = chemicalCategoryService.findByLabForUser(AccountManagerTestUtils.ALPHA_LAB_KEY, false, admin).stream()
//                .filter(category -> category.getName().equals(LabAdminTestUtils.DELETED_CATEGORY))
//                .findAny().get();
//        ChemicalCategory fetched = chemicalCategoryService.findById(deleted.getId());
//        Assertions.assertEquals(LabAdminTestUtils.DELETED_CATEGORY, fetched.getName());
//        Assertions.assertTrue(fetched.getDeleted());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testGetCategoriesByLab_gotNoException() {
//        Principal admin = AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL;
//        chemicalCategoryService.getByLabForUser(AccountManagerTestUtils.ALPHA_LAB_KEY, admin);
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testGetCategoriesByLab_gotRightSize() {
//        Principal admin = AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL;
//        List<ChemicalCategory> categories = chemicalCategoryService.getByLabForUser(AccountManagerTestUtils.ALPHA_LAB_KEY, admin);
//        Assertions.assertEquals(2, categories.size());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testGetCategoriesByLab_gotOrganicAsExpected() {
//        Principal admin = AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL;
//        List<ChemicalCategory> categories = chemicalCategoryService.getByLabForUser(AccountManagerTestUtils.ALPHA_LAB_KEY, admin);
//        categories.stream().filter(category -> category.getName().equals(LabAdminTestUtils.ORGANIC_CATEGORY)).findAny().get();
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testGetCategoriesByLab_gotNoDeleted() {
//        Principal admin = AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL;
//        List<ChemicalCategory> categories = chemicalCategoryService.getByLabForUser(AccountManagerTestUtils.ALPHA_LAB_KEY, admin);
//        boolean deletedNotFound = true;
//        for (ChemicalCategory category :categories) {
//            deletedNotFound = deletedNotFound && !category.getDeleted();
//        }
//        Assertions.assertTrue(deletedNotFound);
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testGetCategoriesByLab_whenBlabAdmin_gotForbiddenException() {
//        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
//            chemicalCategoryService.getByLabForUser(
//                    AccountManagerTestUtils.ALPHA_LAB_KEY, 
//                    AccountManagerTestUtils.BETA_LAB_ADMIN_PRINCIPAL);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//    
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindCategoriesByLab_gotNoException() {
//        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
//        chemicalCategoryService.findByLabForUser(AccountManagerTestUtils.ALPHA_LAB_KEY, false, admin);
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindCategoriesByLab_gotRightSize() {
//        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
//        List<ChemicalCategory> categories = chemicalCategoryService.findByLabForUser(AccountManagerTestUtils.ALPHA_LAB_KEY, false, admin);
//        Assertions.assertEquals(3, categories.size());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindCategoriesByLab_gotDeletedCategory() {
//        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
//        List<ChemicalCategory> categories = chemicalCategoryService.findByLabForUser(AccountManagerTestUtils.ALPHA_LAB_KEY, false, admin);
//        categories.stream().filter(category -> category.getName().equals(LabAdminTestUtils.DELETED_CATEGORY)).findAny().get();
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testGetCategoriesByLab_gotDeletedAsWell() {
//        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
//        List<ChemicalCategory> categories = chemicalCategoryService.findByLabForUser(AccountManagerTestUtils.ALPHA_LAB_KEY, false, admin);
//        boolean deletedFound = false;
//        for (ChemicalCategory category :categories) {
//            deletedFound = deletedFound || category.getDeleted();
//        }
//        Assertions.assertTrue(deletedFound);
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testFindCategoriesByLab_whenBlabAdmin_gotForbiddenException() {
//        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
//            chemicalCategoryService.findByLabForUser(
//                    AccountManagerTestUtils.ALPHA_LAB_KEY,
//                    false,
//                    AccountManagerTestUtils.BETA_LAB_ADMIN_PRINCIPAL);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//    
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testDeleteCategory_gotGotNoException() {
//        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
//        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
//        ChemicalCategory persisted = chemicalCategoryService.getByLabForUser(input.getLabKey(), admin).stream()
//                .filter(category -> category.getName().equals(input.getName()))
//                .findAny().get();
//        chemicalCategoryService.deleteChemicalCategory(persisted.getId(), admin);
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testDeleteCategory_categoryIsDeleted() {
//        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
//        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
//        ChemicalCategory persisted = chemicalCategoryService.getByLabForUser(input.getLabKey(), admin).stream()
//                .filter(category -> category.getName().equals(input.getName()))
//                .findAny().get();
//        chemicalCategoryService.deleteChemicalCategory(persisted.getId(), admin);
//        ChemicalCategory deleted = chemicalCategoryService.findById(persisted.getId());
//        Assertions.assertTrue(deleted.getDeleted());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testDeleteCategory_whenIdNotExists_gotResourceNotFoundException() {
//        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
//        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
//            chemicalCategoryService.deleteChemicalCategory((long)Integer.MAX_VALUE, admin);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testDeleteCategory_whenBetaLabAdmin_gotForbiddenException() {
//        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
//        ChemicalCategory persisted = chemicalCategoryService.getByLabForUser(input.getLabKey(), AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL).stream()
//                .filter(category -> category.getName().equals(input.getName()))
//                .findAny().get();
//        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
//            chemicalCategoryService.deleteChemicalCategory(persisted.getId(), AccountManagerTestUtils.BETA_LAB_ADMIN_PRINCIPAL);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testDeleteCategory_whenAlphaLabUser_gotForbiddenException() {
//        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
//        ChemicalCategory persisted = chemicalCategoryService.getByLabForUser(input.getLabKey(), AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL).stream()
//                .filter(category -> category.getName().equals(input.getName()))
//                .findAny().get();
//        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
//            chemicalCategoryService.deleteChemicalCategory(persisted.getId(), AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testDeleteCategory_whenAlreadyDeleted_gotResourceNotFoundException() {
//        ChemicalCategory persisted = chemicalCategoryService.findByLabForUser(AccountManagerTestUtils.ALPHA_LAB_KEY, false, AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL).stream()
//                .filter(category -> category.getName().equals(LabAdminTestUtils.DELETED_CATEGORY))
//                .findAny().get();
//        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
//            chemicalCategoryService.deleteChemicalCategory(persisted.getId(), AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL);
//        });
//        logger.info("Expected Exception is thrown:");
//        logger.info("with class: " + exception.getClass());
//        logger.info("with message: " + exception.getMessage());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    public void testDeleteCategory_categoriesAreDeletedFromChemicals() {
//        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
//        ChemicalCategory persisted = chemicalCategoryService.getByLabForUser(AccountManagerTestUtils.ALPHA_LAB_KEY, admin).stream()
//                .filter(category -> category.getName().equals(LabAdminTestUtils.ORGANIC_CATEGORY))
//                .findAny().get();
//        chemicalCategoryService.deleteChemicalCategory(persisted.getId(), admin);
//        List<Chemical> chemicals = chemicalService.getChemicalsForAdmin(AccountManagerTestUtils.ALPHA_LAB_KEY, admin, false).stream()
//                .filter(chemical -> chemical.getCategory() != null && chemical.getCategory().equals(persisted))
//                .collect(Collectors.toList());
//        Assertions.assertTrue(chemicals.isEmpty());
//    }



}
