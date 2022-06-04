package com.ksteindl.chemstore.web;

import com.ksteindl.chemstore.domain.entities.Mixture;
import com.ksteindl.chemstore.domain.input.MixtureInput;
import com.ksteindl.chemstore.domain.input.MixtureQuery;
import com.ksteindl.chemstore.service.MixtureService;
import com.ksteindl.chemstore.service.wrapper.PagedList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/mixture")
@CrossOrigin
public class MixtureController {

    private static final Logger logger = LogManager.getLogger(MixtureController.class);
    
    @Autowired
    private MixtureService mixtureService;
    @Autowired
    private MapValidationErrorService mapValidationErrorService;
    
    @PostMapping
    public ResponseEntity<Mixture> createMixture(
            @Valid @RequestBody MixtureInput mixtureInput,
            BindingResult result,
            Principal user) {
        logger.info("POST '/mixture' was called with {} by {}", mixtureInput, user);
        mapValidationErrorService.throwExceptionIfNotValid(result);
        Mixture mixture = mixtureService.createMixtureAsUser(mixtureInput, user);
        logger.info("POST '/mixture' was succesful with returned result \n{}", mixture);
        return ResponseEntity.status(HttpStatus.CREATED).body(mixture);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mixture> updateMixture(
            @Valid @RequestBody MixtureInput mixtureInput,
            BindingResult result,
            @PathVariable Long id,
            Principal user) {
        logger.info("PUT '/mixture/{id}' was called with {} and {} by {}", mixtureInput, id, user);
        mapValidationErrorService.throwExceptionIfNotValid(result);
        Mixture mixture = mixtureService.updateMixture(mixtureInput, id, user);
        logger.info("PUT '/mixture/{id}' was succesful with returned result \n{}", mixture);
        return ResponseEntity.status(HttpStatus.CREATED).body(mixture);
    }

    @GetMapping("/{labKey}")
    public ResponseEntity<PagedList<Mixture>> getMixturesForLab(
            @PathVariable String labKey,
            @RequestParam(value= "projectId", required = false) Long projectId,
            @RequestParam(value= "recipeId", required = false) Long recipeId,
            @RequestParam(value= "availableOn", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate availableOn,
            @RequestParam(value= "available", required = false) Boolean available,
            @RequestParam(value="page", defaultValue = "0") Integer page,
            @RequestParam(value= "size", defaultValue = "10") Integer size,
            Principal user) {
        logger.info("GET '/mixture/{labKey}' was called with {} by {}", labKey, user.getName());
        logger.info("RequestParams: projectId={}, recipeId={}, availableOn={}, available={}, page={}, size={}", 
                projectId, recipeId, availableOn, available, page, size);
        MixtureQuery mixtureQuery = MixtureQuery.builder()
                .labKey(labKey)
                .projectId(projectId)
                .recipeId(recipeId)
                .principal(user)
                .page(page)
                .availableOn(availableOn)
                .available(available)
                .size(size)
                .build();
        PagedList<Mixture> mixtures = mixtureService.getMixturesForLab(mixtureQuery);
        logger.info("GET '/mixture' was successful with content.size={}, totalItems={}, totalPages={}, currentPage={}", 
                mixtures.getContent().size(), mixtures.getTotalItems(), mixtures.getTotalPages(), mixtures.getCurrentPage());
        logger.debug(mixtures.getContent());
        return ResponseEntity.status(HttpStatus.OK).body(mixtures);
    }

    @GetMapping("/{labKey}/{id}")
    public ResponseEntity<Mixture> getMixtureById(
            @PathVariable String labKey,
            @PathVariable Long id,
            Principal user) {
        logger.info("GET '/mixture/{labKey}/{id}' was called with labKey {} and id {} by {}", labKey, id, user);
        Mixture mixture = mixtureService.findByIdForUser(id, user);
        logger.info("GET '/mixture/{labKey}/{id}'was succesful with mixture id {} ", mixture.getId());
        return ResponseEntity.status(HttpStatus.OK).body(mixture);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteMixture(
            @PathVariable Long id,
            Principal user) {
        logger.info("DELETE '/mixture/{id}' was called with {} by {}", id, user);
        mixtureService.deleteMixture(id, user);
        logger.info("DELETE '/mixture/{id}' was succesful");
    }
    
}
