package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.entities.ChemItem;
import com.ksteindl.chemstore.domain.entities.Chemical;
import com.ksteindl.chemstore.domain.entities.ChemicalIngredient;
import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.domain.entities.Mixture;
import com.ksteindl.chemstore.domain.entities.Recipe;
import com.ksteindl.chemstore.domain.entities.RecipeIngredient;
import com.ksteindl.chemstore.domain.input.MixtureInput;
import com.ksteindl.chemstore.domain.input.MixtureQuery;
import com.ksteindl.chemstore.domain.repositories.MixtureRepository;
import com.ksteindl.chemstore.exceptions.ResourceNotFoundException;
import com.ksteindl.chemstore.exceptions.ValidationException;
import com.ksteindl.chemstore.service.wrapper.PagedList;
import com.ksteindl.chemstore.util.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MixtureService {
    
    @Autowired
    private MixtureRepository mixtureRepository;
    @Autowired
    private RecipeService recipeService;
    @Autowired
    private LabService labService;
    @Autowired
    private AppUserService appUserService;
    @Autowired
    private ChemItemService chemItemService;

    public Mixture createMixtureAsUser(MixtureInput input, Principal userPrincipal) {
        AppUser appUser = appUserService.getAppUser(userPrincipal.getName());
        return createMixture(input, appUser);
    }

    public Mixture updateMixture(MixtureInput input, Long id, Principal labManagerPrincipal) {
        Mixture mixture = findById(id);
        Recipe recipe = mixture.getRecipe();
        Lab lab = recipe.getProject().getLab();
        labService.validateLabForManager(lab, labManagerPrincipal);

        AppUser creator = appUserService.findByUsername(input.getUsername()).orElseThrow(() -> new ResourceNotFoundException(Lang.APP_USER_ENTITY_NAME, input.getUsername()));
        labService.validateLabForUser(lab, creator.getUsername());

        validateMixtureUsageConsistency(mixture);

        mixture.getChemItemsForMixtures().clear();
        mixture.getChemItems().clear();

        return updateAndSaveMixture(input, mixture);
    }

    public PagedList<Mixture> getMixturesForLab(MixtureQuery mixtureQuery) {
        labService.findLabForUser(mixtureQuery.getLabKey(), mixtureQuery.getPrincipal());
        Pageable pageable = Pageable.ofSize(mixtureQuery.getSize()).withPage(mixtureQuery.getPage());
        return mixtureRepository.findMixtures(mixtureQuery, pageable);
    }

    public Mixture findById(Long id) {
        return mixtureRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Lang.MIXTURE_ENTITY_NAME, id));
    }

    public void deleteMixture(Long id, Principal labManagerPrincipal) {
        Mixture mixture = findById(id);
        labService.validateLabForManager(mixture.getLab(), labManagerPrincipal);
        checkIfNoMixtureIsCreatedFrom(mixture);
        mixtureRepository.delete(mixture);
    }

    private Mixture createMixture(MixtureInput input, AppUser creator) {
        Mixture mixture = new Mixture();
        
        Recipe recipe = recipeService.findById(input.getRecipeId());
        mixture.setRecipe(recipe);

        Lab lab = recipe.getProject().getLab();
        labService.validateLabForUser(lab, creator.getUsername());
        mixture.setCreator(creator);
        return updateAndSaveMixture(input, mixture);
    }
    
    private Mixture updateAndSaveMixture(MixtureInput input, Mixture mixture) {
        Recipe recipe = mixture.getRecipe();
        LocalDate creationDate = validateAndGetCreationDate(input.getCreationDate());

        fillChemItems(input, mixture, recipe);
        fillMixtureItems(input, mixture, recipe);

        mixture.setCreationDate(creationDate);
        mixture.setExpirationDate(creationDate.plusDays(recipe.getShelfLifeInDays()));
        mixture.setAmount(input.getAmount());
        validateMixtureUsageConsistency(mixture);

        return mixtureRepository.save(mixture);
    }
    

    
    private void fillChemItems(MixtureInput input, Mixture mixture, Recipe recipe) {
        List<ChemItem> chemItems =  input.getChemItemIds().stream()
                .map(chemItemId -> chemItemService.findById(chemItemId))
                .collect(Collectors.toList());
        Map<Chemical, ChemItem> chemItemMap = chemItems.stream().collect(Collectors.toMap(ChemItem::getChemical, chemiItem -> chemiItem));
        for (ChemicalIngredient ingredient : recipe.getChemicalIngredients()) {
            Chemical chemical = ingredient.getIngredient();
            ChemItem chemItem = chemItemMap.get(chemical);
            if (chemItem == null) {
                throw new ValidationException(String.format(Lang.MIXTURE_MISSING_CHEM_ITEM, chemical.getShortName()));
            }
            validateChemItemLab(chemItem, recipe);
            validateChemItemDate(chemItem, input.getCreationDate());
            mixture.addChemItem(chemItem);
        }
    }
    
    private void validateChemItemLab(ChemItem chemItem, Recipe recipe) {
        if (!chemItem.getLab().getKey().equals(recipe.getLab().getKey())) {
            throw new ValidationException(String.format(Lang.MIXTURE_CHEM_ITEM_IS_IN_DIFFERENT_LAB,
                    chemItem.getSeqNumber(), chemItem.getLab().getKey(), recipe.getLab().getKey()));
        }
    }
    
    private void validateChemItemDate(ChemItem chemItem, LocalDate createdDate) {
        LocalDate openingDate = chemItem.getOpeningDate();
        if (openingDate == null) {
            throw new ValidationException(
                    Lang.MIXTURE_CREATION_DATE,
                    String.format(Lang.MIXTURE_CI_OPENING_DATE_NULL, chemItem.getChemical().getShortName()));
        } else if (openingDate.isAfter(createdDate)) {
            throw new ValidationException(
                    Lang.MIXTURE_CREATION_DATE,
                    String.format(Lang.MIXTURE_CI_OPENED_AFTER, chemItem.getChemical().getShortName(), createdDate, chemItem.getOpeningDate()));
        }
        LocalDate consumtionDate = chemItem.getConsumptionDate();
        if (consumtionDate != null && !consumtionDate.isAfter(createdDate)) {
            throw new ValidationException(
                    Lang.MIXTURE_CREATION_DATE,
                    String.format(Lang.MIXTURE_CI_ALREADY_CONSUMED, chemItem.getChemical().getShortName(), chemItem.getConsumptionDate(), createdDate));
        }
        LocalDate expirationDate = chemItem.getExpirationDate();
        if (expirationDate != null && !expirationDate.isAfter(createdDate)) {
            throw new ValidationException(
                    Lang.MIXTURE_CREATION_DATE,
                    String.format(Lang.MIXTURE_CI_ALREADY_EXPIRED, chemItem.getChemical().getShortName(), chemItem.getConsumptionDate(), createdDate));
        }
    }

    private void fillMixtureItems(MixtureInput input, Mixture mixture, Recipe recipe) {
        List<Mixture> mixtureItems =  input.getMixtureItemIds().stream()
                .map(mixtureItemId -> findById(mixtureItemId))
                .collect(Collectors.toList());
        Map<Recipe, Mixture> mixureItemMap = mixtureItems.stream().collect(Collectors.toMap(Mixture::getRecipe, recipeItem -> recipeItem));
        for (RecipeIngredient ingredient : recipe.getRecipeIngredients()) {
            Mixture mixtureItem = mixureItemMap.get(ingredient.getIngredient());
            if (mixtureItem == null) {
                throw new ValidationException(String.format(Lang.MIXTURE_MISSING_MIXTURE_ITEM, ingredient.getIngredient().getName() ,recipe.getName()));
            }
            validateMixtureItemDate(mixtureItem, input.getCreationDate());
            mixture.addMixtureItem(mixtureItem);
        }
    }

    private void validateMixtureItemDate(Mixture mixtureItem, LocalDate createdDate) {
        LocalDate mixtureItemCreationDate = mixtureItem.getCreationDate();
        if (mixtureItemCreationDate.isAfter(createdDate)) {
            throw new ValidationException(Lang.MIXTURE_CREATION_DATE,
                    String.format(Lang.MIXTURE_MIXTURE_ITEM_CREATION_DATE,
                    mixtureItem.getRecipe().getName(), createdDate, mixtureItemCreationDate));
        }
        LocalDate expirationDate = mixtureItem.getExpirationDate();
        if (!expirationDate.isAfter(createdDate)) {
            throw new ValidationException(Lang.MIXTURE_CREATION_DATE,
                    String.format(Lang.MIXTURE_MIXTURE_ITEM_ALREADY_EXPIRED,
                    mixtureItem.getRecipe().getName(), expirationDate, createdDate));
        }
    }
    
    private void validateMixtureUsageConsistency(Mixture toBeUpdated) {
        if (toBeUpdated.getId() != null) {
            mixtureRepository.findMixturesMadeOf(toBeUpdated).forEach(mixtureMadeOf -> {
                if (toBeUpdated.getCreationDate().isAfter(mixtureMadeOf.getCreationDate())) {
                    throw new ValidationException(Lang.MIXTURE_CREATION_DATE,
                            String.format(Lang.MIXTURE_UPDATED_CREATION_DATE_TOO_LATE,
                                    toBeUpdated.getRecipe().getName(), toBeUpdated.getId(),
                                    toBeUpdated.getCreationDate(),
                                    mixtureMadeOf.getRecipe().getName(), mixtureMadeOf.getId(),
                                    mixtureMadeOf.getCreationDate()));
                }
                if (toBeUpdated.getExpirationDate().isBefore(mixtureMadeOf.getCreationDate())) {
                    throw new ValidationException(Lang.MIXTURE_CREATION_DATE,
                            String.format(Lang.MIXTURE_UPDATED_CREATION_DATE_TOO_SOON,
                                    toBeUpdated.getRecipe().getName(), toBeUpdated.getId(),
                                    toBeUpdated.getCreationDate(),
                                    toBeUpdated.getExpirationDate(),
                                    mixtureMadeOf.getRecipe().getName(), mixtureMadeOf.getId(),
                                    mixtureMadeOf.getCreationDate()));
                }
            });
        }
    }


    private void checkIfNoMixtureIsCreatedFrom(Mixture mixture) {
        List<Mixture> mixtures = mixtureRepository.findMixturesMadeOf(mixture);
        if (!mixtures.isEmpty()) {
            throw new ValidationException(String.format(Lang.MIXTURE_CANNOT_BE_DELETED,
                    mixtures.stream().map(Mixture::getIdentifier).collect(Collectors.toList())));
        }
    }
    
    private LocalDate validateAndGetCreationDate(LocalDate creationDate) {
        if (creationDate == null) {
            creationDate = LocalDate.now();
        } else if (creationDate.isAfter(LocalDate.now())) {
            throw new ValidationException(Lang.MIXTURE_ENTITY_NAME,
                    String.format(Lang.MIXTURE_CREATION_DATE_IS_FUTURE, creationDate));
        }
        return creationDate;
    }

}
