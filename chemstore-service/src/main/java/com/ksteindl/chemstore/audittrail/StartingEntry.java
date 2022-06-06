package com.ksteindl.chemstore.audittrail;

import java.util.HashMap;
import java.util.Map;

public class StartingEntry<T extends AuditTracable> {
    
    final EntityLogTemplate<T> template;
    final Map<String, String> oldValues = new HashMap<>();
    final Map<String, String> oldLabels = new HashMap<>();

    public static <T extends AuditTracable> StartingEntry of(EntityLogTemplate<T> template, T entity) {
        return new StartingEntry<>(template, entity);
    }
    
    private StartingEntry(EntityLogTemplate<T> template, T entity) {
        this.template = template;
        template.attributeProducers.forEach(producer -> 
                oldValues.put(producer.attributeName, producer.valueProducer.apply(entity)));
        template.attributeProducers.forEach(producer ->
                oldLabels.put(producer.attributeName, producer.valueLabelProducer.apply(entity)));
    }
}
