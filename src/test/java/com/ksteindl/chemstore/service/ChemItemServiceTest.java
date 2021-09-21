package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.BaseControllerTest;
import com.ksteindl.chemstore.domain.entities.ChemItem;
import com.ksteindl.chemstore.domain.input.ChemItemInput;
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

    @Test
    @Rollback
    @Transactional
    public void testCreateChemItem_whenAllGood_gotNoException() {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        chemItemService.createChemItems(testChemItemInput, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateChemItem_whenAllGood_gotListWithRightSize() {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        List<ChemItem> chemItems = chemItemService.createChemItems(testChemItemInput, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
        Assertions.assertEquals(testChemItemInput.getAmount(), chemItems.size());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateChemItem_whenAllGood_gotExpectedAttributes() {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        List<ChemItem> chemItems = chemItemService.createChemItems(testChemItemInput, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
        ChemItem chemItem = chemItems.get(0);
        Assertions.assertEquals(testChemItemInput.getBatchNumber(), chemItem.getBatchNumber());
        Assertions.assertEquals(testChemItemInput.getQuantity(), chemItem.getQuantity());
        Assertions.assertEquals(testChemItemInput.getExpirationDateBeforeOpened(), chemItem.getExpirationDateBeforeOpened());
        Assertions.assertEquals(testChemItemInput.getArrivalDate(), chemItem.getArrivalDate());
        Assertions.assertEquals(testChemItemInput.getChemicalShortName(), chemItem.getChemical().getShortName());
        Assertions.assertEquals(testChemItemInput.getLabKey(), chemItem.getLab().getKey());
        Assertions.assertEquals(testChemItemInput.getManufacturerId(), chemItem.getManufacturer().getId());
    }
}
