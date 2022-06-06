package com.ksteindl.chemstore.audittrail;

import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.entities.HasLab;
import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.exceptions.ValidationException;
import com.ksteindl.chemstore.service.wrapper.PagedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Principal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuditTrailService {
    
    @Autowired
    private AuditTrailRepository auditTrailRepository;

    @Transactional
    public PagedList<AuditTrailEntry> findAuditTrailEntries(AuditTrailEntryQuery auditTrailEntryQuery, Principal principal) {
        Set<String> actionTypeStrings = auditTrailEntryQuery.getActionTypes();
        if (actionTypeStrings != null) {
            for (String actionTypeString : actionTypeStrings) {
                if (!ActionType.contains(actionTypeString)) {
                    throw new ValidationException("actionType must be amongs the following: " + ActionType.values());
                }
            }
        }
        return auditTrailRepository.findAuditTrailEntries(auditTrailEntryQuery);
    }

    public <T extends HasLab> void createEntry(T entity, AppUser performer, EntityLogTemplate<T> template) {
        createEntry(entity, performer, template, entity.getLab());
    }

    public <T extends AuditTracable> void createEntry(T entity, AppUser performer, EntityLogTemplate<T> template) {
        createEntry(entity, performer, template, null);
    }

    public <T extends HasLab> void updateEntry(StartingEntry<T> startingEntry, T updatedEntity, AppUser performer) {
        updateEntry(startingEntry, updatedEntity, performer, updatedEntity.getLab());
    }

    public <T extends AuditTracable> void updateEntry(StartingEntry<T> startingEntry, T updatedEntity, AppUser performer) {
        updateEntry(startingEntry, updatedEntity, performer, null);
    }

    public <T extends HasLab> void archiveEntry(StartingEntry<T> startingEntry, T entity, AppUser performer) {
        archiveEntry(startingEntry, entity, performer, entity.getLab());
    }

    public <T extends AuditTracable> void archiveEntry(StartingEntry<T> startingEntry, T entity, AppUser performer) {
        archiveEntry(startingEntry, entity, performer, null);
    }

    private <T extends AuditTracable> void createEntry(T entity, AppUser performer, EntityLogTemplate<T> template, Lab lab) {
        AuditTrailEntryContext<T> context = AuditTrailEntryContext.<T>builder()
                .template(template)
                .actionType(ActionType.CREATE)
                .entity(entity)
                .performer(performer)
                .lab(lab)
                .build();
        auditTrailRepository.save(createEntry(context));
    }

    private <T extends AuditTracable> void updateEntry(StartingEntry<T> startingEntry, T updatedLab, AppUser performer, Lab lab) {
        AuditTrailEntryContext<T> context = AuditTrailEntryContext.<T>builder()
                .template(startingEntry.template)
                .actionType(ActionType.UPDATE)
                .entity(updatedLab)
                .performer(performer)
                .startingEntry(startingEntry)
                .lab(lab)
                .build();
        auditTrailRepository.save(createEntry(context));
    }

    private <T extends AuditTracable> void archiveEntry(StartingEntry<T> startingEntry, T entity, AppUser performer, Lab lab) {
        AuditTrailEntryContext<T> context = AuditTrailEntryContext.<T>builder()
                .template(startingEntry.template)
                .actionType(ActionType.ARCHIVE)
                .entity(entity)
                .performer(performer)
                .startingEntry(startingEntry)
                .lab(lab)
                .build();
        auditTrailRepository.save(createEntry(context));
    }
    
    private <T extends AuditTracable> AuditTrailEntry createEntry(AuditTrailEntryContext<T> context) {
        T entity = context.entity;
        EntityLogTemplate<T> template = context.template;
        AuditTrailEntry auditTrailEntry = new AuditTrailEntry();
        List<AttributeProducer<T>> producers = template.attributeProducers;
        auditTrailEntry.setEntityTypeName(template.entityName);
        auditTrailEntry.setEntityTypeLabel(template.entityLabel);
        auditTrailEntry.setDateTime(OffsetDateTime.now());
        auditTrailEntry.setPerformer(context.performer);
        auditTrailEntry.setEntityId(entity.getId());
        auditTrailEntry.setActionType(context.actionType);
        context.getLab().ifPresent(auditTrailEntry::setLab);
        List<EntityLogAttribute> entityLogAttributes = producers.stream()
                .map(producer -> getAttribute(context, producer))
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
        auditTrailEntry.setUpdatedAttributes(entityLogAttributes);
        return auditTrailEntry;
    }


    private static <T extends AuditTracable> Optional<EntityLogAttribute> getAttribute(
            AuditTrailEntryContext<T> context, 
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
            AuditTrailEntryContext<T> context,
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
            AuditTrailEntryContext<T> context, 
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
