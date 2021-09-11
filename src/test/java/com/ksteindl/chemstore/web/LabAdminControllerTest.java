package com.ksteindl.chemstore.web;

import com.ksteindl.chemstore.domain.entities.ChemItem;
import com.ksteindl.chemstore.domain.entities.Chemical;
import com.ksteindl.chemstore.domain.entities.Manufacturer;
import com.ksteindl.chemstore.domain.input.ChemicalInput;
import com.ksteindl.chemstore.domain.input.ManufacturerInput;
import com.ksteindl.chemstore.security.JwtProvider;
import com.ksteindl.chemstore.service.ChemTypeService;
import com.ksteindl.chemstore.service.ChemicalService;
import com.ksteindl.chemstore.service.ManufacturerService;
import com.ksteindl.chemstore.web.utils.AccountManagerTestUtils;
import com.ksteindl.chemstore.web.utils.LabAdminTestUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.core.IsNot;
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
import org.springframework.test.util.AssertionErrors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;

import java.util.Map;
import java.util.Optional;

import static com.ksteindl.chemstore.web.utils.AccountManagerTestUtils.*;
import static com.ksteindl.chemstore.web.utils.LabAdminTestUtils.*;
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
    JwtProvider jwtProvider;
    @Autowired
    private MockMvc mvc;

    private final String BASE_URL = "/api/lab-admin";
    private final String MANUFACTURER_URL = BASE_URL + "/manufacturer";
    private final String CHEMICAL_URL = BASE_URL + "/chemical";
    private final String CHANGED_ETHANOL_EXACT_NAME = "Changed ethanol exact name";

