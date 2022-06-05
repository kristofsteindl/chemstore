package com.ksteindl.chemstore.audittrail;

import org.springframework.data.repository.CrudRepository;

public interface TrailEntryRepository extends CrudRepository<TrailEntry, Long> {
    
    
}
