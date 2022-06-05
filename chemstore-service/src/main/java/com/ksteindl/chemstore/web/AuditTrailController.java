package com.ksteindl.chemstore.web;

import com.ksteindl.chemstore.audittrail.AuditTrailService;
import com.ksteindl.chemstore.audittrail.TrailEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("api/audittrail")
@CrossOrigin
public class AuditTrailController {

    private static final Logger logger = LogManager.getLogger(AuditTrailController.class);
    
    @Autowired
    private AuditTrailService auditTrailService;
    

    @GetMapping("/all")
    public ResponseEntity<List<TrailEntry>> getAudittrails(Principal principal) {
        logger.info("'api/audittrail/all' was called by {}", principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(auditTrailService.getAllEntry());
    }
}
