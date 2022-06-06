package com.ksteindl.chemstore.web;

import com.ksteindl.chemstore.audittrail.AuditTrailEntry;
import com.ksteindl.chemstore.audittrail.AuditTrailEntryQuery;
import com.ksteindl.chemstore.audittrail.AuditTrailService;
import com.ksteindl.chemstore.service.wrapper.PagedList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.OffsetDateTime;
import java.util.Set;

@RestController
@RequestMapping("api/audittrail")
@CrossOrigin
public class AuditTrailController {

    private static final Logger logger = LogManager.getLogger(AuditTrailController.class);
    
    @Autowired
    private AuditTrailService auditTrailService;
    

    @GetMapping
    public ResponseEntity<PagedList<AuditTrailEntry>> getAudittrails(
            @RequestParam(value="page", defaultValue = "0") Integer page,
            @RequestParam(value= "size", defaultValue = "100") Integer size,
            @RequestParam(value= "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(value= "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to,
            @RequestParam(value= "performerIds", required = false) Set<Long> performerIds,
            @RequestParam(value= "labIds", required = false) Set<Long> labIds,
            @RequestParam(value= "entityIds", required = false) Set<Long> entityIds,
            @RequestParam(value= "actionTypes", required = false) Set<String> actionTypes,
            @RequestParam(value= "entityTypes", required = false) Set<String> entityTypes,
            @RequestParam(value= "global", required = false) Boolean global,
            Principal principal) {
        logger.info("'api/audittrail/all' was called by {}", principal.getName());
        AuditTrailEntryQuery query = AuditTrailEntryQuery.builder()
                .page(page)
                .size(size)
                .from(from)
                .to(to)
                .performerIds(performerIds)
                .labIds(labIds)
                .entityIds(entityIds)
                .actionTypes(actionTypes)
                .entityTypes(entityTypes)
                .global(global)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(auditTrailService.findAuditTrailEntries(query, principal));
    }
}
