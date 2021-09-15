package com.ksteindl.chemstore.web;

import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.domain.input.LabInput;
import com.ksteindl.chemstore.security.JwtProvider;
import com.ksteindl.chemstore.service.LabService;
import com.ksteindl.chemstore.web.utils.AccountManagerTestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AccountManagerLabControllerTest extends BaseControllerTest{

    private static final Logger logger = LogManager.getLogger(AccountManagerLabControllerTest.class);

    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    private MockMvc mvc;

    private static final String URL = "/api/account/lab";

    // Delete
    @Test
    @Rollback
    @Transactional
    void testDeleteAlphaLab_whenAuthorized_got204(@Autowired LabService labService) throws Exception {
        Lab persistedAlphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);

        MvcResult result = mvc.perform(delete(URL + "/" + persistedAlphaLab.getId())
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
    }
//
//    @Test
//    @Disabled // TODO ResourceNotFound Exception is expected, else fail()
//    @Rollback
//    @Transactional
//    void testDeleteAlphaLabUser_whenAuthorized_gotResourceNotFoundException(@Autowired AppUserService appUserService) throws Exception {
//        AppUser persistedAlphaLabUser = appUserService.findByUsername(TestUtils.ALPHA_LAB_USER_USERNAME);
//        String url = "/api/account/user/" + persistedAlphaLabUser.getId();
//        mvc.perform(delete(url).header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON));
//        AppUser deletedAppUser = appUserService.findById(persistedAlphaLabUser.getId());
//
//    }
    @Test
    @Rollback
    @Transactional
    void testDeleteAlphaLab_whenAuthorized_gotDeletedFromService(@Autowired LabService labService) throws Exception {
        Lab persistedAlphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);

        mvc.perform(delete(URL + "/" + persistedAlphaLab.getId())
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON));
        Lab alphaLab = labService.findById(persistedAlphaLab.getId(), false);
        Assertions.assertTrue(alphaLab.getDeleted());
    }

    @Test
    @Rollback
    @Transactional
    void testDeleteAlphaLabTwoTimes_whenAuthorized_got400SocondTime(@Autowired LabService labService) throws Exception {
        Lab persistedAlphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        String url = URL + "/" + persistedAlphaLab.getId();
        MvcResult result1 = mvc.perform(delete(url)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
        MvcResult result2 = mvc.perform(delete(url)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        logger.info("status code: " + result1.getResponse().getStatus());
        logger.info("status code: " + result2.getResponse().getStatus());
    }

    @Test
    @Rollback
    @Transactional
    void testDeleteAlphaLabWithNonExistingId_whenAuthorized_got404(@Autowired LabService labService) throws Exception {
        MvcResult result = mvc.perform(delete(URL + "/" + Integer.MAX_VALUE)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
    }

    @Test
    @Rollback
    @Transactional
    void testDeleteAlphaLab_withAlphaLabManager_got403(@Autowired LabService labService) throws Exception {
        Lab persistedAlphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        String token = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_LAB_MANAGER_USERNAME);
        MvcResult result = mvc.perform(delete(URL + "/" + persistedAlphaLab.getId())
                .header("Authorization", token).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
    }

    @Test
    @Rollback
    @Transactional
    void testDeleteAlphaLab_withAlphaLabAdmin_got403(@Autowired LabService labService) throws Exception {
        Lab persistedAlphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        String token = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_LAB_ADMIN_USERNAME);
        MvcResult result = mvc.perform(delete(URL + "/" + persistedAlphaLab.getId())
                .header("Authorization", token).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
    }

    @Test
    @Rollback
    @Transactional
    void testDeleteAlphaLab_withAlphaLabUser_got403(@Autowired LabService labService) throws Exception {
        Lab persistedAlphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        String token = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME);
        MvcResult result = mvc.perform(delete(URL + "/" + persistedAlphaLab.getId())
                .header("Authorization", token).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
    }


    // UPDATE
    @Test
    @Transactional
    @Rollback
    void testUpdateLab_whenAuthorized_got201(@Autowired LabService labService) throws Exception {
        LabInput alphaLabInput = AccountManagerTestUtils.getAlphaLabInput();
        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        String changedLabName = "Changed Alpha Lab";
        alphaLabInput.setName(changedLabName);
        MvcResult result = mvc.perform(put(URL + "/" + alphaLab.getId())
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(alphaLabInput)))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.key").isNotEmpty())
                .andExpect(jsonPath("$.key").value(alphaLab.getKey()))
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(jsonPath("$.name").value(changedLabName))
                .andExpect(jsonPath("$.deleted").isBoolean())
                .andExpect(jsonPath("$.deleted").value(false))
                .andExpect(jsonPath("$.labManagerUsernames").isArray())
                .andExpect(jsonPath("$.labManagerUsernames[0]").isNotEmpty())
                .andExpect(jsonPath("$.labManagerUsernames[*]").value(Matchers.containsInAnyOrder(alphaLabInput.getLabManagerUsernames().get(0), alphaLabInput.getLabManagerUsernames().get(1))))
                .andExpect(status().isOk())
                .andReturn();
        Lab changedGammaLab = labService.findLabByKey(alphaLabInput.getKey());
        Assertions.assertEquals(alphaLabInput.getKey(), changedGammaLab.getKey());
        Assertions.assertEquals(alphaLabInput.getName(), changedGammaLab.getName());
        Assertions.assertEquals(alphaLabInput.getLabManagerUsernames(), changedGammaLab.getLabManagerUsernames());
        Assertions.assertTrue(!changedGammaLab.getDeleted());
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testUpdateLab_whenAlphaBetaLabManager_got403(@Autowired LabService labService) throws Exception {
        LabInput alphaLabInput = AccountManagerTestUtils.getAlphaLabInput();
        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        String changedLabName = "Changed Alpha Lab";
        alphaLabInput.setName(changedLabName);
        String token = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_BETA_LAB_MANAGER_USERNAME);
        MvcResult result = mvc.perform(put(URL + "/" + alphaLab.getId())
                .header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(alphaLab)))
                .andExpect(status().is(403))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testUpdateLab_whenAlphaBetaLabAdmin_got403(@Autowired LabService labService) throws Exception {
        LabInput alphaLabInput = AccountManagerTestUtils.getAlphaLabInput();
        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        String changedLabName = "Changed Alpha Lab";
        alphaLabInput.setName(changedLabName);
        String token = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_BETA_LAB_ADMIN_USERNAME);
        MvcResult result = mvc.perform(put(URL + "/" + alphaLab.getId())
                .header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(alphaLab)))
                .andExpect(status().is(403))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }


    @Test
    @Transactional
    @Rollback
    void testUpdateLab_whenAlphaBetaLabUser_got403(@Autowired LabService labService) throws Exception {
        LabInput alphaLabInput = AccountManagerTestUtils.getAlphaLabInput();
        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        String changedLabName = "Changed Alpha Lab";
        alphaLabInput.setName(changedLabName);
        String token = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_BETA_LAB_USER_USERNAME);
        MvcResult result = mvc.perform(put(URL + "/" + alphaLab.getId())
                .header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(alphaLab)))
                .andExpect(status().is(403))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }



    @Test
    @Transactional
    @Rollback
    void testUpdateLab_whenEmpty1_gotNOK(@Autowired LabService labService) throws Exception {
        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        MvcResult result = mvc.perform(put(URL + "/" + alphaLab.getId())
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testUpdateLab_whenEmpty2_gotNOK(@Autowired LabService labService) throws Exception {
        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        MvcResult result = mvc.perform(put(URL + "/" + alphaLab.getId())
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testUpdateLab_whenKeyIsChanged_got400(@Autowired LabService labService) throws Exception {
        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        LabInput alphaLabInput = AccountManagerTestUtils.getAlphaLabInput();
        alphaLabInput.setKey("changed-key");
        MvcResult result = mvc.perform(put(URL + "/" + alphaLab.getId())
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(alphaLabInput)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.key").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testUpdateLab_whenNameIsEmpty1_got400(@Autowired LabService labService) throws Exception {
        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        LabInput alphaLabInput = AccountManagerTestUtils.getAlphaLabInput();
        alphaLabInput.setName(null);
        MvcResult result = mvc.perform(put(URL + "/" + alphaLab.getId())
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(alphaLabInput)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testUpdateLab_whenNameIsEmpty2_got400(@Autowired LabService labService) throws Exception {
        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        LabInput alphaLabInput = AccountManagerTestUtils.getAlphaLabInput();
        alphaLabInput.setName("");
        MvcResult result = mvc.perform(put(URL + "/" + alphaLab.getId())
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(alphaLabInput)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testUpdateLab_whenNameIsEmpty3_got400(@Autowired LabService labService) throws Exception {
        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        LabInput alphaLabInput = AccountManagerTestUtils.getAlphaLabInput();
        alphaLabInput.setName("  ");
        MvcResult result = mvc.perform(put(URL + "/" + alphaLab.getId())
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(alphaLabInput)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testUpdateLab_whenNameIsNotUnique_got400(@Autowired LabService labService) throws Exception {
        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        LabInput alphaLabInput = AccountManagerTestUtils.getAlphaLabInput();
        alphaLabInput.setName(AccountManagerTestUtils.BETA_LAB_NAME);
        MvcResult result = mvc.perform(put(URL + "/" + alphaLab.getId())
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(alphaLabInput)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testUpdateLab_whenLabManagerUsernameIsEmpty1_got400(@Autowired LabService labService) throws Exception {
        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        LabInput alphaLabInput = AccountManagerTestUtils.getAlphaLabInput();
        alphaLabInput.setLabManagerUsernames(null);
        MvcResult result = mvc.perform(put(URL + "/" + alphaLab.getId())
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(alphaLabInput)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.labManagerUsernames").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }


    @Test
    @Transactional
    @Rollback
    void testUpdateLab_whenLabManagerUsernameIsEmpty2_got400(@Autowired LabService labService) throws Exception {
        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        LabInput alphaLabInput = AccountManagerTestUtils.getAlphaLabInput();
        alphaLabInput.setLabManagerUsernames(new ArrayList<>());
        MvcResult result = mvc.perform(put(URL + "/" + alphaLab.getId())
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(alphaLabInput)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.labManagerUsernames").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testUpdateLab_whenLabManagerUsernameDoesNotExist1_got400(@Autowired LabService labService) throws Exception {
        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        LabInput alphaLabInput = AccountManagerTestUtils.getAlphaLabInput();
        alphaLabInput.setLabManagerUsernames(List.of("nonexistinguser@account.com"));
        MvcResult result = mvc.perform(put(URL + "/" + alphaLab.getId())
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(alphaLabInput)))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.app-user").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testUpdateLab_whenLabManagerUsernameDoesNotExist2_got400(@Autowired LabService labService) throws Exception {
        Lab alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        LabInput alphaLabInput = AccountManagerTestUtils.getAlphaLabInput();
        alphaLabInput.setLabManagerUsernames(List.of(AccountManagerTestUtils.BETA_LAB_MANAGER_USERNAME, "nonexistinguser@account.com"));
        MvcResult result = mvc.perform(put(URL + "/" + alphaLab.getId())
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(alphaLabInput)))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.app-user").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }



    // CREATE
    @Test
    @Transactional
    @Rollback
    void testCreateLab_whenAuthorized_got201(@Autowired LabService labService) throws Exception {
        LabInput labInput = AccountManagerTestUtils.getGammaLabInput();
        MvcResult result = mvc.perform(post(URL)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(labInput)))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.key").isNotEmpty())
                .andExpect(jsonPath("$.key").value(labInput.getKey()))
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(jsonPath("$.name").value(labInput.getName()))
                .andExpect(jsonPath("$.deleted").isBoolean())
                .andExpect(jsonPath("$.deleted").value(false))
                .andExpect(jsonPath("$.labManagerUsernames").isArray())
                .andExpect(jsonPath("$.labManagerUsernames[0]").isNotEmpty())
                .andExpect(jsonPath("$.labManagerUsernames[*]").value(Matchers.containsInAnyOrder(labInput.getLabManagerUsernames().get(0), labInput.getLabManagerUsernames().get(1))))
                .andExpect(status().isCreated())
                .andReturn();
        Lab gammaLab = labService.findLabByKey(labInput.getKey());
        Assertions.assertEquals(labInput.getKey(), gammaLab.getKey());
        Assertions.assertEquals(labInput.getName(), gammaLab.getName());
        Assertions.assertEquals(labInput.getLabManagerUsernames(), gammaLab.getLabManagerUsernames());
        Assertions.assertTrue(!gammaLab.getDeleted());
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testCreateLab_whenAlphaBetaLabManager_got403() throws Exception {
        String token = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_BETA_LAB_MANAGER_USERNAME);
        MvcResult result = mvc.perform(post(URL)
                .header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(AccountManagerTestUtils.getGammaLabInput())))
                .andExpect(status().is(403))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testCreateLab_whenAlphaBetaLabAdmin_got403() throws Exception {
        String token = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_BETA_LAB_ADMIN_USERNAME);
        MvcResult result = mvc.perform(post(URL)
                .header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(AccountManagerTestUtils.getGammaLabInput())))
                .andExpect(status().is(403))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testCreateLab_whenAlphaBetaLabUser_got403() throws Exception {
        String token = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_BETA_LAB_USER_USERNAME);
        MvcResult result = mvc.perform(post(URL)
                .header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(AccountManagerTestUtils.getGammaLabInput())))
                .andExpect(status().is(403))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testCreateLab_whenEmpty0_gotNOK() throws Exception {

        MvcResult result = mvc.perform(post(URL)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testCreateLab_whenEmpty1_gotNOK() throws Exception {

        MvcResult result = mvc.perform(post(URL)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testCreateLab_whenEmpty2_gotNOK() throws Exception {

        MvcResult result = mvc.perform(post(URL)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testCreateLab_whenKeyIsInvalid1_gotNOK() throws Exception {
        LabInput gammaLabInput = AccountManagerTestUtils.getGammaLabInput();
        gammaLabInput.setKey(null);
        MvcResult result = mvc.perform(post(URL)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gammaLabInput)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.key").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testCreateLab_whenKeyIsInvalid2_gotNOK() throws Exception {
        LabInput gammaLabInput = AccountManagerTestUtils.getGammaLabInput();
        gammaLabInput.setKey("");
        MvcResult result = mvc.perform(post(URL)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gammaLabInput)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.key").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testCreateLab_whenKeyIsInvalid3_gotNOK() throws Exception {
        LabInput gammaLabInput = AccountManagerTestUtils.getGammaLabInput();
        gammaLabInput.setKey(" ");
        MvcResult result = mvc.perform(post(URL)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gammaLabInput)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.key").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testCreateLab_whenKeyIsInvalid4_gotNOK() throws Exception {
        LabInput gammaLabInput = AccountManagerTestUtils.getGammaLabInput();
        gammaLabInput.setKey(" hello");
        MvcResult result = mvc.perform(post(URL)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gammaLabInput)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.key").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testCreateLab_whenKeyIsInvalid5_gotNOK() throws Exception {
        LabInput gammaLabInput = AccountManagerTestUtils.getGammaLabInput();
        gammaLabInput.setKey("hello ");
        MvcResult result = mvc.perform(post(URL)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gammaLabInput)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.key").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testCreateLab_whenKeyIsInvalid6_gotNOK() throws Exception {
        LabInput gammaLabInput = AccountManagerTestUtils.getGammaLabInput();
        gammaLabInput.setKey("Hello");
        MvcResult result = mvc.perform(post(URL)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gammaLabInput)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.key").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testCreateLab_whenKeyIsInvalid7_gotNOK() throws Exception {
        LabInput gammaLabInput = AccountManagerTestUtils.getGammaLabInput();
        gammaLabInput.setKey("heLlo");
        MvcResult result = mvc.perform(post(URL)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gammaLabInput)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.key").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testCreateLab_whenKeyIsInvalid9_gotNOK() throws Exception {
        LabInput gammaLabInput = AccountManagerTestUtils.getGammaLabInput();
        gammaLabInput.setKey("hello world");
        MvcResult result = mvc.perform(post(URL)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gammaLabInput)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.key").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testCreateLab_whenKeyIsInvalid10_gotNOK() throws Exception {
        LabInput gammaLabInput = AccountManagerTestUtils.getGammaLabInput();
        gammaLabInput.setKey("hello-8world");
        MvcResult result = mvc.perform(post(URL)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gammaLabInput)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.key").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testCreateLab_whenKeyIsInvalid11_gotNOK() throws Exception {
        LabInput gammaLabInput = AccountManagerTestUtils.getGammaLabInput();
        gammaLabInput.setKey("Hello-world");
        MvcResult result = mvc.perform(post(URL)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gammaLabInput)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.key").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testCreateLab_whenKeyIsValid_gotNOK() throws Exception {
        LabInput gammaLabInput = AccountManagerTestUtils.getGammaLabInput();
        gammaLabInput.setKey("hello-world");
        MvcResult result = mvc.perform(post(URL)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gammaLabInput)))
                .andExpect(status().isCreated())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testCreateLab_whenKeyIsNotUnique_gotNOK() throws Exception {
        LabInput gammaLabInput = AccountManagerTestUtils.getGammaLabInput();
        gammaLabInput.setKey("alab");
        MvcResult result = mvc.perform(post(URL)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gammaLabInput)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testCreateLab_whenNameIsEmpty1_gotNOK() throws Exception {
        LabInput gammaLabInput = AccountManagerTestUtils.getGammaLabInput();
        gammaLabInput.setName(null);
        MvcResult result = mvc.perform(post(URL)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gammaLabInput)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testCreateLab_whenNameIsEmpty2_gotNOK() throws Exception {
        LabInput gammaLabInput = AccountManagerTestUtils.getGammaLabInput();
        gammaLabInput.setName("");
        MvcResult result = mvc.perform(post(URL)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gammaLabInput)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testCreateLab_whenNameIsEmpty3_gotNOK() throws Exception {
        LabInput gammaLabInput = AccountManagerTestUtils.getGammaLabInput();
        gammaLabInput.setName("  ");
        MvcResult result = mvc.perform(post(URL)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gammaLabInput)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testCreateLab_whenNameIsNotUnique_gotNOK() throws Exception {
        LabInput gammaLabInput = AccountManagerTestUtils.getGammaLabInput();
        gammaLabInput.setName("Alpha Lab");
        MvcResult result = mvc.perform(post(URL)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gammaLabInput)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testCreateLab_whenKeyAndNameAreNotUnique_gotNOK() throws Exception {
        LabInput gammaLabInput = AccountManagerTestUtils.getGammaLabInput();
        gammaLabInput.setKey("alab");
        gammaLabInput.setName("Alpha Lab");
        MvcResult result = mvc.perform(post(URL)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gammaLabInput)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testCreateLab_whenLabManagerUsernameIsEmpty1_gotNOK() throws Exception {
        LabInput gammaLabInput = AccountManagerTestUtils.getGammaLabInput();
        gammaLabInput.setLabManagerUsernames(null);
        MvcResult result = mvc.perform(post(URL)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gammaLabInput)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.labManagerUsernames").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testCreateLab_whenLabManagerUsernameIsEmpty2_gotNOK() throws Exception {
        LabInput gammaLabInput = AccountManagerTestUtils.getGammaLabInput();
        gammaLabInput.setLabManagerUsernames(new ArrayList<>());
        MvcResult result = mvc.perform(post(URL)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gammaLabInput)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.labManagerUsernames").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testCreateLab_whenLabManagerUsernameDoesNotExist1_gotNOK() throws Exception {
        LabInput gammaLabInput = AccountManagerTestUtils.getGammaLabInput();
        gammaLabInput.setLabManagerUsernames(List.of("nonexistinguser@account.com"));
        MvcResult result = mvc.perform(post(URL)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gammaLabInput)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.app-user").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testCreateLab_whenLabManagerUsernameDoesNotExist2_gotNOK() throws Exception {
        LabInput gammaLabInput = AccountManagerTestUtils.getGammaLabInput();
        gammaLabInput.setLabManagerUsernames(List.of(AccountManagerTestUtils.BETA_LAB_MANAGER_USERNAME, "nonexistinguser@account.com"));
        MvcResult result = mvc.perform(post(URL)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gammaLabInput)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.app-user").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }



}