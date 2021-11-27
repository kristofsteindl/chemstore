package com.ksteindl.chemstore.web;

import com.ksteindl.chemstore.BaseControllerTest;
import com.ksteindl.chemstore.security.JwtProvider;
import com.ksteindl.chemstore.utils.AccountManagerTestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import javax.transaction.Transactional;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    @Transactional
    @Rollback
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
    @Transactional
    @Rollback
    void testGetAllUsersFromManagedLabs_whenAlphaLabManager_hasAlphaLabUserInAlphaLab() throws Exception{
        mvc.perform(get(URL_USER)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$['alab'][*].username", hasItem(AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME)))
        ;
    }

    @Test
    @Transactional
    @Rollback
    void testGetAllUsersFromManagedLabs_whenAlphaLabManager_doesNotHaveItemBetaLabUser() throws Exception{
        mvc.perform(get(URL_USER)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$['alab'][*].username", IsNot.not(hasItem(AccountManagerTestUtils.BETA_LAB_USER_USERNAME))))
        ;
    }

    @Test
    @Transactional
    @Rollback
    void testGetAllUsersFromManagedLabs_whenAlphaLabManager_hasNoBetaLabUsers() throws Exception{
        mvc.perform(get(URL_USER)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$['blab']").doesNotExist())
        ;
    }


    @Test
    @Transactional
    @Rollback
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
    @Transactional
    @Rollback
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
    @Transactional
    @Rollback
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
    @Transactional
    @Rollback
    void testGetAllUsersFromAlphaLab_whenAlphaLabManager_gotTwoUsers() throws Exception{
        mvc.perform(get(URL_ALPHA_LAB)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @Transactional
    @Rollback
    void testGetAllUsersFromAlphaLab_whenAlphaLabManager_hasItemAlphaLabUser() throws Exception{
        mvc.perform(get(URL_ALPHA_LAB)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[*].username", hasItem(AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME)))
        ;
    }

    @Test
    @Transactional
    @Rollback
    void testGetAllUsersFromAlphaLab_whenAlphaLabManager_doesNotHaveItemBetaLabUser() throws Exception{
        mvc.perform(get(URL_ALPHA_LAB)
                .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[*].username", IsNot.not(hasItem(AccountManagerTestUtils.BETA_LAB_USER_USERNAME))))
        ;
    }

    @Test
    @Transactional
    @Rollback
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
    @Transactional
    @Rollback
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
