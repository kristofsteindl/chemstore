package com.ksteindl.chemstore.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksteindl.chemstore.BaseControllerTest;
import com.ksteindl.chemstore.domain.entities.ChemItem;
import com.ksteindl.chemstore.domain.input.ChemItemInput;
import com.ksteindl.chemstore.service.ChemItemService;
import com.ksteindl.chemstore.service.ManufacturerService;
import com.ksteindl.chemstore.utils.AccountManagerTestUtils;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ChemItemControllerTest extends BaseControllerTest {

    private static final Logger logger = LogManager.getLogger(ChemItemControllerTest.class);

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ManufacturerService manufacturerService;


    private final String BASE_URL = "/api/chem-item";
    private final String ALPHA_BASE_URL = BASE_URL + "/" + AccountManagerTestUtils.ALPHA_LAB_KEY;

    //CHEM ITEM
    //CREATE
    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withAlphaLabUser_got201() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        String stringInput = asJsonString(testChemItemInput);
        MvcResult result = mvc.perform(post(ALPHA_BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(stringInput))
                .andDo(MockMvcResultHandlers.print())
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
        MvcResult result = mvc.perform(post(ALPHA_BASE_URL)
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
        MvcResult result = mvc.perform(post(ALPHA_BASE_URL)
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
        MvcResult result = mvc.perform(post(ALPHA_BASE_URL)
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
        MvcResult result = mvc.perform(post(ALPHA_BASE_URL)
                        .header("Authorization", TOKEN_FOR_BETA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().is(403))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withBetaLabAdmin_got400() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        MvcResult result = mvc.perform(post(ALPHA_BASE_URL)
                        .header("Authorization", TOKEN_FOR_BETA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().is(403))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withBetaLabManager_got400() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        MvcResult result = mvc.perform(post(ALPHA_BASE_URL)
                        .header("Authorization", TOKEN_FOR_BETA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().is(403))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withAlphaLabUser_fetchedFromDbIsTheSameAsInput(@Autowired ChemItemService chemItemService) throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        MvcResult result = mvc.perform(post(ALPHA_BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().isCreated())
                .andReturn();
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map> chemItems = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
        ChemItem fetchedChemItem = chemItemService.findById((long)(Integer)chemItems.get(0).get("id"));
        Assertions.assertEquals(testChemItemInput.getBatchNumber(), chemItems.get(0).get("batchNumber"));
        Assertions.assertEquals(testChemItemInput.getArrivalDate().toString(), chemItems.get(0).get("arrivalDate"));
        Assertions.assertEquals(testChemItemInput.getBatchNumber(), chemItems.get(0).get("batchNumber"));
        Assertions.assertEquals(testChemItemInput.getExpirationDateBeforeOpened().toString(), chemItems.get(0).get("expirationDateBeforeOpened"));
        Assertions.assertEquals(testChemItemInput.getQuantity(), chemItems.get(0).get("quantity"));
        Assertions.assertEquals(testChemItemInput.getUnit(), chemItems.get(0).get("unit"));
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withEmptyInput1_got400() throws Exception {
        MvcResult result = mvc.perform(post(ALPHA_BASE_URL)
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
        MvcResult result = mvc.perform(post(ALPHA_BASE_URL)
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
    void testCreateChemItem_withEmptyLabKey_got400() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().is(404))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }


    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withNonExistingLabKey_got400() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        MvcResult result = mvc.perform(post(BASE_URL + "non-existing")
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().is(404))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withEmptyArrivalDate_got200() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        testChemItemInput.setArrivalDate(null);
        MvcResult result = mvc.perform(post(ALPHA_BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().is(201))
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
        MvcResult result = mvc.perform(post(ALPHA_BASE_URL)
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
        MvcResult result = mvc.perform(post(ALPHA_BASE_URL)
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
        MvcResult result = mvc.perform(post(ALPHA_BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().is(404))
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
        MvcResult result = mvc.perform(post(ALPHA_BASE_URL)
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
        MvcResult result = mvc.perform(post(ALPHA_BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().is(404))
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
        MvcResult result = mvc.perform(post(ALPHA_BASE_URL)
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
        MvcResult result = mvc.perform(post(ALPHA_BASE_URL)
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
        MvcResult result = mvc.perform(post(ALPHA_BASE_URL)
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
        MvcResult result = mvc.perform(post(ALPHA_BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObject.toString()))
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
        MvcResult result = mvc.perform(post(ALPHA_BASE_URL)
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
        MvcResult result = mvc.perform(post(ALPHA_BASE_URL)
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
        MvcResult result = mvc.perform(post(ALPHA_BASE_URL)
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
        MvcResult result = mvc.perform(post(ALPHA_BASE_URL)
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
        MvcResult result = mvc.perform(post(ALPHA_BASE_URL)
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
        MvcResult result = mvc.perform(post(ALPHA_BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObject.toString()))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withMinusAmount_got400() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput(manufacturerService);
        testChemItemInput.setAmount(-1);
        String stringInput = asJsonString(testChemItemInput);
        MvcResult result = mvc.perform(post(ALPHA_BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(stringInput))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }


}
