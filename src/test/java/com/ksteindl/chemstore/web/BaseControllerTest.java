package com.ksteindl.chemstore.web;

import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.entities.Chemical;
import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.domain.entities.Manufacturer;
import com.ksteindl.chemstore.domain.input.AppUserInput;
import com.ksteindl.chemstore.domain.input.ChemicalInput;
import com.ksteindl.chemstore.domain.input.ManufacturerInput;
import com.ksteindl.chemstore.security.JwtProvider;
import com.ksteindl.chemstore.service.AppUserService;
import com.ksteindl.chemstore.service.ChemicalService;
import com.ksteindl.chemstore.service.LabService;
import com.ksteindl.chemstore.service.ManufacturerService;
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
            @Autowired JwtProvider jwtProvider) {
        if (firstRun) {
            AppUser aman = appUserService.crateUser(AccountManagerTestUtils.getAccountManagerInput());
            System.out.println("Account Manager id " + aman.getId());

            AppUserInput alabmanInput = AccountManagerTestUtils.getAlphaLabManagerInput();
            AppUser alabman = appUserService.crateUser(alabmanInput);

            AppUserInput blabmanInput = AccountManagerTestUtils.getBetaLabManagerInput();
            AppUser blabman = appUserService.crateUser(blabmanInput);

            AppUserInput ablabmanInput = AccountManagerTestUtils.getAlphaBetaLabManagerInput();
            AppUser ablabman = appUserService.crateUser(ablabmanInput);

            Lab alab = labService.createLab(AccountManagerTestUtils.getAlphaLabInput());
            Lab blab = labService.createLab(AccountManagerTestUtils.BETA_LAB_INPUT);

            AppUserInput alabAdminInput = AccountManagerTestUtils.getAlphaLabAdminInput();
            alabAdminInput.setLabKeysAsAdmin(List.of(AccountManagerTestUtils.ALPHA_LAB_KEY));
            AppUser alabadmin = appUserService.crateUser(alabAdminInput);

            AppUserInput blabadminInput = AccountManagerTestUtils.getBetaLabAdminInput();
            blabadminInput.setLabKeysAsAdmin(List.of(AccountManagerTestUtils.BETA_LAB_KEY));
            AppUser blabadmin = appUserService.crateUser(blabadminInput);

            AppUserInput ablabadminInput = AccountManagerTestUtils.getAlphaBetaLabAdminInput();
            ablabadminInput.setLabKeysAsAdmin(List.of(AccountManagerTestUtils.ALPHA_LAB_KEY, AccountManagerTestUtils.BETA_LAB_KEY));
            AppUser ablabadmin = appUserService.crateUser(ablabadminInput);

            AppUserInput alabUserInput = AccountManagerTestUtils.getAlphaLabUserInput();
            alabUserInput.setLabKeysAsUser(List.of(AccountManagerTestUtils.ALPHA_LAB_KEY));
            AppUser alabuser = appUserService.crateUser(alabUserInput);

            AppUserInput blabUserInput = AccountManagerTestUtils.getBetaLabUserInput();
            alabUserInput.setLabKeysAsUser(List.of(AccountManagerTestUtils.BETA_LAB_KEY));
            AppUser blabuser = appUserService.crateUser(blabUserInput);

            AppUserInput ablabUserInput = AccountManagerTestUtils.getAlphaBetaLabUserInput();
            ablabUserInput.setLabKeysAsUser(List.of(AccountManagerTestUtils.ALPHA_LAB_KEY, AccountManagerTestUtils.BETA_LAB_KEY));
            AppUser ablabuser = appUserService.crateUser(ablabUserInput);

            AppUser ablabdeleteduser = appUserService.crateUser(AccountManagerTestUtils.ALPHA_BETA_LAB_DELETED_USER_INPUT);
            appUserService.deleteAppUser(ablabdeleteduser.getId());

            // MANUFACTURER
            ManufacturerInput omegaManufacturerInput = LabAdminTestUtils.getOmegaManufacturerInput();
            manufacturerService.createManufacturer(omegaManufacturerInput);

            ManufacturerInput gammaManufacturerInput = LabAdminTestUtils.getGammaManufacturerInput();
            manufacturerService.createManufacturer(gammaManufacturerInput);

            ManufacturerInput deltaManufacturerInput = LabAdminTestUtils.getDeltaManufacturerInput();
            Manufacturer deletedManufacturer = manufacturerService.createManufacturer(deltaManufacturerInput);
            manufacturerService.deleteManufacturer(deletedManufacturer.getId());

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
}
