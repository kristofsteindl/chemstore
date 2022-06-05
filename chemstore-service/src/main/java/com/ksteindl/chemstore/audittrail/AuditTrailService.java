package com.ksteindl.chemstore.audittrail;

import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.entities.AuditTracable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuditTrailService {
    
    @Autowired
    private TrailEntryRepository trailEntryRepository;

    public List<TrailEntry> getAllEntry() {
        Iterator<TrailEntry> iterator = trailEntryRepository.findAll().iterator();
        List<TrailEntry> entries = new ArrayList<>();
        while (iterator.hasNext()) {
            entries.add(iterator.next());
        }
        return entries;
    }

    public <T extends AuditTracable> void createEntry(T entity, AppUser modifier, EntityLogTemplate<T> template) {
        AuditLogEntryContext<T> context = AuditLogEntryContext.<T>builder()
                .template(template)
                .actionType(ActionType.CREATE)
                .entity(entity)
                .modifier(modifier)
                .build();
        trailEntryRepository.save(createEntry(context));
    }

    public <T extends AuditTracable> void archiveEntry(StartingEntry<T> startingEntry, T entity, AppUser modifier) {
        AuditLogEntryContext<T> context = AuditLogEntryContext.<T>builder()
                .template(startingEntry.templates)
                .actionType(ActionType.ARCHIVE)
                .entity(entity)
                .modifier(modifier)
                .startingEntry(startingEntry)
                .build();
        trailEntryRepository.save(createEntry(context));
    }

    public <T extends AuditTracable> void updateEntry(StartingEntry<T> startingEntry, T updatedLab, AppUser modifier) {
        AuditLogEntryContext<T> context = AuditLogEntryContext.<T>builder()
                .template(startingEntry.templates)
                .actionType(ActionType.UPDATE)
                .entity(updatedLab)
                .modifier(modifier)
                .startingEntry(startingEntry)
                .build();
        trailEntryRepository.save(createEntry(context));
    }
    
    private <T extends AuditTracable> TrailEntry createEntry(AuditLogEntryContext<T> context) {
        T entity = context.entity;
        EntityLogTemplate<T> template = context.template;
        TrailEntry trailEntry = new TrailEntry();
        List<AttributeProducer<T>> producers = template.attributeProducers;
        trailEntry.setEntityTypeName(template.entityName);
        trailEntry.setEntityTypeLabel(template.entityLabel);
        trailEntry.setDateTime(OffsetDateTime.now());
        trailEntry.setUser(context.modifier);
        trailEntry.setEntityId(entity.getId());
        trailEntry.setType(context.actionType);
        List<EntityLogAttribute> entityLogAttributes = producers.stream()
                .map(producer -> getAttribute(context, producer))
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
        trailEntry.setUpdatedAttributes(entityLogAttributes);
        return trailEntry;
    }


    private static <T extends AuditTracable> Optional<EntityLogAttribute> getAttribute(
            AuditLogEntryContext<T> context, 
            AttributeProducer<T> producer) {
        ActionType actionType = context.actionType;
        T entity = context.entity;
        EntityLogAttribute.EntityLogAttributeBuilder builder = EntityLogAttribute.builder()
                .attributeName(producer.attributeName)
                .attributeLabel(producer.attributeLabel);
        if (actionType == ActionType.CREATE) {
            builder.newValue(producer.valueProducer.apply(entity))
                    .newLabel(producer.valueLabelProducer.apply(entity));
            return Optional.of(builder.build());
        } if (actionType == ActionType.DELETE) {
            builder.oldValue(producer.valueProducer.apply(entity))
                    .oldLabel(producer.valueLabelProducer.apply(entity));
            return Optional.of(builder.build());
        } if (actionType == ActionType.ARCHIVE) {
            return getArchiveAttribute(builder, context, producer);
        } else {
            return getUpdateAttribute(builder, context, producer);
        }
        
    }

    private static <T extends AuditTracable> Optional<EntityLogAttribute> getArchiveAttribute(
            EntityLogAttribute.EntityLogAttributeBuilder builder,
            AuditLogEntryContext<T> context,
            AttributeProducer<T> producer) {
        T entity = context.entity;
        StartingEntry<T> startingEntry = context.startingEntry;
        String oldValue = startingEntry.oldValues.get(producer.attributeName);
        builder.oldValue(oldValue)
                .oldLabel(startingEntry.oldLabels.get(producer.attributeName))
                .newValue( producer.valueProducer.apply(entity))
                .newLabel(producer.valueLabelProducer.apply(entity));
        return Optional.of(builder.build());
    }

    private static <T extends AuditTracable> Optional<EntityLogAttribute> getUpdateAttribute(
            EntityLogAttribute.EntityLogAttributeBuilder builder, 
            AuditLogEntryContext<T> context, 
            AttributeProducer<T> producer) {
        StartingEntry<T> startingEntry = context.startingEntry;
        T entity = context.entity;
        String oldValue = startingEntry.oldValues.get(producer.attributeName);
        String newValue = producer.valueProducer.apply(entity);
        if (oldValue.equals(newValue)) {
            return Optional.empty();
        } else {
            builder
                    .newValue(newValue)
                    .newLabel(producer.valueLabelProducer.apply(entity))
                    .oldValue(oldValue)
                    .oldLabel(startingEntry.oldLabels.get(producer.attributeName));
            return Optional.of(builder.build());
        }
    }
        
    
    
}
