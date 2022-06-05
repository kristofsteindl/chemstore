package com.ksteindl.chemstore.audittrail;

import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.entities.AuditTracable;
import com.ksteindl.chemstore.domain.entities.Lab;
import lombok.Builder;
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

    public void persistCreateEntry(Lab lab, AppUser modifier) {
        CreateEntryContext<Lab> context = CreateEntryContext.<Lab>builder()
                .template(LogTemplates.LAB_LOG_TEMPLATE)
                .actionType(ActionType.CREATE)
                .entity(lab)
                .modifier(modifier)
                .build();
        trailEntryRepository.save(createEntry(context));
    }

    public void persistUpdateEntry(StartingEntry startingEntry, Lab updatedLab, AppUser modifier) {
        CreateEntryContext context = CreateEntryContext.<Lab>builder()
                .template(LogTemplates.LAB_LOG_TEMPLATE)
                .actionType(ActionType.UPDATE)
                .entity(updatedLab)
                .modifier(modifier)
                .startingEntry(startingEntry)
                .build();
        trailEntryRepository.save(createEntry(context));
    }

    public StartingEntry<Lab> startUpdateEntry(Lab lab) {
        return new StartingEntry<>(LogTemplates.LAB_LOG_TEMPLATE, lab);
    }
    
    
    private <T extends AuditTracable> TrailEntry createEntry(CreateEntryContext<T> context) {
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
            CreateEntryContext<T> context, 
            AttributeProducer<T> producer) {
        ActionType actionType = context.actionType;
        T entity = context.entity;
        EntityLogAttribute.EntityLogAttributeBuilder builder = EntityLogAttribute.builder()
                .attributeName(producer.attributeName)
                .attributeLabel(producer.attributeLabel);
        if (actionType == ActionType.CREATE) {
            builder
                    .newValue(producer.valueProducer.apply(entity))
                    .newLabel(producer.valueLabelProducer.apply(entity));
            return Optional.of(builder.build());
        } if (actionType == ActionType.DELETE) {
            builder
                    .oldValue(producer.valueProducer.apply(entity))
                    .oldLabel(producer.valueLabelProducer.apply(entity));
            return Optional.of(builder.build());
        } else {
            return getUpdateAttribute(builder,  context, producer);
        }
        
    }

    private static <T extends AuditTracable> Optional<EntityLogAttribute> getUpdateAttribute(
            EntityLogAttribute.EntityLogAttributeBuilder builder, 
            CreateEntryContext<T> context, 
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
        
    
    @Builder
    static class CreateEntryContext<T extends AuditTracable> {
        EntityLogTemplate<T> template;
        T entity;
        AppUser modifier;
        ActionType actionType;
        StartingEntry startingEntry;
    }
    
}
