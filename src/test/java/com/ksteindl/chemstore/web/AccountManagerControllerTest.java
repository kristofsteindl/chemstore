package com.ksteindl.chemstore.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.domain.input.AppUserInput;
import com.ksteindl.chemstore.security.JwtProvider;
import com.ksteindl.chemstore.service.AppUserService;
import com.ksteindl.chemstore.service.LabService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
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

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AccountManagerControllerTest {

    private static final Logger logger = LogManager.getLogger(AccountManagerControllerTest.class);

    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    private MockMvc mvc;
    private static String TOKEN_FOR_ACCOUNT_MANAGER;

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

    // UPDATE
    @Test
    @Rollback
    void testUpdateAlabUser_whenAuthorized_got201AndAuthorizedForGetAllUser(@Autowired AppUserService appUserService) throws Exception {
        AppUser persistedAlphaLabUserManager = appUserService.findByUsername(TestUtils.ALPHA_LAB_USER_USERNAME);
        AppUserInput alabuserInput = TestUtils.getAlphaLabUserInput();
        String newFullName = "Changed Alpha Lab User";
        alabuserInput.setFullName(newFullName);

        String url = "/api/account/user/" + persistedAlphaLabUserManager.getId();

        MvcResult result = mvc.perform(put(url)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(alabuserInput)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value(newFullName))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
        // tests must check only one thing, so this is an antipattern, but I want to check if the freshly created user has the proper privlilige
        testGetAllAppUser_whenAuthorized_gotValidResponse(TestUtils.ACCOUNT_MANAGER_USERNAME);
    }

    @Test
    @Rollback
    void testUpdateAppUserWhenIdAndUsernameDoesNotMatch_whenAuthorized_got400(@Autowired AppUserService appUserService) throws Exception {
        AppUser persistedNewAccountManager = appUserService.findByUsername(TestUtils.ACCOUNT_MANAGER_USERNAME);
        logger.debug("in test 2, persistedNewAccountManager fullName: " + persistedNewAccountManager.getFullName());
        AppUserInput amanInput = TestUtils.getAccountManagerInput();
        String newFullName = "Changed Account Manager";
        amanInput.setFullName(newFullName);
        Long alabuserId = appUserService.findByUsername(TestUtils.ALPHA_LAB_USER_USERNAME).getId();
        String url = "/api/account/user/" + alabuserId;

        MvcResult result = mvc.perform(put(url)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(amanInput)))
                .andExpect(status().isBadRequest())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    void testUpdateAppUserWithNonExistingId_whenAuthorized_got400(@Autowired AppUserService appUserService) throws Exception {
        String url = "/api/account/user/" + Integer.MAX_VALUE;
        MvcResult result = mvc.perform(put(url)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(TestUtils.getAccountManagerInput())))
                .andExpect(status().isNotFound())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    void testUpdateAlabuserAppUser_whenEmpty1_gotNOK(@Autowired AppUserService appUserService) throws Exception {

        AppUser persistedAlphaLabUserManager = appUserService.findByUsername(TestUtils.ALPHA_LAB_USER_USERNAME);
        AppUserInput alabuserInput = TestUtils.getAlphaLabUserInput();
        String newFullName = "Changed Alpha Lab User";
        alabuserInput.setFullName(newFullName);

        String url = "/api/account/user/" + persistedAlphaLabUserManager.getId();
        MvcResult result = mvc.perform(put(url)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    void testUpdateAlabuserAppUser_whenEmpty2_gotNOK(@Autowired AppUserService appUserService) throws Exception {
        AppUser persistedAlphaLabUserManager = appUserService.findByUsername(TestUtils.ALPHA_LAB_USER_USERNAME);
        String url = "/api/account/user/" + persistedAlphaLabUserManager.getId();

        AppUserInput alabuserInput = TestUtils.getAlphaLabUserInput();
        String newFullName = "Changed Alpha Lab User";
        alabuserInput.setFullName(newFullName);

        MvcResult result = mvc.perform(put(url)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }


    @Test
    void testUpdateAlabUser_whenNotEmailUsername_gotNOK(@Autowired AppUserService appUserService) throws Exception {
        AppUser persistedAlphaLabUserManager = appUserService.findByUsername(TestUtils.ALPHA_LAB_USER_USERNAME);
        String url = "/api/account/user/" + persistedAlphaLabUserManager.getId();

        AppUserInput appUserInput = TestUtils.getAlphaLabUserInput();
        appUserInput.setUsername("newaman");
        MvcResult result = mvc.perform(put(url)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(appUserInput)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }
//
//    @Test
//    void testCreateAppUser_whenNotUsernameNotUnique_gotNOK() throws Exception {
//        String url = "/api/account/user";
//        String token = jwtProvider.generateToken(TestUtils.ACCOUNT_MANAGER_USERNAME);
//        AppUserInput appUserInput = TestUtils.getNewAccountManagerInput();
//        appUserInput.setUsername(TestUtils.ACCOUNT_MANAGER_USERNAME);
//        MvcResult result = mvc.perform(post(url)
//                .header("Authorization", token)
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
//        String token = jwtProvider.generateToken(TestUtils.ACCOUNT_MANAGER_USERNAME);
//        AppUserInput appUserInput = TestUtils.getNewAccountManagerInput();
//        appUserInput.setUsername("");
//        MvcResult result = mvc.perform(post(url)
//                .header("Authorization", token)
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
//        String token = jwtProvider.generateToken(TestUtils.ACCOUNT_MANAGER_USERNAME);
//        AppUserInput appUserInput = TestUtils.getNewAccountManagerInput();
//        appUserInput.setFullName("");
//        MvcResult result = mvc.perform(post(url)
//                .header("Authorization", token)
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
//        String token = jwtProvider.generateToken(TestUtils.ACCOUNT_MANAGER_USERNAME);
//        AppUserInput appUserInput = TestUtils.getNewAccountManagerInput();
//        appUserInput.setPassword("");
//        MvcResult result = mvc.perform(post(url)
//                .header("Authorization", token)
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
//        String token = jwtProvider.generateToken(TestUtils.ACCOUNT_MANAGER_USERNAME);
//        AppUserInput appUserInput = TestUtils.getNewAccountManagerInput();
//        appUserInput.setPassword("fooooo");
//        appUserInput.setPassword2("barrrrr");
//        MvcResult result = mvc.perform(post(url)
//                .header("Authorization", token)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(appUserInput)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.password").isNotEmpty())
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }

//    @Test
//    void testCreateAppUser_whenOneOfLabAsAdminKeyIsInvalid_gotNOK() throws Exception {
//        String url = "/api/account/user";
//        String token = jwtProvider.generateToken(TestUtils.ACCOUNT_MANAGER_USERNAME);
//        AppUserInput appUserInput = TestUtils.getNewAccountManagerInputWithSomeLabs();
//        appUserInput.setLabKeysAsAdmin(TestUtils.LAB_KEYS_WITH_INVALID_AND_VALID);
//        MvcResult result = mvc.perform(post(url)
//                .header("Authorization", token)
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
//        String token = jwtProvider.generateToken(TestUtils.ACCOUNT_MANAGER_USERNAME);
//        AppUserInput appUserInput = TestUtils.getNewAccountManagerInputWithSomeLabs();
//        appUserInput.setLabKeysAsUser(TestUtils.LAB_KEYS_WITH_INVALID_AND_VALID);
//        MvcResult result = mvc.perform(post(url)
//                .header("Authorization", token)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(appUserInput)))
//                .andExpect(status().is(404))
//                .andExpect(jsonPath("$.lab").isNotEmpty())
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }

    // CREATE
    @Test
    void testCreateAppUserWithManagerRole_whenAuthorized_got201AndAuthorizedForGetAllUser() throws Exception {
        String url = "/api/account/user";

        MvcResult result = mvc.perform(post(url)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(TestUtils.getNewAccountManagerInputWithSomeLabs())))
                .andExpect(status().isCreated())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
        // tests must check only one thing, so this is an antipattern, but I want to check if the freshly created user has the proper privlilige
        testGetAllAppUser_whenAuthorized_gotValidResponse("newaman@account.com");
    }

    @Test
    void testCreateAppUser_whenEmpty1_gotNOK() throws Exception {
        String url = "/api/account/user";
        MvcResult result = mvc.perform(post(url)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    void testCreateAppUser_whenEmpty2_gotNOK() throws Exception {
        String url = "/api/account/user";
        MvcResult result = mvc.perform(post(url)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }


    @Test
    void testCreateAppUser_whenNotEmailUsername_gotNOK() throws Exception {
        String url = "/api/account/user";
        AppUserInput appUserInput = TestUtils.getNewAccountManagerInputWithSomeLabs();
        appUserInput.setUsername("newaman");
        MvcResult result = mvc.perform(post(url)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(appUserInput)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    void testCreateAppUser_whenNotUsernameNotUnique_gotNOK() throws Exception {
        String url = "/api/account/user";
        AppUserInput appUserInput = TestUtils.getNewAccountManagerInputWithSomeLabs();
        appUserInput.setUsername(TestUtils.ACCOUNT_MANAGER_USERNAME);
        MvcResult result = mvc.perform(post(url)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(appUserInput)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    void testCreateAppUser_whenNotUsernameEmpty_gotNOK() throws Exception {
        String url = "/api/account/user";
        AppUserInput appUserInput = TestUtils.getNewAccountManagerInputWithSomeLabs();
        appUserInput.setUsername("");
        MvcResult result = mvc.perform(post(url)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(appUserInput)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    void testCreateAppUser_whenFullNameEmpty_gotNOK() throws Exception {
        String url = "/api/account/user";
        AppUserInput appUserInput = TestUtils.getNewAccountManagerInputWithSomeLabs();
        appUserInput.setFullName("");
        MvcResult result = mvc.perform(post(url)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(appUserInput)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fullName").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    void testCreateAppUser_whenPasswordEmpty_gotNOK() throws Exception {
        String url = "/api/account/user";
        AppUserInput appUserInput = TestUtils.getNewAccountManagerInputWithSomeLabs();
        appUserInput.setPassword("");
        MvcResult result = mvc.perform(post(url)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(appUserInput)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    void testCreateAppUser_whenPasswordNotTheSame_gotNOK() throws Exception {
        String url = "/api/account/user";
        AppUserInput appUserInput = TestUtils.getNewAccountManagerInputWithSomeLabs();
        appUserInput.setPassword("fooooo");
        appUserInput.setPassword2("barrrrr");
        MvcResult result = mvc.perform(post(url)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(appUserInput)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    void testCreateAppUser_whenOneOfLabAsAdminKeyIsInvalid_gotNOK() throws Exception {
        String url = "/api/account/user";
        AppUserInput appUserInput = TestUtils.getNewAccountManagerInputWithSomeLabs();
        appUserInput.setLabKeysAsAdmin(TestUtils.LAB_KEYS_WITH_INVALID_AND_VALID);
        MvcResult result = mvc.perform(post(url)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(appUserInput)))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.lab").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    void testCreateAppUser_whenOneOfLabAsUserKeyIsInvalid_gotNOK() throws Exception {
        String url = "/api/account/user";
        AppUserInput appUserInput = TestUtils.getNewAccountManagerInputWithSomeLabs();
        appUserInput.setLabKeysAsUser(TestUtils.LAB_KEYS_WITH_INVALID_AND_VALID);
        MvcResult result = mvc.perform(post(url)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(appUserInput)))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.lab").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    // READ
    @Test
    void testGetAllAppUsers_whenAuthorized_gotValidResponse() throws Exception {
        testGetAllAppUser_whenAuthorized_gotValidResponse(TestUtils.ACCOUNT_MANAGER_USERNAME);
    }

    @Disabled // TODO Fixme! Insead of checking whether there is a deleted appUser, I should ALPHA_BETA_LAB_DELETED_USER_INPUT is deleted
    @Test
    void testGetAllAppUsers_whenAuthorizedOnlyActiveFalse_gotDeletedUser() throws Exception {
        String url = "/api/account/user";
        mvc.perform(get(url)
                    .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER)
                    .param("only-active", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].deleted").value(Matchers.arrayContainingInAnyOrder(true,false)));
    }

    @Test
    void testGetAllAppUser_whenLabManager_got403() throws Exception {
        String url = "/api/account/user";
        String token = jwtProvider.generateToken(TestUtils.ALPHA_BETA_LAB_MANAGER_USERNAME);
        mvc.perform(get(url).header("Authorization", token))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetAllAppUser_whenLabAdmin_got403() throws Exception {
        String url = "/api/account/user";
        String token = jwtProvider.generateToken(TestUtils.ALPHA_BETA_LAB_ADMIN_USERNAME);
        mvc.perform(get(url).header("Authorization", token))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetAllAppUser_whenUser_got403() throws Exception {
        String url = "/api/account/user";
        String token = jwtProvider.generateToken(TestUtils.ALPHA_BETA_LAB_USER_USERNAME);
        mvc.perform(get(url).header("Authorization", token))
                .andExpect(status().isForbidden());
    }



    private void testGetAllAppUser_whenAuthorized_gotValidResponse(String username) throws Exception {
        String url = "/api/account/user";
        HttpHeaders headers = new HttpHeaders();
        String token = jwtProvider.generateToken(username);
        headers.set("Authorization", token);
        headers.setBearerAuth(token);

        MvcResult result = mvc.perform(get(url).header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].fullName").isString())
                .andExpect(jsonPath("$[0].fullName").isNotEmpty())
                .andExpect(jsonPath("$[0].username").isString())
                .andExpect(jsonPath("$[0].username").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



}