package com.ksteindl.chemstore.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksteindl.chemstore.domain.entities.*;
import com.ksteindl.chemstore.domain.input.AppUserInput;
import com.ksteindl.chemstore.domain.input.ChemTypeInput;
import com.ksteindl.chemstore.domain.input.ChemicalInput;
import com.ksteindl.chemstore.domain.input.ManufacturerInput;
import com.ksteindl.chemstore.security.JwtProvider;
import com.ksteindl.chemstore.service.*;
import com.ksteindl.chemstore.web.utils.AccountManagerTestUtils;
import com.ksteindl.chemstore.web.utils.LabAdminTestUtils;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class BaseControllerTest {

    protected static String TOKEN_FOR_ACCOUNT_MANAGER;
    protected static String TOKEN_FOR_ALPHA_LAB_ADMIN;
    protected static String TOKEN_FOR_ALPHA_LAB_MANAGER;
    protected static String TOKEN_FOR_ALPHA_LAB_USER;
    protected static boolean firstRun = true;

    @BeforeAll
    static void setUpTestDb(
            @Autowired AppUserService appUserService,
            @Autowired LabService labService,
            @Autowired ManufacturerService manufacturerService,
            @Autowired ChemicalService chemicalService,
            @Autowired ChemTypeService chemTypeService,
            @Autowired JwtProvider jwtProvider) {
        if (firstRun) {
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

            // MANUFACTURER
            ManufacturerInput omegaManufacturerInput = LabAdminTestUtils.getOmegaManufacturerInput();
            manufacturerService.createManufacturer(omegaManufacturerInput);

            ManufacturerInput gammaManufacturerInput = LabAdminTestUtils.getGammaManufacturerInput();
            manufacturerService.createManufacturer(gammaManufacturerInput);

            ManufacturerInput deltaManufacturerInput = LabAdminTestUtils.getDeltaManufacturerInput();
            Manufacturer deletedManufacturer = manufacturerService.createManufacturer(deltaManufacturerInput);
            manufacturerService.deleteManufacturer(deletedManufacturer.getId());

            // CHEM TYPE
            ChemTypeInput solidCompundInput = LabAdminTestUtils.getSolidCompoundInput();
            ChemType solidCompund = chemTypeService.createChemType(solidCompundInput);

            ChemTypeInput posphateSolutionInput = LabAdminTestUtils.getPhosphateSolutionInput();
            ChemType posphateSolution = chemTypeService.createChemType(posphateSolutionInput);
            chemTypeService.deleteChemType(posphateSolution.getId());


            // CHEMICAL
            ChemicalInput ethanolInput = LabAdminTestUtils.getEtOHInput();
            Chemical ethanol = chemicalService.createChemical(ethanolInput);

            ChemicalInput methanolInput = LabAdminTestUtils.getMeOHInput();
            Chemical methanol = chemicalService.createChemical(methanolInput);

            ChemicalInput ipaInput = LabAdminTestUtils.getIpaInput();
            Chemical ipa = chemicalService.createChemical(ipaInput);
            chemicalService.deleteChemical(ipa.getId());

            TOKEN_FOR_ACCOUNT_MANAGER = jwtProvider.generateToken(AccountManagerTestUtils.ACCOUNT_MANAGER_USERNAME);
            TOKEN_FOR_ALPHA_LAB_ADMIN = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_LAB_ADMIN_USERNAME);
            TOKEN_FOR_ALPHA_LAB_MANAGER = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_LAB_MANAGER_USERNAME);;
            TOKEN_FOR_ALPHA_LAB_USER = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME);;



            firstRun = false;
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
