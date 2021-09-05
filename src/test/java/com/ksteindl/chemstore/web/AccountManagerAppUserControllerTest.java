package com.ksteindl.chemstore.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.input.AppUserInput;
import com.ksteindl.chemstore.security.JwtProvider;
import com.ksteindl.chemstore.service.AppUserService;
import com.ksteindl.chemstore.web.utils.AccountManagerTestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AccountManagerAppUserControllerTest extends BaseControllerTest{

    private static final Logger logger = LogManager.getLogger(AccountManagerAppUserControllerTest.class);

    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    private MockMvc mvc;

    // UPDATE
    @Test
    @Transactional
    @Rollback
    void testUpdateAlabUser_whenAuthorized_got201AndAuthorizedForGetAllUser(@Autowired AppUserService appUserService) throws Exception {
        AppUser persistedAlphaLabUser = appUserService.findByUsername(AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME).get();
        AppUserInput alabuserInput = AccountManagerTestUtils.getAlphaLabUserInput();
        String newFullName = "Changed Alpha Lab User";
        alabuserInput.setFullName(newFullName);

        String url = "/api/account/user/" + persistedAlphaLabUser.getId();

        MvcResult result = mvc.perform(put(url)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(alabuserInput)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value(newFullName))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
        // tests must check only one thing, so this is an antipattern, but I want to check if the freshly created user has the proper privlilige
        testGetAllAppUser_whenAuthorized_gotValidResponse(AccountManagerTestUtils.ACCOUNT_MANAGER_USERNAME);
    }

    // UPDATE
    @Test
    @Transactional
    @Rollback
    void testUpdateDeletedAlphaLabUser_whenAuthorized_got400(@Autowired AppUserService appUserService) throws Exception {
        AppUser persistedAlphaLabUser = appUserService.findByUsername(AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME).get();
        appUserService.deleteAppUser(persistedAlphaLabUser.getId());
        AppUserInput alabuserInput = AccountManagerTestUtils.getAlphaLabUserInput();
        String newFullName = "Changed Alpha Lab User";
        alabuserInput.setFullName(newFullName);

        String url = "/api/account/user/" + persistedAlphaLabUser.getId();

        MvcResult result = mvc.perform(put(url)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(alabuserInput)))
                .andExpect(status().isBadRequest())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testUpdateAppUserWhenIdAndUsernameDoesNotMatch_whenAuthorized_got400(@Autowired AppUserService appUserService) throws Exception {
        AppUser persistedNewAccountManager = appUserService.findByUsername(AccountManagerTestUtils.ACCOUNT_MANAGER_USERNAME).get();
        logger.debug("in test 2, persistedNewAccountManager fullName: " + persistedNewAccountManager.getFullName());
        AppUserInput amanInput = AccountManagerTestUtils.getAccountManagerInput();
        String newFullName = "Changed Account Manager";
        amanInput.setFullName(newFullName);
        Long alabuserId = appUserService.findByUsername(AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME).get().getId();
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
    @Transactional
    @Rollback
    void testUpdateAppUserWithNonExistingId_whenAuthorized_got400(@Autowired AppUserService appUserService) throws Exception {
        String url = "/api/account/user/" + Integer.MAX_VALUE;
        MvcResult result = mvc.perform(put(url)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(AccountManagerTestUtils.getAccountManagerInput())))
                .andExpect(status().isNotFound())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    void testUpdateAlphaLabUserAppUser_whenEmpty1_gotNOK(@Autowired AppUserService appUserService) throws Exception {

        AppUser persistedAlphaLabUser = appUserService.findByUsername(AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME).get();
        AppUserInput alabuserInput = AccountManagerTestUtils.getAlphaLabUserInput();
        String newFullName = "Changed Alpha Lab User";
        alabuserInput.setFullName(newFullName);

        String url = "/api/account/user/" + persistedAlphaLabUser.getId();
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
    void testUpdateAlphaLabUserAppUser_whenEmpty2_gotNOK(@Autowired AppUserService appUserService) throws Exception {
        AppUser persistedAlphaLabUser = appUserService.findByUsername(AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME).get();
        String url = "/api/account/user/" + persistedAlphaLabUser.getId();

        AppUserInput alabuserInput = AccountManagerTestUtils.getAlphaLabUserInput();
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
    @Rollback
    void testUpdateAlphaLabUser_whenNotEmailUsername_gotNOK(@Autowired AppUserService appUserService) throws Exception {
        AppUser persistedAlphaLabUser = appUserService.findByUsername(AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME).get();
        String url = "/api/account/user/" + persistedAlphaLabUser.getId();

        AppUserInput appUserInput = AccountManagerTestUtils.getAlphaLabUserInput();
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

    @Test
    @Rollback
    void testUpdateAlphaLabUser_whenUsernameEmpty_gotNOK(@Autowired AppUserService appUserService) throws Exception {
        AppUser persistedAlphaLabUser = appUserService.findByUsername(AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME).get();
        String url = "/api/account/user/" + persistedAlphaLabUser.getId();

        AppUserInput appUserInput = AccountManagerTestUtils.getAlphaLabUserInput();
        appUserInput.setUsername("");
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

    @Test
    @Rollback
    void testUpdateAlphaLabUser_whenFullNameEmpty_gotNOK(@Autowired AppUserService appUserService) throws Exception {
        AppUser persistedAlphaLabUser = appUserService.findByUsername(AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME).get();
        String url = "/api/account/user/" + persistedAlphaLabUser.getId();

        AppUserInput appUserInput = AccountManagerTestUtils.getAlphaLabUserInput();
        appUserInput.setFullName("");
        MvcResult result = mvc.perform(put(url)
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
    @Rollback
    void testUpdateAlphaLabUser_whenPasswordEmpty_gotNOK(@Autowired AppUserService appUserService) throws Exception {
        AppUser persistedAlphaLabUser = appUserService.findByUsername(AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME).get();
        String url = "/api/account/user/" + persistedAlphaLabUser.getId();

        AppUserInput appUserInput = AccountManagerTestUtils.getAlphaLabUserInput();
        appUserInput.setPassword("");
        MvcResult result = mvc.perform(put(url)
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
    @Rollback
    void testUpdateAlphaLabUser_whenPasswordsAreNotTheSame_gotNOK(@Autowired AppUserService appUserService) throws Exception {
        AppUser persistedAlphaLabUser = appUserService.findByUsername(AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME).get();
        String url = "/api/account/user/" + persistedAlphaLabUser.getId();

        AppUserInput appUserInput = AccountManagerTestUtils.getAlphaLabUserInput();
        appUserInput.setPassword("fooooo");
        appUserInput.setPassword("baaaaar");
        MvcResult result = mvc.perform(put(url)
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
    @Rollback
    void testUpdateAlphaLabUser_whenOneOfLabAsAdminKeyIsInvalid_gotNOK(@Autowired AppUserService appUserService) throws Exception {
        AppUser persistedAlphaLabUser = appUserService.findByUsername(AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME).get();
        String url = "/api/account/user/" + persistedAlphaLabUser.getId();

        AppUserInput appUserInput = AccountManagerTestUtils.getAlphaLabUserInput();
        appUserInput.setLabKeysAsAdmin(AccountManagerTestUtils.LAB_KEYS_WITH_INVALID_AND_VALID);
        MvcResult result = mvc.perform(put(url)
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
    @Rollback
    void testUpdateAlphaLabUser_whenOneOfLabAsUserKeyIsInvalid_gotNOK(@Autowired AppUserService appUserService) throws Exception {
        AppUser persistedAlphaLabUser = appUserService.findByUsername(AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME).get();
        String url = "/api/account/user/" + persistedAlphaLabUser.getId();

        AppUserInput appUserInput = AccountManagerTestUtils.getAlphaLabUserInput();
        appUserInput.setLabKeysAsUser(AccountManagerTestUtils.LAB_KEYS_WITH_INVALID_AND_VALID);
        MvcResult result = mvc.perform(put(url)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(appUserInput)))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.lab").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    // CREATE
    @Test
    void testCreateAppUserWithManagerRole_whenAuthorized_got201AndAuthorizedForGetAllUser() throws Exception {
        String url = "/api/account/user";

        AppUserInput newmanInput = AccountManagerTestUtils.getNewAccountManagerInputWithSomeLabs();
        MvcResult result = mvc.perform(post(url)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(newmanInput)))
                .andExpect(status().isCreated())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
        // tests must check only one thing, so this is an antipattern, but I want to check if the freshly created user has the proper privlilige
        testGetAllAppUser_whenAuthorized_gotValidResponse(newmanInput.getUsername());
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
        AppUserInput appUserInput = AccountManagerTestUtils.getNewAccountManagerInputWithSomeLabs();
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
        AppUserInput appUserInput = AccountManagerTestUtils.getNewAccountManagerInputWithSomeLabs();
        appUserInput.setUsername(AccountManagerTestUtils.ACCOUNT_MANAGER_USERNAME);
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
        AppUserInput appUserInput = AccountManagerTestUtils.getNewAccountManagerInputWithSomeLabs();
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
        AppUserInput appUserInput = AccountManagerTestUtils.getNewAccountManagerInputWithSomeLabs();
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
        AppUserInput appUserInput = AccountManagerTestUtils.getNewAccountManagerInputWithSomeLabs();
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
        AppUserInput appUserInput = AccountManagerTestUtils.getNewAccountManagerInputWithSomeLabs();
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
        AppUserInput appUserInput = AccountManagerTestUtils.getNewAccountManagerInputWithSomeLabs();
        appUserInput.setLabKeysAsAdmin(AccountManagerTestUtils.LAB_KEYS_WITH_INVALID_AND_VALID);
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
        AppUserInput appUserInput = AccountManagerTestUtils.getNewAccountManagerInputWithSomeLabs();
        appUserInput.setLabKeysAsUser(AccountManagerTestUtils.LAB_KEYS_WITH_INVALID_AND_VALID);
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
        testGetAllAppUser_whenAuthorized_gotValidResponse(AccountManagerTestUtils.ACCOUNT_MANAGER_USERNAME);
    }

    //@Disabled // TODO Fixme! Instead of checking whether there is a deleted appUser, I should ALPHA_BETA_LAB_DELETED_USER_INPUT is deleted
    @Test
    void testGetAllAppUsers_whenAuthorizedOnlyActiveFalse_gotDeletedUser() throws Exception {
        String url = "/api/account/user";
        mvc.perform(get(url)
                    .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER)
                    .param("only-active", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].deleted", Matchers.hasItem(true)))
                .andExpect(jsonPath("$[*].deleted", Matchers.hasItem(false)));
    }

    @Test
    void testGetAllAppUser_whenLabManager_got403() throws Exception {
        String url = "/api/account/user";
        String token = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_BETA_LAB_MANAGER_USERNAME);
        mvc.perform(get(url).header("Authorization", token))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetAllAppUser_whenLabAdmin_got403() throws Exception {
        String url = "/api/account/user";
        String token = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_BETA_LAB_ADMIN_USERNAME);
        mvc.perform(get(url).header("Authorization", token))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetAllAppUser_whenUser_got403() throws Exception {
        String url = "/api/account/user";
        String token = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_BETA_LAB_USER_USERNAME);
        mvc.perform(get(url).header("Authorization", token))
                .andExpect(status().isForbidden());
    }

    // DELETE
    @Test
    @Rollback
    @Transactional
    void testDeleteAlphaLabUser_whenAuthorized_got204(@Autowired AppUserService appUserService) throws Exception {
        AppUser persistedAlphaLabUser = appUserService.findByUsername(AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME).get();
        String url = "/api/account/user/" + persistedAlphaLabUser.getId();

        MvcResult result = mvc.perform(delete(url)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
    }

    @Test
    @Disabled // TODO ResourceNotFound Exception is expected, else fail()
    @Rollback
    @Transactional
    void testDeleteAlphaLabUser_whenAuthorized_gotResourceNotFoundException(@Autowired AppUserService appUserService) throws Exception {
        AppUser persistedAlphaLabUser = appUserService.findByUsername(AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME).get();
        String url = "/api/account/user/" + persistedAlphaLabUser.getId();
        mvc.perform(delete(url).header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON));
        AppUser deletedAppUser = appUserService.findById(persistedAlphaLabUser.getId());

    }

    @Test
    @Rollback
    @Transactional
    void testDeleteAlphaLabUser_whenAuthorized_gotDeletedFromService(@Autowired AppUserService appUserService) throws Exception {
        AppUser persistedAlphaLabUser = appUserService.findByUsername(AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME).get();
        String url = "/api/account/user/" + persistedAlphaLabUser.getId();
        mvc.perform(delete(url).header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON));
        AppUser deletedAppUser = appUserService.findById(persistedAlphaLabUser.getId(), false);
        Assertions.assertTrue(deletedAppUser.getDeleted());
    }

    @Test
    @Rollback
    @Transactional
    void testDeleteAlphaLabUserTwoTimes_whenAuthorized_got400SocondTime(@Autowired AppUserService appUserService) throws Exception {
        AppUser persistedAlphaLabUser = appUserService.findByUsername(AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME).get();
        String url = "/api/account/user/" + persistedAlphaLabUser.getId();

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
    void testDeleteAlphaLabUserWithNonExistingId_whenAuthorized_got404(@Autowired AppUserService appUserService) throws Exception {
        String url = "/api/account/user/" + Integer.MAX_VALUE;

        MvcResult result = mvc.perform(delete(url)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
    }

    @Test
    @Rollback
    @Transactional
    void testDeleteAlphaLabUser_withAlphaLabManager_got403(@Autowired AppUserService appUserService) throws Exception {
        AppUser persistedAlphaLabUser = appUserService.findByUsername(AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME).get();
        String url = "/api/account/user/" + persistedAlphaLabUser.getId();

        String token = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_LAB_MANAGER_USERNAME);
        MvcResult result = mvc.perform(delete(url)
                .header("Authorization", token).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
    }

    @Test
    @Rollback
    @Transactional
    void testDeleteAlphaLabUser_withAlphaLabAdmin_got403(@Autowired AppUserService appUserService) throws Exception {
        AppUser persistedAlphaLabUser = appUserService.findByUsername(AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME).get();
        String url = "/api/account/user/" + persistedAlphaLabUser.getId();

        String token = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_LAB_ADMIN_USERNAME);
        MvcResult result = mvc.perform(delete(url)
                .header("Authorization", token).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
    }

    @Test
    @Rollback
    @Transactional
    void testDeleteAlphaLabUser_withBetaLabUser_got403(@Autowired AppUserService appUserService) throws Exception {
        AppUser persistedAlphaLabUser = appUserService.findByUsername(AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME).get();
        String url = "/api/account/user/" + persistedAlphaLabUser.getId();

        String token = jwtProvider.generateToken(AccountManagerTestUtils.BETA_LAB_USER_USERNAME);
        MvcResult result = mvc.perform(delete(url)
                .header("Authorization", token).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
    }



    private void testGetAllAppUser_whenAuthorized_gotValidResponse(String username) throws Exception {
        String url = "/api/account/user";
        String token = jwtProvider.generateToken(username);

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