//CHEMICAL
    //READ

    @Test
    void testGetAllChemicals_whenLabAdmin_gotValidArray(@Autowired ChemicalService chemicalService) throws Exception {
        mvc.perform(get(CHEMICAL_URL)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].shortName").isString())
                .andExpect(jsonPath("$[0].shortName").isNotEmpty())
                .andExpect(jsonPath("$", hasSize(chemicalService.getChemicals().size())));
    }

    @Test
    void testGetAllChemicals_whenLabAdmin_hasChemicalWithChemType(@Autowired ChemicalService chemicalService) throws Exception {
        mvc.perform(get(CHEMICAL_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[*].chemType", hasItem(isA(Map.class))))
                .andExpect(jsonPath("$[*].chemType.name", hasItem(isA(String.class))))
                .andExpect(jsonPath("$[*].chemType.id", hasItem(isA(Number.class))));
    }

    @Test
    void testGetAllChemicals_whenLabAdmin_hasChemicalWithoutChemType(@Autowired ChemicalService chemicalService) throws Exception {
        mvc.perform(get(CHEMICAL_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[*].chemType", hasItem(nullValue())));
    }


    @Test
    void testGetAllChemicals_whenAuthorized_gotArrayWithoutDeleted() throws Exception {
        mvc.perform(get(CHEMICAL_URL)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[*].deleted", hasItem(false)))
                .andExpect(jsonPath("$[*].deleted", IsNot.not(hasItem(true))));
    }

    @Test
    void testGetAllChemicals_whenAuthorized_gotArrayWithDeleted() throws Exception {
        mvc.perform(get(CHEMICAL_URL)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON).param("only-active", "false"))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[*].deleted", hasItem(false)))
                .andExpect(jsonPath("$[*].deleted", hasItem(true)));
    }


    //DELETE
    @Test
    @Rollback
    @Transactional
    void testDeleteEthanol_whenAuthorized_got204(@Autowired ChemicalService chemicalService) throws Exception {
        Chemical ethanol = chemicalService.getChemicalByShortName(ETHANOL_SHORT_NAME);
        String url = MANUFACTURER_URL + "/" + ethanol.getId();

        MvcResult result = mvc.perform(delete(url)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
    }

    @Test
    @Rollback
    @Transactional
    void testDeleteEthanol_whenUser_got403(@Autowired ChemicalService chemicalService) throws Exception {
        Chemical ethanol = chemicalService.getChemicalByShortName(ETHANOL_SHORT_NAME);
        String url = MANUFACTURER_URL + "/" + ethanol.getId();

        MvcResult result = mvc.perform(delete(url)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(403))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
    }

    @Test
    @Rollback
    @Transactional
    void testDeleteEthanolTwoTimes_whenAuthorized_got400SocondTime(@Autowired ChemicalService chemicalService) throws Exception {
        Chemical ethanol = chemicalService.getChemicalByShortName(ETHANOL_SHORT_NAME);
        String url = MANUFACTURER_URL + "/" + ethanol.getId();

        MvcResult result1 = mvc.perform(delete(url)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
        logger.info("status code: " + result1.getResponse().getStatus());
        MvcResult result2 = mvc.perform(delete(url)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result2.getResponse().getStatus());
    }

    @Test
    @Rollback
    @Transactional
    void testChemicalWithNoExistingId_whenAuthorized_got404(@Autowired ChemicalService chemicalService) throws Exception {
        String url = MANUFACTURER_URL + "/" + Integer.MAX_VALUE;

        MvcResult result = mvc.perform(delete(url)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
    }

    @Test
    @Rollback
    @Transactional
    void testDeleteDeletedChemical_whenAuthorized_got400(@Autowired ChemicalService chemicalService) throws Exception {
        Chemical ipa = chemicalService.getChemicalByShortName(ISOPROPYL_ALCHOL_SHORT_NAME);
        String url = CHEMICAL_URL + "/" + ipa.getId();

        MvcResult result = mvc.perform(delete(url)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
    }



    //UPDATE
    @Test
    @Rollback
    @Transactional
    void testUpdateChemical_withLabAdmin_got201(@Autowired ChemicalService chemicalService) throws Exception {
        testUpdateEthanolExactName_got201(chemicalService, ALPHA_LAB_ADMIN_USERNAME);
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateChemical_withLabManager_got201(@Autowired ChemicalService chemicalService) throws Exception {
        testUpdateEthanolExactName_got201(chemicalService, ALPHA_LAB_MANAGER_USERNAME);
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateChemical_withAccountManager_got201(@Autowired ChemicalService chemicalService) throws Exception {
        testUpdateEthanolExactName_got201(chemicalService, ACCOUNT_MANAGER_USERNAME);
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateChemical_withAlphaLabUser_got403(@Autowired ChemicalService chemicalService) throws Exception {
        testUpdateEthanolExactName(chemicalService, ALPHA_LAB_USER_USERNAME, 403);
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateChemical_withEmptyInput1_got400(@Autowired ChemicalService chemicalService) throws Exception {
        testUpdateEthanolGot400(chemicalService, "");
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateChemical_withEmptyInput2_got400(@Autowired ChemicalService chemicalService) throws Exception {
        testUpdateEthanolGot400(chemicalService, "{}");
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateChemical_withEmptyShortName1_got400(@Autowired ChemicalService chemicalService) throws Exception {
        ChemicalInput input = getAcnInput();
        input.setShortName(null);
        testUpdateEthanolGot400(chemicalService, asJsonString(input));
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateChemical_whenChemTypeIsRemoved_gotChemicalWithoutChemType(@Autowired ChemicalService chemicalService) throws Exception {
        ChemicalInput chemWithType = getChemWithTypeInput();
        chemWithType.setChemTypeId(null);
        Long id = chemicalService.getChemicals().stream()
                .filter(chemical -> chemical.getShortName().equals(CHEMICAL_WITH_CHEM_TYPE_SHORT_NAME))
                .findAny()
                .get()
                .getId();
        MvcResult result = mvc.perform(put(CHEMICAL_URL + "/" + id)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(chemWithType)))
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.chemType", is(nullValue())))
                .andReturn();
        Assertions.assertEquals(null, chemicalService.findById(id).getChemType());
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateChemical_whenChemTypeIsAdded_gotChemicalWithChemType(@Autowired ChemicalService chemicalService) throws Exception {
        ChemicalInput chemWithoutType = getChemWithoutTypeInput();
        chemWithoutType.setChemTypeId(1l);
        Long id = chemicalService.getChemicals().stream()
                .filter(chemical -> chemical.getShortName().equals(CHEMICAL_WITHOUT_CHEM_TYPE_SHORT_NAME))
                .findAny()
                .get()
                .getId();
        MvcResult result = mvc.perform(put(CHEMICAL_URL + "/" + id)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(chemWithoutType)))
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.chemType.id", is(1)))
                .andReturn();
        Assertions.assertEquals(1, chemicalService.findById(id).getChemType().getId());
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateChemical_whenChemTypeIsMinus_got400(@Autowired ChemicalService chemicalService) throws Exception {
        ChemicalInput ethanolInput = getEtOHInput();
        ethanolInput.setChemTypeId(-1l);
        Long id = chemicalService.getChemicals().stream()
                .filter(chemical -> chemical.getShortName().equals(ETHANOL_SHORT_NAME))
                .findAny()
                .get()
                .getId();
        MvcResult result = mvc.perform(put(CHEMICAL_URL + "/" + id)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(ethanolInput)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateChemical_withEmptyShortName2_got400(@Autowired ChemicalService chemicalService) throws Exception {
        ChemicalInput input = getAcnInput();
        input.setShortName("");
        testUpdateEthanolGot400(chemicalService, asJsonString(input));
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateChemical_withEmptyExactName1_got400(@Autowired ChemicalService chemicalService) throws Exception {
        ChemicalInput input = getAcnInput();
        input.setExactName(null);
        testUpdateEthanolGot400(chemicalService, asJsonString(input));
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateChemical_withEmptyExactName2_got400(@Autowired ChemicalService chemicalService) throws Exception {
        ChemicalInput input = getAcnInput();
        input.setExactName("");
        testUpdateEthanolGot400(chemicalService, asJsonString(input));
    }



    @Test
    @Rollback
    @Transactional
    void testUpdateChemical_whenShortNameAlreadyExists_got400(@Autowired ChemicalService chemicalService) throws Exception {
        ChemicalInput ethanolInput = LabAdminTestUtils.getEtOHInput();
        Chemical ethanol = chemicalService.getChemicals().stream()
                .filter(chemical -> chemical.getShortName().equals(LabAdminTestUtils.ETHANOL_SHORT_NAME))
                .findAny()
                .get();
        ethanolInput.setShortName(LabAdminTestUtils.ISOPROPYL_ALCHOL_SHORT_NAME);
        MvcResult result = mvc.perform(put(CHEMICAL_URL + "/" + ethanol.getId())
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(ethanolInput)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateChemical_whenExactNameAlreadyExists_got400(@Autowired ChemicalService chemicalService) throws Exception {
        ChemicalInput ethanolInput = LabAdminTestUtils.getEtOHInput();
        Chemical ethanol = chemicalService.getChemicals().stream()
                .filter(chemical -> chemical.getShortName().equals(LabAdminTestUtils.ETHANOL_SHORT_NAME))
                .findAny()
                .get();
        ethanolInput.setExactName(LabAdminTestUtils.ISOPROPYL_ALCHOL_EXACT_NAME);
        MvcResult result = mvc.perform(put(CHEMICAL_URL + "/" + ethanol.getId())
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(ethanolInput)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    private void testUpdateEthanolGot400(ChemicalService chemicalService, String ethanolInput) throws Exception {
        Chemical ethanol = chemicalService.getChemicals().stream()
                .filter(chemical -> chemical.getShortName().equals(LabAdminTestUtils.ETHANOL_SHORT_NAME))
                .findAny()
                .get();
        MvcResult result = mvc.perform(put(CHEMICAL_URL + "/" + ethanol.getId())
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(ethanolInput))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    private void testUpdateEthanolExactName_got201(ChemicalService chemicalService, String username) throws Exception {
        Long ethanolId = testUpdateEthanolExactName(chemicalService, username, 201);
        Chemical ethanol = chemicalService.findById(ethanolId);
        Assertions.assertEquals(CHANGED_ETHANOL_EXACT_NAME, ethanol.getExactName());
    }


    private Long testUpdateEthanolExactName(ChemicalService chemicalService, String username, Integer statusCode) throws Exception {
        ChemicalInput ethanolInput = LabAdminTestUtils.getEtOHInput();
        String token = jwtProvider.generateToken(username);
        Chemical ethanol = chemicalService.getChemicals().stream()
                .filter(chemical -> chemical.getShortName().equals(LabAdminTestUtils.ETHANOL_SHORT_NAME))
                .findAny()
                .get();
        ethanolInput.setExactName(CHANGED_ETHANOL_EXACT_NAME);
        MvcResult result = mvc.perform(put(CHEMICAL_URL + "/" + ethanol.getId())
                .header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(ethanolInput)))
                .andExpect(status().is(statusCode))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
        return ethanol.getId();
    }


    //CREATE
    @Test
    @Rollback
    @Transactional
    void testCreateChemical_withLabAdmin_got201() throws Exception {
        ChemicalInput input = getAcnInput();
        MvcResult result = mvc.perform(post(CHEMICAL_URL)
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
    void testCreateChemical_withAccountManager_got201() throws Exception {
        ChemicalInput input = getAcnInput();
        MvcResult result = mvc.perform(post(CHEMICAL_URL)
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
    void testCreateChemical_withAlphaLabManager_got201() throws Exception {
        ChemicalInput input = getAcnInput();
        MvcResult result = mvc.perform(post(CHEMICAL_URL)
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
    void testCreateChemical_withAlphaLabUser_got403() throws Exception {
        ChemicalInput input = getAcnInput();
        MvcResult result = mvc.perform(post(CHEMICAL_URL)
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
    void testCreateChemical_withLabAdmin_fetchedFromDbIsExpected(@Autowired ChemicalService chemicalService) throws Exception {
        ChemicalInput input = getAcnInput();
        MvcResult result = mvc.perform(post(CHEMICAL_URL)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(input)))
                .andExpect(status().isCreated())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
        Optional<Chemical> optional = chemicalService.getChemicals().stream().filter(chemical -> chemical.getShortName().equals(input.getShortName())).findAny();
        if (optional.isEmpty()) {
            AssertionErrors.fail("Chemical was not found after the calling succesfully POST " + MANUFACTURER_URL);
        }
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemical_withChemType_gotChemicalWithChemType(@Autowired ChemicalService chemicalService, @Autowired ChemTypeService chemTypeService) throws Exception {
        ChemicalInput input = getAcnInput();
        input.setChemTypeId(chemTypeService.getChemTypes().stream().filter(chemType -> chemType.getName().equals(SOLID_COMPOUND_NAME)).findAny().get().getId());
        MvcResult result = mvc.perform(post(CHEMICAL_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.chemType.name", is(SOLID_COMPOUND_NAME)))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
        JSONObject testChemItemJSONObject = new JSONObject(result.getResponse().getContentAsString());
        Chemical fetchedChemical = chemicalService.findById(testChemItemJSONObject.getLong("id"));
        Assertions.assertEquals(SOLID_COMPOUND_NAME, fetchedChemical.getChemType().getName());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemical_withEmptyInput1_got400() throws Exception {
        MvcResult result = mvc.perform(post(CHEMICAL_URL)
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
    void testCreateChemical_withEmptyInput2_got400() throws Exception {
        MvcResult result = mvc.perform(post(CHEMICAL_URL)
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
    void testCreateChemical_withEmptyShortName1_got400() throws Exception {
        ChemicalInput input = getAcnInput();
        input.setShortName(null);
        MvcResult result = mvc.perform(post(CHEMICAL_URL)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemical_withEmptyShortName2_got400() throws Exception {
        ChemicalInput input = getAcnInput();
        input.setShortName("");
        MvcResult result = mvc.perform(post(CHEMICAL_URL)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemical_withEmptyExactName1_got400() throws Exception {
        ChemicalInput input = getAcnInput();
        input.setExactName(null);
        MvcResult result = mvc.perform(post(CHEMICAL_URL)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemical_withEmptyExactName2_got400() throws Exception {
        ChemicalInput input = getAcnInput();
        input.setExactName("");
        MvcResult result = mvc.perform(post(CHEMICAL_URL)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemical_withInvalidChemType_got400() throws Exception {
        ChemicalInput input = getAcnInput();
        input.setChemTypeId(-1l);
        MvcResult result = mvc.perform(post(CHEMICAL_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemical_whenShortNameAlreadyExists_got400() throws Exception {
        ChemicalInput input = getAcnInput();
        input.setShortName(LabAdminTestUtils.ETHANOL_SHORT_NAME);
        MvcResult result = mvc.perform(post(CHEMICAL_URL)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemical_whenExactNameAlreadyExists_got400() throws Exception {
        ChemicalInput input = getAcnInput();
        input.setExactName(LabAdminTestUtils.ETHANOL_EXACT_NAME);
        MvcResult result = mvc.perform(post(CHEMICAL_URL)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }



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
                .andExpect(status().isBadRequest())
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
                .andExpect(status().is(400))
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
        ChemicalInput acnInput = LabAdminTestUtils.getAcnInput();
        return acnInput;
    }




}