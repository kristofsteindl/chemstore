package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.entities.ChemItem;
import com.ksteindl.chemstore.domain.entities.Chemical;
import com.ksteindl.chemstore.domain.entities.ChemicalCategory;
import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.domain.entities.Manufacturer;
import com.ksteindl.chemstore.domain.entities.Mixture;
import com.ksteindl.chemstore.domain.input.ChemItemInput;
import com.ksteindl.chemstore.domain.input.ChemItemQuery;
import com.ksteindl.chemstore.domain.repositories.ChemItemRepository;
import com.ksteindl.chemstore.domain.repositories.MixtureRepository;
import com.ksteindl.chemstore.exceptions.ForbiddenException;
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

@Service
public class ChemItemService {

    private static final Logger logger = LoggerFactory.getLogger(ChemItemService.class);
    private final static Sort SORT_BY_ID_DESC = Sort.by(Sort.Direction.DESC, "id");
    
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


    public List<ChemItem> createChemItems(ChemItemInput chemItemInput, Principal user) {
        AppUser appUser = appUserService.getMyAppUser(user);
        Lab lab = labService.findLabForUser(chemItemInput.getLabKey(), user);
        LocalDate arrivalDate = validateArrivalDateAndGet(chemItemInput.getArrivalDate());
        Chemical chemical = chemicalService.getByShortName(chemItemInput.getChemicalShortName(), lab);
        Manufacturer manufacturer = manufacturerService.findById(chemItemInput.getManufacturerId());
        LocalDate expirationDateBeforeOpened = validateExpirationDateBeforeOpenedAndGet(chemItemInput.getExpirationDateBeforeOpened());
        String unit = chemItemInput.getUnit();
        unitService.validate(unit);

        ChemItem chemItemTemplate = new ChemItem();

        chemItemTemplate.setArrivedBy(appUser);
        chemItemTemplate.setLab(lab);
        chemItemTemplate.setArrivalDate(arrivalDate);
        chemItemTemplate.setChemical(chemical);
        chemItemTemplate.setManufacturer(manufacturer);
        chemItemTemplate.setExpirationDateBeforeOpened(expirationDateBeforeOpened);
        chemItemTemplate.setUnit(unit);
        chemItemTemplate.setBatchNumber(chemItemInput.getBatchNumber());
        chemItemTemplate.setQuantity(chemItemInput.getQuantity());

        return createBatchedChemItems(chemItemTemplate, chemItemInput.getAmount());
    }

    public ChemItem openChemItem(Long chemItemId, Principal user) {
        ChemItem chemItem = findById(chemItemId);
        AppUser appUser = appUserService.getMyAppUser(user);
        labService.validateLabForUser(chemItem.getLab(), user);
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
        return chemItemRepository.save(chemItem);
    }

    public ChemItem consumeChemItem(Long chemItemId, Principal user) {
        ChemItem chemItem = findById(chemItemId);
        AppUser appUser = appUserService.getMyAppUser(user);
        labService.validateLabForUser(chemItem.getLab(), user);
        if (chemItem.getOpeningDate() == null) {
            //TODO
            throw new ValidationException("Chem item must be opened, before consumption");
        }
        LocalDate now = LocalDate.now();
        chemItem.setConsumedBy(appUser);
        chemItem.setConsumptionDate(now);
        return chemItemRepository.save(chemItem);
    }

    public PagedList<ChemItem> getChemItemsByLab(ChemItemQuery chemItemQuery) {
        labService.findLabForUser(chemItemQuery.getLabKey(), chemItemQuery.getPrincipal());
        Pageable pageable = Pageable.ofSize(chemItemQuery.getSize()).withPage(chemItemQuery.getPage());
        return chemItemRepository.findChemItems(chemItemQuery, pageable);
    }

    public ChemItem findById(Long id) {
        return chemItemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Lang.CHEM_ITEM_ENTITY_NAME, id));
    }
    
    public void hardDeleteChemItem(Long id, Principal principal) {
        AppUser user = appUserService.getAppUser(principal.getName());
        ChemItem chemItem = findById(id);
        List<Mixture> usedMixtures = getUsedMixtureItems(chemItem, principal);
        if (!usedMixtures.isEmpty()) {
           throw new ValidationException(String.format(Lang.CHEM_ITEM_DELETION_MIXTURE_USED));
        }
        boolean labManager = user.getManagedLabs().stream().anyMatch(lab -> lab.getKey().equals(chemItem.getLab().getKey()));
        if (!labManager) {
            //TODO
            throw new ForbiddenException("Deletion only allowed for lab manager");
        }
        chemItemRepository.delete(chemItem);
    }

    private List<Mixture> getUsedMixtureItems(ChemItem usedChemItem, Principal principal) {
        return mixtureRepository.findUsedMixtureItems(usedChemItem);
    }
    

    private LocalDate calcExpDate(ChemItem chemItem) {
        ChemicalCategory category = chemItem.getChemical().getCategory();
        LocalDate expBeforeOpened = chemItem.getExpirationDateBeforeOpened();
        if (category == null) {
            return expBeforeOpened;
        }
        int shelfLifeInDays = (int) category.getShelfLife().toDays();
        LocalDate maxExpDate = LocalDate.now().plusDays(shelfLifeInDays);
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


    private LocalDate validateArrivalDateAndGet(LocalDate arrivalDate) {
        if (arrivalDate == null) {
            arrivalDate = LocalDate.now();
        } else if (arrivalDate.isAfter(LocalDate.now())) {
            throw new ValidationException(Lang.CHEM_ITEM_ARRIVAL_DATE_ATTRIBUTE_NAME,
                    String.format(Lang.CHEM_ITEM_ARRIVAL_DATE_IS_FUTURE, arrivalDate));
        }
        return arrivalDate;
    }

    private LocalDate validateExpirationDateBeforeOpenedAndGet(LocalDate expirationDateBeforeOpened) {
        if (expirationDateBeforeOpened.isBefore(LocalDate.now())) {
            throw new ValidationException(String.format(Lang.CHEM_ITEM_EXP_DATE_IS_IN_PAST, expirationDateBeforeOpened));
        }
        return expirationDateBeforeOpened;
    }

}
