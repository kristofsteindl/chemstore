package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.audittrail.AttributeProducer;
import com.ksteindl.chemstore.audittrail.EntityLogTemplate;
import com.ksteindl.chemstore.domain.entities.Lab;

import java.util.List;
import java.util.stream.Collectors;

class LogTemplates {
    
    private static final List<AttributeProducer> APP_USER_ATTR_PRODUCERS = List.of();
    
    static final List<AttributeProducer<Lab>> LAB_ATTR_PRODUCERS = List.of(
            new AttributeProducer<Lab>(
                    "key",
                    "Lab key",
                    lab -> lab.getKey(),
                    lab -> lab.getKey()),
            new AttributeProducer<Lab>(
                    "name",
                    "Lab name",
                    lab -> lab.getName(),
                    lab -> lab.getName()),
            new AttributeProducer<Lab>(
                    "deleted",
                    "Deleted",
                    lab -> lab.getDeleted().toString(),
                    lab -> lab.getDeleted().toString()),
            new AttributeProducer<Lab>(
                    "labManagers",
                    "Managers of the lab",
                    lab -> lab.getLabManagers().stream()
                            .map(manager -> manager.getId())
                            .collect(Collectors.toList()).toString(),
                    lab -> lab.getLabManagers().stream()
                            .map(manager -> manager.getUsername() + "(" + manager.getFullName() +")")
                            .collect(Collectors.toList()).toString())

    );

    static final EntityLogTemplate<Lab> LAB_LOG_TEMPLATE = new EntityLogTemplate("lab","Lab", LAB_ATTR_PRODUCERS);
}
