package com.ksteindl.chemstore.web;

import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.entities.Project;
import com.ksteindl.chemstore.domain.entities.Recipe;
import com.ksteindl.chemstore.domain.input.ProjectInput;
import com.ksteindl.chemstore.domain.input.RecipeInput;
import com.ksteindl.chemstore.service.AppUserService;
import com.ksteindl.chemstore.service.ProjectService;
import com.ksteindl.chemstore.service.RecipeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lab-manager")
@CrossOrigin
public class LabManagerController {

    private static final Logger logger = LogManager.getLogger(LabManagerController.class);

    @Autowired
    private AppUserService appUserService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private RecipeService recipeService;
    @Autowired
    private MapValidationErrorService mapValidationErrorService;

    @GetMapping("/user")
    public Map<String, List<AppUser>> getUersFromManagedLabs(Principal principal) {
        logger.info("'api/lab-manager/user' was called by {}", principal.getName());
        return appUserService.getUsersFromManagedLabs(principal);
    }

    @GetMapping("/user/{labKey}")
    public List<AppUser> getUersFromManagedLab(@PathVariable String labKey, Principal principal) {
        logger.info("'api/lab-manager/user/{labKey}' was called by {}", principal.getName());
        return appUserService.getUsersFromManagedLab(principal, labKey);
    }
    
    //PROJECT
    @PostMapping("/project")
    public ResponseEntity<Project> createProject(
            @Valid @RequestBody ProjectInput projectInput,
            BindingResult result,
            Principal principal) {
        logger.info("POST '/api/lab-manager/project' was called with {} by {}", projectInput, principal.getName());
        mapValidationErrorService.throwExceptionIfNotValid(result);
        Project project = projectService.createProject(projectInput, principal);
        logger.info("POST '/api/lab-manager/project' was succesful with returned result{}", project);
        return new ResponseEntity<>(project, HttpStatus.CREATED);
    }

    @PutMapping("/project/{id}")
    public ResponseEntity<Project> updateProject(
            @Valid @RequestBody ProjectInput projectInput,
            BindingResult result,
            Principal principal,
            @PathVariable  Long id) {
        logger.info("PUT '/api/lab-manager/project/{id}' was called with id {} and input {}", id, projectInput);
        mapValidationErrorService.throwExceptionIfNotValid(result);
        Project project = projectService.updateProject(projectInput, id, principal);
        logger.info("PUT '/api/lab-manager/project/{id}' was succesful with returned result{}", project);
        return new ResponseEntity<>(project, HttpStatus.CREATED);
    }
    
    @DeleteMapping("/project/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteManufacture(
            @PathVariable Long id,
            Principal principal) {
        logger.info("DELETE '/api/lab-manager/project/{id}' was called with id {} by {}", id, principal.getName());
        projectService.deleteProject(id, principal);
        logger.info("DELETE '/api/lab-manager/project/{id}' was successful");
    }

    @GetMapping("/project/{id}")
    public ResponseEntity<Project> getProject(
            @PathVariable  Long id) {
        logger.info("GET '/project/{id}' was called with id {}", id);
        Project project = projectService.findById(id);
        logger.info("GET '/project/{id}' was succesful with returned result{}", project);
        return new ResponseEntity<>(project, HttpStatus.OK);
    }
    
    //RECIPE
    @PostMapping("/recipe")
    public ResponseEntity<Recipe> createRecipe(
            @Valid @RequestBody RecipeInput recipeInput,
            BindingResult result,
            Principal principal
            ) {
        logger.info("POST 'api/lab-manager/recipe' was called with {} by {}", recipeInput, principal.getName());
        mapValidationErrorService.throwExceptionIfNotValid(result);
        Recipe recipe = recipeService.createRecipe(recipeInput, principal);
        logger.info("POST 'api/lab-manager/recipe' was successful with returned result{}", recipe);
        return new ResponseEntity<>(recipe, HttpStatus.CREATED);
    }

    @PutMapping("/recipe/{id}")
    public ResponseEntity<Recipe> updateRecipe(
            @Valid @RequestBody RecipeInput recipeInput,
            BindingResult result,
            Principal principal,
            @PathVariable  Long id) {
        logger.info("PUT '/api/lab-manager/recipe/{id}' was called with id {} and input {} by {}", id, recipeInput, principal.getName());
        mapValidationErrorService.throwExceptionIfNotValid(result);
        Recipe recipe = recipeService.updateRecipe(recipeInput, id, principal);
        logger.info("PUT '/api/lab-manager/recipe/{id}' was successful with returned result {}", recipe);
        return new ResponseEntity<>(recipe, HttpStatus.CREATED);
    }
    
    @DeleteMapping("/recipe/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteRecipe(
            @PathVariable Long id,
            Principal principal) {
        logger.info("DELETE '/api/lab-manager/recipe/{id} was called with id {} by {}'", id, principal.getName());
        recipeService.deleteRecipe(id, principal);
        logger.info("DELETE '/api/lab-manager/recipe/{id} was successful");

    }

    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<Recipe> getRecipe(
            @PathVariable Long recipeId,
            Principal user
    ) {
        logger.info("GET '/api/lab-manager/recipe/{recipeId}' was called, with recipeId {} by user {}", recipeId, user.getName());
        Recipe recipe = recipeService.getRecipe(recipeId, user);
        logger.info("GET '/api/lab-manager/recipe/{recipeId}' was succesful with recipe {}", recipe);
        return new ResponseEntity<>(recipe, HttpStatus.OK);
    }


}
