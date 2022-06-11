package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.audittrail.ActionType;
import com.ksteindl.chemstore.audittrail.AuditTrailService;
import com.ksteindl.chemstore.audittrail.EntityLogTemplate;
import com.ksteindl.chemstore.audittrail.StartingEntry;
import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.entities.ChemItem;
import com.ksteindl.chemstore.domain.entities.Chemical;
import com.ksteindl.chemstore.domain.entities.ChemicalCategory;
import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.domain.entities.Manufacturer;
import com.ksteindl.chemstore.domain.entities.Mixture;
import com.ksteindl.chemstore.domain.input.ChemItemInput;
import com.ksteindl.chemstore.domain.input.ChemItemQuery;
import com.ksteindl.chemstore.domain.input.ChemItemUpdateInput;
import com.ksteindl.chemstore.domain.repositories.ChemItemRepository;
import com.ksteindl.chemstore.domain.repositories.MixtureRepository;
import com.ksteindl.chemstore.exceptions.ResourceNotFoundException;
import com.ksteindl.chemstore.exceptions.ValidationException;
import com.ksteindl.chemstore.service.wrapper.PagedList;
import com.ksteindl.chemstore.util.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChemItemService {

    private static final Logger logger = LoggerFactory.getLogger(ChemItemService.class);
    private final static EntityLogTemplate<ChemItem> template = LogTemplates.CHEM_ITEM_TEMPLATE;
    
    @Autowired
    private AppUserService appUserService;
    @Autowired
    private LabService labService;
    @Autowired
    private ChemicalService chemicalService;
    @Autowired
    private ManufacturerService manufacturerService;
    @Autowired
    private ChemItemRepository chemItemRepository;
    @Autowired
    private MixtureRepository mixtureRepository;
    @Autowired
    private UnitService unitService;
    @Autowired
    private AuditTrailService auditTrailService;


    public List<ChemItem> createChemItems(ChemItemInput chemItemInput, Principal user) {
        AppUser appUser = appUserService.getAppUser(user.getName());
        Lab lab = labService.findLabForUser(chemItemInput.getLabKey(), user);

        ChemItem chemItemTemplate = new ChemItem();
        
        LocalDate arrivalDate = getArrivalDate(chemItemInput.getArrivalDate());
        chemItemTemplate.setArrivalDate(arrivalDate);
        
        updateExpBeforeOpened(chemItemTemplate, chemItemInput);
        chemItemTemplate.setBatchNumber(chemItemInput.getBatchNumber());
        chemItemTemplate.setArrivedBy(appUser);
        chemItemTemplate.setLab(lab);
        Chemical chemical = chemicalService.getByShortName(chemItemInput.getChemicalShortName(), lab);
        chemItemTemplate.setChemical(chemical);
        updateSimpleAttributes(chemItemInput, chemItemTemplate);
        List<ChemItem> createdList = createBatchedChemItems(chemItemTemplate, chemItemInput.getPieces());
        createdList.forEach(created -> auditTrailService.createEntry(created, user, template));
        return createdList;
    }

    public ChemItem updateChemItem(ChemItemUpdateInput chemItemInput, Long chemItemId, Principal manager) {
        ChemItem chemItem = getById(chemItemId, manager);
        Lab lab = chemItem.getLab();
        labService.validateLabForManager(lab, manager);
        StartingEntry startingEntry = StartingEntry.of(chemItem, manager, template);

        updateArrival(chemItem, chemItemInput);
        updateExpBeforeOpened(chemItem, chemItemInput);
        updateOpening(chemItem, chemItemInput);
        updateConsumption(chemItem, chemItemInput);
        
        updateSimpleAttributes(chemItemInput, chemItem);
        updateBatchAndSeq(chemItem, chemItemInput);
        ChemItem updated = chemItemRepository.save(chemItem);
        auditTrailService.updateEntry(startingEntry, updated);
        return updated;
    }
    
    private void updateExpBeforeOpened(ChemItem chemItem, ChemItemInput chemItemInput) {
        LocalDate expDateBeforeOpened = getExpDateBeforeOpened(chemItem.getArrivalDate(), chemItemInput.getExpirationDateBeforeOpened());
        chemItem.setExpirationDateBeforeOpened(expDateBeforeOpened);
    }

    private void updateConsumption(ChemItem chemItem, ChemItemUpdateInput chemItemInput) {
        String consumedByUsername = chemItemInput.getConsumedByUsername();
        LocalDate consumptionData = chemItemInput.getConsumptionDate();
        List<Mixture> productMixtures = mixtureRepository.findProductMixtureItems(chemItem);
        if (consumedByUsername != null && !consumedByUsername.isEmpty()) {
            AppUser consumedBy = appUserService.getAppUser(consumedByUsername);
            labService.validateLabForUser(chemItem.getLab(), consumedBy.getUsername());
            chemItem.setConsumedBy(consumedBy);
            if (consumptionData == null) {
                //TODO
                throw new ValidationException("For updating consumption, consumptionData is required (beside consumedBy username)");
            }
            List<Mixture> invalidMixtures = productMixtures.stream()
                    .filter(mixture -> mixture.getCreationDate().isBefore(consumptionData))
                    .collect(Collectors.toList());
            if (!invalidMixtures.isEmpty()) {
                throw new ValidationException(String.format(Lang.CHEM_ITEM_CONSUMED_BEFORE_MIX_CREATED,
                        consumptionData, getMixtureListToString(invalidMixtures)));
            }
            chemItem.setConsumptionDate(consumptionData);
        } else {
            chemItem.setConsumedBy(null);
            chemItem.setConsumptionDate(null);
        }
    }
    
    private void updateArrival(ChemItem chemItem, ChemItemUpdateInput chemItemInput) {
        AppUser arrivedBy = appUserService.getAppUser(chemItemInput.getArrivedByUsername());
        labService.validateLabForUser(chemItem.getLab(), arrivedBy.getUsername());
        chemItem.setArrivedBy(arrivedBy);

        LocalDate arrivalDate = getArrivalDate(chemItemInput.getArrivalDate());
        chemItem.setArrivalDate(arrivalDate);
    }
    
    private void updateOpening(ChemItem chemItem, ChemItemUpdateInput chemItemInput) {
        String openedByUsername = chemItemInput.getOpenedByUsername();
        LocalDate openingData = chemItemInput.getOpeningDate();
        List<Mixture> productMixtures = mixtureRepository.findProductMixtureItems(chemItem);
        if (openedByUsername != null && !openedByUsername.isEmpty()) {
            AppUser openedBy = appUserService.getAppUser(openedByUsername);
            labService.validateLabForUser(chemItem.getLab(), openedBy.getUsername());
            chemItem.setOpenedBy(openedBy);
            if (openingData == null) {
                //TODO
                throw new ValidationException("For updating opening, openingData is required (beside openedBy username)");
            }
            if (openingData.isBefore(chemItem.getArrivalDate())) {
                //TODO
                throw new ValidationException("Opening date (openingDate) cannot be before arrival date (arrivalDate)");
            }
            if (openingData.isAfter(LocalDate.now())) {
                //TODO
                throw new ValidationException("Opening date cannot be in the future");
            }
            if (openingData.isAfter(chemItemInput.getExpirationDateBeforeOpened())) {
                //TODO
                throw new ValidationException("Opening date (openingDate) cannot be after expiration date of the chem item" +
                        " before opened (expirationDateBeforeOpened)");
            }
            List<Mixture> invalidMixtures = productMixtures.stream()
                .filter(mixture -> mixture.getCreationDate().isBefore(openingData))
                .collect(Collectors.toList());
            if (!invalidMixtures.isEmpty()) {
                throw new ValidationException(String.format(Lang.CHEM_ITEM_OPENED_AFTER_MIX_CREATED, 
                        openingData, getMixtureListToString(invalidMixtures)));
            }
            chemItem.setOpeningDate(openingData);
            LocalDate expDate = calcExpDate(chemItem);
            invalidMixtures = productMixtures.stream()
                    .filter(mixture -> mixture.getCreationDate().isAfter(expDate))
                    .collect(Collectors.toList());
            if (!invalidMixtures.isEmpty()) {
                throw new ValidationException(String.format(Lang.CHEM_ITEM_EXP_DATE_IS_BEFORE_MIX_CREATED,
                        openingData, getMixtureListToString(invalidMixtures)));
            }
            chemItem.setExpirationDate(expDate);
        } else {
            if (!productMixtures.isEmpty()) {
                throw new ValidationException(String.format(Lang.CHEM_ITEM_UNOPEN_RESTRICTED, 
                        getMixtureListToString(productMixtures)));
            }
            chemItem.setOpenedBy(null);
            chemItem.setOpeningDate(null);
            chemItem.setExpirationDate(null);
        }
    }
    
    private String getMixtureListToString(List<Mixture> mixtures) {
        StringBuilder builder = new StringBuilder("[");
        mixtures.forEach(mixture -> builder.append(mixtures).append("\n"));
        builder.append("]");
        return builder.toString();
        
    }
    
    private void updateBatchAndSeq(ChemItem chemItem, ChemItemUpdateInput chemItemUpdateInput) {
        String newBatchNumber = chemItemUpdateInput.getBatchNumber();
        if (!chemItem.getBatchNumber().equals(newBatchNumber)) {
            chemItem.setBatchNumber(newBatchNumber);
            Integer newSeq = getNextSeqNumber(chemItem);
            chemItem.setSeqNumber(newSeq);
        }
    }

    public ChemItem openChemItem(Long chemItemId, Principal user) {
        ChemItem chemItem = findById(chemItemId);
        StartingEntry startingEntry = StartingEntry.of(chemItem, user, template);
        AppUser appUser = appUserService.getAppUser(user.getName());
        labService.validateLabForUser(chemItem.getLab(), user.getName());
        if (chemItem.getOpeningDate() != null) {
            //TODO remove Strings
            throw new ValidationException("Chem item is already opened");
        }
        LocalDate now = LocalDate.now();
        if (chemItem.getExpirationDateBeforeOpened().isBefore(now)) {
            //TODO remove Strings
            throw new ValidationException("Chem item has already expired");
        }
        chemItem.setOpenedBy(appUser);
        chemItem.setOpeningDate(now);
        chemItem.setExpirationDate(calcExpDate(chemItem));
        ChemItem opened = chemItemRepository.save(chemItem);
        auditTrailService.logEntry(startingEntry, opened, ActionType.OPEN);
        return opened;
    }

    public ChemItem consumeChemItem(Long chemItemId, Principal user) {
        ChemItem chemItem = findById(chemItemId);
        StartingEntry startingEntry = StartingEntry.of(chemItem, user, template);
        AppUser appUser = appUserService.getAppUser(user.getName());
        labService.validateLabForUser(chemItem.getLab(), user.getName());
        if (chemItem.getOpeningDate() == null) {
            //TODO
            throw new ValidationException("Chem item must be opened, before consumption");
        }
        LocalDate now = LocalDate.now();
        chemItem.setConsumedBy(appUser);
        chemItem.setConsumptionDate(now);
        ChemItem consumed = chemItemRepository.save(chemItem);
        auditTrailService.logEntry(startingEntry, consumed, ActionType.CONSUME);
        return consumed;
    }

    public PagedList<ChemItem> getChemItemsByLab(ChemItemQuery chemItemQuery) {
        labService.findLabForUser(chemItemQuery.getLabKey(), chemItemQuery.getPrincipal());
        Pageable pageable = Pageable.ofSize(chemItemQuery.getSize()).withPage(chemItemQuery.getPage());
        return chemItemRepository.findChemItems(chemItemQuery, pageable);
    }

    public ChemItem getById(Long id, Principal user) {
        ChemItem chemItem = findById(id);
        labService.validateLabForUser(chemItem.getLab(),user.getName());
        return chemItem;
    }

    public ChemItem findById(Long id) {
        return chemItemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Lang.CHEM_ITEM_ENTITY_NAME, id));
    }
    
    public void hardDeleteChemItem(Long id, Principal principal) {
        ChemItem chemItem = findById(id);
        labService.validateLabForManager(chemItem.getLab(), principal);
        List<Mixture> usedMixtures = mixtureRepository.findProductMixtureItems(chemItem);
        if (!usedMixtures.isEmpty()) {
            throw new ValidationException(String.format(Lang.CHEM_ITEM_DELETION_MIXTURE_USED));
        }
        auditTrailService.deleteEntry(StartingEntry.of(chemItem, principal, template), chemItem);
        chemItemRepository.delete(chemItem);
    }
    
    private void updateSimpleAttributes(ChemItemInput chemItemInput, ChemItem chemItem) {
        Manufacturer manufacturer = manufacturerService.findById(chemItemInput.getManufacturerId());
        String unit = chemItemInput.getUnit();
        unitService.validate(unit);
        
        chemItem.setManufacturer(manufacturer);
        chemItem.setUnit(unit);
        chemItem.setQuantity(chemItemInput.getQuantity());
    }
    

    private LocalDate calcExpDate(ChemItem chemItem) {
        ChemicalCategory category = chemItem.getChemical().getCategory();
        LocalDate expBeforeOpened = chemItem.getExpirationDateBeforeOpened();
        if (category == null) {
            return expBeforeOpened;
        }
        int shelfLifeInDays = (int) category.getShelfLife().toDays();
        LocalDate maxExpDate = chemItem.getOpeningDate().plusDays(shelfLifeInDays);
        return maxExpDate.isAfter(expBeforeOpened) ? expBeforeOpened : maxExpDate;
    }

    private List<ChemItem> createBatchedChemItems(ChemItem chemItemTemplate, Integer amount) {
        Integer nextSeqNumber = getNextSeqNumber(chemItemTemplate);
        ArrayList<ChemItem> chemItems = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            try {
                ChemItem chemItem = (ChemItem)chemItemTemplate.clone();
                chemItem.setSeqNumber(nextSeqNumber + i);
                chemItems.add(chemItemRepository.save(chemItem));
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
        return chemItems;
    }
    

    private Integer getNextSeqNumber(ChemItem chemItemTemplate) {
        Lab lab = chemItemTemplate.getLab();
        Chemical chemical = chemItemTemplate.getChemical();
        String batchNumber = chemItemTemplate.getBatchNumber();
        Sort sortBySeqDesc = Sort.by(Sort.Direction.DESC, "seqNumber");
        List<ChemItem> equalChemItems = chemItemRepository.findEqualChemItems(lab, chemical, batchNumber, sortBySeqDesc);
        if (equalChemItems.size() == 0) {
           return 1;
        }
        return 1 + equalChemItems.get(0).getSeqNumber();
    }


    private LocalDate getArrivalDate(LocalDate arrivalDate) {
        if (arrivalDate == null) {
            arrivalDate = LocalDate.now();
        } else if (arrivalDate.isAfter(LocalDate.now())) {
            throw new ValidationException(Lang.CHEM_ITEM_ARRIVAL_DATE_ATTRIBUTE_NAME,
                    String.format(Lang.CHEM_ITEM_ARRIVAL_DATE_IS_FUTURE, arrivalDate));
        }
        return arrivalDate;
    }
    

    private LocalDate getExpDateBeforeOpened(LocalDate arrivalDate, LocalDate expDateBeforeOpened) {
        if (expDateBeforeOpened.isBefore(arrivalDate)) {
            throw new ValidationException(String.format(Lang.CHEM_ITEM_EXP_DATE_IS_IN_PAST, expDateBeforeOpened, arrivalDate));
        }
        return expDateBeforeOpened;
    }
}
