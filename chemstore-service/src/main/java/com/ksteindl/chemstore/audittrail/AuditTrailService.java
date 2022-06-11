package com.ksteindl.chemstore.audittrail;

import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.entities.HasLab;
import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.domain.input.AppUserQuery;
import com.ksteindl.chemstore.domain.repositories.appuser.AppUserRepository;
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
    @Autowired
    private AppUserRepository appUserRepository;

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

    public <T extends HasLab> void logEntry(StartingEntry<T> startingEntry, T updatedEntity, ActionType type) {
        logEntry(startingEntry, updatedEntity, type, updatedEntity.getLab());
    }

    public <T extends AuditTracable> void logEntry(StartingEntry<T> startingEntry, T updatedEntity, ActionType type) {
        logEntry(startingEntry, updatedEntity, type, null);
    }
    
    public <T extends HasLab> void createEntry(T entity, Principal performer, EntityLogTemplate<T> template) {
        createEntry(entity, performer, template, entity.getLab());
    }

    public <T extends AuditTracable> void createEntry(T entity, Principal performer, EntityLogTemplate<T> template) {
        createEntry(entity, performer, template, null);
    }

    public <T extends HasLab> void updateEntry(StartingEntry<T> startingEntry, T updatedEntity) {
        updateEntry(startingEntry, updatedEntity, updatedEntity.getLab());
    }

    public <T extends AuditTracable> void updateEntry(StartingEntry<T> startingEntry, T updatedEntity) {
        updateEntry(startingEntry, updatedEntity, null);
    }

    public <T extends HasLab> void archiveEntry(StartingEntry<T> startingEntry, T entity) {
        archiveEntry(startingEntry, entity, entity.getLab());
    }

    public <T extends AuditTracable> void archiveEntry(StartingEntry<T> startingEntry, T entity) {
        archiveEntry(startingEntry, entity, null);
    }

    public <T extends HasLab> void deleteEntry(StartingEntry<T> startingEntry, T entity) {
        deleteEntry(startingEntry, entity, entity.getLab());
    }

    public <T extends AuditTracable> void deleteEntry(StartingEntry<T> startingEntry, T entity) {
        deleteEntry(startingEntry, entity, null);
    }

    private <T extends AuditTracable> void logEntry(StartingEntry<T> startingEntry, T updatedLab, ActionType type, Lab lab) {
        AppUser performerUser = appUserRepository.findAppUsers(AppUserQuery.builder().username(startingEntry.performer.getName()).build()).get(0);
        AuditTrailEntryContext<T> context = AuditTrailEntryContext.<T>builder()
                .template(startingEntry.template)
                .actionType(type)
                .entity(updatedLab)
                .performer(performerUser)
                .startingEntry(startingEntry)
                .lab(lab)
                .build();
        auditTrailRepository.save(createEntry(context));
    }

    private <T extends AuditTracable> void createEntry(T entity, Principal performer, EntityLogTemplate<T> template, Lab lab) {
        AppUser performerUser = appUserRepository.findAppUsers(AppUserQuery.builder().username(performer.getName()).build()).get(0);
        AuditTrailEntryContext<T> context = AuditTrailEntryContext.<T>builder()
                .template(template)
                .actionType(ActionType.CREATE)
                .entity(entity)
                .performer(performerUser)
                .lab(lab)
                .build();
        auditTrailRepository.save(createEntry(context));
    }

    private <T extends AuditTracable> void updateEntry(StartingEntry<T> startingEntry, T updatedLab, Lab lab) {
        AppUser performerUser = appUserRepository.findAppUsers(AppUserQuery.builder().username(startingEntry.performer.getName()).build()).get(0);
        AuditTrailEntryContext<T> context = AuditTrailEntryContext.<T>builder()
                .template(startingEntry.template)
                .actionType(ActionType.UPDATE)
                .entity(updatedLab)
                .performer(performerUser)
                .startingEntry(startingEntry)
                .lab(lab)
                .build();
        auditTrailRepository.save(createEntry(context));
    }

    private <T extends AuditTracable> void archiveEntry(StartingEntry<T> startingEntry, T entity, Lab lab) {
        AppUser performerUser = appUserRepository.findAppUsers(AppUserQuery.builder().username(startingEntry.performer.getName()).build()).get(0);
        AuditTrailEntryContext<T> context = AuditTrailEntryContext.<T>builder()
                .template(startingEntry.template)
                .actionType(ActionType.ARCHIVE)
                .entity(entity)
                .performer(performerUser)
                .startingEntry(startingEntry)
                .lab(lab)
                .build();
        auditTrailRepository.save(createEntry(context));
    }

    private <T extends AuditTracable> void deleteEntry(StartingEntry<T> startingEntry, T entity, Lab lab) {
        AppUser performerUser = appUserRepository.findAppUsers(AppUserQuery.builder().username(startingEntry.performer.getName()).build()).get(0);
        AuditTrailEntryContext<T> context = AuditTrailEntryContext.<T>builder()
                .template(startingEntry.template)
                .actionType(ActionType.DELETE)
                .entity(entity)
                .performer(performerUser)
                .startingEntry(startingEntry)
                .lab(lab)
                .build();
        auditTrailRepository.save(createEntry(context));
    }

    public void logLoginAttempt(LoginLogInput input) {
        AuditTrailEntry auditTrailEntry = new AuditTrailEntry();
        auditTrailEntry.setActionType(ActionType.LOGIN.name());
        auditTrailEntry.setEntityTypeName("loginAction");
        auditTrailEntry.setEntityTypeLabel("Login Action");
        auditTrailEntry.setDateTime(OffsetDateTime.now());
        auditTrailEntry.setPerformer(input.appUserOpt.orElse(null));
        String successString = input.success ? "SUCCESS" : "FAILED";
        auditTrailEntry.setUpdatedAttributes(List.of(
                EntityLogAttribute.builder().attributeName("success").attributeLabel("Success").newValue(successString).newLabel(successString).build(),
                EntityLogAttribute.builder().attributeName("username").attributeLabel("Username").newValue(input.username).newLabel(input.username).build()
        ));
        auditTrailRepository.save(auditTrailEntry);
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
        auditTrailEntry.setActionType(context.actionType.name());
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
        if (actionType == ActionType.PW_CHANGE || actionType == ActionType.PW_RESTORE) {
            return Optional.empty();
        }
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
        if (oldValue == newValue || (oldValue != null && newValue !=null && oldValue.equals(newValue))) {
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
