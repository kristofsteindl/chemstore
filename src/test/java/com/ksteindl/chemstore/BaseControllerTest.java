package com.ksteindl.chemstore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksteindl.chemstore.domain.entities.*;
import com.ksteindl.chemstore.domain.input.*;
import com.ksteindl.chemstore.domain.repositories.ShelfLifeRepositoy;
import com.ksteindl.chemstore.security.JwtProvider;
import com.ksteindl.chemstore.service.*;
import com.ksteindl.chemstore.utils.AccountManagerTestUtils;
import com.ksteindl.chemstore.utils.LabAdminTestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class BaseControllerTest {

    private static final Logger logger = LogManager.getLogger(BaseControllerTest.class);

    protected static String TOKEN_FOR_ACCOUNT_MANAGER;

    protected static String TOKEN_FOR_ALPHA_LAB_ADMIN;
    protected static String TOKEN_FOR_BETA_LAB_ADMIN;

    protected static String TOKEN_FOR_ALPHA_LAB_MANAGER;
    protected static String TOKEN_FOR_BETA_LAB_MANAGER;

    protected static String TOKEN_FOR_ALPHA_LAB_USER;
    protected static String TOKEN_FOR_BETA_LAB_USER;

    private static boolean first = true;


    @Autowired
    protected ChemTypeService chemTypeService;
    @Autowired
    protected ShelfLifeService shelfLifeService;
    @Autowired
    protected JwtProvider jwtProvider;

    @BeforeEach
    public void createShelfLifes() {
        //SHELF LIFE



    }

    @BeforeAll
    static void initDb(
            @Autowired JwtProvider jwtProvider,
            @Autowired AppUserService appUserService,
            @Autowired LabService labService,
            @Autowired ManufacturerService manufacturerService,
            @Autowired ChemicalService chemicalService,
            @Autowired ChemTypeService chemTypeService,
            @Autowired ShelfLifeService shelfLifeService,
            @Autowired ShelfLifeRepositoy shelfLifeRepositoy) {
        if (first) {
            AppUser aman = appUserService.createUser(AccountManagerTestUtils.getAccountManagerInput());
            System.out.println("Account Manager id " + aman.getId());

            AppUserInput alabmanInput = AccountManagerTestUtils.getAlphaLabManagerInput();
            AppUser alabman = appUserService.createUser(alabmanInput);

            AppUserInput blabmanInput = AccountManagerTestUtils.getBetaLabManagerInput();
            AppUser blabman = appUserService.createUser(blabmanInput);

            AppUserInput ablabmanInput = AccountManagerTestUtils.getAlphaBetaLabManagerInput();
            AppUser ablabman = appUserService.createUser(ablabmanInput);

            Lab alab = labService.createLab(AccountManagerTestUtils.getAlphaLabInput());
            Lab blab = labService.createLab(AccountManagerTestUtils.BETA_LAB_INPUT);

            AppUserInput alabAdminInput = AccountManagerTestUtils.getAlphaLabAdminInput();
            alabAdminInput.setLabKeysAsAdmin(List.of(AccountManagerTestUtils.ALPHA_LAB_KEY));
            AppUser alabadmin = appUserService.createUser(alabAdminInput);

            AppUserInput blabadminInput = AccountManagerTestUtils.getBetaLabAdminInput();
            blabadminInput.setLabKeysAsAdmin(List.of(AccountManagerTestUtils.BETA_LAB_KEY));
            AppUser blabadmin = appUserService.createUser(blabadminInput);

            AppUserInput ablabadminInput = AccountManagerTestUtils.getAlphaBetaLabAdminInput();
            ablabadminInput.setLabKeysAsAdmin(List.of(AccountManagerTestUtils.ALPHA_LAB_KEY, AccountManagerTestUtils.BETA_LAB_KEY));
            AppUser ablabadmin = appUserService.createUser(ablabadminInput);

            AppUserInput alabUserInput = AccountManagerTestUtils.getAlphaLabUserInput();
            alabUserInput.setLabKeysAsUser(List.of(AccountManagerTestUtils.ALPHA_LAB_KEY));
            AppUser alabuser = appUserService.createUser(alabUserInput);

            AppUserInput blabUserInput = AccountManagerTestUtils.getBetaLabUserInput();
            alabUserInput.setLabKeysAsUser(List.of(AccountManagerTestUtils.BETA_LAB_KEY));
            AppUser blabuser = appUserService.createUser(blabUserInput);

            AppUserInput ablabUserInput = AccountManagerTestUtils.getAlphaBetaLabUserInput();
            ablabUserInput.setLabKeysAsUser(List.of(AccountManagerTestUtils.ALPHA_LAB_KEY, AccountManagerTestUtils.BETA_LAB_KEY));
            AppUser ablabuser = appUserService.createUser(ablabUserInput);

            AppUser ablabdeleteduser = appUserService.createUser(AccountManagerTestUtils.ALPHA_BETA_LAB_DELETED_USER_INPUT);
            appUserService.deleteAppUser(ablabdeleteduser.getId());

            //MANUFACTURER
            ManufacturerInput omegaManufacturerInput = LabAdminTestUtils.getOmegaManufacturerInput();
            manufacturerService.createManufacturer(omegaManufacturerInput);

            ManufacturerInput gammaManufacturerInput = LabAdminTestUtils.getGammaManufacturerInput();
            manufacturerService.createManufacturer(gammaManufacturerInput);

            ManufacturerInput deltaManufacturerInput = LabAdminTestUtils.getDeltaManufacturerInput();
            Manufacturer deletedManufacturer = manufacturerService.createManufacturer(deltaManufacturerInput);
            manufacturerService.deleteManufacturer(deletedManufacturer.getId());

            //CHEM TYPE
            ChemTypeInput solidCompundInput = LabAdminTestUtils.getSolidCompoundInput();
            ChemType solidCompund = chemTypeService.createChemType(solidCompundInput);

            ChemTypeInput bufferSolutionInput = LabAdminTestUtils.getBufferSolutionInput();
            ChemType bufferSolution = chemTypeService.createChemType(bufferSolutionInput);

            ChemTypeInput waterChemTypeInput = LabAdminTestUtils.getWaterChemTypeInput();
            ChemType waterChemType = chemTypeService.createChemType(waterChemTypeInput);

            ChemTypeInput posphateSolutionInput = LabAdminTestUtils.getPhosphateSolutionInput();
            ChemType posphateSolution = chemTypeService.createChemType(posphateSolutionInput);
            chemTypeService.deleteChemType(posphateSolution.getId());


            //CHEMICAL
            ChemicalInput ethanolInput = LabAdminTestUtils.getEtOHInput();
            Chemical ethanol = chemicalService.createChemical(ethanolInput);

            ChemicalInput methanolInput = LabAdminTestUtils.getMeOHInput();
            methanolInput.setChemTypeId(solidCompund.getId());
            Chemical methanol = chemicalService.createChemical(methanolInput);

            ChemicalInput chemWithTypeInput = LabAdminTestUtils.getChemWithTypeInput();
            chemWithTypeInput.setChemTypeId(solidCompund.getId());
            Chemical chemWithType = chemicalService.createChemical(chemWithTypeInput);

            ChemicalInput chemWithoutTypeInput = LabAdminTestUtils.getChemWithoutTypeInput();
            Chemical chemWithoutType = chemicalService.createChemical(chemWithoutTypeInput);

            ChemicalInput ipaInput = LabAdminTestUtils.getIpaInput();
            Chemical ipa = chemicalService.createChemical(ipaInput);
            chemicalService.deleteChemical(ipa.getId());

            ShelfLife bufferForAlpha = new ShelfLife();
            bufferForAlpha.setDuration(Duration.ofDays(LabAdminTestUtils.BUFFER_FOR_ALPHA_DAYS));
            bufferForAlpha.setChemType(bufferSolution);
            bufferForAlpha.setLab(alab);
            shelfLifeRepositoy.save(bufferForAlpha);

            ShelfLife bufferForBeta = new ShelfLife();
            bufferForBeta.setDuration(Duration.ofDays(LabAdminTestUtils.BUFFER_FOR_BETA_DAYS));
            bufferForBeta.setChemType(bufferSolution);
            bufferForBeta.setLab(blab);
            shelfLifeRepositoy.save(bufferForBeta);

            ShelfLife solidForALpha = new ShelfLife();
            solidForALpha.setDuration(Duration.between(LocalDateTime.now(), LocalDateTime.now().plusYears(LabAdminTestUtils.SOLID_FOR_ALPHA_YEAR)));
            solidForALpha.setChemType(solidCompund);
            solidForALpha.setLab(alab);
            shelfLifeRepositoy.save(solidForALpha);

            ShelfLife waterForAlpha = new ShelfLife();
            waterForAlpha.setDuration(Duration.between(LocalDateTime.now(), LocalDateTime.now().plusYears(LabAdminTestUtils.SOLID_FOR_ALPHA_YEAR)));
            waterForAlpha.setChemType(waterChemType);
            waterForAlpha.setLab(alab);
            waterForAlpha.setDeleted(true);
            shelfLifeRepositoy.save(waterForAlpha);


            TOKEN_FOR_ACCOUNT_MANAGER = jwtProvider.generateToken(AccountManagerTestUtils.ACCOUNT_MANAGER_USERNAME);
            TOKEN_FOR_ALPHA_LAB_ADMIN = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_LAB_ADMIN_USERNAME);
            TOKEN_FOR_ALPHA_LAB_MANAGER = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_LAB_MANAGER_USERNAME);
            TOKEN_FOR_ALPHA_LAB_USER = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME);
            TOKEN_FOR_BETA_LAB_ADMIN= jwtProvider.generateToken(AccountManagerTestUtils.BETA_LAB_ADMIN_USERNAME);
            TOKEN_FOR_BETA_LAB_MANAGER = jwtProvider.generateToken(AccountManagerTestUtils.BETA_LAB_MANAGER_USERNAME);
            TOKEN_FOR_BETA_LAB_USER = jwtProvider.generateToken(AccountManagerTestUtils.BETA_LAB_USER_USERNAME);
            first = false;
        }
    }

    public String asJsonString(final ChemItemInput chemItemInput) {
        try {
            LocalDate arrivalDate = chemItemInput.getArrivalDate();
            LocalDate expDate = chemItemInput.getExpirationDateBeforeOpened();
            chemItemInput.setArrivalDate(null);
            chemItemInput.setExpirationDateBeforeOpened(null);
            String raw = new ObjectMapper().writeValueAsString(chemItemInput);
            chemItemInput.setArrivalDate(arrivalDate);
            chemItemInput.setExpirationDateBeforeOpened(expDate);
            StringBuilder builder = new StringBuilder(raw);
            replaceNullLocalDateAttribute(builder, "arrivalDate", arrivalDate);
            replaceNullLocalDateAttribute(builder, "expirationDateBeforeOpened", expDate);
            String serialized = builder.toString();
            return serialized;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void replaceNullLocalDateAttribute(StringBuilder builder, String attributeName, LocalDate localDate) {
        if (localDate != null) {
            String localDateString = new StringBuilder().append(
                            localDate.getYear()).append("-").
                    append(localDate.getMonthValue() > 9 ? localDate.getMonthValue() : "0" + Integer.toString(localDate.getMonthValue()))
                    .append("-").
                    append(localDate.getDayOfMonth()).toString();
            String replaced = "\"" + attributeName + "\":null";
            int index = builder.indexOf(replaced);
            builder.replace(index, index + replaced.length(), "\"" + attributeName + "\":\"" + localDateString + "\"");
        }
    }

    public String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
