package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.BaseControllerTest;
import com.ksteindl.chemstore.domain.entities.ChemItem;
import com.ksteindl.chemstore.domain.entities.Mixture;
import com.ksteindl.chemstore.domain.input.MixtureInput;
import com.ksteindl.chemstore.domain.input.MixtureQuery;
import com.ksteindl.chemstore.domain.repositories.ChemItemRepository;
import com.ksteindl.chemstore.domain.repositories.MixtureRepository;
import com.ksteindl.chemstore.exceptions.ForbiddenException;
import com.ksteindl.chemstore.exceptions.ResourceNotFoundException;
import com.ksteindl.chemstore.exceptions.ValidationException;
import com.ksteindl.chemstore.utils.AccountManagerTestUtils;
import com.ksteindl.chemstore.utils.LabAdminTestUtils;
import com.ksteindl.chemstore.utils.MixtureUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import jakarta.transaction.Transactional;
import java.security.Principal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class MixtureServiceTest extends BaseControllerTest {
    
    private static final Logger logger = LogManager.getLogger(RecipeServiceTest.class);
    
    @Autowired
    private MixtureService mixtureService;
    @Autowired 
    private ChemItemRepository chemItemRepository;
    @Autowired 
    MixtureRepository mixtureRepository;
    
    private static ChemItem availAcnAlpha;
    private static ChemItem availNH4AcAlpha;
    private static ChemItem availMeOHAlpha;
    private static ChemItem availEtOHAlpha;
    private static Mixture availLisoBuffer;
    
    private final static Principal alphaUser = AccountManagerTestUtils.ALPHA_LAB_USER_PRINCIPAL;
    
    @BeforeAll
    public static void loadIds(@Autowired ChemItemRepository chemItemRepository) {
        PageRequest pageRequest = PageRequest.of(0, 1000);
        List<ChemItem> chemItems = chemItemRepository
                .findAvailableByLab(alphaLab, pageRequest).getContent();
        for (ChemItem chemItem : chemItems) {
            if (chemItem.getChemical().getShortName().equals(LabAdminTestUtils.ACETONITRIL_SHORT_NAME)) {
                availAcnAlpha = chemItem;
            } else if (chemItem.getChemical().getShortName().equals(LabAdminTestUtils.NH4_ACETATE_SHORT_NAME)) {
                availNH4AcAlpha = chemItem;
            } else if (chemItem.getChemical().getShortName().equals(LabAdminTestUtils.METHANOL_SHORT_NAME)) {
                availMeOHAlpha = chemItem;
            } else if (chemItem.getChemical().getShortName().equals(LabAdminTestUtils.ETHANOL_SHORT_NAME)) {
                availEtOHAlpha = chemItem;
            }
        }
        
    }

    @Transactional
    long getLisBufferId() {
        if (availLisoBuffer == null) {
            Pageable pageable = Pageable.ofSize(100).withPage(0);
            MixtureQuery.MixtureQueryBuilder mixtureQueryBuilder = MixtureQuery.builder()
                    .labKey(AccountManagerTestUtils.ALPHA_LAB_KEY)
                    .projectId(alphaLisoBuffer.getProject().getId())
                    .available(true)
                    .page(0)
                    .size(100)
                    .principal(alphaUser);
            MixtureQuery mixtureQuery = mixtureQueryBuilder.build();
            var mixtures = mixtureRepository.findMixtures(mixtureQuery, pageable).getContent().stream().toList();
            for (Mixture mixture :mixtures) {
                if (mixture.getRecipe().getName().equals(LabAdminTestUtils.BUFFER_NAME)) {
                    availLisoBuffer = mixture;
                }
            }
            if (availLisoBuffer == null) {
                throw new ResourceNotFoundException("No available Liso Buffer was found for test initialization");   
            }
        }
        return availLisoBuffer.getId();
    }
    
    @Test
    @Rollback
    @Transactional
    public void createMixture_gotNoException() {
        MixtureInput input = getContEluBMixInput();
        mixtureService.createMixtureAsUser(input, alphaUser);
    }

    @Test
    @Rollback
    @Transactional
    public void createMixture_savedEntityExpected() {
        MixtureInput input = getContEluBMixInput();
        Mixture returned = mixtureService.createMixtureAsUser(input, alphaUser);
        Mixture fetched = mixtureService.findById(returned.getId());
        Assertions.assertEquals(fetched.getRecipe().getId(), input.getRecipeId());
        Assertions.assertEquals(fetched.getCreator().getUsername(), alphaUser.getName());
        Assertions.assertEquals(fetched.getAmount(), input.getAmount());
        Assertions.assertEquals(fetched.getCreationDate(), LocalDate.now());
    }

    @Test
    @Rollback
    @Transactional
    public void createMixture_savedChemItemsAreExpected() {
        MixtureInput input = getContEluBMixInput();
        Mixture returned = mixtureService.createMixtureAsUser(input, alphaUser);
        Mixture fetched = mixtureService.findById(returned.getId());
        Set<ChemItem> chemItems = new HashSet<>(fetched.getChemItems());
        Assertions.assertTrue(chemItems.contains(availNH4AcAlpha));
        Assertions.assertTrue(chemItems.contains(availMeOHAlpha));
    }

    @Test
    @Rollback
    @Transactional
    public void createMixture_savedMixtureItemIsExpected() {
        MixtureInput input = getContEluBMixInput();
        Mixture returned = mixtureService.createMixtureAsUser(input, alphaUser);
        Mixture fetched = mixtureService.findById(returned.getId());
        Assertions.assertEquals(fetched.getMixtureItems().get(0), availLisoBuffer);
    }

    @Test
    @Rollback
    @Transactional
    public void createMixture_whenCreatorIsDifferent_principalIsSaved() {
        MixtureInput input = getContEluBMixInput();
        input.setUsername(AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL.getName());
        Mixture mixture = mixtureService.createMixtureAsUser(input, alphaUser);
        Assertions.assertEquals(mixture.getCreator().getUsername(), alphaUser.getName());
    }

    @Test
    @Rollback
    @Transactional
    public void createMixture_whenBLabAdmin_gotForbiddenException() {
        MixtureInput input = getContEluBMixInput();
        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            mixtureService.createMixtureAsUser(input, AccountManagerTestUtils.BETA_LAB_ADMIN_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }
    

    @Test
    @Rollback
    @Transactional
    public void createMixture_whenAccountManager_gotForbiddenException() {
        MixtureInput input = getContEluBMixInput();
        Exception exception = Assertions.assertThrows(ForbiddenException.class, () -> {
            mixtureService.createMixtureAsUser(input, AccountManagerTestUtils.ACCOUNT_MANAGER_PRINCIPAL);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void createMixture_whenEmptyInput_gotException() {
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            mixtureService.createMixtureAsUser(new MixtureInput(), alphaUser);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void createMixture_whenRecipeIdDoesNotExist_gotResourceNotFoundException() {
        MixtureInput input = getContEluBMixInput();
        input.setRecipeId((long)Integer.MAX_VALUE);
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            mixtureService.createMixtureAsUser(input, alphaUser);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void createMixture_whenRecipeDeleted_gotResourceNotFoundException() {
        MixtureInput input = getContEluBMixInput();
        input.setRecipeId(alphaLisoDeletedRecipe.getId());
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            mixtureService.createMixtureAsUser(input, alphaUser);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void createMixture_whenCreationDateIsInTheFuture_gotValidationException() {
        MixtureInput input = getContEluBMixInput();
        input.setCreationDate(LocalDate.now().plusDays(1));
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            mixtureService.createMixtureAsUser(input, alphaUser);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void createMixture_whenOneChemItemDoesNotExist_gotResourceNotFoundException() {
        MixtureInput input = getContEluBMixInput();
        input.getChemItemIds().add((long)Integer.MAX_VALUE);
        Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            mixtureService.createMixtureAsUser(input, alphaUser);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void createMixture_whenChemItemIsMissing_gotValidationException() {
        MixtureInput input = getContEluBMixInput();
        input.getChemItemIds().remove(0);
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            mixtureService.createMixtureAsUser(input, alphaUser);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void createMixture_whenMeOHIsInAnotherLab_gotValidationException() {
        MixtureInput input = getContEluBMixInput();
        ChemItem meOhFromAnotherLab = chemItemRepository.findAvailableByLab(betaLab, PageRequest.of(0, 1000)).getContent().stream()
                .filter(chemItem -> chemItem.getChemical().getShortName().equals(LabAdminTestUtils.METHANOL_SHORT_NAME))
                .findAny().orElseThrow(() -> new ResourceNotFoundException("No available MeOH was found in lab B"));
        for (int i = 0; i < input.getChemItemIds().size(); i++) {
            if (input.getChemItemIds().get(i) == availMeOHAlpha.getId()) {
                input.getChemItemIds().remove(i);
                input.getChemItemIds().add(i, meOhFromAnotherLab.getId());
            }
        }
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            mixtureService.createMixtureAsUser(input, alphaUser);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void createMixture_whenMeOHIsInNotOpened_gotValidationException() {
        MixtureInput input = getContEluBMixInput();
        ChemItem MeOHNotOpened = chemItemRepository.findByLab(alphaLab, PageRequest.of(0, 1000)).getContent().stream()
                .filter(chemItem -> chemItem.getChemical().getShortName().equals(LabAdminTestUtils.METHANOL_SHORT_NAME))
                .filter(chemItem -> chemItem.getOpeningDate() == null)
                .findAny().get();
        for (int i = 0; i < input.getChemItemIds().size(); i++) {
            if (input.getChemItemIds().get(i) == availMeOHAlpha.getId()) {
                input.getChemItemIds().remove(i);
                input.getChemItemIds().add(i, MeOHNotOpened.getId());
            }
        }
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            mixtureService.createMixtureAsUser(input, alphaUser);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void createMixture_whenMeOhIsConsumed_gotValidationException() {
        MixtureInput input = getContEluBMixInput();
        ChemItem expiredMeOH = chemItemRepository.findByLab(alphaLab, PageRequest.of(0, 1000)).getContent().stream()
                .filter(chemItem -> chemItem.getChemical().getShortName().equals(LabAdminTestUtils.METHANOL_SHORT_NAME))
                .filter(chemItem -> chemItem.getConsumptionDate() != null)
                .findAny().get();
        for (int i = 0; i < input.getChemItemIds().size(); i++) {
            if (input.getChemItemIds().get(i) == availMeOHAlpha.getId()) {
                input.getChemItemIds().remove(i);
                input.getChemItemIds().add(i, expiredMeOH.getId());
            }
        }
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            mixtureService.createMixtureAsUser(input, alphaUser);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void createMixture_whenNH4AcIsExpired_gotValidationException() {
        MixtureInput input = getContEluBMixInput();
        ChemItem nh4Ac = chemItemRepository.findByLab(alphaLab, PageRequest.of(0, 1000)).getContent().stream()
                .filter(chemItem -> chemItem.getChemical().getShortName().equals(LabAdminTestUtils.NH4_ACETATE_SHORT_NAME))
                .filter(chemItem -> chemItem.getExpirationDate().isBefore(LocalDate.now()))
                .findAny().get();
        for (int i = 0; i < input.getChemItemIds().size(); i++) {
            if (input.getChemItemIds().get(i) == availNH4AcAlpha.getId()) {
                input.getChemItemIds().remove(i);
                input.getChemItemIds().add(i, nh4Ac.getId());
            }
        }
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> {
            mixtureService.createMixtureAsUser(input, alphaUser);
        });
        logger.info("Expected Exception is thrown:");
        logger.info("with class: " + exception.getClass());
        logger.info("with message: " + exception.getMessage());
    }
    
    
    
    
    private MixtureInput getContEluBMixInput() {
        MixtureInput input = MixtureUtils.getLisoContEluBMixInputForAlpha();
        input.setRecipeId(alphaLisoEluB.getId());
        input.getMixtureItemIds().add(getLisBufferId());
        input.getChemItemIds().add(availNH4AcAlpha.getId());
        input.getChemItemIds().add(availMeOHAlpha.getId());
        return input;
    }
}
