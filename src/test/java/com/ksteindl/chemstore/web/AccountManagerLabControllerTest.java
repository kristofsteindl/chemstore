package com.ksteindl.chemstore.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.security.JwtProvider;
import com.ksteindl.chemstore.service.AppUserService;
import com.ksteindl.chemstore.service.LabService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Rollback
@ActiveProfiles("test")
class AccountManagerLabControllerTest {

    private static final Logger logger = LogManager.getLogger(AccountManagerLabControllerTest.class);

    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    private MockMvc mvc;
    private static String TOKEN_FOR_ACCOUNT_MANAGER;

    private static final String URL = "/api/account/lab";

    @BeforeAll
    static void setUpTestDb(
            @Autowired AppUserService appUserService,
            @Autowired LabService labService,
            @Autowired JwtProvider jwtProvider) {
        AppUser aman = appUserService.crateUser(TestUtils.getAccountManagerInput());
        TOKEN_FOR_ACCOUNT_MANAGER = jwtProvider.generateToken(TestUtils.ACCOUNT_MANAGER_USERNAME);
        System.out.println("Account Manager id " + aman.getId());

        AppUser alabman = appUserService.crateUser(TestUtils.ALPHA_LAB_MANAGER_INPUT);
        AppUser blabman = appUserService.crateUser(TestUtils.BETA_LAB_MANAGER_INPUT);
        AppUser ablabman = appUserService.crateUser(TestUtils.ALPHA_BETA_LAB_MANAGER_INPUT);

        AppUser alabadmin = appUserService.crateUser(TestUtils.ALPHA_LAB_ADMIN_INPUT);
        AppUser blabadmin = appUserService.crateUser(TestUtils.BETA_LAB_ADMIN_INPUT);
        AppUser ablabadmin = appUserService.crateUser(TestUtils.ALPHA_BETA_LAB_ADMIN_INPUT);

        AppUser alabuser = appUserService.crateUser(TestUtils.getAlphaLabUserInput());
        AppUser blabuser = appUserService.crateUser(TestUtils.BETA_LAB_USER_INPUT);
        AppUser ablabuser = appUserService.crateUser(TestUtils.ALPHA_BETA_LAB_USER_INPUT);
        AppUser ablabdeleteduser = appUserService.crateUser(TestUtils.ALPHA_BETA_LAB_DELETED_USER_INPUT);
        appUserService.deleteAppUser(ablabdeleteduser.getId());

        Lab alab = labService.createLab(TestUtils.ALPHA_LAB_INPUT);
        Lab blab = labService.createLab(TestUtils.BETA_LAB_INPUT);
    }


    // CREATE
    @Test
    @Transactional
    @Rollback
    void testCreateLab_whenAuthorized_got201() throws Exception {

        MvcResult result = mvc.perform(post(URL)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(TestUtils.getGammaLabInput())))
                .andExpect(status().isCreated())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

//    @Test
//    void testCreateAppUser_whenEmpty1_gotNOK() throws Exception {
//        String url = "/api/account/user";
//        MvcResult result = mvc.perform(post(url)
//                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(""))
//                .andExpect(status().isBadRequest())
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//    @Test
//    void testCreateAppUser_whenEmpty2_gotNOK() throws Exception {
//        String url = "/api/account/user";
//        MvcResult result = mvc.perform(post(url)
//                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{}"))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.username").isNotEmpty())
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//
//    @Test
//    void testCreateAppUser_whenNotEmailUsername_gotNOK() throws Exception {
//        String url = "/api/account/user";
//        AppUserInput appUserInput = TestUtils.getNewAccountManagerInputWithSomeLabs();
//        appUserInput.setUsername("newaman");
//        MvcResult result = mvc.perform(post(url)
//                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(appUserInput)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.username").isNotEmpty())
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//    @Test
//    void testCreateAppUser_whenNotUsernameNotUnique_gotNOK() throws Exception {
//        String url = "/api/account/user";
//        AppUserInput appUserInput = TestUtils.getNewAccountManagerInputWithSomeLabs();
//        appUserInput.setUsername(TestUtils.ACCOUNT_MANAGER_USERNAME);
//        MvcResult result = mvc.perform(post(url)
//                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(appUserInput)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message").isNotEmpty())
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//    @Test
//    void testCreateAppUser_whenNotUsernameEmpty_gotNOK() throws Exception {
//        String url = "/api/account/user";
//        AppUserInput appUserInput = TestUtils.getNewAccountManagerInputWithSomeLabs();
//        appUserInput.setUsername("");
//        MvcResult result = mvc.perform(post(url)
//                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(appUserInput)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.username").isNotEmpty())
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//    @Test
//    void testCreateAppUser_whenFullNameEmpty_gotNOK() throws Exception {
//        String url = "/api/account/user";
//        AppUserInput appUserInput = TestUtils.getNewAccountManagerInputWithSomeLabs();
//        appUserInput.setFullName("");
//        MvcResult result = mvc.perform(post(url)
//                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(appUserInput)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.fullName").isNotEmpty())
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//    @Test
//    void testCreateAppUser_whenPasswordEmpty_gotNOK() throws Exception {
//        String url = "/api/account/user";
//        AppUserInput appUserInput = TestUtils.getNewAccountManagerInputWithSomeLabs();
//        appUserInput.setPassword("");
//        MvcResult result = mvc.perform(post(url)
//                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(appUserInput)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.password").isNotEmpty())
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//    @Test
//    void testCreateAppUser_whenPasswordNotTheSame_gotNOK() throws Exception {
//        String url = "/api/account/user";
//        AppUserInput appUserInput = TestUtils.getNewAccountManagerInputWithSomeLabs();
//        appUserInput.setPassword("fooooo");
//        appUserInput.setPassword2("barrrrr");
//        MvcResult result = mvc.perform(post(url)
//                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(appUserInput)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.password").isNotEmpty())
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//    @Test
//    void testCreateAppUser_whenOneOfLabAsAdminKeyIsInvalid_gotNOK() throws Exception {
//        String url = "/api/account/user";
//        AppUserInput appUserInput = TestUtils.getNewAccountManagerInputWithSomeLabs();
//        appUserInput.setLabKeysAsAdmin(TestUtils.LAB_KEYS_WITH_INVALID_AND_VALID);
//        MvcResult result = mvc.perform(post(url)
//                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(appUserInput)))
//                .andExpect(status().is(404))
//                .andExpect(jsonPath("$.lab").isNotEmpty())
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//    @Test
//    void testCreateAppUser_whenOneOfLabAsUserKeyIsInvalid_gotNOK() throws Exception {
//        String url = "/api/account/user";
//        AppUserInput appUserInput = TestUtils.getNewAccountManagerInputWithSomeLabs();
//        appUserInput.setLabKeysAsUser(TestUtils.LAB_KEYS_WITH_INVALID_AND_VALID);
//        MvcResult result = mvc.perform(post(url)
//                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(appUserInput)))
//                .andExpect(status().is(404))
//                .andExpect(jsonPath("$.lab").isNotEmpty())
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }


    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



}