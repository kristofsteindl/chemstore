package com.ksteindl.chemstore.audittrail;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Set;

@Builder
@Data
public class AuditTrailEntryQuery {

    private Integer page = 0;
    private Integer size = 100;
    private OffsetDateTime from;
    private OffsetDateTime to;
    private Set<Long> performerIds;
    private Set<Long> labIds;
    private Set<Long> entityIds;
    private Set<String> actionTypes;
    private Set<String> entityTypes;
    private Boolean global;
}
