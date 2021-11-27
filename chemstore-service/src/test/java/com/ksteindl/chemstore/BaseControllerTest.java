package com.ksteindl.chemstore;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksteindl.chemstore.domain.entities.*;
import com.ksteindl.chemstore.domain.input.*;
import com.ksteindl.chemstore.security.JwtProvider;
import com.ksteindl.chemstore.service.*;
import com.ksteindl.chemstore.utils.AccountManagerTestUtils;
import com.ksteindl.chemstore.utils.LabAdminTestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class BaseControllerTest {

    private static final Logger logger = LogManager.getLogger(BaseControllerTest.class);

    protected static String TOKEN_FOR_ACCOUNT_MANAGER;

    protected static String TOKEN_FOR_ALPHA_LAB_ADMIN;
    protected static String TOKEN_FOR_BETA_LAB_ADMIN;

    protected static String TOKEN_FOR_ALPHA_LAB_MANAGER;
    protected static String TOKEN_FOR_BETA_LAB_MANAGER;

    protected static String TOKEN_FOR_ALPHA_LAB_USER;
    protected static String TOKEN_FOR_BETA_LAB_USER;
    protected static String TOKEN_FOR_PW_CHANGED_USER;

    private static boolean first = true;


    @Autowired
    protected ChemicalCategoryService chemicalCategoryService;
    @Autowired
    protected JwtProvider jwtProvider;

    @BeforeEach
    public void createShelfLifes() {
        //SHELF LIFE
    }

    @BeforeAll
    static void initDb(
            @Autowired MockMvc mvc,
            @Autowired AppUserService appUserService,
            @Autowired LabService labService,
            @Autowired ManufacturerService manufacturerService,
            @Autowired ChemicalCategoryService categoryService,
            @Autowired ChemicalService chemicalService,
            @Autowired ChemItemService chemItemService) throws Exception {
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

            AppUserInput pwChangedUserInput = AccountManagerTestUtils.getPwChangedUserInput();
            AppUser pwChangedUser = appUserService.createUser(pwChangedUserInput);

            AppUserInput ablabDeleteduserInput = AccountManagerTestUtils.ALPHA_BETA_LAB_DELETED_USER_INPUT;
            ablabDeleteduserInput.setLabKeysAsUser(List.of(AccountManagerTestUtils.ALPHA_LAB_KEY, AccountManagerTestUtils.BETA_LAB_KEY));
            AppUser ablabdeleteduser = appUserService.createUser(ablabDeleteduserInput);
            appUserService.deleteAppUser(ablabdeleteduser.getId());

            //MANUFACTURER| Omega, Gamma, (Delta)
            ManufacturerInput omegaManufacturerInput = LabAdminTestUtils.getOmegaManufacturerInput();
            Manufacturer omegaMan = manufacturerService.createManufacturer(omegaManufacturerInput);

            ManufacturerInput gammaManufacturerInput = LabAdminTestUtils.getGammaManufacturerInput();
            Manufacturer gammaMan = manufacturerService.createManufacturer(gammaManufacturerInput);

            ManufacturerInput deltaManufacturerInput = LabAdminTestUtils.getDeltaManufacturerInput();
            Manufacturer deletedManufacturer = manufacturerService.createManufacturer(deltaManufacturerInput);
            manufacturerService.deleteManufacturer(deletedManufacturer.getId());

            //CATEGORY
            //Alpha: Solid, Buffer, (Deleted)
            ChemicalCategoryInput organicForAlphaCategoryInput = LabAdminTestUtils.getOrganicForAlphaInput();
            ChemicalCategory organicForAlphaCompund = categoryService.createCategory(organicForAlphaCategoryInput, AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL);

            ChemicalCategoryInput bufferForAlphaCategoryInput = LabAdminTestUtils.getBufferForAlphaInput();
            ChemicalCategory bufferForAlphaCompund = categoryService.createCategory(bufferForAlphaCategoryInput, AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL);

            ChemicalCategoryInput deletedForAlphaCategoryInput = LabAdminTestUtils.getDeletedForAlphaInput();
            ChemicalCategory deletedForAlphaCompund = categoryService.createCategory(deletedForAlphaCategoryInput, AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL);
            categoryService.deleteChemicalCategory(deletedForAlphaCompund.getId(), AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL);

            //Beta: Solid
            ChemicalCategoryInput organicForBetaCategoryInput = LabAdminTestUtils.getOrganicForBetaInput();
            ChemicalCategory organicForBetaCompund = categoryService.createCategory(organicForAlphaCategoryInput, AccountManagerTestUtils.BETA_LAB_ADMIN_PRINCIPAL);


            //CHEMICAL
            ChemicalInput acnForAlphaInput = LabAdminTestUtils.getAcnForAlphaInput();
            acnForAlphaInput.setCategoryId(organicForAlphaCompund.getId());
            Chemical acnForAlpha = chemicalService.createChemical(acnForAlphaInput, AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL);

            ChemicalInput etOHInput = LabAdminTestUtils.getEtOhForAlphaInput();
            etOHInput.setCategoryId(organicForAlphaCompund.getId());
            Chemical etOH = chemicalService.createChemical(etOHInput, AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL);

            ChemicalInput meOHInput = LabAdminTestUtils.getMeOhForAlphaInput();
            Chemical meOH = chemicalService.createChemical(meOHInput, AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL);

            ChemicalInput nh4AcInput = LabAdminTestUtils.getNH4AcForAlphaInput();
            nh4AcInput.setCategoryId(bufferForAlphaCompund.getId());
            Chemical nh4Ac = chemicalService.createChemical(nh4AcInput, AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL);

            ChemicalInput ipaInput = LabAdminTestUtils.getIpaForAlphaInput();
            ipaInput.setCategoryId(organicForAlphaCompund.getId());
            Chemical ipa = chemicalService.createChemical(ipaInput, AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL);
            chemicalService.deleteChemical(ipa.getId(), AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL);

            ChemicalInput acnForBetaInput = LabAdminTestUtils.getAcnForBetaInput();
            acnForBetaInput.setCategoryId(organicForBetaCompund.getId());
            Chemical acnForBeta = chemicalService.createChemical(acnForBetaInput, AccountManagerTestUtils.BETA_LAB_ADMIN_PRINCIPAL);


//            ChemItemInput cii1 = ChemItemInput.builder()
//                    .setManufacturerId(omegaMan.getId())
//                    .setAmount(3)
//                    .setQuantity(2500.0)
//                    .setBatchNumber("1234")
//                    .setUnit("mA")
//                    .setChemicalName(ethanol.getShortName())
//                    .setExpirationDateBeforeOpened(LocalDate.now().plusMonths(6))
//                    .build();
//            chemItemService.createChemItems(alab.getKey(), cii1, AccountManagerTestUtils.ALPHA_LAB_ADMIN_PRINCIPAL);
            TOKEN_FOR_ACCOUNT_MANAGER = getToken(mvc, AccountManagerTestUtils.ACCOUNT_MANAGER_USERNAME);
            TOKEN_FOR_ALPHA_LAB_ADMIN = getToken(mvc, AccountManagerTestUtils.ALPHA_LAB_ADMIN_USERNAME);
            TOKEN_FOR_ALPHA_LAB_MANAGER = getToken(mvc, AccountManagerTestUtils.ALPHA_LAB_MANAGER_USERNAME);
            TOKEN_FOR_ALPHA_LAB_USER = getToken(mvc, AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME);
            TOKEN_FOR_BETA_LAB_ADMIN= getToken(mvc, AccountManagerTestUtils.BETA_LAB_ADMIN_USERNAME);
            TOKEN_FOR_BETA_LAB_MANAGER = getToken(mvc, AccountManagerTestUtils.BETA_LAB_MANAGER_USERNAME);
            TOKEN_FOR_BETA_LAB_USER = getToken(mvc, AccountManagerTestUtils.BETA_LAB_USER_USERNAME);
            TOKEN_FOR_PW_CHANGED_USER = getToken(mvc, AccountManagerTestUtils.PW_CHANGED_USER_USERNAME);
            PasswordInput passwordInput = new PasswordInput();
            passwordInput.setOldPassword(AccountManagerTestUtils.PW_CHANGED_USER_USERNAME.split("@")[0]);
            passwordInput.setNewPassword(AccountManagerTestUtils.PW_CHANGED_USER_PASSWORD);
            passwordInput.setNewPassword2(AccountManagerTestUtils.PW_CHANGED_USER_PASSWORD);
            mvc.perform(patch("/api/logged-in/user")
                    .header("Authorization", TOKEN_FOR_PW_CHANGED_USER)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asStaticJsonString(passwordInput)));
            first = false;
        }
    }

    private static String getToken(MockMvc mvc, String userName) throws Exception {
        MvcResult result = mvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asStaticJsonString(Map.of("username", userName,
                                "password", userName.split("@")[0]))))
                .andReturn();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> loginResponse = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
        logger.info("Requiring token for tests for " + userName + " with /login, getting response: " + result.getResponse().getContentAsString());
        return loginResponse.get("token");
    }

    public static String asStaticJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
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
                    append(localDate.getMonthValue() > 9 ? localDate.getMonthValue() : "0" + localDate.getMonthValue())
                    .append("-").
                    append(localDate.getDayOfMonth() > 9 ? localDate.getDayOfMonth() : "0" + localDate.getDayOfMonth()).toString();
            String replaced = "\"" + attributeName + "\":null";
            int index = builder.indexOf(replaced);
            builder.replace(index, index + replaced.length(), "\"" + attributeName + "\":\"" + localDateString + "\"");
        }
    }

    public String asJsonString(final Object obj) {
        try {
            String serialized = new ObjectMapper().writeValueAsString(obj);
            return serialized;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
