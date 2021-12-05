package com.ksteindl.chemstore.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksteindl.chemstore.BaseControllerTest;
import com.ksteindl.chemstore.domain.entities.ChemicalCategory;
import com.ksteindl.chemstore.domain.entities.Manufacturer;
import com.ksteindl.chemstore.domain.input.ChemicalCategoryInput;
import com.ksteindl.chemstore.domain.input.ChemicalInput;
import com.ksteindl.chemstore.domain.input.ManufacturerInput;
import com.ksteindl.chemstore.service.ChemicalCategoryService;
import com.ksteindl.chemstore.service.ManufacturerService;
import com.ksteindl.chemstore.utils.AccountManagerTestUtils;
import com.ksteindl.chemstore.utils.LabAdminTestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.core.IsNot;
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
import org.springframework.test.util.AssertionErrors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;
import java.util.Optional;

import static com.ksteindl.chemstore.utils.AccountManagerTestUtils.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LabAdminControllerTest extends BaseControllerTest{

    private static final Logger logger = LogManager.getLogger(LabAdminControllerTest.class);

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ChemicalCategoryService categoryService;

    private final String BASE_URL = "/api/lab-admin";
    private final String MANUFACTURER_URL = BASE_URL + "/manufacturer";
    private final String CHEMICAL_URL = BASE_URL + "/chemical";
    private final String CATEGORY_URL = BASE_URL + "/chem-category";
    private final String CHANGED_ETHANOL_EXACT_NAME = "Changed ethanol exact name";




//CHEMICAL CATEGORY
    //CREATE
    @Test
    @Rollback
    @Transactional
    void testCreateCategory_whenAlphaLabAdmin_got201() throws Exception {
        ChemicalCategoryInput input = LabAdminTestUtils.getSolidForAlphaInput();
        MvcResult result = mvc.perform(post(CATEGORY_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().isCreated())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateCategory_whenAlphaLabManager_got201() throws Exception {
        ChemicalCategoryInput input = LabAdminTestUtils.getSolidForAlphaInput();
        MvcResult result = mvc.perform(post(CATEGORY_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().isCreated())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateCategory_whenBetaLabManager_got403() throws Exception {
        ChemicalCategoryInput input = LabAdminTestUtils.getSolidForAlphaInput();
        MvcResult result = mvc.perform(post(CATEGORY_URL)
                        .header("Authorization", TOKEN_FOR_BETA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(403))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateCategory_whenBetaLabAdmin_got403() throws Exception {
        ChemicalCategoryInput input = LabAdminTestUtils.getSolidForAlphaInput();
        MvcResult result = mvc.perform(post(CATEGORY_URL)
                        .header("Authorization", TOKEN_FOR_BETA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(403))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateCategory_whenAlphaLabUser_got403() throws Exception {
        ChemicalCategoryInput input = LabAdminTestUtils.getSolidForAlphaInput();
        MvcResult result = mvc.perform(post(CATEGORY_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(403))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateCategory_whenAccountManager_got403() throws Exception {
        ChemicalCategoryInput input = LabAdminTestUtils.getSolidForAlphaInput();
        MvcResult result = mvc.perform(post(CATEGORY_URL)
                        .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(403))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateCategory_whenNameMissing_got400() throws Exception {
        ChemicalCategoryInput input = LabAdminTestUtils.getSolidForAlphaInput();
        input.setName(null);
        MvcResult result = mvc.perform(post(CATEGORY_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andReturn();
        String response = result.getResponse().getContentAsString();
        Assertions.assertTrue(response.contains("name"));
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateCategory_whenNameBlank_got400() throws Exception {
        ChemicalCategoryInput input = LabAdminTestUtils.getSolidForAlphaInput();
        input.setName("");
        MvcResult result = mvc.perform(post(CATEGORY_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andReturn();
        String response = result.getResponse().getContentAsString();
        Assertions.assertTrue(response.contains("\"name\""));
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateCategory_whenNameAlreadyExists_got400() throws Exception {
        ChemicalCategoryInput input = LabAdminTestUtils.getSolidForAlphaInput();
        input.setName(LabAdminTestUtils.ORGANIC_CATEGORY);
        MvcResult result = mvc.perform(post(CATEGORY_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andReturn();
        String response = result.getResponse().getContentAsString();
        Assertions.assertTrue(response.contains("\"message\""));
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateCategory_whenEmptyInput1_got400() throws Exception {
        MvcResult result = mvc.perform(post(CATEGORY_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateCategory_whenEmptyInput2_got400() throws Exception {
        MvcResult result = mvc.perform(post(CATEGORY_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateCategory_whenLabKeyMissing_got400() throws Exception {
        ChemicalCategoryInput input = LabAdminTestUtils.getSolidForAlphaInput();
        input.setLabKey(null);
        MvcResult result = mvc.perform(post(CATEGORY_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andReturn();
        String response = result.getResponse().getContentAsString();
        Assertions.assertTrue(response.contains("\"labKey\""));
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateCategory_whenLabKeyNotExists_got404() throws Exception {
        ChemicalCategoryInput input = LabAdminTestUtils.getSolidForAlphaInput();
        input.setLabKey("not-existing");
        MvcResult result = mvc.perform(post(CATEGORY_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(404))
                .andReturn();
        String response = result.getResponse().getContentAsString();
        Assertions.assertTrue(response.contains("\"lab\""));
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateCategory_whenUnitDoesNotExists_got400() throws Exception {
        ChemicalCategoryInput input = LabAdminTestUtils.getSolidForAlphaInput();
        input.setUnit("uNOT");
        MvcResult result = mvc.perform(post(CATEGORY_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andReturn();
        String response = result.getResponse().getContentAsString();
        Assertions.assertTrue(response.contains("\"unit\""));
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateCategory_whenAmountBelowZero_got400() throws Exception {
        ChemicalCategoryInput input = LabAdminTestUtils.getSolidForAlphaInput();
        input.setAmount(-1);
        MvcResult result = mvc.perform(post(CATEGORY_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andReturn();
        String response = result.getResponse().getContentAsString();
        Assertions.assertTrue(response.contains("\"amount\""));
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateCategory_whenAmountMissing_got400() throws Exception {
        ChemicalCategoryInput input = LabAdminTestUtils.getSolidForAlphaInput();
        input.setAmount(null);
        MvcResult result = mvc.perform(post(CATEGORY_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andReturn();
        String response = result.getResponse().getContentAsString();
        Assertions.assertTrue(response.contains("\"amount\""));
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateCategory_whenDeletedLabKey_got4xx() throws Exception {
        ChemicalCategoryInput input = LabAdminTestUtils.getSolidForAlphaInput();
        input.setLabKey(DELTA_LAB_KEY);
        MvcResult result = mvc.perform(post(CATEGORY_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is4xxClientError())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateCategory_whenAlphaLabAdmin_got201() throws Exception {
        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
        ChemicalCategory persisted = chemicalCategoryService.getByLab(input.getLabKey(), ALPHA_LAB_ADMIN_PRINCIPAL).stream()
                .filter(category -> category.getName().equals(input.getName()))
                .findAny().get();
        String newName = "this is changed";
        String newUnit = "d";
        Integer newAmount = 1;
        input.setName(newName);
        input.setUnit(newUnit);
        input.setAmount(newAmount);
        MvcResult result = mvc.perform(put(CATEGORY_URL + "/" + persisted.getId())
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().isCreated())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateCategory_whenAlphaLabAdmin_returnedDataIsExpected() throws Exception {
        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
        ChemicalCategory persisted = chemicalCategoryService.getByLab(input.getLabKey(), ALPHA_LAB_ADMIN_PRINCIPAL).stream()
                .filter(category -> category.getName().equals(input.getName()))
                .findAny().get();
        String newName = "this is changed";
        String newUnit = "d";
        Integer newAmount = 1;
        input.setName(newName);
        input.setUnit(newUnit);
        input.setAmount(newAmount);
        MvcResult result = mvc.perform(put(CATEGORY_URL + "/" + persisted.getId())
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(jsonPath("$.name", is(newName)))
                .andExpect(jsonPath("$.lab.key", is(persisted.getLab().getKey())))
                .andExpect(jsonPath("$.deleted", is(false)))
                .andExpect(status().isCreated())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateCategory_whenAlphaLabAdmin_fetcehdDataIsExpected() throws Exception {
        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
        ChemicalCategory persisted = chemicalCategoryService.getByLab(input.getLabKey(), ALPHA_LAB_ADMIN_PRINCIPAL).stream()
                .filter(category -> category.getName().equals(input.getName()))
                .findAny().get();
        String newName = "this is changed";
        String newUnit = "d";
        Integer newAmount = 1;
        input.setName(newName);
        input.setUnit(newUnit);
        input.setAmount(newAmount);
        MvcResult result = mvc.perform(put(CATEGORY_URL + "/" + persisted.getId())
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().isCreated())
                .andReturn();
        ChemicalCategory category = categoryService.findById(persisted.getId(),ALPHA_LAB_ADMIN_PRINCIPAL);
        Assertions.assertEquals(newName, category.getName());
        Assertions.assertEquals(ALPHA_LAB_KEY, category.getLab().getKey());
        Assertions.assertEquals((long)newAmount, category.getShelfLife().getSeconds()/60/60/24);
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateCategory_whenAlphaLabManager_got201() throws Exception {
        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
        ChemicalCategory persisted = chemicalCategoryService.getByLab(input.getLabKey(), ALPHA_LAB_ADMIN_PRINCIPAL).stream()
                .filter(category -> category.getName().equals(input.getName()))
                .findAny().get();
        String newName = "this is changed";
        String newUnit = "d";
        Integer newAmount = 1;
        input.setName(newName);
        input.setUnit(newUnit);
        input.setAmount(newAmount);
        MvcResult result = mvc.perform(put(CATEGORY_URL + "/" + persisted.getId())
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().isCreated())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateCategory_whenBetaLabManager_got403() throws Exception {
        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
        ChemicalCategory persisted = chemicalCategoryService.getByLab(input.getLabKey(), ALPHA_LAB_ADMIN_PRINCIPAL).stream()
                .filter(category -> category.getName().equals(input.getName()))
                .findAny().get();
        String newName = "this is changed";
        String newUnit = "d";
        Integer newAmount = 1;
        input.setName(newName);
        input.setUnit(newUnit);
        input.setAmount(newAmount);
        MvcResult result = mvc.perform(put(CATEGORY_URL + "/" + persisted.getId())
                        .header("Authorization", TOKEN_FOR_BETA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(403))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateCategory_whenBetaLabAdmin_got403() throws Exception {
        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
        ChemicalCategory persisted = chemicalCategoryService.getByLab(input.getLabKey(), ALPHA_LAB_ADMIN_PRINCIPAL).stream()
                .filter(category -> category.getName().equals(input.getName()))
                .findAny().get();
        String newName = "this is changed";
        String newUnit = "d";
        Integer newAmount = 1;
        input.setName(newName);
        input.setUnit(newUnit);
        input.setAmount(newAmount);
        MvcResult result = mvc.perform(put(CATEGORY_URL + "/" + persisted.getId())
                        .header("Authorization", TOKEN_FOR_BETA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(403))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateCategory_whenAlphaLabUser_got403() throws Exception {
        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
        ChemicalCategory persisted = chemicalCategoryService.getByLab(input.getLabKey(), ALPHA_LAB_ADMIN_PRINCIPAL).stream()
                .filter(category -> category.getName().equals(input.getName()))
                .findAny().get();
        String newName = "this is changed";
        String newUnit = "d";
        Integer newAmount = 1;
        input.setName(newName);
        input.setUnit(newUnit);
        input.setAmount(newAmount);
        MvcResult result = mvc.perform(put(CATEGORY_URL + "/" + persisted.getId())
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(403))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateCategory_whenAccountManager_got403() throws Exception {
        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
        ChemicalCategory persisted = chemicalCategoryService.getByLab(input.getLabKey(), ALPHA_LAB_ADMIN_PRINCIPAL).stream()
                .filter(category -> category.getName().equals(input.getName()))
                .findAny().get();
        String newName = "this is changed";
        String newUnit = "d";
        Integer newAmount = 1;
        input.setName(newName);
        input.setUnit(newUnit);
        input.setAmount(newAmount);
        MvcResult result = mvc.perform(put(CATEGORY_URL + "/" + persisted.getId())
                        .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(403))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateCategory_whenNameMissing_got400() throws Exception {
        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
        ChemicalCategory persisted = chemicalCategoryService.getByLab(input.getLabKey(), ALPHA_LAB_ADMIN_PRINCIPAL).stream()
                .filter(category -> category.getName().equals(input.getName()))
                .findAny().get();
        String newName = null;
        String newUnit = "d";
        Integer newAmount = 1;
        input.setName(newName);
        input.setUnit(newUnit);
        input.setAmount(newAmount);
        MvcResult result = mvc.perform(put(CATEGORY_URL + "/" + persisted.getId())
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andReturn();
        String response = result.getResponse().getContentAsString();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateCategory_whenNameBlank_got400() throws Exception {
        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
        ChemicalCategory persisted = chemicalCategoryService.getByLab(input.getLabKey(), ALPHA_LAB_ADMIN_PRINCIPAL).stream()
                .filter(category -> category.getName().equals(input.getName()))
                .findAny().get();
        String newName = "";
        String newUnit = "d";
        Integer newAmount = 1;
        input.setName(newName);
        input.setUnit(newUnit);
        input.setAmount(newAmount);
        MvcResult result = mvc.perform(put(CATEGORY_URL + "/" + persisted.getId())
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andReturn();
        String response = result.getResponse().getContentAsString();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateCategory_whenEmptyInput1_got400() throws Exception {
        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
        ChemicalCategory persisted = chemicalCategoryService.getByLab(input.getLabKey(), ALPHA_LAB_ADMIN_PRINCIPAL).stream()
                .filter(category -> category.getName().equals(input.getName()))
                .findAny().get();
        MvcResult result = mvc.perform(put(CATEGORY_URL + "/" + persisted.getId())
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().is(400))
                .andReturn();
        String response = result.getResponse().getContentAsString();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateCategory_whenEmptyInput2_got400() throws Exception {
        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
        ChemicalCategory persisted = chemicalCategoryService.getByLab(input.getLabKey(), ALPHA_LAB_ADMIN_PRINCIPAL).stream()
                .filter(category -> category.getName().equals(input.getName()))
                .findAny().get();
        MvcResult result = mvc.perform(put(CATEGORY_URL + "/" + persisted.getId())
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().is(400))
                .andReturn();
        String response = result.getResponse().getContentAsString();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateCategory_whenLabKeyNotExists_got404() throws Exception {
        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
        ChemicalCategory persisted = chemicalCategoryService.getByLab(input.getLabKey(), ALPHA_LAB_ADMIN_PRINCIPAL).stream()
                .filter(category -> category.getName().equals(input.getName()))
                .findAny().get();
        input.setLabKey("not-existing");
        MvcResult result = mvc.perform(put(CATEGORY_URL + "/" + persisted.getId())
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(404))
                .andReturn();
        String response = result.getResponse().getContentAsString();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateCategory_whenUnitNotExists_got400() throws Exception {
        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
        ChemicalCategory persisted = chemicalCategoryService.getByLab(input.getLabKey(), ALPHA_LAB_ADMIN_PRINCIPAL).stream()
                .filter(category -> category.getName().equals(input.getName()))
                .findAny().get();
        input.setUnit("uNOT");
        MvcResult result = mvc.perform(put(CATEGORY_URL + "/" + persisted.getId())
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andReturn();
        String response = result.getResponse().getContentAsString();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateCategory_whenAmountBelowZero_got400() throws Exception {
        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
        ChemicalCategory persisted = chemicalCategoryService.getByLab(input.getLabKey(), ALPHA_LAB_ADMIN_PRINCIPAL).stream()
                .filter(category -> category.getName().equals(input.getName()))
                .findAny().get();
        input.setAmount(-1);
        MvcResult result = mvc.perform(put(CATEGORY_URL + "/" + persisted.getId())
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andReturn();
        String response = result.getResponse().getContentAsString();
        Assertions.assertTrue(response.contains("\"amount\""));
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateCategory_whenAmountMissing_got400() throws Exception {
        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
        ChemicalCategory persisted = chemicalCategoryService.getByLab(input.getLabKey(), ALPHA_LAB_ADMIN_PRINCIPAL).stream()
                .filter(category -> category.getName().equals(input.getName()))
                .findAny().get();
        input.setAmount(null);
        MvcResult result = mvc.perform(put(CATEGORY_URL + "/" + persisted.getId())
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andReturn();
        String response = result.getResponse().getContentAsString();
        Assertions.assertTrue(response.contains("\"amount\""));
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateCategory_whenDeletedLabKey_got4xx() throws Exception {
        ChemicalCategoryInput input = LabAdminTestUtils.getOrganicForAlphaInput();
        ChemicalCategory persisted = chemicalCategoryService.getByLab(input.getLabKey(), ALPHA_LAB_ADMIN_PRINCIPAL).stream()
                .filter(category -> category.getName().equals(input.getName()))
                .findAny().get();
        input.setLabKey(DELTA_LAB_KEY);
        MvcResult result = mvc.perform(put(CATEGORY_URL + "/" + persisted.getId())
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(403))
                .andReturn();
        String response = result.getResponse().getContentAsString();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    
    
    //READ

    @Test
    @Rollback
    @Transactional
    void testGetCategoriesForAlphaLab_whenAlphaLabAdmin_got200() throws Exception {
        mvc.perform(get(CATEGORY_URL + "?labKey=" + ALPHA_LAB_KEY)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    
    @Test
    @Rollback
    @Transactional
    void testGetCategoriesForAlphaLab_whenAlphaLabAdmin_gotValidArray() throws Exception {
        mvc.perform(get(CATEGORY_URL + "?labKey=" + ALPHA_LAB_KEY)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].lab.key").isString())
                .andExpect(jsonPath("$[0].name").isString())
                .andExpect(jsonPath("$[0].shelfLife").isString())
                .andExpect(jsonPath("$[0].deleted").isBoolean())
                .andExpect(jsonPath("$[*].deleted", hasItem(false)))
                .andExpect(jsonPath("$[*].deleted", hasItem(true)))
                .andExpect(jsonPath("$", hasSize(chemicalCategoryService.getByLab(ALPHA_LAB_KEY, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL).size())));
    }

    @Test
    @Rollback
    @Transactional
    void testGetCategoriesForAlphaLab_whenAlphaLabManager_got200() throws Exception {
        mvc.perform(get(CATEGORY_URL + "?labKey=" + ALPHA_LAB_KEY)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Rollback
    @Transactional
    void testGetCategoriesForAlphaLab_whenBetaLabAdmin_got403() throws Exception {
        mvc.perform(get(CATEGORY_URL + "?labKey=" + ALPHA_LAB_KEY)
                        .header("Authorization", TOKEN_FOR_BETA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(403));
    }

    @Test
    @Rollback
    @Transactional
    void testGetCategoriesForAlphaLab_whenAlphaLabUser_got403() throws Exception {
        mvc.perform(get(CATEGORY_URL + "?labKey=" + ALPHA_LAB_KEY)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(403));
    }

    @Test
    @Rollback
    @Transactional
    void testGetCategoriesForAlphaLab_whenAccountManager_got403() throws Exception {
        mvc.perform(get(CATEGORY_URL + "?labKey=" + ALPHA_LAB_KEY)
                        .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(403));
    }

    @Test
    @Rollback
    @Transactional
    void testGetCategories_whenKeyDoesNotExists_got404() throws Exception {
        mvc.perform(get(CATEGORY_URL + "?labKey=" + "not-existing")
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    @Rollback
    @Transactional
    void testGetCategories_whenLabKeyMissing_got400() throws Exception {
        mvc.perform(get(CATEGORY_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    @Rollback
    @Transactional
    void testGetCategoriesForAlphaLab_withOnlyActiveFalse_gotValidArray() throws Exception {
        mvc.perform(get(CATEGORY_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("labKey", ALPHA_LAB_KEY)
                        .param("onlyActive", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[*].deleted", hasItem(false)))
                .andExpect(jsonPath("$[*].deleted", hasItem(true)));
    }

    @Test
    @Rollback
    @Transactional
    void testGetCategoriesForAlphaLab_withOnlyActiveTrue_gotValidArray() throws Exception {
        mvc.perform(get(CATEGORY_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("labKey", ALPHA_LAB_KEY)
                        .param("onlyActive", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[*].deleted", hasItem(false)))
                .andExpect(jsonPath("$[*].deleted", IsNot.not(hasItem(true))));
    }
    
//
//    //DELETE
//    @Test
//    @Rollback
//    @Transactional
//    void testDeleteShelfLife_whenLabManager_got204() throws Exception {
//        ChemicalCategoryInput input = getOrganicForAlphaInput();
//        Long id = chemicalCategoryService.findByLab(input.getLabKey(), ALPHA_LAB_MANAGER_PRINCIPAL).stream()
//                .filter(shelfLife -> shelfLife.getChemType().getName().equals(SOLID_CATEGORY))
//                .findAny().get().getId();
//        MvcResult result = mvc.perform(delete(SHELF_LIFE_URL + "/" + id)
//                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().is(204))
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testDeleteShelfLife_whenLabAdmin_got204() throws Exception {
//        ChemicalCategoryInput input = getOrganicForAlphaInput();
//        Long id = chemicalCategoryService.findByLab(input.getLabKey(), ALPHA_LAB_MANAGER_PRINCIPAL).stream()
//                .filter(shelfLife -> shelfLife.getChemType().getName().equals(SOLID_CATEGORY))
//                .findAny().get().getId();
//        MvcResult result = mvc.perform(delete(SHELF_LIFE_URL + "/" + id)
//                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().is(204))
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testDeleteShelfLife_whenNotRelatedManager_got400() throws Exception {
//        ChemicalCategoryInput input = getOrganicForAlphaInput();
//        Long id = chemicalCategoryService.findByLab(input.getLabKey(), ALPHA_LAB_MANAGER_PRINCIPAL).stream()
//                .filter(shelfLife -> shelfLife.getChemType().getName().equals(SOLID_CATEGORY))
//                .findAny().get().getId();
//        MvcResult result = mvc.perform(delete(SHELF_LIFE_URL + "/" + id)
//                        .header("Authorization", TOKEN_FOR_BETA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().is(400))
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testDeleteShelfLife_whenUser_got403() throws Exception {
//        ChemicalCategoryInput input = getOrganicForAlphaInput();
//        Long id = chemicalCategoryService.findByLab(input.getLabKey(), ALPHA_LAB_MANAGER_PRINCIPAL).stream()
//                .filter(shelfLife -> shelfLife.getChemType().getName().equals(SOLID_CATEGORY))
//                .findAny().get().getId();
//        MvcResult result = mvc.perform(delete(SHELF_LIFE_URL + "/" + id)
//                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().is(403))
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testDeleteShelfLife_whenNonExistingId_got404() throws Exception {
//        MvcResult result = mvc.perform(delete(SHELF_LIFE_URL + "/" + Integer.MAX_VALUE)
//                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().is(404))
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testDeleteShelfLife_whenAlreadyDeleted_got404() throws Exception {
//        Long id = chemicalCategoryService.getCategories(false).stream()
//                .filter(ShelfLife::getDeleted)
//                .findAny().get().getId();
//        MvcResult result = mvc.perform(delete(SHELF_LIFE_URL + "/" + id)
//                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER))
//                .andExpect(status().is(404))
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }



//CHEMICAL

    //READ
//    @Test
//    @Rollback
//    @Transactional
//    void testGetAllChemicals_whenLabAdmin_gotValidArray(@Autowired ChemicalService chemicalService) throws Exception {
//        mvc.perform(get(CHEMICAL_URL)
//                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().is(200))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$").isNotEmpty())
//                .andExpect(jsonPath("$[0].id").isNumber())
//                .andExpect(jsonPath("$[0].shortName").isString())
//                .andExpect(jsonPath("$[0].shortName").isNotEmpty())
//                .andExpect(jsonPath("$", hasSize(chemicalService.getChemicals().size())));
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testGetAllChemicals_whenLabAdmin_hasChemicalWithChemType(@Autowired ChemicalService chemicalService) throws Exception {
//        mvc.perform(get(CHEMICAL_URL)
//                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().is(200))
//                .andExpect(jsonPath("$[*].chemType", hasItem(isA(Map.class))))
//                .andExpect(jsonPath("$[*].chemType.name", hasItem(isA(String.class))))
//                .andExpect(jsonPath("$[*].chemType.id", hasItem(isA(Number.class))));
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testGetAllChemicals_whenLabAdmin_hasChemicalWithoutChemType(@Autowired ChemicalService chemicalService) throws Exception {
//        mvc.perform(get(CHEMICAL_URL)
//                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().is(200))
//                .andExpect(jsonPath("$[*].chemType", hasItem(nullValue())));
//    }
//
//
//    @Test
//    @Rollback
//    @Transactional
//    void testGetAllChemicals_whenAuthorized_gotArrayWithoutDeleted() throws Exception {
//        mvc.perform(get(CHEMICAL_URL)
//                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().is(200))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$").isNotEmpty())
//                .andExpect(jsonPath("$[*].deleted", hasItem(false)))
//                .andExpect(jsonPath("$[*].deleted", IsNot.not(hasItem(true))));
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testGetAllChemicals_whenAuthorized_gotArrayWithDeleted() throws Exception {
//        mvc.perform(get(CHEMICAL_URL)
//                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON).param("only-active", "false"))
//                .andExpect(status().is(200))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$").isNotEmpty())
//                .andExpect(jsonPath("$[*].deleted", hasItem(false)))
//                .andExpect(jsonPath("$[*].deleted", hasItem(true)));
//    }
//
//
//    //DELETE
//    @Test
//    @Rollback
//    @Transactional
//    void testDeleteEthanol_whenAuthorized_got204(@Autowired ChemicalService chemicalService) throws Exception {
//        Chemical ethanol = chemicalService.findByShortName(ETHANOL_SHORT_NAME);
//        String url = MANUFACTURER_URL + "/" + ethanol.getId();
//
//        MvcResult result = mvc.perform(delete(url)
//                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNoContent())
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testDeleteEthanol_whenUser_got403(@Autowired ChemicalService chemicalService) throws Exception {
//        Chemical ethanol = chemicalService.findByShortName(ETHANOL_SHORT_NAME);
//        String url = MANUFACTURER_URL + "/" + ethanol.getId();
//
//        MvcResult result = mvc.perform(delete(url)
//                .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().is(403))
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testDeleteEthanolTwoTimes_whenAuthorized_got400SocondTime(@Autowired ChemicalService chemicalService) throws Exception {
//        Chemical ethanol = chemicalService.findByShortName(ETHANOL_SHORT_NAME);
//        String url = MANUFACTURER_URL + "/" + ethanol.getId();
//
//        MvcResult result1 = mvc.perform(delete(url)
//                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNoContent())
//                .andReturn();
//        logger.info("status code: " + result1.getResponse().getStatus());
//        MvcResult result2 = mvc.perform(delete(url)
//                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().is(404))
//                .andReturn();
//        logger.info("status code: " + result2.getResponse().getStatus());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testChemicalWithNoExistingId_whenAuthorized_got404(@Autowired ChemicalService chemicalService) throws Exception {
//        String url = MANUFACTURER_URL + "/" + Integer.MAX_VALUE;
//
//        MvcResult result = mvc.perform(delete(url)
//                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().is(404))
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testDeleteDeletedChemical_whenAuthorized_got400(@Autowired ChemicalService chemicalService) throws Exception {
//        Chemical ipa = chemicalService.getForChemItem(ISOPROPYL_ALCHOL_SHORT_NAME, false);
//        String url = CHEMICAL_URL + "/" + ipa.getId();
//
//        MvcResult result = mvc.perform(delete(url)
//                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().is(404))
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//    }
//
//
//
//    //UPDATE
//    @Test
//    @Rollback
//    @Transactional
//    void testUpdateChemical_withLabAdmin_got201(@Autowired ChemicalService chemicalService) throws Exception {
//        testUpdateEthanolExactName_got201(chemicalService, ALPHA_LAB_ADMIN_USERNAME);
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testUpdateChemical_withLabManager_got201(@Autowired ChemicalService chemicalService) throws Exception {
//        testUpdateEthanolExactName_got201(chemicalService, ALPHA_LAB_MANAGER_USERNAME);
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testUpdateChemical_withAccountManager_got201(@Autowired ChemicalService chemicalService) throws Exception {
//        testUpdateEthanolExactName_got201(chemicalService, ACCOUNT_MANAGER_USERNAME);
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testUpdateChemical_withAlphaLabUser_got403(@Autowired ChemicalService chemicalService) throws Exception {
//        testUpdateEthanolExactName(chemicalService, ALPHA_LAB_USER_USERNAME, 403);
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testUpdateChemical_withEmptyInput1_got400(@Autowired ChemicalService chemicalService) throws Exception {
//        testUpdateEthanolGot400(chemicalService, "");
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testUpdateChemical_withEmptyInput2_got400(@Autowired ChemicalService chemicalService) throws Exception {
//        testUpdateEthanolGot400(chemicalService, "{}");
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testUpdateChemical_withEmptyShortName1_got400(@Autowired ChemicalService chemicalService) throws Exception {
//        ChemicalInput input = getAcnInput();
//        input.setShortName(null);
//        testUpdateEthanolGot400(chemicalService, asJsonString(input));
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testUpdateChemical_whenChemTypeIsRemoved_gotChemicalWithoutChemType(@Autowired ChemicalService chemicalService) throws Exception {
//        ChemicalInput chemWithType = getChemWithTypeInput();
//        chemWithType.setChemTypeId(null);
//        Long id = chemicalService.getChemicals().stream()
//                .filter(chemical -> chemical.getShortName().equals(CHEMICAL_WITH_CHEM_TYPE_SHORT_NAME))
//                .findAny()
//                .get()
//                .getId();
//        MvcResult result = mvc.perform(put(CHEMICAL_URL + "/" + id)
//                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
//                        .content(asJsonString(chemWithType)))
//                .andExpect(status().is(201))
//                .andExpect(jsonPath("$.chemType", is(nullValue())))
//                .andReturn();
//        Assertions.assertEquals(null, chemicalService.findById(id).getChemType());
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testUpdateChemical_whenChemTypeIsAdded_gotChemicalWithChemType(@Autowired ChemicalService chemicalService) throws Exception {
//        ChemicalInput chemWithoutType = getChemWithoutTypeInput();
//        chemWithoutType.setChemTypeId(1l);
//        Long id = chemicalService.getChemicals().stream()
//                .filter(chemical -> chemical.getShortName().equals(CHEMICAL_WITHOUT_CHEM_TYPE_SHORT_NAME))
//                .findAny()
//                .get()
//                .getId();
//        MvcResult result = mvc.perform(put(CHEMICAL_URL + "/" + id)
//                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
//                        .content(asJsonString(chemWithoutType)))
//                .andExpect(status().is(201))
//                .andExpect(jsonPath("$.chemType.id", is(1)))
//                .andReturn();
//        Assertions.assertEquals(1, chemicalService.findById(id).getChemType().getId());
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testUpdateChemical_whenChemTypeIsMinus_got400(@Autowired ChemicalService chemicalService) throws Exception {
//        ChemicalInput ethanolInput = getEtOHInput();
//        ethanolInput.setChemTypeId(-1l);
//        Long id = chemicalService.getChemicals().stream()
//                .filter(chemical -> chemical.getShortName().equals(ETHANOL_SHORT_NAME))
//                .findAny()
//                .get()
//                .getId();
//        MvcResult result = mvc.perform(put(CHEMICAL_URL + "/" + id)
//                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
//                        .content(asJsonString(ethanolInput)))
//                .andExpect(status().is(400))
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testUpdateChemical_withEmptyShortName2_got400(@Autowired ChemicalService chemicalService) throws Exception {
//        ChemicalInput input = getAcnInput();
//        input.setShortName("");
//        testUpdateEthanolGot400(chemicalService, asJsonString(input));
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testUpdateChemical_withEmptyExactName1_got400(@Autowired ChemicalService chemicalService) throws Exception {
//        ChemicalInput input = getAcnInput();
//        input.setExactName(null);
//        testUpdateEthanolGot400(chemicalService, asJsonString(input));
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testUpdateChemical_withEmptyExactName2_got400(@Autowired ChemicalService chemicalService) throws Exception {
//        ChemicalInput input = getAcnInput();
//        input.setExactName("");
//        testUpdateEthanolGot400(chemicalService, asJsonString(input));
//    }
//
//
//
//    @Test
//    @Rollback
//    @Transactional
//    void testUpdateChemical_whenShortNameAlreadyExists_got400(@Autowired ChemicalService chemicalService) throws Exception {
//        ChemicalInput ethanolInput = LabAdminTestUtils.getEtOHInput();
//        Chemical ethanol = chemicalService.getChemicals().stream()
//                .filter(chemical -> chemical.getShortName().equals(LabAdminTestUtils.ETHANOL_SHORT_NAME))
//                .findAny()
//                .get();
//        ethanolInput.setShortName(LabAdminTestUtils.ISOPROPYL_ALCHOL_SHORT_NAME);
//        MvcResult result = mvc.perform(put(CHEMICAL_URL + "/" + ethanol.getId())
//                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(ethanolInput)))
//                .andExpect(status().is(400))
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testUpdateChemical_whenExactNameAlreadyExists_got400(@Autowired ChemicalService chemicalService) throws Exception {
//        ChemicalInput ethanolInput = LabAdminTestUtils.getEtOHInput();
//        Chemical ethanol = chemicalService.getChemicals().stream()
//                .filter(chemical -> chemical.getShortName().equals(LabAdminTestUtils.ETHANOL_SHORT_NAME))
//                .findAny()
//                .get();
//        ethanolInput.setExactName(LabAdminTestUtils.ISOPROPYL_ALCHOL_EXACT_NAME);
//        MvcResult result = mvc.perform(put(CHEMICAL_URL + "/" + ethanol.getId())
//                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(ethanolInput)))
//                .andExpect(status().is(400))
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//    private void testUpdateEthanolGot400(ChemicalService chemicalService, String ethanolInput) throws Exception {
//        Chemical ethanol = chemicalService.getChemicals().stream()
//                .filter(chemical -> chemical.getShortName().equals(LabAdminTestUtils.ETHANOL_SHORT_NAME))
//                .findAny()
//                .get();
//        MvcResult result = mvc.perform(put(CHEMICAL_URL + "/" + ethanol.getId())
//                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
//                .content(ethanolInput))
//                .andExpect(status().is(400))
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//    private void testUpdateEthanolExactName_got201(ChemicalService chemicalService, String username) throws Exception {
//        Long ethanolId = testUpdateEthanolExactName(chemicalService, username, 201);
//        Chemical ethanol = chemicalService.findById(ethanolId);
//        Assertions.assertEquals(CHANGED_ETHANOL_EXACT_NAME, ethanol.getExactName());
//    }
//
//
//    private Long testUpdateEthanolExactName(ChemicalService chemicalService, String username, Integer statusCode) throws Exception {
//        ChemicalInput ethanolInput = LabAdminTestUtils.getEtOHInput();
//        String token = jwtProvider.generateToken(username);
//        Chemical ethanol = chemicalService.getChemicals().stream()
//                .filter(chemical -> chemical.getShortName().equals(LabAdminTestUtils.ETHANOL_SHORT_NAME))
//                .findAny()
//                .get();
//        ethanolInput.setExactName(CHANGED_ETHANOL_EXACT_NAME);
//        MvcResult result = mvc.perform(put(CHEMICAL_URL + "/" + ethanol.getId())
//                .header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(ethanolInput)))
//                .andExpect(status().is(statusCode))
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//        return ethanol.getId();
//    }
//
//
//    //CREATE
//    @Test
//    @Rollback
//    @Transactional
//    void testCreateChemical_withLabAdmin_got201() throws Exception {
//        ChemicalInput input = getAcnInput();
//        MvcResult result = mvc.perform(post(CHEMICAL_URL)
//                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(input)))
//                .andExpect(status().isCreated())
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testCreateChemical_withAccountManager_got201() throws Exception {
//        ChemicalInput input = getAcnInput();
//        MvcResult result = mvc.perform(post(CHEMICAL_URL)
//                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(input)))
//                .andExpect(status().isCreated())
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testCreateChemical_withAlphaLabManager_got201() throws Exception {
//        ChemicalInput input = getAcnInput();
//        MvcResult result = mvc.perform(post(CHEMICAL_URL)
//                .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(input)))
//                .andExpect(status().isCreated())
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testCreateChemical_withAlphaLabUser_got403() throws Exception {
//        ChemicalInput input = getAcnInput();
//        MvcResult result = mvc.perform(post(CHEMICAL_URL)
//                .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(input)))
//                .andExpect(status().is(403))
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testCreateChemical_withLabAdmin_fetchedFromDbIsExpected(@Autowired ChemicalService chemicalService) throws Exception {
//        ChemicalInput input = getAcnInput();
//        MvcResult result = mvc.perform(post(CHEMICAL_URL)
//                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(input)))
//                .andExpect(status().isCreated())
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//        Optional<Chemical> optional = chemicalService.getChemicals().stream().filter(chemical -> chemical.getShortName().equals(input.getShortName())).findAny();
//        if (optional.isEmpty()) {
//            AssertionErrors.fail("Chemical was not found after the calling succesfully POST " + MANUFACTURER_URL);
//        }
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testCreateChemical_withChemType_gotChemicalWithChemType(@Autowired ChemicalService chemicalService, @Autowired ChemTypeService chemTypeService) throws Exception {
//        ChemicalInput input = getAcnInput();
//        input.setChemTypeId(chemTypeService.getChemTypes().stream().filter(chemType -> chemType.getName().equals(SOLID_CATEGORY)).findAny().get().getId());
//        MvcResult result = mvc.perform(post(CHEMICAL_URL)
//                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
//                        .content(asJsonString(input)))
//                .andExpect(status().is(201))
//                .andExpect(jsonPath("$.chemType.name", is(SOLID_CATEGORY)))
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//        JSONObject testChemItemJSONObject = new JSONObject(result.getResponse().getContentAsString());
//        Chemical fetchedChemical = chemicalService.findById(testChemItemJSONObject.getLong("id"));
//        Assertions.assertEquals(SOLID_CATEGORY, fetchedChemical.getChemType().getName());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testCreateChemical_withEmptyInput1_got400() throws Exception {
//        MvcResult result = mvc.perform(post(CHEMICAL_URL)
//                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
//                .content(""))
//                .andExpect(status().is(400))
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testCreateChemical_withEmptyInput2_got400() throws Exception {
//        MvcResult result = mvc.perform(post(CHEMICAL_URL)
//                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
//                .content("{}"))
//                .andExpect(status().is(400))
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testCreateChemical_withEmptyShortName1_got400() throws Exception {
//        ChemicalInput input = getAcnInput();
//        input.setShortName(null);
//        MvcResult result = mvc.perform(post(CHEMICAL_URL)
//                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(input)))
//                .andExpect(status().is(400))
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testCreateChemical_withEmptyShortName2_got400() throws Exception {
//        ChemicalInput input = getAcnInput();
//        input.setShortName("");
//        MvcResult result = mvc.perform(post(CHEMICAL_URL)
//                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(input)))
//                .andExpect(status().is(400))
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testCreateChemical_withEmptyExactName1_got400() throws Exception {
//        ChemicalInput input = getAcnInput();
//        input.setExactName(null);
//        MvcResult result = mvc.perform(post(CHEMICAL_URL)
//                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(input)))
//                .andExpect(status().is(400))
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testCreateChemical_withEmptyExactName2_got400() throws Exception {
//        ChemicalInput input = getAcnInput();
//        input.setExactName("");
//        MvcResult result = mvc.perform(post(CHEMICAL_URL)
//                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(input)))
//                .andExpect(status().is(400))
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testCreateChemical_withInvalidChemType_got400() throws Exception {
//        ChemicalInput input = getAcnInput();
//        input.setChemTypeId(-1l);
//        MvcResult result = mvc.perform(post(CHEMICAL_URL)
//                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
//                        .content(asJsonString(input)))
//                .andExpect(status().is(400))
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testCreateChemical_whenShortNameAlreadyExists_got400() throws Exception {
//        ChemicalInput input = getAcnInput();
//        input.setShortName(LabAdminTestUtils.ETHANOL_SHORT_NAME);
//        MvcResult result = mvc.perform(post(CHEMICAL_URL)
//                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(input)))
//                .andExpect(status().is(400))
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testCreateChemical_whenExactNameAlreadyExists_got400() throws Exception {
//        ChemicalInput input = getAcnInput();
//        input.setExactName(LabAdminTestUtils.ETHANOL_EXACT_NAME);
//        MvcResult result = mvc.perform(post(CHEMICAL_URL)
//                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(input)))
//                .andExpect(status().is(400))
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }



//MANUFACTURER
    //DELETE
    @Test
    @Rollback
    @Transactional
    void testDeleteOmegaManufacturer_whenAuthorized_got204(@Autowired ManufacturerService manufacturerService) throws Exception {
        Manufacturer persistedOmegaManufacturer = manufacturerService.getManufacturers().stream().filter(man -> man.getName().equals(LabAdminTestUtils.OMEGA_MANUFACTURER_NAME)).findAny().get();
        String url = MANUFACTURER_URL + "/" + persistedOmegaManufacturer.getId();

        MvcResult result = mvc.perform(delete(url)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
    }

    @Test
    @Rollback
    @Transactional
    void testDeleteOmegaManufacturer_whenUser_got403(@Autowired ManufacturerService manufacturerService) throws Exception {
        Manufacturer persistedOmegaManufacturer = manufacturerService.getManufacturers().stream().filter(man -> man.getName().equals(LabAdminTestUtils.OMEGA_MANUFACTURER_NAME)).findAny().get();
        String token = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME);
        String url = MANUFACTURER_URL + "/" + persistedOmegaManufacturer.getId();

        MvcResult result = mvc.perform(delete(url)
                .header("Authorization", token).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(403))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
    }

    @Test
    @Rollback
    @Transactional
    void testDeleteOmegaManufacturerTwoTimes_whenAuthorized_got400SocondTime(@Autowired ManufacturerService manufacturerService) throws Exception {
        Manufacturer persistedOmegaManufacturer = manufacturerService.getManufacturers().stream().filter(man -> man.getName().equals(LabAdminTestUtils.OMEGA_MANUFACTURER_NAME)).findAny().get();
        String url = MANUFACTURER_URL + "/" + persistedOmegaManufacturer.getId();

        MvcResult result1 = mvc.perform(delete(url)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
        logger.info("status code: " + result1.getResponse().getStatus());
        MvcResult result2 = mvc.perform(delete(url)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404))
                .andReturn();
        logger.info("status code: " + result2.getResponse().getStatus());
    }

    @Test
    @Rollback
    @Transactional
    void testDeleteManufacturerWithNonExistingId_whenAuthorized_got404(@Autowired ManufacturerService manufacturerService) throws Exception {
       String url = MANUFACTURER_URL + "/" + Integer.MAX_VALUE;

        MvcResult result = mvc.perform(delete(url)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
    }

    @Test
    @Rollback
    @Transactional
    void testDeleteDeletedManufacturer_whenAuthorized_got400(@Autowired ManufacturerService manufacturerService) throws Exception {
        Manufacturer persistedDeletedManufacturer = manufacturerService.getManufacturers(false).stream().filter(man -> man.getName().equals(LabAdminTestUtils.DELTA_MANUFACTURER_NAME)).findAny().get();
        String url = MANUFACTURER_URL + "/" + persistedDeletedManufacturer.getId();

        MvcResult result = mvc.perform(delete(url)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
    }

    @Test
    @Rollback
    @Transactional
    void testDeleteManufacturer_whenUser_got403(@Autowired ManufacturerService manufacturerService) throws Exception {
        Manufacturer persistedOmegaManufacturer = manufacturerService.getManufacturers().stream().filter(man -> man.getName().equals(LabAdminTestUtils.OMEGA_MANUFACTURER_NAME)).findAny().get();
        String token = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME);
        String url = MANUFACTURER_URL + "/" + persistedOmegaManufacturer.getId();

        MvcResult result = mvc.perform(delete(url)
                .header("Authorization", token).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(403))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
    }



    //READ
    @Test
    @Rollback
    @Transactional
    void testGetAllManufacturers_whenAuthorized_gotValidArray(@Autowired ManufacturerService manufacturerService) throws Exception {
       mvc.perform(get(MANUFACTURER_URL)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].name").isString())
                .andExpect(jsonPath("$[0].name").isNotEmpty())
                .andExpect(jsonPath("$", hasSize(manufacturerService.getManufacturers().size())));
    }

    @Test
    @Rollback
    @Transactional
    void testGetAllManufacturers_whenAuthorized_gotArrayWithoutDeleted() throws Exception {
        mvc.perform(get(MANUFACTURER_URL)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[*].deleted", hasItem(false)))
                .andExpect(jsonPath("$[*].deleted", IsNot.not(hasItem(true))));
    }

    @Test
    @Rollback
    @Transactional
    void testGetAllManufacturersWithOnlyActiveFalse_whenAuthorized_gotArrayWithDeleted() throws Exception {
        mvc.perform(get(MANUFACTURER_URL)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON).param("only-active", "false"))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[*].deleted", hasItem(false)))
                .andExpect(jsonPath("$[*].deleted", hasItem(true)));
    }

    //UPDATE

    @Test
    @Rollback
    @Transactional
    void testUpdateManufacturer_withLabAdmin_got201(@Autowired ManufacturerService manufacturerService) throws Exception {
        testUpdateManufacturer_got201(manufacturerService, ALPHA_LAB_ADMIN_USERNAME);
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateManufacturer_withAccountManager_got201(@Autowired ManufacturerService manufacturerService) throws Exception {
        testUpdateManufacturer_got201(manufacturerService, ACCOUNT_MANAGER_USERNAME);
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateManufacturer_withAlphaLabManager_got201(@Autowired ManufacturerService manufacturerService) throws Exception {
        testUpdateManufacturer_got201(manufacturerService, AccountManagerTestUtils.ALPHA_LAB_MANAGER_USERNAME);
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateManufacturer_withAlphaLabUser_got403(@Autowired ManufacturerService manufacturerService) throws Exception {
        testUpdateManufacturer_got403(manufacturerService, ALPHA_LAB_USER_USERNAME);
    }

    private void testUpdateManufacturer_got201(ManufacturerService manufacturerService, String username) throws Exception {
        testUpdateManufacturer(manufacturerService, username, 201);
    }

    private void testUpdateManufacturer_got403(ManufacturerService manufacturerService, String username) throws Exception {
        testUpdateManufacturer(manufacturerService, username, 403);
    }

    private void testUpdateManufacturer(ManufacturerService manufacturerService, String username, Integer statusCode) throws Exception {
        ManufacturerInput input = LabAdminTestUtils.getOmegaManufacturerInput();
        String token = jwtProvider.generateToken(username);
        Manufacturer omegaManufacturer = manufacturerService.getManufacturers().stream()
                .filter(manufacturer -> manufacturer.getName().equals(LabAdminTestUtils.OMEGA_MANUFACTURER_NAME))
                .findAny()
                .get();
        input.setName("Changed Omega Manufacturer");
        MvcResult result = mvc.perform(put(MANUFACTURER_URL + "/" + omegaManufacturer.getId())
                .header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(input)))
                .andExpect(status().is(statusCode))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateManufacturer_withLabAdmin_fetchedDataFromDbIsExpected(@Autowired ManufacturerService manufacturerService) throws Exception {
        ManufacturerInput input = LabAdminTestUtils.getOmegaManufacturerInput();
        String token = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_LAB_MANAGER_USERNAME);
        Manufacturer omegaManufacturer = manufacturerService.getManufacturers().stream()
                .filter(manufacturer -> manufacturer.getName().equals(LabAdminTestUtils.OMEGA_MANUFACTURER_NAME))
                .findAny()
                .get();
        String changedName = "Changed Omega Manufacturer";
        input.setName(changedName);
        MvcResult result = mvc.perform(put(MANUFACTURER_URL + "/" + omegaManufacturer.getId())
                .header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(input)))
                .andExpect(status().is(201))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
        Optional<Manufacturer> optional = manufacturerService.getManufacturers().stream().filter(manufacturer -> manufacturer.getName().equals(changedName)).findAny();
        if (optional.isEmpty()) {
            AssertionErrors.fail("Manufacturer was not found after the calling succesfully POST " + MANUFACTURER_URL);
        }
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateManufacturer_withEmptyInput1_got400(@Autowired ManufacturerService manufacturerService) throws Exception {
        String token = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_LAB_MANAGER_USERNAME);
        Manufacturer omegaManufacturer = manufacturerService.getManufacturers().stream()
                .filter(manufacturer -> manufacturer.getName().equals(LabAdminTestUtils.OMEGA_MANUFACTURER_NAME))
                .findAny()
                .get();
        MvcResult result = mvc.perform(put(MANUFACTURER_URL + "/" + omegaManufacturer.getId())
                .header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateManufacturer_withEmptyInput2_got400(@Autowired ManufacturerService manufacturerService) throws Exception {
        String token = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_LAB_MANAGER_USERNAME);
        Manufacturer omegaManufacturer = manufacturerService.getManufacturers().stream()
                .filter(manufacturer -> manufacturer.getName().equals(LabAdminTestUtils.OMEGA_MANUFACTURER_NAME))
                .findAny()
                .get();
        MvcResult result = mvc.perform(put(MANUFACTURER_URL + "/" + omegaManufacturer.getId())
                .header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateManufacturer_withEmptyName1_got400(@Autowired ManufacturerService manufacturerService) throws Exception {
        ManufacturerInput input = LabAdminTestUtils.getOmegaManufacturerInput();
        String token = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_LAB_MANAGER_USERNAME);
        Manufacturer omegaManufacturer = manufacturerService.getManufacturers().stream()
                .filter(manufacturer -> manufacturer.getName().equals(LabAdminTestUtils.OMEGA_MANUFACTURER_NAME))
                .findAny()
                .get();
        input.setName(null);
        MvcResult result = mvc.perform(put(MANUFACTURER_URL + "/" + omegaManufacturer.getId())
                .header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateManufacturer_withEmptyName2_got400(@Autowired ManufacturerService manufacturerService) throws Exception {
        ManufacturerInput input = LabAdminTestUtils.getOmegaManufacturerInput();
        String token = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_LAB_MANAGER_USERNAME);
        Manufacturer omegaManufacturer = manufacturerService.getManufacturers().stream()
                .filter(manufacturer -> manufacturer.getName().equals(LabAdminTestUtils.OMEGA_MANUFACTURER_NAME))
                .findAny()
                .get();
        input.setName("");
        MvcResult result = mvc.perform(put(MANUFACTURER_URL + "/" + omegaManufacturer.getId())
                .header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateManufacturer_whenAlreadyExists_got400(@Autowired ManufacturerService manufacturerService) throws Exception {
        ManufacturerInput input = LabAdminTestUtils.getOmegaManufacturerInput();
        String token = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_LAB_MANAGER_USERNAME);
        Manufacturer omegaManufacturer = manufacturerService.getManufacturers().stream()
                .filter(manufacturer -> manufacturer.getName().equals(LabAdminTestUtils.OMEGA_MANUFACTURER_NAME))
                .findAny()
                .get();
        input.setName(LabAdminTestUtils.DELTA_MANUFACTURER_NAME);
        MvcResult result = mvc.perform(put(MANUFACTURER_URL + "/" + omegaManufacturer.getId())
                .header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }


    // CREATE

    @Test
    @Rollback
    @Transactional
    void testCreateManufacturer_withLabAdmin_got201() throws Exception {
        ManufacturerInput input = LabAdminTestUtils.getAlphaManufacturerInput();
        MvcResult result = mvc.perform(post(MANUFACTURER_URL)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(input)))
                .andExpect(status().isCreated())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateManufacturer_withAccountManager_got201() throws Exception {
        ManufacturerInput input = LabAdminTestUtils.getAlphaManufacturerInput();
        MvcResult result = mvc.perform(post(MANUFACTURER_URL)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(input)))
                .andExpect(status().isCreated())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateManufacturer_withAlphaLabManager_got201() throws Exception {
        ManufacturerInput input = LabAdminTestUtils.getAlphaManufacturerInput();
        String token = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_LAB_MANAGER_USERNAME);
        MvcResult result = mvc.perform(post(MANUFACTURER_URL)
                .header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(input)))
                .andExpect(status().isCreated())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }


    @Test
    @Rollback
    @Transactional
    void testCreateManufacturer_withAlphaLabUser_got403() throws Exception {
        ManufacturerInput input = LabAdminTestUtils.getAlphaManufacturerInput();
        String token = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME);
        MvcResult result = mvc.perform(post(MANUFACTURER_URL)
                .header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(input)))
                .andExpect(status().is(403))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateManufacturer_withLabAdmin_fetchedFromDbIsExpected(@Autowired ManufacturerService manufacturerService) throws Exception {
        ManufacturerInput input = LabAdminTestUtils.getAlphaManufacturerInput();
        MvcResult result = mvc.perform(post(MANUFACTURER_URL)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(input)))
                .andExpect(status().isCreated())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
        Optional<Manufacturer> optional = manufacturerService.getManufacturers().stream().filter(manufacturer -> manufacturer.getName().equals(input.getName())).findAny();
        if (optional.isEmpty()) {
            AssertionErrors.fail("Manufacturer was found after the calling succesfully POST " + MANUFACTURER_URL);
        }
    }

    @Test
    @Rollback
    @Transactional
    void testCreateManufacturer_withEmptyInput1_got400() throws Exception {
        MvcResult result = mvc.perform(post(MANUFACTURER_URL)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateManufacturer_withEmptyInput2_got400() throws Exception {
        MvcResult result = mvc.perform(post(MANUFACTURER_URL)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateManufacturer_withEmptyName1_got400() throws Exception {
        ManufacturerInput input = LabAdminTestUtils.getAlphaManufacturerInput();
        input.setName(null);
        MvcResult result = mvc.perform(post(MANUFACTURER_URL)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateManufacturer_withEmptyName2_got400() throws Exception {
        ManufacturerInput input = LabAdminTestUtils.getAlphaManufacturerInput();
        input.setName("");
        MvcResult result = mvc.perform(post(MANUFACTURER_URL)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateManufacturer_whenAlreadyExists_got400() throws Exception {
        ManufacturerInput input = LabAdminTestUtils.getOmegaManufacturerInput();
        MvcResult result = mvc.perform(post(MANUFACTURER_URL)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    private ChemicalInput getAcnInput() {
        ChemicalInput acnInput = LabAdminTestUtils.getAcnForAlphaInput();
        return acnInput;
    }

    public String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }




}