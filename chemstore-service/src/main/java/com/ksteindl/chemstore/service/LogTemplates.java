package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.audittrail.AttributeProducer;
import com.ksteindl.chemstore.audittrail.EntityLogTemplate;
import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.entities.ChemicalCategory;
import com.ksteindl.chemstore.domain.entities.Lab;

import java.util.List;
import java.util.stream.Collectors;

class LogTemplates {
    
    public static final List<AttributeProducer<AppUser>> APP_USER_ATTR_PRODUCERS = List.of(
            new AttributeProducer<>(
                    "id",
                    "id",
                    user -> user.getId().toString(),
                    user -> user.getId().toString()),
            new AttributeProducer<>(
                    "username",
                    "username",
                    user -> user.getUsername(),
                    user -> user.getUsername()),
            new AttributeProducer<>(
                    "password",
                    "password",
                    user -> "pw cahnged",
                    user -> "pw cahnged"),
            new AttributeProducer<>(
                    "fullName",
                    "fullName",
                    user -> user.getFullName(),
                    user -> user.getFullName()),
            new AttributeProducer<>(
                    "deleted",
                    "Deleted",
                    user -> user.getDeleted().toString(),
                    user -> user.getDeleted().toString()),
            new AttributeProducer<>(
                    "roles",
                    "Roles",
                    user -> user.getRoles().toString(),
                    user -> user.getRoles().toString()),
            new AttributeProducer<>(
                    "labsAsUser",
                    "technician in labs",
                    user -> user.getLabsAsUser().stream()
                            .map(lab -> lab.getId())
                            .collect(Collectors.toList()).toString(),
                    user -> user.getLabsAsUser().stream()
                            .map(lab -> lab.toLabel())
                            .collect(Collectors.toList()).toString()),
            new AttributeProducer<>(
                    "labsAsAdmin",
                    "admin in labs",
                    user -> user.getLabsAsAdmin().stream()
                            .map(lab -> lab.getId())
                            .collect(Collectors.toList()).toString(),
                    user -> user.getLabsAsAdmin().stream()
                            .map(lab -> lab.toLabel())
                            .collect(Collectors.toList()).toString())
            
    );
    
    private static final List<AttributeProducer<Lab>> LAB_ATTR_PRODUCERS = List.of(
            new AttributeProducer<>(
                    "id",
                    "id",
                    lab -> lab.getId().toString(),
                    lab -> lab.getId().toString()),
            new AttributeProducer<>(
                    "key",
                    "Lab key",
                    lab -> lab.getKey(),
                    lab -> lab.getKey()),
            new AttributeProducer<>(
                    "name",
                    "Lab name",
                    lab -> lab.getName(),
                    lab -> lab.getName()),
            new AttributeProducer<>(
                    "deleted",
                    "Deleted",
                    lab -> lab.getDeleted().toString(),
                    lab -> lab.getDeleted().toString()),
            new AttributeProducer<>(
                    "labManagers",
                    "Managers of the lab",
                    lab -> lab.getLabManagers().stream()
                            .map(manager -> manager.getId())
                            .collect(Collectors.toList()).toString(),
                    lab -> lab.getLabManagers().stream()
                            .map(manager -> manager.getUsername() + "(" + manager.getFullName() +")")
                            .collect(Collectors.toList()).toString())

    );

    private static final List<AttributeProducer<ChemicalCategory>> CHEM_CAT_ATTR_PRODUCERS = List.of(
            new AttributeProducer<>(
                    "id",
                    "id",
                    category -> category.getId().toString(),
                    category -> category.getId().toString()),
            new AttributeProducer<>(
                    "name",
                    "name",
                    category -> category.getName(),
                    category -> category.getName()),
            new AttributeProducer<>(
                    "lab",
                    "lab",
                    category -> category.getLab().getId().toString(),
                    category -> category.getLab().getKey() + "(" + category.getLab().getName() + ")"),
            new AttributeProducer<>(
                    "shelfLife",
                    "Shelf life (in days)",
                    category -> category.getShelfLife().toString(),
                    category -> String.valueOf(category.getShelfLife().getSeconds() / 60 / 60 / 24)),
            new AttributeProducer<>(
                    "deleted",
                    "Deleted",
                    category -> category.getDeleted().toString(),
                    category -> category.getDeleted().toString())
    );

    static final EntityLogTemplate<Lab> LAB_LOG_TEMPLATE = new EntityLogTemplate("lab","Lab", LAB_ATTR_PRODUCERS);
    static final EntityLogTemplate<ChemicalCategory> CHEM_CAT_TEMPLATE = new EntityLogTemplate("chemicalCategory","Chemical Category", CHEM_CAT_ATTR_PRODUCERS);
    static final EntityLogTemplate<AppUser> APP_USER_TEMPLATE = new EntityLogTemplate("appUser","User", APP_USER_ATTR_PRODUCERS);
}
