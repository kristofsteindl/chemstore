package com.ksteindl.chemstore.domain.entities;

import com.ksteindl.chemstore.audittrail.AuditTracable;

public interface HasLab extends AuditTracable {
    
    Lab getLab();
    
}
