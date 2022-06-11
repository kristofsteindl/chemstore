package com.ksteindl.chemstore.audittrail;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

public class StartingEntry<T extends AuditTracable> {
    
    final Principal performer;
    final EntityLogTemplate<T> template;
    final Map<String, String> oldValues = new HashMap<>();
    final Map<String, String> oldLabels = new HashMap<>();

    public static <T extends AuditTracable> StartingEntry of(T entity, Principal performer, EntityLogTemplate<T> template) {
        return new StartingEntry<>(entity, performer, template);
    }
    
    private StartingEntry(T entity, Principal performer, EntityLogTemplate<T> template) {
        this.template = template;
        this.performer = performer;
        template.attributeProducers.forEach(producer -> 
                oldValues.put(producer.attributeName, producer.valueProducer.apply(entity)));
        template.attributeProducers.forEach(producer ->
                oldLabels.put(producer.attributeName, producer.valueLabelProducer.apply(entity)));
    }
}
