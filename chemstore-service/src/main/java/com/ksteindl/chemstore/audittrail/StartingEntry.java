package com.ksteindl.chemstore.audittrail;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

public class StartingEntry<T extends AuditTracable> {
    
    final EntityLogTemplate<T> template;
    final Principal performer;
    final Map<String, String> oldValues = new HashMap<>();
    final Map<String, String> oldLabels = new HashMap<>();

    public static <T extends AuditTracable> StartingEntry of(EntityLogTemplate<T> template, T entity, Principal performer) {
        return new StartingEntry<>(template, entity, performer);
    }
    
    private StartingEntry(EntityLogTemplate<T> template, T entity, Principal performer) {
        this.template = template;
        this.performer = performer;
        template.attributeProducers.forEach(producer -> 
                oldValues.put(producer.attributeName, producer.valueProducer.apply(entity)));
        template.attributeProducers.forEach(producer ->
                oldLabels.put(producer.attributeName, producer.valueLabelProducer.apply(entity)));
    }
}
