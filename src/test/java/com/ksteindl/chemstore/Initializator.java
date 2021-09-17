package com.ksteindl.chemstore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksteindl.chemstore.domain.entities.*;
import com.ksteindl.chemstore.domain.input.*;
import com.ksteindl.chemstore.security.Authority;
import com.ksteindl.chemstore.security.JwtProvider;
import com.ksteindl.chemstore.security.role.Role;
import com.ksteindl.chemstore.security.role.RoleRepository;
import com.ksteindl.chemstore.security.role.RoleService;
import com.ksteindl.chemstore.service.*;
import com.ksteindl.chemstore.utils.AccountManagerTestUtils;
import com.ksteindl.chemstore.utils.LabAdminTestUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Initializator implements ApplicationRunner {

    public String TOKEN_FOR_ACCOUNT_MANAGER;
    public String TOKEN_FOR_ALPHA_LAB_ADMIN;
    public String TOKEN_FOR_ALPHA_LAB_MANAGER;
    public String TOKEN_FOR_ALPHA_LAB_USER;

    @Autowired AppUserService appUserService;
    @Autowired LabService labService;
    @Autowired ManufacturerService manufacturerService;
    @Autowired ChemicalService chemicalService;
    @Autowired ChemTypeService chemTypeService;
    @Autowired ShelfLifeService shelfLifeService;
    @Autowired JwtProvider jwtProvider;
    @Autowired RoleRepository roleRepository;

    private final String SUPERADMIN = "superadmin";

    @Override
    public void run(ApplicationArguments args) throws Exception {
        RoleService.ROLES.stream()
                .filter(role -> roleRepository.findByRole(role).isEmpty())
                .forEach(role -> roleRepository.save(new Role(role)));
        if (!appUserService.findByUsername(SUPERADMIN).isPresent()) {
            AppUserInput superAdminInput = AppUserInput.builder()
                    .fullName(SUPERADMIN)
                    .username(SUPERADMIN)
                    .password(SUPERADMIN)
                    .password2(SUPERADMIN)
                    .roles(List.of(Authority.ACCOUNT_MANAGER))
                    .build();
            appUserService.createUser(superAdminInput);
        }
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

            //SHELF LIFE
            ShelfLifeInput bufferForAlphaInput = ShelfLifeInput.builder()
                    .amount(LabAdminTestUtils.BUFFER_FOR_ALPHA_DAYS)
                    .unit(LabAdminTestUtils.BUFFER_FOR_ALPHA_UNIT)
                    .labKey(AccountManagerTestUtils.ALPHA_LAB_KEY)
                    .chemTypeId(bufferSolution.getId()).build();
            ShelfLifeInput bufferForBetaInput = ShelfLifeInput.builder()
                    .amount(LabAdminTestUtils.BUFFER_FOR_BETA_DAYS)
                    .unit(LabAdminTestUtils.BUFFER_FOR_BETA_UNIT)
                    .labKey(AccountManagerTestUtils.BETA_LAB_KEY)
                    .chemTypeId(bufferSolution.getId()).build();
            ShelfLifeInput soliodForAlphaInput = ShelfLifeInput.builder()
                    .amount(LabAdminTestUtils.SOLID_FOR_ALPHA_YEAR)
                    .unit(LabAdminTestUtils.SOLID_FOR_ALPHA_UNIT)
                    .labKey(AccountManagerTestUtils.BETA_LAB_KEY)
                    .chemTypeId(bufferSolution.getId()).build();


            shelfLifeService.createShelfLife(bufferForAlphaInput, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
            shelfLifeService.createShelfLife(bufferForBetaInput,  AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
            shelfLifeService.createShelfLife(soliodForAlphaInput,  AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);

            TOKEN_FOR_ACCOUNT_MANAGER = jwtProvider.generateToken(AccountManagerTestUtils.ACCOUNT_MANAGER_USERNAME);
            TOKEN_FOR_ALPHA_LAB_ADMIN = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_LAB_ADMIN_USERNAME);
            TOKEN_FOR_ALPHA_LAB_MANAGER = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_LAB_MANAGER_USERNAME);;
            TOKEN_FOR_ALPHA_LAB_USER = jwtProvider.generateToken(AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME);;



    }
    public String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
