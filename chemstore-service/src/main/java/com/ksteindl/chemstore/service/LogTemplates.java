package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.audittrail.AttributeProducer;
import com.ksteindl.chemstore.audittrail.EntityLogTemplate;
import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.entities.ChemItem;
import com.ksteindl.chemstore.domain.entities.Chemical;
import com.ksteindl.chemstore.domain.entities.ChemicalCategory;
import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.domain.entities.Manufacturer;

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

    private static final List<AttributeProducer<Chemical>> CHEM_ATTR_PRODUCERS = List.of(
            new AttributeProducer<>(
                    "id",
                    "id",
                    chemical -> chemical.getId().toString(),
                    chemical -> chemical.getId().toString()),
            new AttributeProducer<>(
                    "deleted",
                    "Deleted",
                    category -> category.getDeleted().toString(),
                    category -> category.getDeleted().toString()),
            new AttributeProducer<>(
                    "lab",
                    "lab",
                    chemical -> chemical.getLab().getId().toString(),
                    chemical -> chemical.getLab().getKey() + "(" + chemical.getLab().getName() + ")"),
            new AttributeProducer<>(
                    "shortName",
                    "short name",
                    chemical -> chemical.getShortName(),
                    chemical -> chemical.getShortName()),
            new AttributeProducer<>(
                    "exactName",
                    "Exact Name",
                    chemical -> chemical.getExactName(),
                    chemical -> chemical.getExactName()),
            new AttributeProducer<>(
                    "category",
                    "Category",
                    chemical -> chemical.getCategory().getId().toString(),
                    chemical -> chemical.getCategory().getName())

    );

    private static final List<AttributeProducer<ChemItem>> CHEM_ITEM_ATTR_PRODUCERS = List.of(
            new AttributeProducer<>(
                    "id",
                    "id",
                    chemItem -> chemItem.getId().toString(),
                    chemItem -> chemItem.getId().toString()),
            new AttributeProducer<>(
                    "lab",
                    "lab",
                    chemItem -> chemItem.getLab().getId().toString(),
                    chemItem -> chemItem.getLab().toLabel()),
            new AttributeProducer<>(
                    "chemical",
                    "Chemical",
                    chemItem -> chemItem.getChemical().getId().toString(),
                    chemItem -> chemItem.getChemical().getShortName()),
            new AttributeProducer<>(
                    "arrivalDate",
                    "Arrival Date",
                    chemItem -> chemItem.getArrivalDate() == null ? null : chemItem.getArrivalDate().toString(),
                    chemItem -> chemItem.getArrivalDate() == null ? null : chemItem.getArrivalDate().toString()),
            new AttributeProducer<>(
                    "arrivedBy",
                    "Arrived by",
                    chemItem -> chemItem.getArrivalDate() == null ? null : chemItem.getArrivalDate().toString(),
                    chemItem -> chemItem.getArrivalDate() == null ? null :  chemItem.getArrivalDate().toString()),
            new AttributeProducer<>(
                    "arrivedBy",
                    "Arrived by",
                    chemItem -> chemItem.getArrivedBy() == null ? null : chemItem.getArrivedBy().getId().toString(),
                    chemItem -> chemItem.getArrivedBy() == null ? null :  chemItem.getArrivedBy().toLabel()),
            new AttributeProducer<>(
                    "manufacturer",
                    "Manufacturer",
                    chemItem -> chemItem.getManufacturer().getId().toString(),
                    chemItem -> chemItem.getManufacturer().getName()),
            new AttributeProducer<>(
                    "batchnumber",
                    "Batch number",
                    chemItem -> chemItem.getBatchNumber(),
                    chemItem -> chemItem.getBatchNumber()),
            new AttributeProducer<>(
                    "quantity",
                    "Quantity",
                    chemItem -> chemItem.getQuantity().toString(),
                    chemItem -> chemItem.getQuantity().toString()),
            new AttributeProducer<>(
                    "unit",
                    "unit",
                    chemItem -> chemItem.getUnit(),
                    chemItem -> chemItem.getUnit()),
            new AttributeProducer<>(
                    "seqNumber",
                    "Sequence number",
                    chemItem -> chemItem.getSeqNumber().toString(),
                    chemItem -> chemItem.getSeqNumber().toString()),
            new AttributeProducer<>(
                    "expirationDateBeforeOpened",
                    "Expiration Date before opened",
                    chemItem -> chemItem.getExpirationDateBeforeOpened() == null ? null 
                            : chemItem.getExpirationDateBeforeOpened().toString(),
                    chemItem -> chemItem.getExpirationDateBeforeOpened() == null ? null
                            : chemItem.getExpirationDateBeforeOpened().toString()),
            new AttributeProducer<>(
                    "openingDate",
                    "Opening Date",
                    chemItem -> chemItem.getOpeningDate() == null ? null
                            : chemItem.getOpeningDate().toString(),
                    chemItem -> chemItem.getOpeningDate() == null ? null
                            : chemItem.getOpeningDate().toString()),
            new AttributeProducer<>(
                    "openedBy",
                    "Opened by",
                    chemItem -> chemItem.getOpenedBy() == null ? null
                            : chemItem.getOpenedBy().getId().toString(),
                    chemItem -> chemItem.getOpenedBy() == null ? null
                            : chemItem.getOpenedBy().toLabel()),
            new AttributeProducer<>(
                    "expirationDate",
                    "Expiration Date",
                    chemItem -> chemItem.getExpirationDate() == null ? null
                            : chemItem.getExpirationDate().toString(),
                    chemItem -> chemItem.getExpirationDate() == null ? null
                            : chemItem.getExpirationDate().toString()),
            new AttributeProducer<>(
                    "consumptionDate",
                    "Consumtpion Date",
                    chemItem -> chemItem.getConsumptionDate() == null ? null
                            : chemItem.getConsumptionDate().toString(),
                    chemItem -> chemItem.getConsumptionDate() == null ? null
                            : chemItem.getConsumptionDate().toString()),
            new AttributeProducer<>(
                    "consumedBy",
                    "Consumed by",
                    chemItem -> chemItem.getConsumedBy() == null ? null
                            : chemItem.getConsumedBy().getId().toString(),
                    chemItem -> chemItem.getConsumedBy() == null ? null
                            : chemItem.getConsumedBy().toLabel())
    );

    private static final List<AttributeProducer<Manufacturer>> MANUFACTURER_ATTR_PRODUCERS = List.of(
            new AttributeProducer<>(
                    "id",
                    "id",
                    manufacturer -> manufacturer.getId().toString(),
                    manufacturer -> manufacturer.getId().toString()),
            new AttributeProducer<>(
                    "name",
                    "name",
                    manufacturer -> manufacturer.getName(),
                    manufacturer -> manufacturer.getName()),
            new AttributeProducer<>(
                    "deleted",
                    "Deleted",
                    manufacturer -> manufacturer.getDeleted().toString(),
                    manufacturer -> manufacturer.getDeleted().toString())
    );

    static final EntityLogTemplate<Lab> LAB_LOG_TEMPLATE = new EntityLogTemplate("lab","Lab", LAB_ATTR_PRODUCERS);
    static final EntityLogTemplate<ChemicalCategory> CHEM_CAT_TEMPLATE = new EntityLogTemplate("chemicalCategory","Chemical Category", CHEM_CAT_ATTR_PRODUCERS);
    static final EntityLogTemplate<AppUser> APP_USER_TEMPLATE = new EntityLogTemplate("appUser","User", APP_USER_ATTR_PRODUCERS);
    static final EntityLogTemplate<Chemical> CHEM_TEMPLATE = new EntityLogTemplate("chemical","Chemical", CHEM_ATTR_PRODUCERS);
    static final EntityLogTemplate<ChemItem> CHEM_ITEM_TEMPLATE = new EntityLogTemplate("chemItem","Chem Item", CHEM_ITEM_ATTR_PRODUCERS);
    static final EntityLogTemplate<Manufacturer> MANUFACTURER_TEMPLATE = new EntityLogTemplate("manufacturer","Mafufacturer", MANUFACTURER_ATTR_PRODUCERS);
}
