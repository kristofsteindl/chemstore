package com.ksteindl.chemstore.utils;

import com.ksteindl.chemstore.domain.input.AppUserInput;
import com.ksteindl.chemstore.domain.input.LabInput;
import com.ksteindl.chemstore.security.Authority;

import java.security.Principal;
import java.util.List;

public class AccountManagerTestUtils {

    public static final Principal ALPHA_LAB_MANAGER_PRINCIPAL = new MockPrincipal(AccountManagerTestUtils.ALPHA_LAB_MANAGER_USERNAME);
    public static final Principal BETA_LAB_MANAGER_PRINCIPAL = new MockPrincipal(AccountManagerTestUtils.BETA_LAB_MANAGER_USERNAME);


    public static final String ACCOUNT_MANAGER_USERNAME = "aman@account.com";
    public static final String ACCOUNT_MANAGER_FULL_NAME = "Account Manager";
    public static final String ACCOUNT_MANAGER_PASSWORD = "amannn";
    public static AppUserInput getAccountManagerInput(){
        return AppUserInput.builder()
                .username(ACCOUNT_MANAGER_USERNAME)
                .fullName(ACCOUNT_MANAGER_FULL_NAME)
                .password(ACCOUNT_MANAGER_PASSWORD)
                .password2(ACCOUNT_MANAGER_PASSWORD)
                .roles(List.of(Authority.ACCOUNT_MANAGER))
                .build();
    }

    public static final String NEW_ACCOUNT_MANAGER_INPUT_USERNAME = "newaman@account.com";
    public static final String NEW_ACCOUNT_MANAGER_INPUT_FULL_NAME = "New Account Manager";
    public static final String NEW_ACCOUNT_MANAGER_PASSWORD = "newaman";
    public static AppUserInput getNewAccountManagerInputWithoutLabs() {
        return AppUserInput.builder()
                .username(NEW_ACCOUNT_MANAGER_INPUT_USERNAME)
                .fullName(NEW_ACCOUNT_MANAGER_INPUT_FULL_NAME)
                .password(NEW_ACCOUNT_MANAGER_PASSWORD)
                .password2(NEW_ACCOUNT_MANAGER_PASSWORD)
                .roles(List.of(Authority.ACCOUNT_MANAGER))
                .labKeysAsUser(List.of(AccountManagerTestUtils.ALPHA_LAB_KEY))
                .labKeysAsAdmin(List.of(AccountManagerTestUtils.BETA_LAB_KEY))
                .build();
    }

    public static AppUserInput getNewAccountManagerInputWithSomeLabs() {
        AppUserInput managerInput = getNewAccountManagerInputWithoutLabs();
        managerInput.setLabKeysAsAdmin(List.of(ALPHA_LAB_KEY));
        managerInput.setLabKeysAsUser(List.of(BETA_LAB_KEY));
        return  managerInput;
    }

    public static final String ALPHA_LAB_MANAGER_USERNAME = "alabman@account.com";
    public static final String ALPHA_LAB_MANAGER_FULL_NAME = "Alpha Lab Manager";
    public static final String ALPHA_LAB_MANAGER_PASSWORD = "alabman";
    public static AppUserInput getAlphaLabManagerInput() {
        return AppUserInput.builder()
                .username(ALPHA_LAB_MANAGER_USERNAME)
                .fullName(ALPHA_LAB_MANAGER_FULL_NAME)
                .password(ALPHA_LAB_MANAGER_PASSWORD)
                .password2(ALPHA_LAB_MANAGER_PASSWORD)
                .build();
    }

    public static final String BETA_LAB_MANAGER_USERNAME = "blabman@account.com";
    public static final String BETA_LAB_MANAGER_FULL_NAME = "Beta Lab Manager";
    public static final String BETA_LAB_MANAGER_PASSWORD = "blabman";

    public static AppUserInput getBetaLabManagerInput() {
        return AppUserInput.builder()
                .username(BETA_LAB_MANAGER_USERNAME)
                .fullName(BETA_LAB_MANAGER_FULL_NAME)
                .password(BETA_LAB_MANAGER_PASSWORD)
                .password2(BETA_LAB_MANAGER_PASSWORD)
                .build();
    }

    public static final String ALPHA_BETA_LAB_MANAGER_USERNAME = "ablabman@account.com";
    public static final String ALPHA_BETA_LAB_MANAGER_FULL_NAME = "Alpha Beta Lab Manager";
    public static final String ALPHA_BETA_LAB_MANAGER_PASSWORD = "ablabman";
    public static AppUserInput getAlphaBetaLabManagerInput() {
        return AppUserInput.builder()
                .username(ALPHA_BETA_LAB_MANAGER_USERNAME)
                .fullName(ALPHA_BETA_LAB_MANAGER_FULL_NAME)
                .password(ALPHA_BETA_LAB_MANAGER_PASSWORD)
                .password2(ALPHA_BETA_LAB_MANAGER_PASSWORD)
                .build();
    }

    public static final String ALPHA_LAB_ADMIN_USERNAME = "alabadmin@account.com";
    public static final String ALPHA_LAB_ADMIN_FULL_NAME = "Alpha Lab Admin";
    public static final String ALPHA_LAB_ADMIN_PASSWORD = "alabadmin";
    public static AppUserInput getAlphaLabAdminInput() {
        return AppUserInput.builder()
                .username(ALPHA_LAB_ADMIN_USERNAME)
                .fullName(ALPHA_LAB_ADMIN_FULL_NAME)
                .password(ALPHA_LAB_ADMIN_PASSWORD)
                .password2(ALPHA_LAB_ADMIN_PASSWORD)
                .build();
    }

