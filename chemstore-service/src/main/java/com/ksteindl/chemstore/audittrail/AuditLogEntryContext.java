package com.ksteindl.chemstore.audittrail;

import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.entities.AuditTracable;
import lombok.Builder;

@Builder
public class AuditLogEntryContext <T extends AuditTracable>{
    
    EntityLogTemplate<T> template;
    T entity;
    AppUser modifier;
    ActionType actionType;
    StartingEntry startingEntry;
    
}
