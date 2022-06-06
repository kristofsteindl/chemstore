package com.ksteindl.chemstore.audittrail;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class EntityLogTemplate<T extends AuditTracable> {
    
    public final String entityName;
    public final String entityLabel;
    public final List<AttributeProducer<T>> attributeProducers;
    
}
