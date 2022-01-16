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
import java.util.List;

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
            @Autowired ChemicalCategoryRepository chemicalCategoryRepository,
            @Autowired ChemicalService chemicalService) {
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
    
    
    // READ
    @Test
    @Rollback
    @Transactional
    public void testGetChemicalByShortName_whenValid_gotNoException() {
        chemicalService.getByShortName(LabAdminTestUtils.ACETONITRIL_SHORT_NAME, alphaLab);
    }

    @Test
    @Rollback
    @Transactional
    public void testGetChemicalByShortName_whenValid_gotAttributesAsExpected() {
        Chemical acn = chemicalService.getByShortName(LabAdminTestUtils.ACETONITRIL_SHORT_NAME, alphaLab);
        Assertions.assertEquals(LabAdminTestUtils.ACETONITRIL_SHORT_NAME, acn.getShortName());
        Assertions.assertEquals(LabAdminTestUtils.ACETONITRIL_EXACT_NAME, acn.getExactName());
        Assertions.assertEquals(alphaLab.getKey(), acn.getLab().getKey());
        Assertions.assertEquals(LabAdminTestUtils.ORGANIC_CATEGORY, acn.getCategory().getName());
        Assertions.assertFalse(acn.getDeleted());
    }

    @Test
    @Rollback
    @Transactional
    public void testGetChemicalByShortName_whenShortNameDoesNotExist_gotResourceNotFoundException() {
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            chemicalService.getByShortName("not-existing", alphaLab);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testGetChemicalByShortName_whenAlreadyDeleted_gotResourceNotFoundException() {
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            chemicalService.getByShortName(LabAdminTestUtils.IPA_SHORT_NAME, alphaLab);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testGetChemicalByShortName_whenChemicalExistsAnotherLab_gotResourceNotFoundException(@Autowired LabService labService) {
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            Lab betaLab = labService.findLabByKey(AccountManagerTestUtils.BETA_LAB_KEY);
            chemicalService.getByShortName(LabAdminTestUtils.ETHANOL_SHORT_NAME, betaLab);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testGetChemicalByShortName_whenInvalidLab_gotException() {
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            Lab invalidLab = new Lab();
            invalidLab.setId((long)Integer.MAX_VALUE);
            chemicalService.getByShortName(LabAdminTestUtils.ACETONITRIL_SHORT_NAME, invalidLab);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testGetChemicalsForUser_whenValid_gotNoException() {
        chemicalService.getChemicalsForUser(AccountManagerTestUtils.ALPHA_LAB_KEY, AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL);
    }

    @Test
    @Rollback
    @Transactional
    public void testGetChemicalsForUser_whenValid_gotRightSize() {
        List<Chemical> chemicals = chemicalService.getChemicalsForUser(
                AccountManagerTestUtils.ALPHA_LAB_KEY,
                AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL);
        // hard coded expected value as the size. If db setup changes, it should be changed too
        Assertions.assertEquals(4, chemicals.size());
        Chemical acn = chemicals.stream().filter(chem -> chem.getShortName().equals(LabAdminTestUtils.ACETONITRIL_SHORT_NAME)).findAny().get();
        Assertions.assertEquals(LabAdminTestUtils.ACETONITRIL_EXACT_NAME, acn.getExactName());
        Assertions.assertEquals(alphaLab.getKey(), acn.getLab().getKey());
        Assertions.assertEquals(LabAdminTestUtils.ORGANIC_CATEGORY, acn.getCategory().getName());
        Assertions.assertFalse(acn.getDeleted());
    }

    @Test
    @Rollback
    @Transactional
    public void testGetChemicalsForUser_whenValid_gotNoDeleted() {
        List<Chemical> chemicals = chemicalService.getChemicalsForUser(
                AccountManagerTestUtils.ALPHA_LAB_KEY,
                AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL);
        chemicals.forEach(chemical -> Assertions.assertFalse(chemical.getDeleted()));
    }

    @Test
    @Rollback
    @Transactional
    public void testGetChemicalsForUser_whenValid_gotAttributesAsExpected() {
        List<Chemical> chemicals = chemicalService.getChemicalsForUser(
                AccountManagerTestUtils.ALPHA_LAB_KEY, 
                AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL);
        Chemical acn = chemicals.stream().filter(chem -> chem.getShortName().equals(LabAdminTestUtils.ACETONITRIL_SHORT_NAME)).findAny().get();
        Assertions.assertEquals(LabAdminTestUtils.ACETONITRIL_EXACT_NAME, acn.getExactName());
        Assertions.assertEquals(alphaLab.getKey(), acn.getLab().getKey());
        Assertions.assertEquals(LabAdminTestUtils.ORGANIC_CATEGORY, acn.getCategory().getName());
        Assertions.assertFalse(acn.getDeleted());
    }

    @Test
    @Rollback
    @Transactional
    public void testGetChemicalById_whenValid_gotNoException() {
        List<Chemical> chemicals = chemicalService.getChemicalsForUser(
                AccountManagerTestUtils.ALPHA_LAB_KEY,
                AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL);
        Chemical acnInAlpha = chemicals.stream().filter(chem -> chem.getShortName().equals(LabAdminTestUtils.ACETONITRIL_SHORT_NAME)).findAny().get();
        chemicalService.findById(acnInAlpha.getId(), AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL);
    }

    @Test
    @Rollback
    @Transactional
    public void testGetChemicalById_whenValid__gotAttributesAsExpected() {
        List<Chemical> chemicals = chemicalService.getChemicalsForUser(
                AccountManagerTestUtils.ALPHA_LAB_KEY,
                AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL);
        Chemical acnInAlpha = chemicals.stream().filter(chem -> chem.getShortName().equals(LabAdminTestUtils.ACETONITRIL_SHORT_NAME)).findAny().get();
        Chemical fetched = chemicalService.findById(acnInAlpha.getId(), AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL);
        Assertions.assertEquals(LabAdminTestUtils.ACETONITRIL_SHORT_NAME, fetched.getShortName());
        Assertions.assertEquals(LabAdminTestUtils.ACETONITRIL_EXACT_NAME, fetched.getExactName());
        Assertions.assertEquals(alphaLab.getKey(), fetched.getLab().getKey());
        Assertions.assertEquals(LabAdminTestUtils.ORGANIC_CATEGORY, fetched.getCategory().getName());
        Assertions.assertFalse(fetched.getDeleted());
    }
    

    @Test
    @Rollback
    @Transactional
    public void testGetChemicalById_whenIdNonExisting_gotResourceNotFoundException() {
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            chemicalService.findById((long)Integer.MAX_VALUE, AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testGetChemicalById_whenAlreadyDeleted_gotResourceNotFoundException(@Autowired ChemicalRepository chemicalRepository) {
        Iterable<Chemical> chemIterable = chemicalRepository.findAll();
        Chemical ipa = null;
        for (Chemical chemical: chemIterable) {
            if (chemical.getShortName().equals(LabAdminTestUtils.IPA_SHORT_NAME)) {
                ipa = chemical; 
            }
        }
        Chemical finalIpa = ipa;
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            chemicalService.findById(finalIpa.getId(), AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testGetChemicalById_whenChemicalIsInAnotherLab_gotForbiddenException(@Autowired ChemicalRepository chemicalRepository) {
        List<Chemical> chemicals = chemicalService.getChemicalsForUser(
                AccountManagerTestUtils.ALPHA_LAB_KEY,
                AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL);
        Chemical acnInAlpha = chemicals.stream().filter(chem -> chem.getShortName().equals(LabAdminTestUtils.ACETONITRIL_SHORT_NAME)).findAny().get();

        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            chemicalService.findById(acnInAlpha.getId(), AccountManagerTestUtils.BETA_LAB_USER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testGetChemicalById_whenOnlyActiveFalseAndChemIsDeleted_gotNoException(@Autowired ChemicalRepository chemicalRepository) {
        Iterable<Chemical> chemIterable = chemicalRepository.findAll();
        Chemical ipa = null;
        for (Chemical chemical: chemIterable) {
            if (chemical.getShortName().equals(LabAdminTestUtils.IPA_SHORT_NAME)) {
                ipa = chemical;
            }
        }
        Chemical finalIpa = ipa;
        Chemical fetched = chemicalService.findById(finalIpa.getId(), AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL, false);
        Assertions.assertEquals(LabAdminTestUtils.IPA_SHORT_NAME,fetched.getShortName());
    }


    @Test
    @Rollback
    @Transactional
    public void testDeleteChemical_gotGotNoException() {
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
        List<Chemical> chemicals = chemicalService.getChemicalsForUser(
                AccountManagerTestUtils.ALPHA_LAB_KEY,
                AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL);
        Chemical acnInAlpha = chemicals.stream().filter(chem -> chem.getShortName().equals(LabAdminTestUtils.ACETONITRIL_SHORT_NAME)).findAny().get();
        chemicalService.deleteChemical(acnInAlpha.getId(), admin);
    }

    @Test
    @Rollback
    @Transactional
    public void testDeleteChemical_gotGotNoException(@Autowired ChemicalRepository chemicalRepository) {
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
        List<Chemical> chemicals = chemicalService.getChemicalsForUser(
                AccountManagerTestUtils.ALPHA_LAB_KEY,
                AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL);
        Chemical acnInAlpha = chemicals.stream().filter(chem -> chem.getShortName().equals(LabAdminTestUtils.ACETONITRIL_SHORT_NAME)).findAny().get();
        chemicalService.deleteChemical(acnInAlpha.getId(), admin);
        Chemical deleted = chemicalRepository.findById(acnInAlpha.getId()).get();
        Assertions.assertTrue(deleted.getDeleted());
    }

    @Test
    @Rollback
    @Transactional
    public void testDeleteChemical_whenIdDoesNotExist_gotResourceNotFoundException(@Autowired ChemicalRepository chemicalRepository) {
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
    public void testDeleteChemical_whenBetaLabAdmin_gotForbiddenException() {
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
        List<Chemical> chemicals = chemicalService.getChemicalsForUser(
                AccountManagerTestUtils.ALPHA_LAB_KEY,
                AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL);
        Chemical acnInAlpha = chemicals.stream().filter(chem -> chem.getShortName().equals(LabAdminTestUtils.ACETONITRIL_SHORT_NAME)).findAny().get();
        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            chemicalService.deleteChemical(acnInAlpha.getId(), AccountManagerTestUtils.BETA_LAB_ADMIN_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testDeleteChemical_whenAlphaLabUser_gotForbiddenException() {
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
        List<Chemical> chemicals = chemicalService.getChemicalsForUser(
                AccountManagerTestUtils.ALPHA_LAB_KEY,
                AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL);
        Chemical acnInAlpha = chemicals.stream().filter(chem -> chem.getShortName().equals(LabAdminTestUtils.ACETONITRIL_SHORT_NAME)).findAny().get();
        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            chemicalService.deleteChemical(acnInAlpha.getId(), AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testDeleteChemical_whenAlreadyDeleted_gotResourceNotFoundException(@Autowired ChemicalRepository chemicalRepository) {
        Principal admin = AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL;
        List<Chemical> chemicals = chemicalService.getChemicalsForUser(
                AccountManagerTestUtils.ALPHA_LAB_KEY,
                AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL);
        Chemical nh4 = chemicals.stream().filter(chem -> chem.getShortName().equals(LabAdminTestUtils.NH4_ACETATE_SHORT_NAME)).findAny().get();
        chemicalService.deleteChemical(nh4.getId(), admin);
        Chemical deleted = chemicalRepository.findById(nh4.getId()).get();
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            chemicalService.deleteChemical(deleted.getId(), AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }
    

}
