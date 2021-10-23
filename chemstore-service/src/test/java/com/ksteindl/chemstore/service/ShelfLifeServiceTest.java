package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.BaseControllerTest;
import com.ksteindl.chemstore.domain.entities.ChemType;
import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.domain.entities.ShelfLife;
import com.ksteindl.chemstore.domain.input.ShelfLifeInput;
import com.ksteindl.chemstore.domain.repositories.ShelfLifeRepositoy;
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ShelfLifeServiceTest extends BaseControllerTest{

    private static final Logger logger = LogManager.getLogger(ShelfLifeServiceTest.class);

    @Autowired
    private ShelfLifeService shelfLifeService;
    @Autowired
    private ShelfLifeRepositoy shelfLifeRepositoy;
    @Autowired
    private ChemTypeService chemTypeService;
    @Autowired
    private LabService labService;

    // CREATE
    // TODO seq number check
    @Test
    @Rollback
    @Transactional
    public void testCreateShelfLife_whenAllValid_gotNoException() {
        ShelfLifeInput input = LabAdminTestUtils.getSolidForBetaInput();
        Long chemTypeId = chemTypeService.getChemTypes().stream()
                .filter(chemType -> chemType.getName().equals(LabAdminTestUtils.SOLID_COMPOUND_NAME))
                .findAny()
                .get()
                .getId();
        input.setChemTypeId(chemTypeId);
        shelfLifeService.createShelfLife(input, AccountManagerTestUtils.BETA_LAB_MANAGER_PRINCIPAL);
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateShelfLife_whenAllValid_savedValuesAsExpected() {
        ShelfLifeInput input = LabAdminTestUtils.getSolidForBetaInput();
        Long chemTypeId = chemTypeService.getChemTypes().stream()
                .filter(chemType -> chemType.getName().equals(LabAdminTestUtils.SOLID_COMPOUND_NAME))
                .findAny()
                .get()
                .getId();
        input.setChemTypeId(chemTypeId);
        Long id = shelfLifeService.createShelfLife(input, AccountManagerTestUtils.BETA_LAB_MANAGER_PRINCIPAL).getId();
        ShelfLife persisted = shelfLifeService.findById(id);
        Assertions.assertEquals(input.getLabKey(), persisted.getLab().getKey());
        Assertions.assertEquals(input.getChemTypeId(), persisted.getChemType().getId());
        Assertions.assertEquals(Duration.between(LocalDateTime.now(), LocalDateTime.now().plusYears(LabAdminTestUtils.SOLID_FOR_BETA_YEAR)).toHours(), persisted.getDuration().toHours());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateShelfLife_whenEmptyInput_gotException() {
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            ShelfLifeInput input = ShelfLifeInput.builder().build();
            Long chemTypeId = chemTypeService.getChemTypes().stream()
                    .filter(chemType -> chemType.getName().equals(LabAdminTestUtils.SOLID_COMPOUND_NAME))
                    .findAny()
                    .get()
                    .getId();
            input.setChemTypeId(chemTypeId);
            shelfLifeService.createShelfLife(input, AccountManagerTestUtils.BETA_LAB_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateShelfLife_whenChemTypeIdDoesNotExist_gotResourceNotFoundException() {
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            ShelfLifeInput input = LabAdminTestUtils.getSolidForBetaInput();
            input.setChemTypeId((long)Integer.MAX_VALUE);
            shelfLifeService.createShelfLife(input, AccountManagerTestUtils.BETA_LAB_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateShelfLife_whenChemTypeIdNull_gotException() {
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            ShelfLifeInput input = LabAdminTestUtils.getSolidForBetaInput();
            input.setChemTypeId(null);
            shelfLifeService.createShelfLife(input, AccountManagerTestUtils.BETA_LAB_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateShelfLife_whenLabKeyDoesNotExist_gotResourceNotFoundException() {
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            ShelfLifeInput input = LabAdminTestUtils.getSolidForBetaInput();
            Long chemTypeId = chemTypeService.getChemTypes().stream()
                    .filter(chemType -> chemType.getName().equals(LabAdminTestUtils.SOLID_COMPOUND_NAME))
                    .findAny()
                    .get()
                    .getId();
            input.setChemTypeId(chemTypeId);
            input.setLabKey("non-existing-lab-key");
            shelfLifeService.createShelfLife(input, AccountManagerTestUtils.BETA_LAB_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateShelfLife_whenUserNeitherAdminNorManager_gotValidationException() {
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            ShelfLifeInput input = LabAdminTestUtils.getSolidForBetaInput();
            Long chemTypeId = chemTypeService.getChemTypes().stream()
                    .filter(chemType -> chemType.getName().equals(LabAdminTestUtils.SOLID_COMPOUND_NAME))
                    .findAny()
                    .get()
                    .getId();
            input.setChemTypeId(chemTypeId);
            shelfLifeService.createShelfLife(input, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateShelfLife_whenDurationUnitIsNotValid_gotValidationException() {
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            ShelfLifeInput input = LabAdminTestUtils.getSolidForBetaInput();
            Long chemTypeId = chemTypeService.getChemTypes().stream()
                    .filter(chemType -> chemType.getName().equals(LabAdminTestUtils.SOLID_COMPOUND_NAME))
                    .findAny()
                    .get()
                    .getId();
            input.setUnit("not-valid-unit");
            input.setChemTypeId(chemTypeId);
            shelfLifeService.createShelfLife(input, AccountManagerTestUtils.BETA_LAB_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateShelfLife_whenAlreadyExistsForLabAndChemType_gotValidationException() {
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            ShelfLifeInput input = LabAdminTestUtils.getSolidForBetaInput();
            Long chemTypeId = chemTypeService.getChemTypes().stream()
                    .filter(chemType -> chemType.getName().equals(LabAdminTestUtils.BUFFER_SOLUTION_NAME))
                    .findAny()
                    .get()
                    .getId();
            input.setChemTypeId(chemTypeId);
            shelfLifeService.createShelfLife(input, AccountManagerTestUtils.BETA_LAB_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
        Assertions.assertTrue(exception.getMessage().contains("exists"));
    }

    //UPDATE
    @Test
    @Rollback
    @Transactional
    public void testUpdateShelfLife_whenAllValid_gotNoException() {
        ChemType buffer = chemTypeService.getChemTypes().stream()
                .filter(chemType -> chemType.getName().equals(LabAdminTestUtils.BUFFER_SOLUTION_NAME))
                .findAny()
                .get();
        Lab betaLab = labService.findLabByKey(AccountManagerTestUtils.BETA_LAB_KEY);
        shelfLifeService.getShelfLifes(false).forEach(shelfLife -> logger.info("Shelf life for " + shelfLife.getChemType().getName() + " for " + shelfLife.getLab().getKey()));
        ShelfLife solidForBeta = shelfLifeService.findByLabAndChemType(betaLab, buffer).get();
        ShelfLifeInput input = LabAdminTestUtils.getSolidForBetaInput();
        input.setChemTypeId(solidForBeta.getChemType().getId());
        input.setAmount(1);
        input.setUnit("d");
        shelfLifeService.updateShelfLife(input, solidForBeta.getId(), AccountManagerTestUtils.BETA_LAB_MANAGER_PRINCIPAL);
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateShelfLife_whenAllValid_savedValuesAsExpected() {
        ChemType buffer = chemTypeService.getChemTypes().stream()
                .filter(chemType -> chemType.getName().equals(LabAdminTestUtils.BUFFER_SOLUTION_NAME))
                .findAny()
                .get();
        Lab betaLab = labService.findLabByKey(AccountManagerTestUtils.BETA_LAB_KEY);
        shelfLifeService.getShelfLifes(false).forEach(shelfLife -> logger.info("Shelf life for " + shelfLife.getChemType().getName() + " for " + shelfLife.getLab().getKey()));
        ShelfLife solidForBeta = shelfLifeService.findByLabAndChemType(betaLab, buffer).get();
        ShelfLifeInput input = LabAdminTestUtils.getSolidForBetaInput();
        input.setChemTypeId(solidForBeta.getChemType().getId());
        input.setAmount(1);
        input.setUnit("d");
        shelfLifeService.updateShelfLife(input, solidForBeta.getId(), AccountManagerTestUtils.BETA_LAB_MANAGER_PRINCIPAL);
        ShelfLife persisted = shelfLifeService.findById(solidForBeta.getId());
        Assertions.assertEquals(input.getLabKey(), persisted.getLab().getKey());
        Assertions.assertEquals(input.getChemTypeId(), persisted.getChemType().getId());
        Assertions.assertEquals(Duration.between(LocalDateTime.now(), LocalDateTime.now().plusDays(1)).toHours(), persisted.getDuration().toHours());
    }


    @Test
    @Rollback
    @Transactional
    public void testUpdateShelfLife_whenEmptyInput_gotException() {
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            ChemType buffer = chemTypeService.getChemTypes().stream()
                    .filter(chemType -> chemType.getName().equals(LabAdminTestUtils.BUFFER_SOLUTION_NAME))
                    .findAny()
                    .get();
            Lab betaLab = labService.findLabByKey(AccountManagerTestUtils.BETA_LAB_KEY);
            shelfLifeService.getShelfLifes(false).forEach(shelfLife -> logger.info("Shelf life for " + shelfLife.getChemType().getName() + " for " + shelfLife.getLab().getKey()));
            ShelfLife solidForBeta = shelfLifeService.findByLabAndChemType(betaLab, buffer).get();
            ShelfLifeInput input = ShelfLifeInput.builder().build();
            shelfLifeService.updateShelfLife(input, solidForBeta.getId(), AccountManagerTestUtils.BETA_LAB_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateShelfLife_whenChemTypeIdDoesNotExist_gotResourceNotFoundException() {
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            ChemType buffer = chemTypeService.getChemTypes().stream()
                    .filter(chemType -> chemType.getName().equals(LabAdminTestUtils.BUFFER_SOLUTION_NAME))
                    .findAny()
                    .get();
            Lab betaLab = labService.findLabByKey(AccountManagerTestUtils.BETA_LAB_KEY);
            shelfLifeService.getShelfLifes(false).forEach(shelfLife -> logger.info("Shelf life for " + shelfLife.getChemType().getName() + " for " + shelfLife.getLab().getKey()));
            ShelfLife solidForBeta = shelfLifeService.findByLabAndChemType(betaLab, buffer).get();
            ShelfLifeInput input = LabAdminTestUtils.getSolidForBetaInput();
            input.setChemTypeId((long)Integer.MAX_VALUE);
            input.setAmount(1);
            input.setUnit("d");
            shelfLifeService.updateShelfLife(input, solidForBeta.getId(), AccountManagerTestUtils.BETA_LAB_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateShelfLife_whenLabKeyDoesNotExist_gotResourceNotFoundException() {
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            ChemType buffer = chemTypeService.getChemTypes().stream()
                    .filter(chemType -> chemType.getName().equals(LabAdminTestUtils.BUFFER_SOLUTION_NAME))
                    .findAny()
                    .get();
            Lab betaLab = labService.findLabByKey(AccountManagerTestUtils.BETA_LAB_KEY);
            shelfLifeService.getShelfLifes(false).forEach(shelfLife -> logger.info("Shelf life for " + shelfLife.getChemType().getName() + " for " + shelfLife.getLab().getKey()));
            ShelfLife solidForBeta = shelfLifeService.findByLabAndChemType(betaLab, buffer).get();
            ShelfLifeInput input = LabAdminTestUtils.getSolidForBetaInput();
            input.setChemTypeId(buffer.getId());
            input.setLabKey("non-existing-labKey");
            input.setAmount(1);
            input.setUnit("d");
            shelfLifeService.updateShelfLife(input, solidForBeta.getId(), AccountManagerTestUtils.BETA_LAB_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateShelfLife_whenUserNeitherAdminNorManager_gotValidationException() {
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            ChemType buffer = chemTypeService.getChemTypes().stream()
                    .filter(chemType -> chemType.getName().equals(LabAdminTestUtils.BUFFER_SOLUTION_NAME))
                    .findAny()
                    .get();
            Lab betaLab = labService.findLabByKey(AccountManagerTestUtils.BETA_LAB_KEY);
            shelfLifeService.getShelfLifes(false).forEach(shelfLife -> logger.info("Shelf life for " + shelfLife.getChemType().getName() + " for " + shelfLife.getLab().getKey()));
            ShelfLife solidForBeta = shelfLifeService.findByLabAndChemType(betaLab, buffer).get();
            ShelfLifeInput input = LabAdminTestUtils.getSolidForBetaInput();
            input.setChemTypeId(buffer.getId());
            input.setAmount(1);
            input.setUnit("d");
            shelfLifeService.updateShelfLife(input, solidForBeta.getId(), AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateShelfLife_whenDurationUnitIsNotValid_gotValidationException() {
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            ChemType buffer = chemTypeService.getChemTypes().stream()
                    .filter(chemType -> chemType.getName().equals(LabAdminTestUtils.BUFFER_SOLUTION_NAME))
                    .findAny()
                    .get();
            Lab betaLab = labService.findLabByKey(AccountManagerTestUtils.BETA_LAB_KEY);
            logger.info("Size of shelfLifeService.getShelfLifes(): " + shelfLifeService.getShelfLifes());
            shelfLifeService.getShelfLifes(false).forEach(shelfLife -> logger.info("Shelf life for " + shelfLife.getChemType().getName() + " for " + shelfLife.getLab().getKey()));
            ShelfLife solidForBeta = shelfLifeService.findByLabAndChemType(betaLab, buffer).get();
            ShelfLifeInput input = LabAdminTestUtils.getSolidForBetaInput();
            input.setChemTypeId(buffer.getId());
            input.setAmount(1);
            input.setUnit("e");
            shelfLifeService.updateShelfLife(input, solidForBeta.getId(), AccountManagerTestUtils.BETA_LAB_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateShelfLife_whenAlreadyExistsForLabAndChemType_gotValidationException() {
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            ChemType buffer = chemTypeService.getChemTypes().stream()
                    .filter(chemType -> chemType.getName().equals(LabAdminTestUtils.BUFFER_SOLUTION_NAME))
                    .findAny()
                    .get();
            ChemType solid = chemTypeService.getChemTypes().stream()
                    .filter(chemType -> chemType.getName().equals(LabAdminTestUtils.SOLID_COMPOUND_NAME))
                    .findAny()
                    .get();
            Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
            shelfLifeService.getShelfLifes(false).forEach(shelfLife -> logger.info("Shelf life for " + shelfLife.getChemType().getName() + " for " + shelfLife.getLab().getKey()));
            ShelfLife bufferForAlpha = shelfLifeService.findByLabAndChemType(alphaLab, buffer).get();
            ShelfLifeInput input = LabAdminTestUtils.getSolidForAlphaInput();
            input.setChemTypeId(buffer.getId());
            input.setAmount(1);
            input.setUnit("d");
            shelfLifeService.updateShelfLife(input, bufferForAlpha.getId(), AccountManagerTestUtils.BETA_LAB_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testFindSHelfLifeById_whenIdValid_gotNoException() {
        ChemType buffer = chemTypeService.findByName(LabAdminTestUtils.BUFFER_SOLUTION_NAME).get();
        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        ShelfLife bufferForAlpha = shelfLifeService.findByLabAndChemType(alphaLab, buffer).get();
        ShelfLife foundById = shelfLifeService.findById(bufferForAlpha.getId());
    }

    @Test
    @Rollback
    @Transactional
    public void testFindSHelfLifeById_whenIdValid_gotWhatExpected() {
        ChemType buffer = chemTypeService.findByName(LabAdminTestUtils.BUFFER_SOLUTION_NAME).get();
        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        ShelfLife bufferForAlpha = shelfLifeService.findByLabAndChemType(alphaLab, buffer).get();
        ShelfLife foundById = shelfLifeService.findById(bufferForAlpha.getId());
        Assertions.assertEquals(bufferForAlpha.getLab(), foundById.getLab());
        Assertions.assertEquals(bufferForAlpha.getChemType(), foundById.getChemType());
        Assertions.assertEquals(bufferForAlpha.getDuration().toHours(), foundById.getDuration().toHours());
    }

    @Test
    @Rollback
    @Transactional
    public void testFindSHelfLifeById_whenIdNotValid_gotResourceNotFoundException() {
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            shelfLifeService.findById((long)Integer.MAX_VALUE);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testFindShelfLifeById_whenDeleted_gotResourceNotFoundException() {
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            Long deletedId = shelfLifeService.getShelfLifes(false).stream().filter(ShelfLife::getDeleted).findAny().get().getId();
            shelfLifeService.findById(deletedId);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testFindSHelfLifeByIdActiveTrue_whenIdValid_gotNoException() {
        ChemType buffer = chemTypeService.findByName(LabAdminTestUtils.BUFFER_SOLUTION_NAME).get();
        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        ShelfLife bufferForAlpha = shelfLifeService.findByLabAndChemType(alphaLab, buffer).get();
        ShelfLife foundById = shelfLifeService.findById(bufferForAlpha.getId(), true);
    }

    @Test
    @Rollback
    @Transactional
    public void testFindSHelfLifeById_whenIdValidActiveTrue_gotWhatExpected() {
        ChemType buffer = chemTypeService.findByName(LabAdminTestUtils.BUFFER_SOLUTION_NAME).get();
        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        ShelfLife bufferForAlpha = shelfLifeService.findByLabAndChemType(alphaLab, buffer).get();
        ShelfLife foundById = shelfLifeService.findById(bufferForAlpha.getId(), true);
        Assertions.assertEquals(bufferForAlpha.getLab(), foundById.getLab());
        Assertions.assertEquals(bufferForAlpha.getChemType(), foundById.getChemType());
        Assertions.assertEquals(bufferForAlpha.getDuration().toHours(), foundById.getDuration().toHours());
    }

    @Test
    @Rollback
    @Transactional
    public void testFindSHelfLifeByIdActiveTrue_whenIdNotValid_gotResourceNotFoundException() {
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            shelfLifeService.findById((long)Integer.MAX_VALUE, true);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testFindShelfLifeByIdActiveTrue_whenDeleted_gotResourceNotFoundException() {
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            Long deletedId = shelfLifeService.getShelfLifes(false).stream().filter(ShelfLife::getDeleted).findAny().get().getId();
            shelfLifeService.findById(deletedId, true);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testFindSHelfLifeByIdActiveFalse_whenIdValid_gotNoException() {
        ChemType buffer = chemTypeService.findByName(LabAdminTestUtils.BUFFER_SOLUTION_NAME).get();
        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        ShelfLife bufferForAlpha = shelfLifeService.findByLabAndChemType(alphaLab, buffer).get();
        ShelfLife foundById = shelfLifeService.findById(bufferForAlpha.getId(), false);
    }

    @Test
    @Rollback
    @Transactional
    public void testFindSHelfLifeById_whenIdValidActiveFalse_gotWhatExpected() {
        ChemType buffer = chemTypeService.findByName(LabAdminTestUtils.BUFFER_SOLUTION_NAME).get();
        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        ShelfLife bufferForAlpha = shelfLifeService.findByLabAndChemType(alphaLab, buffer).get();
        ShelfLife foundById = shelfLifeService.findById(bufferForAlpha.getId(), false);
        Assertions.assertEquals(bufferForAlpha.getLab(), foundById.getLab());
        Assertions.assertEquals(bufferForAlpha.getChemType(), foundById.getChemType());
        Assertions.assertEquals(bufferForAlpha.getDuration().toHours(), foundById.getDuration().toHours());
    }

    @Test
    @Rollback
    @Transactional
    public void testFindSHelfLifeByIdActiveFalse_whenIdNotValid_gotResourceNotFoundException() {
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            shelfLifeService.findById((long)Integer.MAX_VALUE, false);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testFindShelfLifeByIdActiveFalse_whenDeleted_gotWhatExpected() {
        ShelfLife deletedShelfLife = shelfLifeService.getShelfLifes(false).stream().filter(ShelfLife::getDeleted).findAny().get();
        ShelfLife found = shelfLifeService.findById(deletedShelfLife.getId(), false);
        Assertions.assertEquals(deletedShelfLife.getLab(), found.getLab());
        Assertions.assertEquals(deletedShelfLife.getChemType(), found.getChemType());
        Assertions.assertEquals(deletedShelfLife.getDuration().toHours(), found.getDuration().toHours());
    }

    @Test
    @Rollback
    @Transactional
    public void testFindShelfLifeByLabAndChemType_whenIdValid_gotNoException() {
        ChemType buffer = chemTypeService.findByName(LabAdminTestUtils.BUFFER_SOLUTION_NAME).get();
        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        shelfLifeService.findByLabAndChemType(alphaLab, buffer).get();
    }

    @Test
    @Rollback
    @Transactional
    public void testFindShelfLifeByLabAndChemType_whenIdValid_gotWhatExpected() {
        ChemType buffer = chemTypeService.findByName(LabAdminTestUtils.BUFFER_SOLUTION_NAME).get();
        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        ShelfLife bufferForAlpha = shelfLifeService.findByLabAndChemType(alphaLab, buffer).get();
        ShelfLife foundById = shelfLifeRepositoy.findById(bufferForAlpha.getId()).get();
        Assertions.assertEquals(foundById.getLab(), bufferForAlpha.getLab());
        Assertions.assertEquals(foundById.getChemType(), bufferForAlpha.getChemType());
        Assertions.assertEquals(foundById.getDuration().toHours(), bufferForAlpha.getDuration().toHours());
    }

    @Test
    @Rollback
    @Transactional
    public void testFindShelfLifeByLabAndChemType_whenChemTypeNotValid_gotResourceNotFoundException() {
        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        Assertions.assertTrue(shelfLifeService.findByLabAndChemType(alphaLab, getNonexistingChemType()).isEmpty());
    }

    @Test
    @Rollback
    @Transactional
    public void testFindShelfLifeByLabAndChemType_whenLabNotValid_gotResourceNotFoundException() {
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            ChemType buffer = chemTypeService.findByName(LabAdminTestUtils.BUFFER_SOLUTION_NAME).get();
            Assertions.assertTrue(shelfLifeService.findByLabAndChemType(getNonexistingLab(), buffer).isEmpty());
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());

    }

    @Test
    @Rollback
    @Transactional
    public void testFindShelfLifeByLabAndChemType_whenLabAmdChemTypeNotExisting_gotResourceNotFoundException() {
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            Assertions.assertTrue(shelfLifeService.findByLabAndChemType(getNonexistingLab(), getNonexistingChemType()).isEmpty());
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testFindShelfLifeByLabAndChemType_whenNoHit_gotEmptyOptional() {
        ChemType waterChemType = chemTypeService.findByName(LabAdminTestUtils.WATER_CHEM_TYPE_NAME).get();
        Lab betaLab = labService.findLabByKey(AccountManagerTestUtils.BETA_LAB_KEY);
        Assertions.assertTrue(shelfLifeService.findByLabAndChemType(betaLab, waterChemType).isEmpty());
    }

    @Test
    @Rollback
    @Transactional
    public void testFindShelfLifeByLabAndChemType_whenDeleted_gotWhatExpected() {
        ShelfLife deletedShelfLife = shelfLifeService.getShelfLifes(false).stream().filter(ShelfLife::getDeleted).findAny().get();
        ShelfLife found = shelfLifeService.findByLabAndChemType(deletedShelfLife.getLab(), deletedShelfLife.getChemType()).get();
        Assertions.assertEquals(deletedShelfLife.getId(), found.getId());
        Assertions.assertTrue(found.getDeleted());
    }

    @Test
    @Rollback
    @Transactional
    public void testFindShelfLifeByLab_whenLabValid_gotNoException() {
        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        shelfLifeService.findByLab(alphaLab.getKey(), AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);

    }

    @Test
    @Rollback
    @Transactional
    public void testFindShelfLifeByLab_whenLabValid_gotWhatExpected() {
        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        List<ShelfLife> shelfLifesForAlphaLab = shelfLifeService.findByLab(alphaLab.getKey(), AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
        Assertions.assertTrue(!shelfLifesForAlphaLab.isEmpty());
        shelfLifesForAlphaLab.forEach(shelfLife -> {
            Assertions.assertTrue(!shelfLife.getDeleted());
            Assertions.assertEquals(alphaLab, shelfLife.getLab());
        });
    }

    @Test
    @Rollback
    @Transactional
    public void testFindShelfLifeByLab_whenLabNonValid_gotResourceNotFoundException() {
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            shelfLifeService.findByLab("non-existing-key", AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testFindShelfLifeByLabOATrue_whenLabValid_gotNoException() {
        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        shelfLifeService.findByLab(alphaLab.getKey(), true, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);

    }

    @Test
    @Rollback
    @Transactional
    public void testFindShelfLifeByLabOATrue_whenLabValid_gotWhatExpected() {
        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        List<ShelfLife> shelfLifesForAlphaLab = shelfLifeService.findByLab(alphaLab.getKey(), true, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
        Assertions.assertTrue(!shelfLifesForAlphaLab.isEmpty());
        shelfLifesForAlphaLab.forEach(shelfLife -> {
            Assertions.assertTrue(!shelfLife.getDeleted());
            Assertions.assertEquals(alphaLab, shelfLife.getLab());
        });
    }

    @Test
    @Rollback
    @Transactional
    public void testFindShelfLifeByLabOATrue_whenLabNonValid_gotResourceNotFoundException() {
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            shelfLifeService.findByLab("non-existing-key", true, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testFindShelfLifeByLabOAFalse_whenLabValid_gotNoException() {
        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        shelfLifeService.findByLab(alphaLab.getKey(), false, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);

    }

    @Test
    @Rollback
    @Transactional
    public void testFindShelfLifeByLabOAFalse_whenLabValid_gotWhatExpected() {
        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        List<ShelfLife> shelfLifesForAlphaLab = shelfLifeService.findByLab(alphaLab.getKey(), false, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
        Assertions.assertTrue(!shelfLifesForAlphaLab.isEmpty());
        shelfLifesForAlphaLab.forEach(shelfLife -> {
            Assertions.assertEquals(alphaLab, shelfLife.getLab());
        });
    }

    @Test
    @Rollback
    @Transactional
    public void testFindShelfLifeByLabOAFalse_whenLabValid_gotDeletedItem() {
        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        List<ShelfLife> shelfLifesForAlphaLab = shelfLifeService.findByLab(alphaLab.getKey(), false, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
        Assertions.assertTrue(shelfLifesForAlphaLab.stream().anyMatch(ShelfLife::getDeleted));
    }

    @Test
    @Rollback
    @Transactional
    public void testFindShelfLifeByLabOAFalse_whenLabNonValid_gotResourceNotFoundException() {
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            shelfLifeService.findByLab("non-existing-key", false, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testGetShelfLifes_gotNoException() {
        shelfLifeService.getShelfLifes();
    }

    @Test
    @Rollback
    @Transactional
    public void testGetShelfLifes_gotWhatExpected() {
        List<ShelfLife> shelfLifes = shelfLifeService.getShelfLifes();
        Assertions.assertTrue(!shelfLifes.isEmpty());
        shelfLifes.forEach(shelfLife -> {
            Assertions.assertTrue(!shelfLife.getDeleted());
        });
    }

    @Test
    @Rollback
    @Transactional
    public void testGetShelfLifesOATrue_gotNoException() {
        shelfLifeService.getShelfLifes(true);
    }

    @Test
    @Rollback
    @Transactional
    public void testGetShelfLifesOATrue_gotWhatExpected() {
        List<ShelfLife> shelfLifes = shelfLifeService.getShelfLifes(true);
        Assertions.assertTrue(!shelfLifes.isEmpty());
        shelfLifes.forEach(shelfLife -> {
            Assertions.assertTrue(!shelfLife.getDeleted());
        });
    }

    @Test
    @Rollback
    @Transactional
    public void testGetShelfLifesOAFalse_gotNoException() {
        shelfLifeService.getShelfLifes(false);
    }

    @Test
    @Rollback
    @Transactional
    public void testGetShelfLifesOAFalse_gotDeletedItem() {
        List<ShelfLife> shelfLifes = shelfLifeService.getShelfLifes(false);
        shelfLifes.forEach(shelfLife -> logger.info("lab key: " + shelfLife.getLab().getKey() + ", chem type: " + shelfLife.getChemType().getName()));
        Assertions.assertTrue(shelfLifes.stream().anyMatch(ShelfLife::getDeleted));
    }

    //DELETE
    @Test
    @Rollback
    @Transactional
    public void testDeleteShelfLife_whenIdValid_gotNoException() {
        ChemType buffer = chemTypeService.findByName(LabAdminTestUtils.BUFFER_SOLUTION_NAME).get();
        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        ShelfLife bufferForAlpha = shelfLifeService.findByLabAndChemType(alphaLab, buffer).get();
        shelfLifeService.deleteShelfLife(bufferForAlpha.getId(), AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
    }

    @Test
    @Rollback
    @Transactional
    public void testDeleteShelfLife_whenIdValid_notFoundAfterwards() {
        ChemType buffer = chemTypeService.findByName(LabAdminTestUtils.BUFFER_SOLUTION_NAME).get();
        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        ShelfLife bufferForAlpha = shelfLifeService.findByLabAndChemType(alphaLab, buffer).get();
        shelfLifeService.deleteShelfLife(bufferForAlpha.getId(), AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            shelfLifeService.findById(bufferForAlpha.getId());
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testDeleteShelfLife_whenIdDoesNotExist_gotResourceNotFoudnException() {
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            shelfLifeService.deleteShelfLife((long)Integer.MAX_VALUE, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testDeleteShelfLife_whenAdminHasNoRightToSHelfLife_gotValidationException() {
        ChemType buffer = chemTypeService.findByName(LabAdminTestUtils.BUFFER_SOLUTION_NAME).get();
        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        ShelfLife bufferForAlpha = shelfLifeService.findByLabAndChemType(alphaLab, buffer).get();
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            shelfLifeService.deleteShelfLife(bufferForAlpha.getId(), AccountManagerTestUtils.BETA_LAB_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }






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
