package com.ksteindl.chemstore.web;

import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.security.JwtProvider;
import com.ksteindl.chemstore.service.AppUserService;
import com.ksteindl.chemstore.service.LabService;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseControllerTest {

    protected static String TOKEN_FOR_ACCOUNT_MANAGER;
    protected static boolean firstRun = true;

    @BeforeAll
    static void setUpTestDb(
            @Autowired AppUserService appUserService,
            @Autowired LabService labService,
            @Autowired JwtProvider jwtProvider) {
        if (firstRun) {
            AppUser aman = appUserService.crateUser(TestUtils.getAccountManagerInput());
            TOKEN_FOR_ACCOUNT_MANAGER = jwtProvider.generateToken(TestUtils.ACCOUNT_MANAGER_USERNAME);
            System.out.println("Account Manager id " + aman.getId());

            AppUser alabman = appUserService.crateUser(TestUtils.ALPHA_LAB_MANAGER_INPUT);
            AppUser blabman = appUserService.crateUser(TestUtils.BETA_LAB_MANAGER_INPUT);
            AppUser ablabman = appUserService.crateUser(TestUtils.ALPHA_BETA_LAB_MANAGER_INPUT);

            AppUser alabadmin = appUserService.crateUser(TestUtils.ALPHA_LAB_ADMIN_INPUT);
            AppUser blabadmin = appUserService.crateUser(TestUtils.BETA_LAB_ADMIN_INPUT);
            AppUser ablabadmin = appUserService.crateUser(TestUtils.ALPHA_BETA_LAB_ADMIN_INPUT);

            AppUser alabuser = appUserService.crateUser(TestUtils.getAlphaLabUserInput());
            AppUser blabuser = appUserService.crateUser(TestUtils.BETA_LAB_USER_INPUT);
            AppUser ablabuser = appUserService.crateUser(TestUtils.ALPHA_BETA_LAB_USER_INPUT);
            AppUser ablabdeleteduser = appUserService.crateUser(TestUtils.ALPHA_BETA_LAB_DELETED_USER_INPUT);
            appUserService.deleteAppUser(ablabdeleteduser.getId());

            Lab alab = labService.createLab(TestUtils.getAlphaLabInput());
            Lab blab = labService.createLab(TestUtils.BETA_LAB_INPUT);
            firstRun = false;
        }

    }
}
