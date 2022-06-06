package com.ksteindl.chemstore.audittrail;

import com.ksteindl.chemstore.service.wrapper.PagedList;

public interface AuditTrailCustomRepository {

    PagedList<AuditTrailEntry> findAuditTrailEntries(AuditTrailEntryQuery auditTrailEntryQuery);
    
}
