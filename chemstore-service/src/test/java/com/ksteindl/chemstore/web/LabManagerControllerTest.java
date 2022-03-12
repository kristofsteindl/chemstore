package com.ksteindl.chemstore.web;

import com.ksteindl.chemstore.BaseControllerTest;
import com.ksteindl.chemstore.domain.entities.Project;
import com.ksteindl.chemstore.domain.input.ProjectInput;
import com.ksteindl.chemstore.security.JwtProvider;
import com.ksteindl.chemstore.service.ProjectService;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;

import static com.ksteindl.chemstore.utils.AccountManagerTestUtils.ALPHA_LAB_KEY;
import static com.ksteindl.chemstore.utils.AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL;
import static com.ksteindl.chemstore.utils.AccountManagerTestUtils.BETA_LAB_KEY;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LabManagerControllerTest extends BaseControllerTest {

    private static final Logger logger = LogManager.getLogger(LabManagerControllerTest.class);
    
    private static final String URL_USER = "/api/lab-manager/user";
    private static final String URL_PROJECT= "/api/lab-manager/project/";
    private static final String URL_ALPHA_LAB = URL_USER + "/" + AccountManagerTestUtils.ALPHA_LAB_KEY;

    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ProjectService projectService;


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
    
    
    //PROJECT
    //CREATE
    @Test
    @Rollback
    @Transactional
    void testCreateProject_whenAlphaLabManager_got201() throws Exception {
        ProjectInput input = LabAdminTestUtils.getRosuForAlphaInput();
        MvcResult result = mvc.perform(post(URL_PROJECT)
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
    void testCreateProject_whenBetaLabManager_got403() throws Exception {
        ProjectInput input = LabAdminTestUtils.getRosuForAlphaInput();
        MvcResult result = mvc.perform(post(URL_PROJECT)
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
    void testCreateProject_whenAlphaLabAdmin_got403() throws Exception {
        ProjectInput input = LabAdminTestUtils.getRosuForAlphaInput();
        MvcResult result = mvc.perform(post(URL_PROJECT)
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
    void testCreateProject_whenAlphaLabUser_got403() throws Exception {
        ProjectInput input = LabAdminTestUtils.getRosuForAlphaInput();
        MvcResult result = mvc.perform(post(URL_PROJECT)
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
    void testCreateProject_whenAccountManager_got403() throws Exception {
        ProjectInput input = LabAdminTestUtils.getRosuForAlphaInput();
        MvcResult result = mvc.perform(post(URL_PROJECT)
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
    void testCreateProject_whenInputBlank_got4xx() throws Exception {
        MvcResult result = mvc.perform(post(URL_PROJECT)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString("")))
                .andExpect(status().is4xxClientError())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }
    

    @Test
    @Rollback
    @Transactional
    void testCreateProject_whenNameNull_got400() throws Exception {
        ProjectInput input = LabAdminTestUtils.getRosuForAlphaInput();
        input.setName(null);
        MvcResult result = mvc.perform(post(URL_PROJECT)
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
    void testCreateProject_whenNameBlank_got400() throws Exception {
        ProjectInput input = LabAdminTestUtils.getRosuForAlphaInput();
        input.setName("");
        MvcResult result = mvc.perform(post(URL_PROJECT)
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
    void testCreateProject_whenLabKeyNull_got400() throws Exception {
        ProjectInput input = LabAdminTestUtils.getRosuForAlphaInput();
        input.setLabKey(null);
        MvcResult result = mvc.perform(post(URL_PROJECT)
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
    void testCreateProject_whenLabKeyBlank_got400() throws Exception {
        ProjectInput input = LabAdminTestUtils.getRosuForAlphaInput();
        input.setLabKey("");
        MvcResult result = mvc.perform(post(URL_PROJECT)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    //UPDATE
    @Test
    @Rollback
    @Transactional
    void testUpdateProject_got201() throws Exception {
        ProjectInput input = LabAdminTestUtils.getAmloForAlphaInput();
        Project persisted = projectService.getProjects(input.getLabKey(), ALPHA_LAB_MANAGER_PRINCIPAL).stream()
                .filter(project -> project.getName().equals(input.getName()))
                .findAny().get();
        input.setName("Updated name");
        MvcResult result = mvc.perform(put(URL_PROJECT + persisted.getId())
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(201))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateProject_gotExpectedValues() throws Exception {
        ProjectInput input = LabAdminTestUtils.getAmloForAlphaInput();
        Project persisted = projectService.getProjects(input.getLabKey(), ALPHA_LAB_MANAGER_PRINCIPAL).stream()
                .filter(project -> project.getName().equals(input.getName()))
                .findAny().get();
        String updatedName = "Updated name";
        input.setName(updatedName);
        MvcResult result = mvc.perform(put(URL_PROJECT + persisted.getId())
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.name", is(updatedName)))
                .andExpect(jsonPath("$.lab.key", is(ALPHA_LAB_KEY)))
                .andExpect(jsonPath("$.deleted", is(false)))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateProject_fetchedDataIsExpected() throws Exception {
        ProjectInput input = LabAdminTestUtils.getAmloForAlphaInput();
        Project persisted = projectService.getProjects(input.getLabKey(), ALPHA_LAB_MANAGER_PRINCIPAL).stream()
                .filter(project -> project.getName().equals(input.getName()))
                .findAny().get();
        String updatedName = "Updated name";
        input.setName(updatedName);
        MvcResult result = mvc.perform(put(URL_PROJECT + persisted.getId())
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(201))
                .andReturn();
        Project project = projectService.findById(persisted.getId());
        Assertions.assertEquals(updatedName, project.getName());
        Assertions.assertEquals(ALPHA_LAB_KEY, project.getLab().getKey());
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateProject_whenLabKeyDiffers_fetchedLabDoesNotChange() throws Exception {
        ProjectInput input = LabAdminTestUtils.getAmloForAlphaInput();
        Project persisted = projectService.getProjects(input.getLabKey(), ALPHA_LAB_MANAGER_PRINCIPAL).stream()
                .filter(project -> project.getName().equals(input.getName()))
                .findAny().get();
        input.setLabKey(BETA_LAB_KEY);
        MvcResult result = mvc.perform(put(URL_PROJECT + persisted.getId())
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(201))
                .andReturn();
        Project project = projectService.findById(persisted.getId());
        Assertions.assertEquals(ALPHA_LAB_KEY, project.getLab().getKey());
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateProject_whenBetaManager_got403() throws Exception {
        ProjectInput input = LabAdminTestUtils.getAmloForAlphaInput();
        Project persisted = projectService.getProjects(input.getLabKey(), ALPHA_LAB_MANAGER_PRINCIPAL).stream()
                .filter(project -> project.getName().equals(input.getName()))
                .findAny().get();
        input.setName("Updated name");
        MvcResult result = mvc.perform(put(URL_PROJECT + persisted.getId())
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
    void testUpdateProject_whenAlphaUser_got403() throws Exception {
        ProjectInput input = LabAdminTestUtils.getAmloForAlphaInput();
        Project persisted = projectService.getProjects(input.getLabKey(), ALPHA_LAB_MANAGER_PRINCIPAL).stream()
                .filter(project -> project.getName().equals(input.getName()))
                .findAny().get();
        input.setName("Updated name");
        MvcResult result = mvc.perform(put(URL_PROJECT + persisted.getId())
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
    void testUpdateProject_whenAlphaAdmin_got403() throws Exception {
        ProjectInput input = LabAdminTestUtils.getAmloForAlphaInput();
        Project persisted = projectService.getProjects(input.getLabKey(), ALPHA_LAB_MANAGER_PRINCIPAL).stream()
                .filter(project -> project.getName().equals(input.getName()))
                .findAny().get();
        input.setName("Updated name");
        MvcResult result = mvc.perform(put(URL_PROJECT + persisted.getId())
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
    void testUpdateProject_AccountManager_got403() throws Exception {
        ProjectInput input = LabAdminTestUtils.getAmloForAlphaInput();
        Project persisted = projectService.getProjects(input.getLabKey(), ALPHA_LAB_MANAGER_PRINCIPAL).stream()
                .filter(project -> project.getName().equals(input.getName()))
                .findAny().get();
        input.setName("Updated name");
        MvcResult result = mvc.perform(put(URL_PROJECT + persisted.getId())
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
    void testUpdateProject_whenNameNull_got400() throws Exception {
        ProjectInput input = LabAdminTestUtils.getAmloForAlphaInput();
        Project persisted = projectService.getProjects(input.getLabKey(), ALPHA_LAB_MANAGER_PRINCIPAL).stream()
                .filter(project -> project.getName().equals(input.getName()))
                .findAny().get();
        input.setName(null);
        MvcResult result = mvc.perform(put(URL_PROJECT + persisted.getId())
                        .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateProject_whenNameBlank_got400() throws Exception {
        ProjectInput input = LabAdminTestUtils.getAmloForAlphaInput();
        Project persisted = projectService.getProjects(input.getLabKey(), ALPHA_LAB_MANAGER_PRINCIPAL).stream()
                .filter(project -> project.getName().equals(input.getName()))
                .findAny().get();
        input.setName("");
        MvcResult result = mvc.perform(put(URL_PROJECT + persisted.getId())
                        .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateProject_whenEmptyInput1_got400() throws Exception {
        ProjectInput input = LabAdminTestUtils.getAmloForAlphaInput();
        Project persisted = projectService.getProjects(input.getLabKey(), ALPHA_LAB_MANAGER_PRINCIPAL).stream()
                .filter(project -> project.getName().equals(input.getName()))
                .findAny().get();

        MvcResult result = mvc.perform(put(URL_PROJECT + persisted.getId())
                        .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString("")))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateProject_whenEmptyInput2_got400() throws Exception {
        ProjectInput input = LabAdminTestUtils.getAmloForAlphaInput();
        Project persisted = projectService.getProjects(input.getLabKey(), ALPHA_LAB_MANAGER_PRINCIPAL).stream()
                .filter(project -> project.getName().equals(input.getName()))
                .findAny().get();

        MvcResult result = mvc.perform(put(URL_PROJECT + persisted.getId())
                        .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString("{}")))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    
    //DELETE
    @Test
    @Rollback
    @Transactional
    void testDeleteProject_got204() throws Exception {
        ProjectInput input = LabAdminTestUtils.getAmloForAlphaInput();
        Project persisted = projectService.getProjects(input.getLabKey(), ALPHA_LAB_MANAGER_PRINCIPAL).stream()
                .filter(project -> project.getName().equals(input.getName()))
                .findAny().get();
        MvcResult result = mvc.perform(delete(URL_PROJECT + persisted.getId())
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(204))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testDeleteProject_fetchedIsDeleted() throws Exception {
        ProjectInput input = LabAdminTestUtils.getAmloForAlphaInput();
        Project persisted = projectService.getProjects(input.getLabKey(), ALPHA_LAB_MANAGER_PRINCIPAL).stream()
                .filter(project -> project.getName().equals(input.getName()))
                .findAny().get();
        MvcResult result = mvc.perform(delete(URL_PROJECT + persisted.getId())
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(204))
                .andReturn();
        Project project = projectService.findById(persisted.getId(), false);
        Assertions.assertTrue(project.getDeleted());
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testDeleteProject_whenIdDoesNotExist_got404() throws Exception {
        MvcResult result = mvc.perform(delete(URL_PROJECT + "1234567")
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testDeleteProject_whenAlreadyDeleted_got404() throws Exception {
        Project persisted = projectService.getProjects(ALPHA_LAB_KEY, ALPHA_LAB_MANAGER_PRINCIPAL, false).stream()
                .filter(project -> project.getDeleted())
                .findAny().get();
        MvcResult result = mvc.perform(delete(URL_PROJECT + persisted.getId())
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testDeleteProject_whenBetaManager_got403() throws Exception {
        ProjectInput input = LabAdminTestUtils.getAmloForAlphaInput();
        Project persisted = projectService.getProjects(input.getLabKey(), ALPHA_LAB_MANAGER_PRINCIPAL).stream()
                .filter(project -> project.getName().equals(input.getName()))
                .findAny().get();
        MvcResult result = mvc.perform(delete(URL_PROJECT + persisted.getId())
                        .header("Authorization", TOKEN_FOR_BETA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(403))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testDeleteProject_whenAccountManager_got403() throws Exception {
        ProjectInput input = LabAdminTestUtils.getAmloForAlphaInput();
        Project persisted = projectService.getProjects(input.getLabKey(), ALPHA_LAB_MANAGER_PRINCIPAL).stream()
                .filter(project -> project.getName().equals(input.getName()))
                .findAny().get();
        MvcResult result = mvc.perform(delete(URL_PROJECT + persisted.getId())
                        .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(403))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testDeleteProject_whenAlphaUser_got403() throws Exception {
        ProjectInput input = LabAdminTestUtils.getAmloForAlphaInput();
        Project persisted = projectService.getProjects(input.getLabKey(), ALPHA_LAB_MANAGER_PRINCIPAL).stream()
                .filter(project -> project.getName().equals(input.getName()))
                .findAny().get();
        MvcResult result = mvc.perform(delete(URL_PROJECT + persisted.getId())
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(403))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testDeleteProject_whenAlphaAdmin_got403() throws Exception {
        ProjectInput input = LabAdminTestUtils.getAmloForAlphaInput();
        Project persisted = projectService.getProjects(input.getLabKey(), ALPHA_LAB_MANAGER_PRINCIPAL).stream()
                .filter(project -> project.getName().equals(input.getName()))
                .findAny().get();
        MvcResult result = mvc.perform(delete(URL_PROJECT + persisted.getId())
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(403))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }
    
    

}
