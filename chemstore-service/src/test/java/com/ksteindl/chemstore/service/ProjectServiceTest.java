package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.BaseControllerTest;
import com.ksteindl.chemstore.domain.entities.Project;
import com.ksteindl.chemstore.domain.input.ProjectInput;
import com.ksteindl.chemstore.domain.repositories.ProjectRepository;
import com.ksteindl.chemstore.exceptions.ForbiddenException;
import com.ksteindl.chemstore.exceptions.ResourceNotFoundException;
import com.ksteindl.chemstore.exceptions.ValidationException;
import com.ksteindl.chemstore.utils.AccountManagerTestUtils;
import com.ksteindl.chemstore.utils.LabAdminTestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.List;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ProjectServiceTest extends BaseControllerTest{

    private static final Logger logger = LogManager.getLogger(ProjectServiceTest.class);
    
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectRepository projectRepository;
    private Principal alphaManager = AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL;
    private Principal alphaLabUser = AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL;
    
    private String alphaLabKey = AccountManagerTestUtils.ALPHA_LAB_KEY;
    

    // CREATE
    @Test
    @Rollback
    @Transactional
    public void testCreateProject_whenAllValid_gotNoException() {
        ProjectInput input = LabAdminTestUtils.getRosuForAlphaInput();
        projectService.createProject(input, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateProject_whenAllValid_savedValuesAsExpected() {
        ProjectInput input = LabAdminTestUtils.getRosuForAlphaInput();
        Project returned = projectService.createProject(input, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
        Project fetched = projectRepository.findById(returned.getId()).get();
        Assertions.assertEquals(input.getName(), fetched.getName());
        Assertions.assertEquals(input.getLabKey(), fetched.getLab().getKey());
        Assertions.assertFalse(fetched.getDeleted());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateProject_whenAlabAdmin_gotForbiddenException() {
        ProjectInput input = LabAdminTestUtils.getRosuForAlphaInput();
        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            projectService.createProject(input, AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateProject_whenBlabManager_gotForbiddenException() {
        ProjectInput input = LabAdminTestUtils.getRosuForAlphaInput();
        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            projectService.createProject(input, AccountManagerTestUtils.BETA_LAB_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateProject_whenEmptyInput_gotException() {
        ProjectInput input = new ProjectInput();
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            projectService.createProject(input, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateProject_whenShortNameAlreadyExists_gotValidationExteption() {
        ProjectInput input = LabAdminTestUtils.getRosuForAlphaInput();
        input.setName(LabAdminTestUtils.AMLO_NAME);
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            projectService.createProject(input, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateProject_whenLabKeyDoesNotExist_gotResourceNotFoundException() {
        ProjectInput input = LabAdminTestUtils.getRosuForAlphaInput();
        input.setLabKey("not-existing");
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            projectService.createProject(input, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateProject_whenLabIsDeleted_gotResourceNotFoundException() {
        ProjectInput input = LabAdminTestUtils.getRosuForAlphaInput();
        input.setLabKey(AccountManagerTestUtils.DELTA_LAB_KEY);
        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            projectService.createProject(input, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }
    


    // UPDATE
    @Test
    @Rollback
    @Transactional
    public void testUpdateProject_whenAllValid_gotNoException() {
        Principal manager = AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL;
        Project project = projectService.getProjects(AccountManagerTestUtils.ALPHA_LAB_KEY, manager).stream()
                .filter(gotProject -> gotProject.getName().equals(LabAdminTestUtils.AMLO_NAME))
                .findAny()
                .get();
        
        ProjectInput input = LabAdminTestUtils.getAmloForAlphaInput();
        input.setName("Updated Amlo");
        projectService.updateProject(input, project.getId(), manager);
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateProject_whenAllValid_savedValuesAsExpected() {
        Principal manager = AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL;
        Project project = projectService.getProjects(AccountManagerTestUtils.ALPHA_LAB_KEY, manager).stream()
                .filter(gotProject -> gotProject.getName().equals(LabAdminTestUtils.AMLO_NAME))
                .findAny()
                .get();

        ProjectInput input = LabAdminTestUtils.getAmloForAlphaInput();
        input.setName("Updated Amlo");
        Project returned = projectService.updateProject(input, project.getId(), manager);
        
        Project fetched = projectRepository.findById(returned.getId()).get();
        Assertions.assertEquals(input.getName(), fetched.getName());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateProject_whenAlabAdmin_gotForbiddenException() {
        Project project = projectService.getProjects(AccountManagerTestUtils.ALPHA_LAB_KEY, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL).stream()
                .filter(gotProject -> gotProject.getName().equals(LabAdminTestUtils.AMLO_NAME))
                .findAny()
                .get();

        ProjectInput input = LabAdminTestUtils.getAmloForAlphaInput();
        input.setName("Updated Amlo");
        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            projectService.updateProject(input, project.getId(), AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateProject_whenBlabManager_gotForbiddenException() {
        Project project = projectService.getProjects(AccountManagerTestUtils.ALPHA_LAB_KEY, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL).stream()
                .filter(gotProject -> gotProject.getName().equals(LabAdminTestUtils.AMLO_NAME))
                .findAny()
                .get();

        ProjectInput input = LabAdminTestUtils.getAmloForAlphaInput();
        input.setName("Updated Amlo");
        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            projectService.updateProject(input, project.getId(), AccountManagerTestUtils.BETA_LAB_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateProject_whenProjectNameAlreadyExists_gotValidationException() {
        Principal manager = AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL;
        Project project = projectService.getProjects(AccountManagerTestUtils.ALPHA_LAB_KEY, manager).stream()
                .filter(gotProject -> gotProject.getName().equals(LabAdminTestUtils.AMLO_NAME))
                .findAny()
                .get();

        ProjectInput input = LabAdminTestUtils.getAmloForAlphaInput();
        input.setName("Lisinopril");
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            projectService.updateProject(input, project.getId(), manager);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateProject_whenLabKeyChanges_labStaysTheSame() {
        Principal manager = AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL;
        Project project = projectService.getProjects(AccountManagerTestUtils.ALPHA_LAB_KEY, manager).stream()
                .filter(gotProject -> gotProject.getName().equals(LabAdminTestUtils.AMLO_NAME))
                .findAny()
                .get();

        ProjectInput input = LabAdminTestUtils.getAmloForAlphaInput();
        input.setLabKey(AccountManagerTestUtils.BETA_LAB_KEY);
        Project updated = projectService.updateProject(input, project.getId(), manager);
        
        Assertions.assertFalse(updated.getLab().getKey().equals(AccountManagerTestUtils.BETA_LAB_KEY));
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateProject_whenProjectExistsWithSameNameWithAnotherLab_updated() {
        Principal manager = AccountManagerTestUtils.ALPHA_BETA_LAB_MANAGER_PRINCIPAL;
        Project project = projectService.getProjects(AccountManagerTestUtils.ALPHA_LAB_KEY, manager).stream()
                .filter(gotProject -> gotProject.getName().equals(LabAdminTestUtils.AMLO_NAME))
                .findAny()
                .get();

        ProjectInput input = LabAdminTestUtils.getAmloForAlphaInput();
        String updatedName = "Indapamide";
        input.setName(updatedName);
        Project updated = projectService.updateProject(input, project.getId(), manager);

        Assertions.assertEquals(updated.getName(), updatedName);
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateProject_whenIdDoesNotExist_gotResourceNotFoundException() {
        Principal manager = AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL;
        ProjectInput input = LabAdminTestUtils.getAmloForAlphaInput();
        input.setName("Updated name");
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            projectService.updateProject(input, (long) Integer.MAX_VALUE, manager);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testUpdateProject_whenProjectAlreadyDeleted_gotResourceNotFoundException() {
        Principal manager = AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL;
        Project project = projectService.getProjects(AccountManagerTestUtils.ALPHA_LAB_KEY, manager, false).stream()
                .filter(gotProject -> gotProject.getDeleted() && gotProject.getLab().getKey().equals(AccountManagerTestUtils.ALPHA_LAB_KEY))
                .findAny()
                .get();
        ProjectInput input = LabAdminTestUtils.getAmloForAlphaInput();
        input.setName("Updated name");
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            projectService.updateProject(input, project.getId(), manager);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    
    // DELETE
    @Test
    @Rollback
    @Transactional
    public void testDeleteProject_whenAllValid_gotNoException() {
        Principal manager = AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL;
        Project project = projectService.getProjects(AccountManagerTestUtils.ALPHA_LAB_KEY, manager).stream()
                .filter(gotProject ->
                        gotProject.getLab().getKey().equals(AccountManagerTestUtils.ALPHA_LAB_KEY) &&
                                gotProject.getName().equals(LabAdminTestUtils.AMLO_NAME))
                .findAny()
                .get();
        projectService.deleteProject(project.getId(), manager);
    }

    @Test
    @Rollback
    @Transactional
    public void testDeleteProject_whenAllValid_gotFetchedDeleted() {
        Principal manager = AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL;
        Project project = projectService.getProjects(AccountManagerTestUtils.ALPHA_LAB_KEY, manager).stream()
                .filter(gotProject ->
                        gotProject.getLab().getKey().equals(AccountManagerTestUtils.ALPHA_LAB_KEY) &&
                                gotProject.getName().equals(LabAdminTestUtils.AMLO_NAME))
                .findAny()
                .get();
        projectService.deleteProject(project.getId(), manager);
        Project deleted = projectRepository.findById(project.getId()).get();
        Assertions.assertTrue(deleted.getDeleted());
    }

    @Test
    @Rollback
    @Transactional
    public void testDeleteProject_whenAlphaLabAdmin_gotForbiddenException() {
        Principal manager = AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL;
        Project project = projectService.getProjects(AccountManagerTestUtils.ALPHA_LAB_KEY, manager).stream()
                .filter(gotProject ->
                        gotProject.getLab().getKey().equals(AccountManagerTestUtils.ALPHA_LAB_KEY) &&
                                gotProject.getName().equals(LabAdminTestUtils.AMLO_NAME))
                .findAny()
                .get();
        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            projectService.deleteProject(project.getId(), AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testDeleteProject_whenBetaLabManager_gotForbiddenEception() {
        Principal manager = AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL;
        Project project = projectService.getProjects(AccountManagerTestUtils.ALPHA_LAB_KEY, manager).stream()
                .filter(gotProject ->
                        gotProject.getLab().getKey().equals(AccountManagerTestUtils.ALPHA_LAB_KEY) &&
                                gotProject.getName().equals(LabAdminTestUtils.AMLO_NAME))
                .findAny()
                .get();
        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            projectService.deleteProject(project.getId(), AccountManagerTestUtils.BETA_LAB_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testDeleteProject_whenIdDoesNotExist_gotResourceNotFoundException() {
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            projectService.deleteProject((long) Integer.MAX_VALUE, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testDeleteProject_whenAlreadyDeleted_gotResourceNotFoundException() {
        Principal manager = AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL;
        Project project = projectService.getProjects(AccountManagerTestUtils.ALPHA_LAB_KEY, manager, false).stream()
                .filter(gotProject ->
                        gotProject.getLab().getKey().equals(AccountManagerTestUtils.ALPHA_LAB_KEY) && 
                        gotProject.getDeleted())
                .findAny()
                .get();
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            projectService.deleteProject(project.getId(), AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    // READ
    @Test
    @Rollback
    @Transactional
    public void testGetProjects_gotNoException() {
        projectService.getProjects(alphaLabKey, alphaLabUser);
    }

    @Test
    @Rollback
    @Transactional
    public void testGetProjects_ReturnedListSizeIsGreaterThenOne() {
        List<Project> projects = projectService.getProjects(alphaLabKey, alphaLabUser);
        Assertions.assertTrue(projects.size() > 1);
    }

    @Test
    @Rollback
    @Transactional
    public void testGetProjects_eachProjectIsForAlpha() {
        List<Project> projects = projectService.getProjects(alphaLabKey, alphaLabUser);
        Assertions.assertFalse(projects.stream().anyMatch(project -> !project.getLab().getKey().equals(alphaLabKey)));
    }

    @Test
    @Rollback
    @Transactional
    public void testGetProjects_neitherProjectIsNotDeleted() {
        List<Project> projects = projectService.getProjects(alphaLabKey, alphaLabUser);
        Assertions.assertFalse(projects.stream().anyMatch(project -> project.getDeleted()));
    }

    @Test
    @Rollback
    @Transactional
    public void testGetProjects_whenLabKeyIsInvalid_gotResourceNotFoundException() {
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            projectService.getProjects("non-existing", alphaLabUser);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testGetProjects_whenBetaLabUser_gotForbiddenFoundException() {
        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            projectService.getProjects(alphaLabKey, AccountManagerTestUtils.BETA_LAB_USER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testGetProjectsWithDeleted_gotNoException() {
        projectService.getProjects(alphaLabKey, alphaLabUser, false);
    }

    @Test
    @Rollback
    @Transactional
    public void testGetProjectsWithDeleted_eachProjectIsForAlpha() {
        List<Project> projects = projectService.getProjects(alphaLabKey, alphaLabUser, false);
        Assertions.assertFalse(projects.stream().anyMatch(project -> !project.getLab().getKey().equals(alphaLabKey)));
    }

    @Test
    @Rollback
    @Transactional
    public void testGetProjectsWithDeleted_thereIsBothDeletedAndNotDeleted() {
        List<Project> projects = projectService.getProjects(alphaLabKey, alphaLabUser, false);
        Assertions.assertTrue(projects.stream().anyMatch(project -> project.getDeleted()));
        Assertions.assertTrue(projects.stream().anyMatch(project -> !project.getDeleted()));
    }

    @Test
    @Rollback
    @Transactional
    public void testGetProjectsWithDeleted_whenLabKeyIsInvalid_gotResourceNotFoundException() {
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            projectService.getProjects("non-existing", alphaLabUser, false);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testGetProjectsWithDeleted_whenBetaLabUser_gotForbiddenFoundException() {
        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            projectService.getProjects(alphaLabKey, AccountManagerTestUtils.BETA_LAB_USER_PRINCIPAL, false);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testGetProjectsOnlyAvailable_gotNoException() {
        projectService.getProjects(alphaLabKey, alphaLabUser, true);
    }

    @Test
    @Rollback
    @Transactional
    public void testGetProjectsOnlyAvailable_ReturnedListSizeIsGreaterThenOne() {
        List<Project> projects = projectService.getProjects(alphaLabKey, alphaLabUser, true);
        Assertions.assertTrue(projects.size() > 1);
    }

    @Test
    @Rollback
    @Transactional
    public void testGetProjectsOnlyAvailable_eachProjectIsForAlpha() {
        List<Project> projects = projectService.getProjects(alphaLabKey, alphaLabUser, true);
        Assertions.assertFalse(projects.stream().anyMatch(project -> !project.getLab().getKey().equals(alphaLabKey)));
    }

    @Test
    @Rollback
    @Transactional
    public void testGetProjectsOnlyAvailable_neitherProjectIsNotDeleted() {
        List<Project> projects = projectService.getProjects(alphaLabKey, alphaLabUser, true);
        Assertions.assertFalse(projects.stream().anyMatch(project -> project.getDeleted()));
    }

    @Test
    @Rollback
    @Transactional
    public void testGetProjectsOnlyAvailable_whenLabKeyIsInvalid_gotResourceNotFoundException() {
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            projectService.getProjects("non-existing", alphaLabUser, true);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testGetProjects_whenBetaLabUserOnlyAvailable_gotForbiddenFoundException() {
        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            projectService.getProjects(alphaLabKey, AccountManagerTestUtils.BETA_LAB_USER_PRINCIPAL, true);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testFindProjectById_gotNoException() {
        Project target = projectService.getProjects(alphaLabKey, alphaLabUser).stream()
                .filter(found -> found.getName().equals(LabAdminTestUtils.AMLO_NAME) && found.getLab().getKey().equals(alphaLabKey))
                .findAny().get();
        projectService.findById(target.getId());
    }

    @Test
    @Rollback
    @Transactional
    public void testFindProjectById_gotExpectedProject() {
        Project target = projectService.getProjects(alphaLabKey, alphaLabUser).stream()
                .filter(found -> found.getName().equals(LabAdminTestUtils.AMLO_NAME) && found.getLab().getKey().equals(alphaLabKey))
                .findAny().get();
        Project project = projectService.findById(target.getId());
        Assertions.assertEquals(LabAdminTestUtils.AMLO_NAME, project.getName());
        Assertions.assertEquals(alphaLabKey, project.getLab().getKey());
        Assertions.assertFalse(project.getDeleted());
    }

    @Test
    @Rollback
    @Transactional
    public void testFindProjectById_whenIdDoesNotExist_gotResourceNotFoundException() {
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            projectService.findById((long) Integer.MAX_VALUE);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testFindProjectById_whenAlreadyDeleted_gotResourceNotFoundException() {
        Project target = projectService.getProjects(alphaLabKey, alphaLabUser, false).stream()
                .filter(found -> found.getDeleted())
                .findAny().get();

        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            projectService.findById(target.getId());
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testFindProjectByIdOnlyActive_gotNoException() {
        Project target = projectService.getProjects(alphaLabKey, alphaLabUser).stream()
                .filter(found -> found.getName().equals(LabAdminTestUtils.AMLO_NAME) && found.getLab().getKey().equals(alphaLabKey))
                .findAny().get();
        projectService.findById(target.getId(), true);
    }

    @Test
    @Rollback
    @Transactional
    public void testFindProjectByIdOnlyActive_gotExpectedProject() {
        Project target = projectService.getProjects(alphaLabKey, alphaLabUser).stream()
                .filter(found -> found.getName().equals(LabAdminTestUtils.AMLO_NAME) && found.getLab().getKey().equals(alphaLabKey))
                .findAny().get();
        Project project = projectService.findById(target.getId(), true);
        Assertions.assertEquals(LabAdminTestUtils.AMLO_NAME, project.getName());
        Assertions.assertEquals(alphaLabKey, project.getLab().getKey());
        Assertions.assertFalse(project.getDeleted());
    }

    @Test
    @Rollback
    @Transactional
    public void testFindProjectByIdOnlyActive_whenIdDoesNotExist_gotResourceNotFoundException() {
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            projectService.findById((long) Integer.MAX_VALUE, true);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testFindProjectByIdOnlyActive_whenAlreadyDeleted_gotResourceNotFoundException() {
        Project target = projectService.getProjects(alphaLabKey, alphaLabUser, false).stream()
                .filter(found -> found.getDeleted())
                .findAny().get();

        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            projectService.findById(target.getId(), true);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testFindProjectByIdOnlyActiveFalse_gotNoException() {
        Project target = projectService.getProjects(alphaLabKey, alphaLabUser, false).stream()
                .filter(found -> found.getDeleted())
                .findAny().get();
        projectService.findById(target.getId(), false);
    }

    @Test
    @Rollback
    @Transactional
    public void testFindProjectByIdOnlyActiveFalse_gotExpectedProject() {
        Project target = projectService.getProjects(alphaLabKey, alphaLabUser, false).stream()
                .filter(found -> found.getDeleted())
                .findAny().get();
        Project project = projectService.findById(target.getId(), false);
        Assertions.assertEquals("Pantoprazole", project.getName());
        Assertions.assertEquals(alphaLabKey, project.getLab().getKey());
        Assertions.assertTrue(project.getDeleted());
    }

    @Test
    @Rollback
    @Transactional
    public void testFindProjectByIdOnlyActiveFalse_whenIdDoesNotExist_gotResourceNotFoundException() {
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            projectService.findById((long) Integer.MAX_VALUE, false);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void testFindProjectByIdOnlyActiveFalse_whenAlreadyDeleted_gotDeletedProject() {
        Project target = projectService.getProjects(alphaLabKey, alphaLabUser, false).stream()
                .filter(found -> found.getDeleted())
                .findAny().get();

        Project project = projectService.findById(target.getId(), false);
        Assertions.assertTrue(project.getDeleted());
    }

    

}
