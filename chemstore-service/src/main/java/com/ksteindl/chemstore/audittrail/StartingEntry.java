package com.ksteindl.chemstore.audittrail;

import com.ksteindl.chemstore.domain.entities.AuditTracable;

import java.util.HashMap;
import java.util.Map;

public class StartingEntry<T extends AuditTracable> {
    
    final Map<String, String> oldValues = new HashMap<>();
    final Map<String, String> oldLabels = new HashMap<>();

    public StartingEntry(EntityLogTemplate<T> template, T entity) {
        template.attributeProducers.forEach(producer -> 
                oldValues.put(producer.attributeName, producer.valueProducer.apply(entity)));
        template.attributeProducers.forEach(producer ->
                oldLabels.put(producer.attributeName, producer.valueLabelProducer.apply(entity)));
    }
}
