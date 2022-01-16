package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.BaseControllerTest;
import com.ksteindl.chemstore.domain.entities.ChemItem;
import com.ksteindl.chemstore.domain.entities.Manufacturer;
import com.ksteindl.chemstore.domain.input.ChemItemInput;
import com.ksteindl.chemstore.domain.repositories.ChemicalRepository;
import com.ksteindl.chemstore.exceptions.ForbiddenException;
import com.ksteindl.chemstore.exceptions.ResourceNotFoundException;
import com.ksteindl.chemstore.exceptions.ValidationException;
import com.ksteindl.chemstore.utils.AccountManagerTestUtils;
import com.ksteindl.chemstore.utils.ChemItemTestUtils;
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
import java.time.LocalDate;
import java.util.List;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ChemItemServiceTest extends BaseControllerTest {

    private static final Logger logger = LogManager.getLogger(ChemItemServiceTest.class);

    @Autowired
    private ChemItemService chemItemService;
    @Autowired
    private ManufacturerService manufacturerService;
    @Autowired
    private ChemicalService chemicalService;

    @Test
    @Rollback
    @Transactional
    public void testCreateChemItem_whenAllGood_gotNoException() {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        chemItemService.createChemItems(
                AccountManagerTestUtils.ALPHA_LAB_KEY,
                testChemItemInput,
                AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL);
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateChemItem_whenAllGood_gotListWithRightSize() {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        List<ChemItem> chemItems = chemItemService.createChemItems(
                AccountManagerTestUtils.ALPHA_LAB_KEY,
                testChemItemInput,
                AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
        Assertions.assertEquals(testChemItemInput.getAmount(), chemItems.size());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateChemItem_whenAllGood_gotExpectedAttributes() {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        List<ChemItem> chemItems = chemItemService.createChemItems(AccountManagerTestUtils.ALPHA_LAB_KEY, testChemItemInput, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
        ChemItem chemItem = chemItems.get(0);
        Assertions.assertEquals(testChemItemInput.getBatchNumber(), chemItem.getBatchNumber());
        Assertions.assertEquals(testChemItemInput.getQuantity(), chemItem.getQuantity());
        Assertions.assertEquals(testChemItemInput.getExpirationDateBeforeOpened(), chemItem.getExpirationDateBeforeOpened());
        Assertions.assertEquals(testChemItemInput.getArrivalDate(), chemItem.getArrivalDate());
        Assertions.assertEquals(testChemItemInput.getChemicalShortName(), chemItem.getChemical().getShortName());
        Assertions.assertEquals(AccountManagerTestUtils.ALPHA_LAB_KEY, chemItem.getLab().getKey());
        Assertions.assertEquals(testChemItemInput.getManufacturerId(), chemItem.getManufacturer().getId());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateChemItem_whenAllGood_fetchedDateAreExpected() {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        List<ChemItem> chemItems = chemItemService.createChemItems(
                AccountManagerTestUtils.ALPHA_LAB_KEY,
                testChemItemInput,
                AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
        Long id = chemItems.get(0).getId();
        ChemItem chemItem = chemItemService.findById(id);
        Assertions.assertEquals(testChemItemInput.getBatchNumber(), chemItem.getBatchNumber());
        Assertions.assertEquals(testChemItemInput.getQuantity(), chemItem.getQuantity());
        Assertions.assertEquals(testChemItemInput.getExpirationDateBeforeOpened(), chemItem.getExpirationDateBeforeOpened());
        Assertions.assertEquals(testChemItemInput.getArrivalDate(), chemItem.getArrivalDate());
        Assertions.assertEquals(testChemItemInput.getChemicalShortName(), chemItem.getChemical().getShortName());
        Assertions.assertEquals(AccountManagerTestUtils.ALPHA_LAB_KEY, chemItem.getLab().getKey());
        Assertions.assertEquals(testChemItemInput.getManufacturerId(), chemItem.getManufacturer().getId());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateChemItem_whenLabAdmin_gotNoException() {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        chemItemService.createChemItems(
                AccountManagerTestUtils.ALPHA_LAB_KEY,
                testChemItemInput,
                AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL);
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateChemItem_whenLabManager_gotNoException() {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        chemItemService.createChemItems(AccountManagerTestUtils.ALPHA_LAB_KEY,
                testChemItemInput,
                AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateChemItem_whenBetaLabUser_gotUnauthorizedException() {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            chemItemService.createChemItems(
                    AccountManagerTestUtils.ALPHA_LAB_KEY,
                    testChemItemInput,
                    AccountManagerTestUtils.BETA_LAB_USER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateChemItem_whenBetaLabAdmin_gotUnauthorizedException() {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            chemItemService.createChemItems(
                    AccountManagerTestUtils.ALPHA_LAB_KEY,
                    testChemItemInput,
                    AccountManagerTestUtils.BETA_LAB_ADMIN_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateChemItem_whenEmptyLabKey_gotResourceNotFoundException() {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            chemItemService.createChemItems(
                    "",
                    testChemItemInput,
                    AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateChemItem_whenLabKeyDoesNotEixists_gotResourceNotFoundException() {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            chemItemService.createChemItems(
                    "non-existing",
                    testChemItemInput,
                    AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateChemItem_whenArricalDateEmpty_gotNoException() {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        testChemItemInput.setArrivalDate(null);
        chemItemService.createChemItems(
                AccountManagerTestUtils.ALPHA_LAB_KEY,
                testChemItemInput,
                AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL);
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateChemItem_whenArrivalDateInTheFuture_gotValidationException() {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            testChemItemInput.setArrivalDate(LocalDate.now().plusDays(1));
            chemItemService.createChemItems(
                    AccountManagerTestUtils.ALPHA_LAB_KEY,
                    testChemItemInput,
                    AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateChemItem_whenChemicalShortNameDoesntExist_gotResourceNotFoundException() {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            testChemItemInput.setChemicalShortName("not-existing");
            chemItemService.createChemItems(
                    AccountManagerTestUtils.ALPHA_LAB_KEY,
                    testChemItemInput,
                    AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateChemItem_whenChemicalDeleted_gotResourceNotFoundException(@Autowired ChemicalRepository chemicalRepository) {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            testChemItemInput.setChemicalShortName("IPA");
            chemItemService.createChemItems(
                    AccountManagerTestUtils.ALPHA_LAB_KEY,
                    testChemItemInput,
                    AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateChemItem_whenManufacturerDoesntExist_gotResourceNotFoundException() {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            testChemItemInput.setManufacturerId((long)Integer.MAX_VALUE);
            chemItemService.createChemItems(
                    AccountManagerTestUtils.ALPHA_LAB_KEY,
                    testChemItemInput,
                    AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateChemItem_whenManufacturerDeleted_gotResourceNotFoundException() {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        Long deletedManufacturerId = manufacturerService.getManufacturers(false).stream()
                .filter(Manufacturer::getDeleted)
                .findAny()
                .get()
                .getId();
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            testChemItemInput.setManufacturerId(deletedManufacturerId);
            chemItemService.createChemItems(
                    AccountManagerTestUtils.ALPHA_LAB_KEY,
                    testChemItemInput,
                    AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateChemItem_whenExpDateIsThePast_gotValidationException() {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            testChemItemInput.setExpirationDateBeforeOpened(LocalDate.now().minusDays(1));
            chemItemService.createChemItems(
                    AccountManagerTestUtils.ALPHA_LAB_KEY,
                    testChemItemInput,
                    AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateChemItem_whenUnitIsNotValid_gotValidationException() {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            testChemItemInput.setUnit("non-valid");
            chemItemService.createChemItems(
                    AccountManagerTestUtils.ALPHA_LAB_KEY,
                    testChemItemInput,
                    AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }


    /*

    @Test
    @Rollback
    @Transactional
    public void testCreateChemItem_whenAllGood_gotNoException() {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        chemItemService.createChemItems(
                AccountManagerTestUtils.ALPHA_LAB_KEY,
                testChemItemInput,
                AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL);
    }

        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {

        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
     */


}
