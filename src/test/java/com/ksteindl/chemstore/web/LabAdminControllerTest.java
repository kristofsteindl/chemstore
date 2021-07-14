package com.ksteindl.chemstore.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.entities.Manufacturer;
import com.ksteindl.chemstore.domain.input.AppUserInput;
import com.ksteindl.chemstore.domain.input.ManufacturerInput;
import com.ksteindl.chemstore.security.JwtProvider;
import com.ksteindl.chemstore.service.AppUserService;
import com.ksteindl.chemstore.service.ManufacturerService;
import com.ksteindl.chemstore.web.utils.AccountManagerTestUtils;
import com.ksteindl.chemstore.web.utils.LabAdminTestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsNot;
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
import org.springframework.test.util.AssertionErrors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.transaction.Transactional;

import java.util.Optional;

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
    
//MANUFACTURER
    //DELETE
    @Test
    @Rollback
    @Transactional
    void testDeleteOmegaManufacturer_whenAuthorized_got204(@Autowired ManufacturerService manufacturerService) throws Exception {
        Manufacturer persistedOmegaManufacturer = manufacturerService.getManufacturers().stream().filter(man -> man.getName().equals(LabAdminTestUtils.OMEGA_MANUFACTURER_NAME)).findAny().get();
        String token = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_LAB_ADMIN_USERNAME);
        String url = MANUFACTURER_URL + "/" + persistedOmegaManufacturer.getId();

        MvcResult result = mvc.perform(delete(url)
                .header("Authorization", token).contentType(MediaType.APPLICATION_JSON))
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
        String token = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_LAB_ADMIN_USERNAME);
        String url = MANUFACTURER_URL + "/" + persistedOmegaManufacturer.getId();

        MvcResult result1 = mvc.perform(delete(url)
                .header("Authorization", token).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
        logger.info("status code: " + result1.getResponse().getStatus());
        MvcResult result2 = mvc.perform(delete(url)
                .header("Authorization", token).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        logger.info("status code: " + result2.getResponse().getStatus());
    }

    @Test
    @Rollback
    @Transactional
    void testDeleteManufacturerWithNonExistingId_whenAuthorized_got204(@Autowired ManufacturerService manufacturerService) throws Exception {
        String token = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_LAB_ADMIN_USERNAME);
        String url = MANUFACTURER_URL + "/" + Integer.MAX_VALUE;

        MvcResult result = mvc.perform(delete(url)
                .header("Authorization", token).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
    }

    @Test
    @Rollback
    @Transactional
    void testDeleteDeletedManufacturer_whenAuthorized_got404(@Autowired ManufacturerService manufacturerService) throws Exception {
        Manufacturer persistedDeletedManufacturer = manufacturerService.getManufacturers(false).stream().filter(man -> man.getName().equals(LabAdminTestUtils.DELTA_MANUFACTURER_NAME)).findAny().get();
        String token = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_LAB_ADMIN_USERNAME);
        String url = MANUFACTURER_URL + "/" + persistedDeletedManufacturer.getId();

        MvcResult result = mvc.perform(delete(url)
                .header("Authorization", token).contentType(MediaType.APPLICATION_JSON))
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
    void testGetAllManufacturers_whenAuthorized_gotValidArray(@Autowired ManufacturerService manufacturerService) throws Exception {
        String token = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_LAB_ADMIN_USERNAME);
        mvc.perform(get(MANUFACTURER_URL)
                .header("Authorization", token).contentType(MediaType.APPLICATION_JSON))
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
        String token = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_LAB_ADMIN_USERNAME);
        mvc.perform(get(MANUFACTURER_URL)
                .header("Authorization", token).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[*].deleted", hasItem(false)))
                .andExpect(jsonPath("$[*].deleted", IsNot.not(hasItem(true))));
    }

    @Test
    void testGetAllManufacturersWithOnlyActiveFalse_whenAuthorized_gotArrayWithDeleted() throws Exception {
        String token = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_LAB_ADMIN_USERNAME);
        mvc.perform(get(MANUFACTURER_URL)
                .header("Authorization", token).contentType(MediaType.APPLICATION_JSON).param("only-active", "false"))
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
        testUpdateManufacturer_got201(manufacturerService, AccountManagerTestUtils.ALPHA_LAB_ADMIN_USERNAME);
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateManufacturer_withAccountManager_got201(@Autowired ManufacturerService manufacturerService) throws Exception {
        testUpdateManufacturer_got201(manufacturerService, AccountManagerTestUtils.ACCOUNT_MANAGER_USERNAME);
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
        testUpdateManufacturer_got403(manufacturerService, AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME);
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
            AssertionErrors.fail("Manufacturer was found after the calling succesfully POST " + MANUFACTURER_URL);
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

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



}