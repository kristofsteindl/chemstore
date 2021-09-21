package com.ksteindl.chemstore.web;

import com.ksteindl.chemstore.BaseControllerTest;
import com.ksteindl.chemstore.domain.entities.ChemItem;
import com.ksteindl.chemstore.domain.input.ChemItemInput;
import com.ksteindl.chemstore.service.ChemItemService;
import com.ksteindl.chemstore.service.ManufacturerService;
import com.ksteindl.chemstore.utils.ChemItemTestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ChemItemControllerTest extends BaseControllerTest {

    private static final Logger logger = LogManager.getLogger(ChemItemControllerTest.class);

    @Autowired
    private MockMvc mvc;@Autowired
    private ManufacturerService manufacturerService;


    private final String BASE_URL = "/api/chem-controller";

    //CHEM ITEM
    //CREATE
    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withAlphaLabUser_got201() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().isCreated())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withAlphaLabAdmin_got201() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().isCreated())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withAlphaLabManager_got201() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().isCreated())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withAccountManager_got403() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().is(403))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withBetaLabUser_got400() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withBetaLabAdmin_got400() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withBetaLabManager_got400() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withAlphaLabUser_fetchedFromDbIsTheSameAsInput(@Autowired ChemItemService chemItemService) throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().isCreated())
                .andReturn();
        JSONObject testChemItemJSONObject = new JSONObject(result.getResponse().getContentAsString());
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
        ChemItem fetchedChemItem = chemItemService.findById(testChemItemJSONObject.getLong("id"));
        Assertions.assertEquals(testChemItemInput.getLabKey(), testChemItemJSONObject.getString("labKey"));
        Assertions.assertEquals(testChemItemInput.getArrivalDate(), testChemItemJSONObject.getString("arrivalDate"));
        Assertions.assertEquals(testChemItemInput.getBatchNumber(), testChemItemJSONObject.getString("batchNumber"));
        Assertions.assertEquals(testChemItemInput.getChemicalShortName(), testChemItemJSONObject.getJSONObject("chemical").getString("shortName"));
        Assertions.assertEquals(testChemItemInput.getManufacturerId(), testChemItemJSONObject.getJSONObject("manufacturer").getString("name"));
        Assertions.assertEquals(testChemItemInput.getExpirationDateBeforeOpened(), testChemItemJSONObject.getString("expirationDateBeforeOpened"));
        Assertions.assertEquals(testChemItemInput.getQuantity(), testChemItemJSONObject.getDouble("quantity"));
        Assertions.assertEquals(testChemItemInput.getUnit(), testChemItemJSONObject.getString("unit"));
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withEmptyInput1_got400() throws Exception {
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withEmptyInput2_got400() throws Exception {
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withEmptyLabKey1_got400() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        testChemItemInput.setLabKey(null);
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withEmptyLabKey2_got400() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        testChemItemInput.setLabKey("");
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withNonExistingLabKey_got400() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        testChemItemInput.setLabKey("non-existing");
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withEmptyArrivalDate1_got400() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        testChemItemInput.setArrivalDate(null);
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withEmptyArrivalDate2_got400() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        JSONObject jsonObject = new JSONObject(asJsonString(testChemItemInput)).put("arrivalDate", "");
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(jsonObject)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }


    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withInvalidArrivalDate_got400() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        JSONObject jsonObject = new JSONObject(asJsonString(testChemItemInput)).put("arrivalDate", "2050-05");
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(jsonObject)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withEmptyChemicalShortName1_got400() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        testChemItemInput.setChemicalShortName(null);
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withEmptyChemicalShortName2_got400() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        testChemItemInput.setChemicalShortName("");
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withNonExistingChemicalShortName_got400() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        testChemItemInput.setChemicalShortName("non-existing");
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withEmptyManufacturerId1_got400() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        testChemItemInput.setManufacturerId(null);
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }


    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withNonExistingManufacturerName_got400() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        testChemItemInput.setManufacturerId((long)Integer.MAX_VALUE);
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withEmptyBatchNumber1_got400() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        testChemItemInput.setBatchNumber(null);
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withEmptyBatchNumber2_got400() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        testChemItemInput.setBatchNumber("");
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withEmptyQuantity1_got400() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        testChemItemInput.setQuantity(null);
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withEmptyQuantity2_got400() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        JSONObject jsonObject = new JSONObject(asJsonString(testChemItemInput)).put("quantity", "");
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(jsonObject)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withStringQuantity_got400() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        JSONObject jsonObject = new JSONObject(asJsonString(testChemItemInput)).put("quantity", "this-is-not-a-number");
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(jsonObject)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }


    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withZeroQuantity1_got400() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        testChemItemInput.setQuantity(0.0);
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withMinus1Quantity1_got400() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        testChemItemInput.setQuantity(-1.0);
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withEmptyUnit1_got400() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        testChemItemInput.setUnit(null);
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withEmptyUnit2_got400() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        testChemItemInput.setUnit("");
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withEmptyExpirationDateBeforeOpened1_got400() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        testChemItemInput.setExpirationDateBeforeOpened(null);
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withEmptyExpirationDateBeforeOpened2_got400() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        JSONObject jsonObject = new JSONObject(asJsonString(testChemItemInput)).put("expirationDateBeforeOpened", "");
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(jsonObject)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }


    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withInvalidExpirationDateBeforeOpened_got400() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        JSONObject jsonObject = new JSONObject(asJsonString(testChemItemInput)).put("expirationDateBeforeOpened", "2050-05");
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(jsonObject)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

}
