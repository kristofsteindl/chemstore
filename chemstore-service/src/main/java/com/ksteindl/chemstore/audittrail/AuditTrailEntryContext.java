package com.ksteindl.chemstore.audittrail;

import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.entities.Lab;
import lombok.Builder;

import java.util.Optional;

@Builder
public class AuditTrailEntryContext<T extends AuditTracable>{
    
    EntityLogTemplate<T> template;
    T entity;
    AppUser performer;
    ActionType actionType;
    StartingEntry startingEntry;
    private Lab lab;
    
    Optional<Lab> getLab() {
        return Optional.ofNullable(lab);
    }
    
}
