package com.ksteindl.chemstore.audittrail;

import org.springframework.data.repository.CrudRepository;

public interface AuditTrailRepository extends CrudRepository<AuditTrailEntry, Long>, AuditTrailCustomRepository {
    
    
}
