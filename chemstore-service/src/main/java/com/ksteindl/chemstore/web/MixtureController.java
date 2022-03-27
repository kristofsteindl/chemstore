package com.ksteindl.chemstore.web;

import com.ksteindl.chemstore.domain.entities.Mixture;
import com.ksteindl.chemstore.domain.input.MixtureInput;
import com.ksteindl.chemstore.service.MixtureService;
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
    public ResponseEntity<List<Mixture>> getMixturesForLab(
            @PathVariable String labKey,
            Principal user) {
        logger.info("GET '/mixture/{labKey}' was called with {} by {}", labKey, user);
        List<Mixture> mixtures = mixtureService.getMixturesForLab(labKey, user);
        logger.info("GET '/mixture' was succesful with {} size mixture list", mixtures.size());
        return ResponseEntity.status(HttpStatus.OK).body(mixtures);
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