    public static final String BETA_LAB_ADMIN_USERNAME = "blabadmin@account.com";
    public static final String BETA_LAB_ADMIN_FULL_NAME = "Beta Lab Admin";
    public static final String BETA_LAB_ADMIN_PASSWORD = "blabadmin";
    public static AppUserInput getBetaLabAdminInput() {
        return AppUserInput.builder()
                .username(BETA_LAB_ADMIN_USERNAME)
                .fullName(BETA_LAB_ADMIN_FULL_NAME)
                .password(BETA_LAB_ADMIN_PASSWORD)
                .password2(BETA_LAB_ADMIN_PASSWORD)
                .build();
    }

    public static final String ALPHA_BETA_LAB_ADMIN_USERNAME = "ablabadmin@account.com";
    public static final String ALPHA_BETA_LAB_ADMIN_FULL_NAME = "Alpha Beta Lab Admin";
    public static final String ALPHA_BETA_LAB_ADMIN_PASSWORD = "ablabadmin";
    public static AppUserInput getAlphaBetaLabAdminInput() {
        return AppUserInput.builder()
                .username(ALPHA_BETA_LAB_ADMIN_USERNAME)
                .fullName(ALPHA_BETA_LAB_ADMIN_FULL_NAME)
                .password(ALPHA_BETA_LAB_ADMIN_PASSWORD)
                .password2(ALPHA_BETA_LAB_ADMIN_PASSWORD)
                .build();
    }

    public static final String ALPHA_LAB_USER_USERNAME = "alabuser@account.com";
    public static final String ALPHA_LAB_USER_FULL_NAME = "Alpha Lab User";
    public static final String ALPHA_LAB_USER_PASSWORD = "alabuser";
    public static AppUserInput getAlphaLabUserInput() {
        return AppUserInput.builder()
                .username(ALPHA_LAB_USER_USERNAME)
                .fullName(ALPHA_LAB_USER_FULL_NAME)
                .password(ALPHA_LAB_USER_PASSWORD)
                .password2(ALPHA_LAB_USER_PASSWORD)
                .build();
    }

    public static final String BETA_LAB_USER_USERNAME = "blabuser@account.com";
    public static final String BETA_LAB_USER_FULL_NAME = "Beta Lab User";
    public static final String BETA_LAB_USER_PASSWORD = "blabuser";
    public static AppUserInput getBetaLabUserInput() {
        return AppUserInput.builder()
                .username(BETA_LAB_USER_USERNAME)
                .fullName(BETA_LAB_USER_FULL_NAME)
                .password(BETA_LAB_USER_PASSWORD)
                .password2(BETA_LAB_USER_PASSWORD)
                .build();
    }

    public static final String ALPHA_BETA_LAB_USER_USERNAME = "ablabuser@account.com";
    public static final String ALPHA_BETA_LAB_USER_FULL_NAME = "Alpha Beta Lab User";
    public static final String ALPHA_BETA_LAB_USER_PASSWORD = "ablabuser";
    public static AppUserInput getAlphaBetaLabUserInput() {
        return AppUserInput.builder()
                .username(ALPHA_BETA_LAB_USER_USERNAME)
                .fullName(ALPHA_BETA_LAB_USER_FULL_NAME)
                .password(ALPHA_BETA_LAB_USER_PASSWORD)
                .password2(ALPHA_BETA_LAB_USER_PASSWORD)
                .build();
    }

    public static final String ALPHA_BETA_LAB_DELETED_USER_USERNAME = "ablabdeleteduser@account.com";
    public static final String ALPHA_BETA_LAB_DELETED_USER_FULL_NAME = "Alpha Beta Lab Deleted User";
    public static final String ALPHA_BETA_LAB_DELETED_USER_PASSWORD = "ablabdeleteduser";
    public static AppUserInput ALPHA_BETA_LAB_DELETED_USER_INPUT = AppUserInput.builder()
            .username(ALPHA_BETA_LAB_DELETED_USER_USERNAME)
            .fullName(ALPHA_BETA_LAB_DELETED_USER_FULL_NAME)
            .password(ALPHA_BETA_LAB_DELETED_USER_PASSWORD)
            .password2(ALPHA_BETA_LAB_DELETED_USER_PASSWORD)
            .build();

    public static final String ALPHA_LAB_KEY = "alab";
    public static final String ALPHA_LAB_NAME = "Alpha Lab";
    public static LabInput getAlphaLabInput() {
        return LabInput.builder()
                .key(ALPHA_LAB_KEY)
                .name(ALPHA_LAB_NAME)
                .labManagerUsernames(List.of(ALPHA_LAB_MANAGER_USERNAME, ALPHA_BETA_LAB_MANAGER_USERNAME))
                .build();
    }

    public static final String BETA_LAB_KEY = "blab";
    public static final String BETA_LAB_NAME = "Beta Lab";
    public static LabInput BETA_LAB_INPUT = LabInput.builder()
            .key(BETA_LAB_KEY)
            .name(BETA_LAB_NAME)
            .labManagerUsernames(List.of(BETA_LAB_MANAGER_USERNAME, ALPHA_BETA_LAB_MANAGER_USERNAME))
            .build();

    public static final String GAMMA_LAB_KEY = "glab";
    public static final String GAMMA_LAB_NAME = "Gamma Lab";
    public static LabInput getGammaLabInput() {
        return LabInput.builder()
                .key(GAMMA_LAB_KEY)
                .name(GAMMA_LAB_NAME)
                .labManagerUsernames(List.of(BETA_LAB_MANAGER_USERNAME, ALPHA_BETA_LAB_MANAGER_USERNAME))
                .build();
    }

    public static final List<String> LAB_KEYS_WITH_INVALID_AND_VALID = List.of(ALPHA_LAB_KEY, "this-is-invalid", BETA_LAB_KEY);

}
