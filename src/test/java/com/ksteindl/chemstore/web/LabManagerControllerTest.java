package com.ksteindl.chemstore.web;

import com.ksteindl.chemstore.BaseControllerTest;
import com.ksteindl.chemstore.domain.entities.ChemType;
import com.ksteindl.chemstore.domain.input.ChemTypeInput;
import com.ksteindl.chemstore.security.JwtProvider;
import com.ksteindl.chemstore.service.ChemTypeService;
import com.ksteindl.chemstore.utils.AccountManagerTestUtils;
import com.ksteindl.chemstore.utils.LabAdminTestUtils;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LabManagerControllerTest extends BaseControllerTest {

    private static final Logger logger = LogManager.getLogger(LabManagerControllerTest.class);

    private static final String URL_CHEM_TYPE = "/api/lab-manager/chem-type";
    private static final String URL_USER = "/api/lab-manager/user";
    private static final String URL_ALPHA_LAB = URL_USER + "/" + AccountManagerTestUtils.ALPHA_LAB_KEY;

    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    private MockMvc mvc;

    @Test
    void testGetAllChemTypes_whenLabManager_got200(@Autowired ChemTypeService chemTypeService) throws Exception {
        mvc.perform(get(URL_CHEM_TYPE)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
    }

    @Test
    void testGetAllChemTypes_whenAccountManager_got200(@Autowired ChemTypeService chemTypeService) throws Exception {
        mvc.perform(get(URL_CHEM_TYPE)
                        .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
    }

    @Test
    void testGetAllChemTypes_whenLabAdmin_got403(@Autowired ChemTypeService chemTypeService) throws Exception {
        mvc.perform(get(URL_CHEM_TYPE)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(403));
    }

    @Test
    void testGetAllChemTypes_whenLabUser_got403(@Autowired ChemTypeService chemTypeService) throws Exception {
        mvc.perform(get(URL_CHEM_TYPE)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(403));
    }

    @Test
    void testGetAllChemTypes_whenLabManager_gotValidArray(@Autowired ChemTypeService chemTypeService) throws Exception {
        mvc.perform(get(URL_CHEM_TYPE)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].name").isString())
                .andExpect(jsonPath("$[0].name").isNotEmpty())
                .andExpect(jsonPath("$", hasSize(chemTypeService.getChemTypes().size())));
    }

    @Test
    void testGetAllChemTypes_whenLabManager_gotArrayWIthoutDeleted(@Autowired ChemTypeService chemTypeService) throws Exception {
        mvc.perform(get(URL_CHEM_TYPE)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[*].deleted", hasItem(false)))
                .andExpect(jsonPath("$[*].deleted", IsNot.not(hasItem(true))));
    }

    @Test
    void testGetAllChemTypes_WithOnlyActiveFalse_gotArrayWithDeleted(@Autowired ChemTypeService chemTypeService) throws Exception {
        mvc.perform(get(URL_CHEM_TYPE)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("only-active", "false"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[*].deleted", hasItem(false)))
                .andExpect(jsonPath("$[*].deleted", hasItem(true)));
    }


    //DELETE
    @Test
    @Rollback
    @Transactional
    void testDeleteChemType_whenLabManager_got204(@Autowired ChemTypeService chemTypeService) throws Exception {
        ChemType persistedChemType = chemTypeService.getChemTypes().stream().filter(ct -> ct.getName().equals(LabAdminTestUtils.SOLID_COMPOUND_NAME)).findAny().get();
        String url = URL_CHEM_TYPE + "/" + persistedChemType.getId();

        MvcResult result = mvc.perform(delete(url)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
    }

    @Test
    @Rollback
    @Transactional
    void testDeleteChemType_whenUser_got403(@Autowired ChemTypeService chemTypeService) throws Exception {
        ChemType persistedChemType = chemTypeService.getChemTypes().stream().filter(ct -> ct.getName().equals(LabAdminTestUtils.SOLID_COMPOUND_NAME)).findAny().get();
        String url = URL_CHEM_TYPE + "/" + persistedChemType.getId();

        MvcResult result = mvc.perform(delete(url)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(403))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
    }

    @Test
    @Rollback
    @Transactional
    void testDeleteChemType_whenLabAdmin_got403(@Autowired ChemTypeService chemTypeService) throws Exception {
        ChemType persistedChemType = chemTypeService.getChemTypes().stream().filter(ct -> ct.getName().equals(LabAdminTestUtils.SOLID_COMPOUND_NAME)).findAny().get();
        String url = URL_CHEM_TYPE + "/" + persistedChemType.getId();

        MvcResult result = mvc.perform(delete(url)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(403))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
    }

    @Test
    @Rollback
    @Transactional
    void testDeleteChemTypeTwoTimes_whenAuthorized_got400SocondTime(@Autowired ChemTypeService chemTypeService) throws Exception {
        ChemType persistedChemType = chemTypeService.getChemTypes().stream().filter(ct -> ct.getName().equals(LabAdminTestUtils.SOLID_COMPOUND_NAME)).findAny().get();
        String url = URL_CHEM_TYPE + "/" + persistedChemType.getId();

        MvcResult result1 = mvc.perform(delete(url)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
        logger.info("status code: " + result1.getResponse().getStatus());
        MvcResult result2 = mvc.perform(delete(url)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

    }

    @Test
    @Rollback
    @Transactional
    void testDeleteChemType_WithNonExistingId_got204(@Autowired ChemTypeService chemTypeService) throws Exception {
        String url = URL_CHEM_TYPE + "/" + Integer.MAX_VALUE;
        MvcResult result = mvc.perform(delete(url)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
    }

    @Test
    @Rollback
    @Transactional
    void testDeleteDeletedChemType_whenLabManager_got204(@Autowired ChemTypeService chemTypeService) throws Exception {
        ChemType persistedChemType = chemTypeService.getChemTypes(false).stream().filter(ct -> ct.getName().equals(LabAdminTestUtils.PHOSPHATE_SOLUTION_NAME)).findAny().get();
        String url = URL_CHEM_TYPE + "/" + persistedChemType.getId();

        MvcResult result = mvc.perform(delete(url)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
    }


    // UPDATE
    @Test
    @Rollback
    @Transactional
    void testUpdateChemType_withAlphaLabManager_got201(@Autowired ChemTypeService chemTypeService) throws Exception {
        ChemTypeInput input = LabAdminTestUtils.getSolidCompoundInput();
        Long id = chemTypeService.getChemTypes().stream()
                .filter(storedInput ->  input.getName().equals(storedInput.getName()))
                .findAny()
                .get()
                .getId();
        String changedName = "Changed name";
        input.setName(changedName);
        MvcResult result = mvc.perform(put(URL_CHEM_TYPE + "/" + id)
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
    void testUpdateChemType_withAccountManager_got201(@Autowired ChemTypeService chemTypeService) throws Exception {
        ChemTypeInput input = LabAdminTestUtils.getSolidCompoundInput();
        Long id = chemTypeService.getChemTypes().stream()
                .filter(storedInput ->  input.getName().equals(storedInput.getName()))
                .findAny()
                .get()
                .getId();
        String changedName = "Changed name";
        input.setName(changedName);
        MvcResult result = mvc.perform(put(URL_CHEM_TYPE + "/" + id)
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
    void testUpdateChemType_withAlphaLabAdmin_got400(@Autowired ChemTypeService chemTypeService) throws Exception {
        ChemTypeInput input = LabAdminTestUtils.getSolidCompoundInput();
        Long id = chemTypeService.getChemTypes().stream()
                .filter(storedInput ->  input.getName().equals(storedInput.getName()))
                .findAny()
                .get()
                .getId();
        String changedName = "Changed name";
        input.setName(changedName);
        MvcResult result = mvc.perform(put(URL_CHEM_TYPE + "/" + id)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(403))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateChemType_withAlphaLabUser_got400(@Autowired ChemTypeService chemTypeService) throws Exception {
        ChemTypeInput input = LabAdminTestUtils.getSolidCompoundInput();
        Long id = chemTypeService.getChemTypes().stream()
                .filter(storedInput ->  input.getName().equals(storedInput.getName()))
                .findAny()
                .get()
                .getId();
        String changedName = "Changed name";
        input.setName(changedName);
        MvcResult result = mvc.perform(put(URL_CHEM_TYPE + "/" + id)
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
    void testUpdateChemType_withAlphaLabManager_fetchedFromDbIsExpected(@Autowired ChemTypeService chemTypeService) throws Exception {
        ChemTypeInput input = LabAdminTestUtils.getSolidCompoundInput();
        Long id = chemTypeService.getChemTypes().stream()
                .filter(storedInput ->  input.getName().equals(storedInput.getName()))
                .findAny()
                .get()
                .getId();
        String changedName = "Changed name";
        input.setName(changedName);
        MvcResult result = mvc.perform(put(URL_CHEM_TYPE + "/" + id)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().isCreated())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
        ChemType organicSolvent = chemTypeService.findById(id);
        Assertions.assertEquals(input.getName(), organicSolvent.getName());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateChemType_withEmptyInput1_got400(@Autowired ChemTypeService chemTypeService) throws Exception {
        ChemTypeInput input = LabAdminTestUtils.getSolidCompoundInput();
        Long id = chemTypeService.getChemTypes().stream()
                .filter(storedInput ->  input.getName().equals(storedInput.getName()))
                .findAny()
                .get()
                .getId();
        MvcResult result = mvc.perform(put(URL_CHEM_TYPE + "/" + id)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateChemType_withEmptyInput2_got400(@Autowired ChemTypeService chemTypeService) throws Exception {
        ChemTypeInput input = LabAdminTestUtils.getSolidCompoundInput();
        Long id = chemTypeService.getChemTypes().stream()
                .filter(storedInput ->  input.getName().equals(storedInput.getName()))
                .findAny()
                .get()
                .getId();
        MvcResult result = mvc.perform(put(URL_CHEM_TYPE + "/" + id)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateChemType_withEmptyName1_got400(@Autowired ChemTypeService chemTypeService) throws Exception {
        ChemTypeInput input = LabAdminTestUtils.getSolidCompoundInput();
        Long id = chemTypeService.getChemTypes().stream()
                .filter(storedInput ->  input.getName().equals(storedInput.getName()))
                .findAny()
                .get()
                .getId();
        input.setName(null);
        MvcResult result = mvc.perform(put(URL_CHEM_TYPE + "/" + id)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }
    @Test
    @Rollback
    @Transactional
    void testUpdateChemType_withEmptyName2_got400(@Autowired ChemTypeService chemTypeService) throws Exception {
        ChemTypeInput input = LabAdminTestUtils.getSolidCompoundInput();
        Long id = chemTypeService.getChemTypes().stream()
                .filter(storedInput ->  input.getName().equals(storedInput.getName()))
                .findAny()
                .get()
                .getId();
        input.setName("");
        MvcResult result = mvc.perform(put(URL_CHEM_TYPE + "/" + id)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateChemType_whenNameAlreadyExists_got400(@Autowired ChemTypeService chemTypeService) throws Exception {
        ChemTypeInput input = LabAdminTestUtils.getSolidCompoundInput();
        Long id = chemTypeService.getChemTypes().stream()
                .filter(storedInput ->  input.getName().equals(storedInput.getName()))
                .findAny()
                .get()
                .getId();
        input.setName(LabAdminTestUtils.PHOSPHATE_SOLUTION_NAME);
        MvcResult result = mvc.perform(put(URL_CHEM_TYPE + "/" + id)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }


    @Test
    @Rollback
    @Transactional
    void testUpdateChemType_whenIdDoesNotExist_got404(@Autowired ChemTypeService chemTypeService) throws Exception {
        ChemTypeInput input = LabAdminTestUtils.getPhosphateSolutionInput();
        input.setName("Changed input");
        MvcResult result = mvc.perform(put(URL_CHEM_TYPE + "/" + Integer.MAX_VALUE)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(404))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }


    // CREATE
    @Test
    @Rollback
    @Transactional
    void testChemType_withAlphaLabManager_got201() throws Exception {
        ChemTypeInput input = LabAdminTestUtils.getOrganicSolvantInput();
        MvcResult result = mvc.perform(post(URL_CHEM_TYPE)
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
    void testChemType_withAccountManager_got201() throws Exception {
        ChemTypeInput input = LabAdminTestUtils.getOrganicSolvantInput();
        MvcResult result = mvc.perform(post(URL_CHEM_TYPE)
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
    void testChemType_withAlphaLabAdmin_got403() throws Exception {
        ChemTypeInput input = LabAdminTestUtils.getOrganicSolvantInput();
        MvcResult result = mvc.perform(post(URL_CHEM_TYPE)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(403))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testChemType_withAlphaLabUser_got403() throws Exception {
        ChemTypeInput input = LabAdminTestUtils.getOrganicSolvantInput();
        MvcResult result = mvc.perform(post(URL_CHEM_TYPE)
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
    void testChemType_withAlphaLabManager_fetchedFromDbIsExpected(@Autowired ChemTypeService chemTypeService) throws Exception {
        ChemTypeInput input = LabAdminTestUtils.getOrganicSolvantInput();
        MvcResult result = mvc.perform(post(URL_CHEM_TYPE)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().isCreated())
                .andReturn();
        JSONObject testChemItemJSONObject = new JSONObject(result.getResponse().getContentAsString());
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
        ChemType organicSolvent = chemTypeService.findById(testChemItemJSONObject.getLong("id"));
        Assertions.assertEquals(input.getName(), organicSolvent.getName());
    }

    @Test
    @Rollback
    @Transactional
    void testChemType_withEmptyInput1_got400() throws Exception {
        MvcResult result = mvc.perform(post(URL_CHEM_TYPE)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testChemType_withEmptyInput2_got400() throws Exception {
        MvcResult result = mvc.perform(post(URL_CHEM_TYPE)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testChemType_whenEmptyName1_got400() throws Exception {
        ChemTypeInput input = LabAdminTestUtils.getOrganicSolvantInput();
        input.setName(null);
        MvcResult result = mvc.perform(post(URL_CHEM_TYPE)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testChemType_whenEmptyName2_got400() throws Exception {
        ChemTypeInput input = LabAdminTestUtils.getOrganicSolvantInput();
        input.setName("");
        MvcResult result = mvc.perform(post(URL_CHEM_TYPE)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testChemType_withAlreadyExists_got400() throws Exception {
        ChemTypeInput input = LabAdminTestUtils.getOrganicSolvantInput();
        input.setName(LabAdminTestUtils.SOLID_COMPOUND_NAME);
        MvcResult result = mvc.perform(post(URL_CHEM_TYPE)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }


    @Test
    void testGetAllUsersFromManagedLabs_whenAlphaLabManager_gotCorrectResponseObject() throws Exception{
        mvc.perform(get(URL_USER)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[*]").isNotEmpty())
                .andExpect(jsonPath("$[*]").isArray())
        ;
    }

    @Test
    void testGetAllUsersFromManagedLabs_whenAlphaLabManager_hasAlphaLabUserInAlphaLab() throws Exception{
        mvc.perform(get(URL_USER)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$['alab'][*].username", hasItem(AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME)))
        ;
    }

    @Test
    void testGetAllUsersFromManagedLabs_whenAlphaLabManager_doesNotHaveItemBetaLabUser() throws Exception{
        mvc.perform(get(URL_USER)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$['alab'][*].username", IsNot.not(hasItem(AccountManagerTestUtils.BETA_LAB_USER_USERNAME))))
        ;
    }

    @Test
    void testGetAllUsersFromManagedLabs_whenAlphaLabManager_hasNoBetaLabUsers() throws Exception{
        mvc.perform(get(URL_USER)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$['blab']").doesNotExist())
        ;
    }


    @Test
    void testGetAllUsersFromManagedLabs_whenAlphaLabUser_got403() throws Exception{
        MvcResult result = mvc.perform(get(URL_USER)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(403))
                .andReturn()
        ;
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    void testGetAllUsersFromManagedLabs_whenAlphaLabAdmin_got403() throws Exception{
        MvcResult result = mvc.perform(get(URL_USER)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(403))
                .andReturn()
                ;
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    void testGetAllUsersFromManagedLabs_whenAlphaLabManager_didNotgetBetaLabUsers() throws Exception{
        MvcResult result = mvc.perform(get(URL_USER)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$['blab']").doesNotExist())
                .andReturn()
        ;
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    void testGetAllUsersFromAlphaLab_whenAlphaLabManager_gotTwoUsers() throws Exception{
        mvc.perform(get(URL_ALPHA_LAB)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testGetAllUsersFromAlphaLab_whenAlphaLabManager_hasItemAlphaLabUser() throws Exception{
        mvc.perform(get(URL_ALPHA_LAB)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[*].username", hasItem(AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME)))
        ;
    }

    @Test
    void testGetAllUsersFromAlphaLab_whenAlphaLabManager_doesNotHaveItemBetaLabUser() throws Exception{
        mvc.perform(get(URL_ALPHA_LAB)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[*].username", IsNot.not(hasItem(AccountManagerTestUtils.BETA_LAB_USER_USERNAME))))
        ;
    }

    @Test
    void testGetAllUsersFromAlphaLab_whenAlphaLabAdmin_got403() throws Exception{
        MvcResult result = mvc.perform(get(URL_ALPHA_LAB)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(403))
                .andReturn()
                ;
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    void testGetAllUsersFromAlphaLab_whenAlphaLabUser_got403() throws Exception{
        MvcResult result = mvc.perform(get(URL_ALPHA_LAB)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(403))
                .andReturn()
                ;
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

}
