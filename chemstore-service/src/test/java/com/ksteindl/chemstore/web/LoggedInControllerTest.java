package com.ksteindl.chemstore.web;

import com.ksteindl.chemstore.BaseControllerTest;
import com.ksteindl.chemstore.domain.input.PasswordInput;
import com.ksteindl.chemstore.security.JwtProvider;
import com.ksteindl.chemstore.utils.AccountManagerTestUtils;
import com.ksteindl.chemstore.utils.LabAdminTestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsNot;
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

import jakarta.transaction.Transactional;
import java.util.Map;

import static com.ksteindl.chemstore.utils.AccountManagerTestUtils.ALPHA_LAB_KEY;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LoggedInControllerTest extends BaseControllerTest {

    private static final Logger logger = LogManager.getLogger(LoggedInControllerTest.class);

    private final static String URL = "/api/logged-in";
    private final static String URL_USERS = URL + "/user";
    private final static String URL_MANUFACTURER = URL + "/manufacturer";
    private final static String URL_CHEMICAL = URL + "/chemical";
    private final static String URL_CHEMICAL_ALAB = URL_CHEMICAL + "/alab";
    private final static String URL_ME = URL + "/user/me";
    private final static String CATEGORY_URL = URL + "/chem-category";
    private final static String RECIPE_URL = URL + "/recipe";

    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    private MockMvc mvc;

    //USER
    @Test
    @Transactional
    @Rollback
    void testUpdateAlphaLabUser_whenAllValid_got201() throws Exception {
        PasswordInput passwordInput = getNewPasswordInput();
        MvcResult result = mvc.perform(patch(URL_USERS)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(passwordInput)))
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.username", is(AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME)))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testUpdateAlphaLabUser_whenAllValid_canLoginAfterWithNewPassword() throws Exception {
        PasswordInput passwordInput = getNewPasswordInput();
        MvcResult result = mvc.perform(patch(URL_USERS)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(passwordInput)))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
        mvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asStaticJsonString(Map.of(
                                "username", AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME,
                                "password", passwordInput.getNewPassword()))))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    @Transactional
    @Rollback
    void testUpdateAlphaLabUser_whenAllValid_canNOTLoginAfterWithOldPassword() throws Exception {
        PasswordInput passwordInput = getNewPasswordInput();
        MvcResult result = mvc.perform(patch(URL_USERS)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(passwordInput)))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
        mvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asStaticJsonString(Map.of(
                                "username", AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME,
                                "password", AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME.split("@")[0]))))
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.username").isString())
                .andExpect(jsonPath("$.password").isString());
    }

    @Test
    @Transactional
    @Rollback
    void testUpdateAlphaLabUser_whenPasswordEmpty1_got400() throws Exception {
        PasswordInput passwordInput = getNewPasswordInput();
        passwordInput.setNewPassword(null);
        MvcResult result = mvc.perform(patch(URL_USERS)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(passwordInput)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.newPassword").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testUpdateAlphaLabUser_whenPasswordEmpty2_got400() throws Exception {
        PasswordInput passwordInput = getNewPasswordInput();
        passwordInput.setNewPassword(" ");
        MvcResult result = mvc.perform(patch(URL_USERS)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(passwordInput)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.newPassword").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testUpdateAlphaLabUser_whenPasswordTooShort_got400() throws Exception {
        PasswordInput passwordInput = getNewPasswordInput();
        passwordInput.setNewPassword("yo123");
        MvcResult result = mvc.perform(patch(URL_USERS)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(passwordInput)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.newPassword").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testUpdateAlphaLabUser_whenPasswordsAreNotTheSame_got400() throws Exception {
        PasswordInput passwordInput = getNewPasswordInput();
        passwordInput.setNewPassword2("yo123456");
        MvcResult result = mvc.perform(patch(URL_USERS)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(passwordInput)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.newPassword").isNotEmpty())
                .andExpect(jsonPath("$.newPassword2").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Transactional
    @Rollback
    void testUpdateAlphaLabUser_whenOldPasswordIncorrect_got400() throws Exception {
        PasswordInput passwordInput = getNewPasswordInput();
        passwordInput.setOldPassword("no-goo");
        MvcResult result = mvc.perform(patch(URL_USERS)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(passwordInput)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.oldPassword").isNotEmpty())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    private PasswordInput getNewPasswordInput() {
        PasswordInput passwordInput = new PasswordInput();
        String newPassword = "brand-new-password";
        passwordInput.setOldPassword(AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME.split("@")[0]);
        passwordInput.setNewPassword(newPassword);
        passwordInput.setNewPassword2(newPassword);
        return passwordInput;
    }

    //CHEMICAL
    @Test
    @Transactional
    void testGetAllChemicals_whenAlphaLabUserLoggedIn_got200() throws Exception{
        mvc.perform(get(URL_CHEMICAL_ALAB)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
        ;
    }

    @Test
    @Transactional
    void testGetAllChemicals_whenAlphaLabUserLoggedIn_hasItemEtOH() throws Exception{
        mvc.perform(get(URL_CHEMICAL_ALAB)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].shortName",  hasItem(LabAdminTestUtils.ETHANOL_SHORT_NAME)))
        ;
    }

    @Test
    @Transactional
    void testGetAllChemicals_whenAlphaLabUserLoggedIn_hasItemMeOH() throws Exception{
        mvc.perform(get(URL_CHEMICAL_ALAB)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].shortName",  hasItem(LabAdminTestUtils.METHANOL_SHORT_NAME)))
        ;
    }

    @Test
@Transactional
    void testGetAllChemicals_whenAlphaLabUserLoggedIn_hasNoItemIPA() throws Exception{
        mvc.perform(get(URL_CHEMICAL_ALAB)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].shortName",  IsNot.not(hasItem(LabAdminTestUtils.IPA_SHORT_NAME))))
        ;
    }

    @Test
    @Transactional
    void testGetAllChemicals_whenAlphaLabUserLoggedIn_EtOHHasProperAttributes() throws Exception{
        mvc.perform(get(URL_CHEMICAL_ALAB)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].id").exists())
                .andExpect(jsonPath("$[*].shortName",  hasItem(LabAdminTestUtils.ETHANOL_SHORT_NAME)))
                .andExpect(jsonPath("$[*].exactName",  hasItem(LabAdminTestUtils.ETHANOL_EXACT_NAME)))
                .andExpect(jsonPath("$[*].deleted",  IsNot.not(hasItem(true))))
        ;
    }

    @Test
    @Transactional
    void testGetAllChemicals_whenNoUserIsLoggedIn_got401() throws Exception{
        mvc.perform(get(URL_CHEMICAL_ALAB)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(401))
        ;
    }

    //MANUFACTURER
    @Test
    @Transactional
    void testGetAllManufacturers_whenAlphaLabUserLoggedIn_got200() throws Exception{
        mvc.perform(get(URL_MANUFACTURER)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
        ;
    }

    @Test
    @Transactional
    void testGetAllManufacturers_whenAlphaLabUserLoggedIn_hasItemOmagaManufacturer() throws Exception{
        mvc.perform(get(URL_MANUFACTURER)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].name",  hasItem(LabAdminTestUtils.OMEGA_MANUFACTURER_NAME)))
        ;
    }

    @Test
    @Transactional
    void testGetAllManufacturers_whenAlphaLabUserLoggedIn_hasItemGammaManufacturer() throws Exception{
        mvc.perform(get(URL_MANUFACTURER)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].name",  hasItem(LabAdminTestUtils.GAMMA_MANUFACTURER_NAME)))
        ;
    }

    @Test
    @Transactional
    void testGetAllManufacturers_whenAlphaLabUserLoggedIn_hasNoItemDeltaManufacturer() throws Exception{
        mvc.perform(get(URL_MANUFACTURER)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].name", IsNot.not(hasItem(LabAdminTestUtils.DELTA_MANUFACTURER_NAME))))
        ;
    }

    @Test
    @Transactional
    void testGetAllManufacturers_whenAlphaLabUserLoggedIn_gammaManufacturerHasProperAttributes() throws Exception{
        mvc.perform(get(URL_MANUFACTURER)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].id").exists())
                .andExpect(jsonPath("$[*].name",  hasItem(LabAdminTestUtils.GAMMA_MANUFACTURER_NAME)))
                .andExpect(jsonPath("$[*].deleted",  IsNot.not(hasItem(true))))
        ;
    }

    @Test
    @Transactional
    void testGetAllManufacturers_whenNoUserIsLoggedIn_got401() throws Exception{
        mvc.perform(get(URL_MANUFACTURER)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(401))
        ;
    }


    //USER
    @Test
    @Transactional
    void testGetAllAppUsers_whenAlphaLabUserLoggedIn_got200() throws Exception{
        mvc.perform(get(URL_USERS)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
        ;
    }

    @Test
    @Transactional
    void testGetAllAppUsers_whenAlphaLabUserLoggedIn_hasItemAlphaLabUser() throws Exception{
        mvc.perform(get(URL_USERS)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].username",  hasItem(AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME)))
        ;
    }

    @Test
    @Transactional
    void testGetAllAppUsers_whenAlphaLabUserLoggedIn_hasItemBetaLabUser() throws Exception{
        mvc.perform(get(URL_USERS)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].username",  hasItem(AccountManagerTestUtils.BETA_LAB_USER_USERNAME)))
        ;
    }

    @Test
    @Transactional
    void testGetAllAppUsers_whenAlphaLabUserLoggedIn_hasItemAccountManager() throws Exception{
        mvc.perform(get(URL_USERS)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].username",  hasItem(AccountManagerTestUtils.ACCOUNT_MANAGER_USERNAME)))
        ;
    }

    @Test
    @Transactional
    void testGetAllAppUsers_whenAlphaLabUserLoggedIn_hasAlphaLabUserHasCardAttributes() throws Exception{
        mvc.perform(get(URL_USERS)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[*].username", hasItem(AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME)))
                .andExpect(jsonPath("$[*].fullName", hasItem(AccountManagerTestUtils.ALPHA_LAB_USER_FULL_NAME)))
                .andExpect(jsonPath("$[*].id").exists())
        ;
    }

    @Test
    @Transactional
    void testGetAllAppUser_whenNoUserIsLoggedIn_got401() throws Exception{
        mvc.perform(get(URL_USERS)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(401))
        ;
    }

    //ME
    @Test
    @Transactional
    void testUserProfile_whenAlphaLabUserLoggedIn_got200() throws Exception{
        mvc.perform(get(URL_ME)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$").isNotEmpty())
        ;
    }

    @Test
    @Transactional
    void testUserProfile_whenNoUserIsLoggedIn_got401() throws Exception{
        mvc.perform(get(URL_ME)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(401))
        ;
    }

    @Test
    @Transactional
    void testUserProfile_whenAlphaLabUserLoggedIn_simpleAttributesAreCorrect() throws Exception{
        mvc.perform(get(URL_ME)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username", is(AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME)))
                .andExpect(jsonPath("$.fullName", is(AccountManagerTestUtils.ALPHA_LAB_USER_FULL_NAME)))
                .andExpect(jsonPath("$.deleted", is(false)))
        ;
    }

    @Test
    @Transactional
    void testUserProfile_whenAlphaLabUserLoggedIn_hasNoPassword() throws Exception{
        mvc.perform(get(URL_ME)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.password").doesNotExist());
        ;
    }

    @Test
    @Transactional
    void testUserProfile_whenAlphaLabUserLoggedIn_labsAsUserHasAlphaLab() throws Exception{
        mvc.perform(get(URL_ME)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.labsAsUser").isArray())
                .andExpect(jsonPath("$.labsAsUser[*].key", hasItem(AccountManagerTestUtils.ALPHA_LAB_KEY)))
        ;
    }

    @Test
    @Transactional
    void testUserProfile_whenAlphaLabUserLoggedIn_labsAsUserHasNoBetaLab() throws Exception{
        mvc.perform(get(URL_ME)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.labsAsUser[*].key", IsNot.not(hasItem(AccountManagerTestUtils.BETA_LAB_KEY))))
        ;
    }

    @Test
    @Transactional
    void testUserProfile_whenAlphaLabUserLoggedIn_labsAsAdminIsEmpty() throws Exception{
        mvc.perform(get(URL_ME)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.labsAsAdmin").isArray())
                .andExpect(jsonPath("$.labsAsAdmin").isEmpty());
    }

    @Test
    @Transactional
    void testUserProfile_whenAlphaLabUserLoggedIn_rolesIsEmpty() throws Exception{
        mvc.perform(get(URL_ME)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles").isEmpty());
    }

    @Test
    @Transactional
    void testUserProfile_whenAlphaLabUserLoggedIn_managedLabsIsEmpty() throws Exception{
        mvc.perform(get(URL_ME)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.managedLabs").isArray())
                .andExpect(jsonPath("$.managedLabs").isEmpty());
    }

    @Test
    @Transactional
    void testUserProfile_whenAlphaLabAdminLoggedIn_labsAsAdminHasAlphaLab() throws Exception{
        mvc.perform(get(URL_ME)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.labsAsAdmin[*].key", hasItem(AccountManagerTestUtils.ALPHA_LAB_KEY)))
                ;
    }

    @Test
    @Transactional
    void testUserProfile_whenAlphaLabAdminLoggedIn_labsAsAdminHasNoBetaLab() throws Exception{
        mvc.perform(get(URL_ME)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.labsAsAdmin[*].key", IsNot.not(hasItem(AccountManagerTestUtils.BETA_LAB_KEY))))
        ;
    }

    @Test
    @Transactional
    void testUserProfile_whenAlphaLabManagerLoggedIn_managedLabsHasAlphaLab() throws Exception{
        mvc.perform(get(URL_ME)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.managedLabs[*].key", hasItem(AccountManagerTestUtils.ALPHA_LAB_KEY)));
    }

    @Test
    @Transactional
    void testUserProfile_whenAlphaLabManagerLoggedIn_managedLabsHasNoBetaLab() throws Exception{
        mvc.perform(get(URL_ME)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.managedLabs[*].key", IsNot.not(hasItem(AccountManagerTestUtils.BETA_LAB_KEY))));
    }

    @Test
    @Transactional
    void testUserProfile_whenAccountManagerLoggedIn_rolesHasAccountManager() throws Exception{
        mvc.perform(get(URL_ME)
                .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.roles[*].key", hasItem("ACCOUNT_MANAGER")))
        ;

    }
    
    //CATEGORY
    @Test
    @Rollback
    @Transactional
    void testGetCategoriesForAlphaLab_whenAlphaLabUser_got200() throws Exception {
        mvc.perform(get(CATEGORY_URL + "/" + ALPHA_LAB_KEY)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
    }
    
    @Test
    @Rollback
    @Transactional
    void testGetCategoriesForAlphaLab_whenAlphaLabAdmin_got200() throws Exception {
        mvc.perform(get(CATEGORY_URL + "/" + ALPHA_LAB_KEY)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Rollback
    @Transactional
    void testGetCategoriesForAlphaLab_whenAlphaLabUser_gotValidArray() throws Exception {
        mvc.perform(get(CATEGORY_URL + "/" + ALPHA_LAB_KEY)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].lab.key").isString())
                .andExpect(jsonPath("$[0].name").isString())
                .andExpect(jsonPath("$[0].shelfLife").isString())
                .andExpect(jsonPath("$[0].deleted").isBoolean())
                .andExpect(jsonPath("$[*].deleted", hasItem(false)))
                .andExpect(jsonPath("$[*].deleted", IsNot.not(hasItem(true))))
                .andExpect(jsonPath("$", hasSize(chemicalCategoryService.getByLabForUser(ALPHA_LAB_KEY, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL).size())));
    }

    @Test
    @Rollback
    @Transactional
    void testGetCategoriesForAlphaLab_whenAlphaLabManager_got200() throws Exception {
        mvc.perform(get(CATEGORY_URL + "/" + ALPHA_LAB_KEY)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Rollback
    @Transactional
    void testGetCategoriesForAlphaLab_whenBetaLabAdmin_got403() throws Exception {
        mvc.perform(get(CATEGORY_URL + "/" + ALPHA_LAB_KEY)
                        .header("Authorization", TOKEN_FOR_BETA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(403));
    }

    @Test
    @Rollback
    @Transactional
    void testGetCategoriesForAlphaLab_whenAccountManager_got403() throws Exception {
        mvc.perform(get(CATEGORY_URL + "/" + ALPHA_LAB_KEY)
                        .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(403));
    }


    @Test
    @Rollback
    @Transactional
    void testGetCategoriesForAlphaLab_withOnlyActiveFalse_gotValidArray() throws Exception {
        mvc.perform(get(CATEGORY_URL + "/" + ALPHA_LAB_KEY)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER)
                        .contentType(MediaType.APPLICATION_JSON)
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
        mvc.perform(get(CATEGORY_URL + "/" + ALPHA_LAB_KEY)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("onlyActive", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[*].deleted", hasItem(false)))
                .andExpect(jsonPath("$[*].deleted", IsNot.not(hasItem(true))));
    }


    @Test
    @Rollback
    @Transactional
    void testGetCategories_whenKeyDoesNotExists_got404() throws Exception {
        mvc.perform(get(CATEGORY_URL + "/" + "not-existing")
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }
    
    // RECIPE
    @Test
    @Rollback
    @Transactional
    void testGetRecipesForAlphaLisoProject_got200() throws Exception {
        mvc.perform(get(RECIPE_URL + "/" + alphaLisoProject.getId())
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
    }

    @Test
    @Rollback
    @Transactional
    void testGetRecipesForAlphaLisoProject_gotValidArray() throws Exception {
        mvc.perform(get(RECIPE_URL + "/" + alphaLisoProject.getId())
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    @Rollback
    @Transactional
    void testGetRecipesForAlphaLisoProject_gotRecipeAttributesTypeAsExpected() throws Exception {
        mvc.perform(get(RECIPE_URL + "/" + alphaLisoProject.getId())
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].labKey", is(ALPHA_LAB_KEY)))
                .andExpect(jsonPath("$[0].projectName", is(LabAdminTestUtils.LISI_NAME)))
                .andExpect(jsonPath("$[0].name").isString())
                .andExpect(jsonPath("$[0].shelfLifeInDays").isNumber())
                .andExpect(jsonPath("$[0].amount").isNumber())
                .andExpect(jsonPath("$[0].unit").isString());
    }

    @Test
    @Rollback
    @Transactional
    void testGetRecipesForAlphaLisoProject_noDeleted() throws Exception {
        mvc.perform(get(RECIPE_URL + "/" + alphaLisoProject.getId())
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].deleted").isBoolean())
                .andExpect(jsonPath("$[*].deleted", hasItem(false)))
                .andExpect(jsonPath("$[*].deleted", IsNot.not(hasItem(true))));
    }

    @Test
    @Rollback
    @Transactional
    void testGetRecipesForAlphaLisoProject_hasReciCorrectSize() throws Exception {
        mvc.perform(get(RECIPE_URL + "/" + alphaLisoProject.getId())
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(recipeService.getRecipes(alphaLisoProject.getId(), AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL, true).size())));

    }

    @Test
    @Rollback
    @Transactional
    void testGetRecipesForAlphaLisoProject_hasCorrectSize() throws Exception {
        mvc.perform(get(RECIPE_URL + "/" + alphaLisoProject.getId())
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(recipeService.getRecipes(alphaLisoProject.getId(), AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL, true).size())));

    }

    // ChemicalIngredients
    @Test
    @Rollback
    @Transactional
    void testGetRecipesForAlphaLisoProject_hasChemicalIngredientsArray() throws Exception {
        mvc.perform(get(RECIPE_URL + "/" + alphaLisoProject.getId())
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].chemicalIngredients").isArray());
    }

    @Test
    @Rollback
    @Transactional
    void testGetRecipesForAlphaLisoProject_hasChemicalIngredientsArrayIsNotEmpty() throws Exception {
        mvc.perform(get(RECIPE_URL + "/" + alphaLisoProject.getId())
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[*].chemicalIngredients", hasSize(greaterThan(0))));
    }

    @Test
    @Rollback
    @Transactional
    void testGetRecipesForAlphaLisoProject_hasChemicalIngredientELementHasExpectedAttributes() throws Exception {
        mvc.perform(get(RECIPE_URL + "/" + alphaLisoProject.getId())
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[*].chemicalIngredients[0].id", hasItem(Matchers.isA(Number.class))))
                .andExpect(jsonPath("$[*].chemicalIngredients[0].amount", hasItem(Matchers.isA(Number.class))))
                .andExpect(jsonPath("$[*].chemicalIngredients[0].unit", hasItem(Matchers.isA(String.class))))
                .andExpect(jsonPath("$[*].chemicalIngredients[0].ingredient.id", hasItem(Matchers.isA(Number.class))))
                .andExpect(jsonPath("$[*].chemicalIngredients[0].ingredient.shortName", hasItem(Matchers.isA(String.class))))
        ;
    }
    
    // RecipeIngredients
    @Test
    @Rollback
    @Transactional
    void testGetRecipesForAlphaLisoProject_hasRecipeIngredientsArray() throws Exception {
        mvc.perform(get(RECIPE_URL + "/" + alphaLisoProject.getId())
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].recipeIngredients").isArray());
    }

    @Test
    @Rollback
    @Transactional
    void testGetRecipesForAlphaLisoProject_hasRecipeIngredientsArrayIsNotEmpty() throws Exception {
        mvc.perform(get(RECIPE_URL + "/" + alphaLisoProject.getId())
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[*].recipeIngredients", hasSize(greaterThan(0))));
    }

    @Test
    @Rollback
    @Transactional
    void testGetRecipesForAlphaLisoProject_hasRecipeIngredientELementHasExpectedAttributes() throws Exception {
        mvc.perform(get(RECIPE_URL + "/" + alphaLisoProject.getId())
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[*].recipeIngredients[0].id", hasItem(Matchers.isA(Number.class))))
                .andExpect(jsonPath("$[*].recipeIngredients[0].amount", hasItem(Matchers.isA(Number.class))))
                .andExpect(jsonPath("$[*].recipeIngredients[0].unit", hasItem(Matchers.isA(String.class))))
                .andExpect(jsonPath("$[*].recipeIngredients[0].ingredient.id", hasItem(Matchers.isA(Number.class))))
                .andExpect(jsonPath("$[*].recipeIngredients[0].ingredient.name", hasItem(Matchers.isA(String.class))))
        ;
    }

    @Test
    @Rollback
    @Transactional
    void testGetRecipesForAlphaLisoProject_whenBetaLabUser_got403() throws Exception {
        mvc.perform(get(RECIPE_URL + "/" + alphaLisoProject.getId())
                        .header("Authorization", TOKEN_FOR_BETA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(403));
    }

    @Test
    @Rollback
    @Transactional
    void testGetRecipesForAlphaLisoProject_whenAccountManager_got403() throws Exception {
        mvc.perform(get(RECIPE_URL + "/" + alphaLisoProject.getId())
                        .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(403));
    }

    @Test
    @Rollback
    @Transactional
    void testGetRecipesForAlphaLisoProject_whenIsDoesNotExist_got404() throws Exception {
        mvc.perform(get(RECIPE_URL + "/" + Integer.MAX_VALUE)
                        .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    @Rollback
    @Transactional
    void testGetRecipesForAlphaLisoProject_withOnlyActiveTrue_noDeleted() throws Exception {
        mvc.perform(get(RECIPE_URL + "/" + alphaLisoProject.getId())
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .param("onlyActive", "true"))                
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].deleted").isBoolean())
                .andExpect(jsonPath("$[*].deleted", hasItem(false)))
                .andExpect(jsonPath("$[*].deleted", IsNot.not(hasItem(true))));
    }

    @Test
    @Rollback
    @Transactional
    void testGetRecipesForAlphaLisoProject_withOnlyActiveFalse_thereIsDeleted() throws Exception {
        mvc.perform(get(RECIPE_URL + "/" + alphaLisoProject.getId())
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .param("onlyActive", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].deleted").isBoolean())
                .andExpect(jsonPath("$[*].deleted", hasItem(false)))
                .andExpect(jsonPath("$[*].deleted", hasItem(true)));
    }
    



}
