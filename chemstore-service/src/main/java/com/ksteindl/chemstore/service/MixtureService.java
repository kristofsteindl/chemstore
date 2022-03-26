package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.entities.ChemItem;
import com.ksteindl.chemstore.domain.entities.Chemical;
import com.ksteindl.chemstore.domain.entities.ChemicalIngredient;
import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.domain.entities.Mixture;
import com.ksteindl.chemstore.domain.entities.Project;
import com.ksteindl.chemstore.domain.entities.Recipe;
import com.ksteindl.chemstore.domain.entities.RecipeIngredient;
import com.ksteindl.chemstore.domain.input.MixtureInput;
import com.ksteindl.chemstore.domain.repositories.MixtureRepository;
import com.ksteindl.chemstore.exceptions.ResourceNotFoundException;
import com.ksteindl.chemstore.exceptions.ValidationException;
import com.ksteindl.chemstore.util.Lang;
import org.springframework.beans.factory.annotation.Autowired;
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
        Mixture mixture = new Mixture();
        Recipe recipe = recipeService.findById(input.getRecipeId());
        Project project = recipe.getProject();
        Lab lab = project.getLab();
        labService.validateLabForUser(lab, userPrincipal);
        LocalDate creationDate = validateAndGetCreationDate(input.getCreationDate());
        AppUser appUser = appUserService.getMyAppUser(userPrincipal);
        
        fillChemItems(input, mixture, recipe);
        fillMixtureItems(input, mixture, recipe);

        mixture.setRecipe(recipe);
        mixture.setCreator(appUser);
        mixture.setCreationDate(creationDate);
        mixture.setExpirationDate(creationDate.plusDays(recipe.getShelfLifeInDays()));
        mixture.setAmount(input.getAmount());
        
        return mixtureRepository.save(mixture);
    }
    
    public List<Mixture> getMixturesForLab(String labKey, Principal user) {
        Lab lab = labService.findLabForUser(labKey, user);
        return mixtureRepository.findByLab(lab);
    }

    public Mixture findById(Long id) {
        return mixtureRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Lang.MIXTURE_ENTITY_NAME, id));
    }
    
    private void fillChemItems(MixtureInput input, Mixture mixture, Recipe recipe) {
        List<ChemItem> chemItems =  input.getChemItemIds().stream()
                .map(chemItemId -> chemItemService.findById(chemItemId))
                .collect(Collectors.toList());
        validateChemItemDate(chemItems, input.getCreationDate());
        Map<Chemical, ChemItem> chemItemMap = chemItems.stream().collect(Collectors.toMap(ChemItem::getChemical, chemiItem -> chemiItem));
        for (ChemicalIngredient ingredient : recipe.getChemicalIngredients()) {
            ChemItem fromRecipe = chemItemMap.get(ingredient.getIngredient());
            if (fromRecipe == null) {
                throw new ValidationException("Chemical %s is not provided for mixture input");
            }
            mixture.addChemItem(fromRecipe);
        }
    }

    private void validateChemItemDate(List<ChemItem> chemItems, LocalDate createdDate) {
        chemItems.forEach(chemItem -> validateChemItemDate(chemItem, createdDate));
    }
    
    private void validateChemItemDate(ChemItem chemItem, LocalDate createdDate) {
        LocalDate arrivalDate = chemItem.getArrivalDate();
        if (arrivalDate.isAfter(createdDate)) {
            throw new ValidationException("Chemical (chemItem) is arrived after mixture creation date");
        }
        LocalDate openingDate = chemItem.getOpeningDate();
        if (openingDate == null) {
            throw new ValidationException("Chemical (chemItem) is not opened yet, mixture is cannot be created out of it");
        } else if (openingDate.isAfter(createdDate)) {
            throw new ValidationException("Chemical (chemItem) is opened after, mixture creation date");
        }
        LocalDate consumtionDate = chemItem.getConsumptionDate();
        if (consumtionDate != null && !consumtionDate.isAfter(createdDate)) {
            throw new ValidationException("Chemical (chemItem) is consumed before, mixture creation date");
        }
        LocalDate expirationDate = chemItem.getExpirationDate();
        if (expirationDate != null && !expirationDate.isAfter(createdDate)) {
            throw new ValidationException("Chemical (chemItem) is expired, before mixture was created");
        }
    }

    private void fillMixtureItems(MixtureInput input, Mixture mixture, Recipe recipe) {
        List<Mixture> mixtureItems =  input.getMixtureItemIds().stream()
                .map(mixtureItemId -> findById(mixtureItemId))
                .collect(Collectors.toList());
        validateMixItemDate(mixtureItems, input.getCreationDate());
        Map<Recipe, Mixture> mixureItemMap = mixtureItems.stream().collect(Collectors.toMap(Mixture::getRecipe, recipeItem -> recipeItem));
        for (RecipeIngredient ingredient : recipe.getRecipeIngredients()) {
            Mixture fromRecipe = mixureItemMap.get(ingredient.getIngredient());
            if (fromRecipe == null) {
                throw new ValidationException("Chemical %s is not provided for mixture input");
            }
            mixture.addMixtureItem(fromRecipe);
        }
    }

    private void validateMixItemDate(List<Mixture> mixtureItems, LocalDate createdDate) {
        mixtureItems.forEach(mixtureItem -> validateMixtureItemDate(mixtureItem, createdDate));
    }

    private void validateMixtureItemDate(Mixture mixtureItem, LocalDate createdDate) {
        LocalDate mixtureItemCreationDate = mixtureItem.getCreationDate();
        if (mixtureItemCreationDate.isAfter(createdDate)) {
            throw new ValidationException("Mixture (mixtureItem) created after, than the mixture creation date");
        }
        LocalDate expirationDate = mixtureItem.getExpirationDate();
        if (!expirationDate.isAfter(createdDate)) {
            throw new ValidationException("Mixture %s is expired, before mixture was created");
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
