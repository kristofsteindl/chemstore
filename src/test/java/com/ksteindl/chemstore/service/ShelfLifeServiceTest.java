package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.domain.entities.ChemType;
import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.domain.entities.ShelfLife;
import com.ksteindl.chemstore.domain.input.*;
import com.ksteindl.chemstore.BaseControllerTest;
import com.ksteindl.chemstore.exceptions.ResourceNotFoundException;
import com.ksteindl.chemstore.exceptions.ValidationException;
import com.ksteindl.chemstore.utils.AccountManagerTestUtils;
import com.ksteindl.chemstore.utils.LabAdminTestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ShelfLifeServiceTest extends BaseControllerTest{

    private static final Logger logger = LogManager.getLogger(ShelfLifeServiceTest.class);

    @Autowired
    private ShelfLifeService shelfLifeService;
    @Autowired
    private ChemTypeService chemTypeService;
    @Autowired
    private LabService labService;

    // CREATE
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
    public void testFindSHelfLifeById_whenDeleted_gotResourceNotFoundException() {
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            Long deletedId = shelfLifeService.getShelfLifes(false).stream().filter(ShelfLife::getDeleted).findAny().get().getId();
            shelfLifeService.findById(deletedId);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }



}
