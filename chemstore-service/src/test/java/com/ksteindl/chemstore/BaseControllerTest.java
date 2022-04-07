package com.ksteindl.chemstore;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksteindl.chemstore.domain.entities.Chemical;
import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.domain.entities.Project;
import com.ksteindl.chemstore.domain.entities.Recipe;
import com.ksteindl.chemstore.domain.input.ChemItemInput;
import com.ksteindl.chemstore.domain.repositories.ChemicalRepository;
import com.ksteindl.chemstore.domain.repositories.ProjectRepository;
import com.ksteindl.chemstore.domain.repositories.RecipeRepository;
import com.ksteindl.chemstore.security.JwtProvider;
import com.ksteindl.chemstore.service.ChemicalCategoryService;
import com.ksteindl.chemstore.service.LabService;
import com.ksteindl.chemstore.service.RecipeService;
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
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class BaseControllerTest {

    private static final Logger logger = LogManager.getLogger(BaseControllerTest.class);

    protected static String TOKEN_FOR_ACCOUNT_MANAGER;
    protected static String TOKEN_FOR_ALPHA_LAB_ADMIN;
    protected static String TOKEN_FOR_BETA_LAB_ADMIN;
    protected static String TOKEN_FOR_ALPHA_LAB_MANAGER;
    protected static String TOKEN_FOR_BETA_LAB_MANAGER;
    protected static String TOKEN_FOR_ALPHA_BETA_LAB_MANAGER;
    protected static String TOKEN_FOR_ALPHA_LAB_USER;
    protected static String TOKEN_FOR_BETA_LAB_USER;
    protected static String TOKEN_FOR_PW_CHANGED_USER;


    protected static Chemical alphaAcn;
    protected static Chemical alphaMeOH;
    protected static Chemical alphaEtOH;
    
    protected static Recipe alphaLisoBuffer;
    protected static Recipe alphaLisoContAElu;
    protected static Recipe alphaLisoEluB;
    protected static Recipe alphaLisoDeletedRecipe;
    
    protected static Project alphaLisoProject;
    protected static Project alphaDeletedProject;
    protected static Project betaLisoProject;
    
    protected static Lab alphaLab;
    protected static Lab betaLab;

    private static boolean first = true;


    @Autowired
    protected ChemicalCategoryService chemicalCategoryService;
    @Autowired
    protected RecipeService recipeService;
    @Autowired
    protected JwtProvider jwtProvider;

    @BeforeEach
    public void createShelfLifes() {
        //SHELF LIFE
    }

    @BeforeAll
    static void initDb(
            @Autowired MockMvc mvc,
            @Autowired ChemicalRepository chemicalRepository,
            @Autowired RecipeRepository recipeRepository,
            @Autowired ProjectRepository projectRepository,
            @Autowired LabService labService) throws Exception {
        if (first) {
            TOKEN_FOR_ACCOUNT_MANAGER = getToken(mvc, AccountManagerTestUtils.ACCOUNT_MANAGER_USERNAME);
            TOKEN_FOR_ALPHA_LAB_ADMIN = getToken(mvc, AccountManagerTestUtils.ALPHA_LAB_ADMIN_USERNAME);
            TOKEN_FOR_ALPHA_LAB_MANAGER = getToken(mvc, AccountManagerTestUtils.ALPHA_LAB_MANAGER_USERNAME);
            TOKEN_FOR_ALPHA_BETA_LAB_MANAGER = getToken(mvc, AccountManagerTestUtils.ALPHA_BETA_LAB_MANAGER_USERNAME);
            TOKEN_FOR_ALPHA_LAB_USER = getToken(mvc, AccountManagerTestUtils.ALPHA_LAB_USER_USERNAME);
            TOKEN_FOR_BETA_LAB_ADMIN= getToken(mvc, AccountManagerTestUtils.BETA_LAB_ADMIN_USERNAME);
            TOKEN_FOR_BETA_LAB_MANAGER = getToken(mvc, AccountManagerTestUtils.BETA_LAB_MANAGER_USERNAME);
            TOKEN_FOR_BETA_LAB_USER = getToken(mvc, AccountManagerTestUtils.BETA_LAB_USER_USERNAME);
            TOKEN_FOR_PW_CHANGED_USER = getToken(mvc, AccountManagerTestUtils.PW_CHANGED_USER_USERNAME, AccountManagerTestUtils.PW_CHANGED_USER_PASSWORD);
            alphaLab = labService.findLabByKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
            betaLab = labService.findLabByKey(AccountManagerTestUtils.BETA_LAB_KEY);
            for (Project project : projectRepository.findAll()) {
                if (project.getLab().getId().equals(alphaLab.getId())) {
                    if (project.getName().equals(LabAdminTestUtils.LISI_NAME)) {
                        alphaLisoProject = project;
                    }
                    if (project.getDeleted()) {
                        alphaDeletedProject = project;
                    }
                } else if (project.getLab().getId().equals(betaLab.getId())) {
                    if (!project.getDeleted()) {
                        betaLisoProject = project;
                    }
                }
            }
            
            alphaAcn = chemicalRepository.findByShortNameAndLab(LabAdminTestUtils.ACETONITRIL_SHORT_NAME, alphaLab).get();
            alphaMeOH = chemicalRepository.findByShortNameAndLab(LabAdminTestUtils.METHANOL_SHORT_NAME, alphaLab).get();
            alphaEtOH = chemicalRepository.findByShortNameAndLab(LabAdminTestUtils.ETHANOL_SHORT_NAME, alphaLab).get();
            alphaLisoBuffer = recipeRepository.findByNameAndProject(LabAdminTestUtils.BUFFER_NAME, alphaLisoProject).get();
            alphaLisoContAElu = recipeRepository.findByNameAndProject(LabAdminTestUtils.CONTENT_ELUENT_A_NAME, alphaLisoProject).get();
            alphaLisoEluB = recipeRepository.findByNameAndProject(LabAdminTestUtils.CONTENT_ELUENT_B_NAME, alphaLisoProject).get();
            alphaLisoDeletedRecipe = recipeRepository.findByNameAndProject(LabAdminTestUtils.DELETED_ALPHA_LISO_RECIPE, alphaLisoProject).get();
            first = false;
        }
    }

    private static String getToken(MockMvc mvc, String userName, String password) throws Exception {
        MvcResult result = mvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asStaticJsonString(Map.of("username", userName,
                                "password", password))))
                .andReturn();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> loginResponse = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
        logger.info("Requiring token for tests for " + userName + " with /login, getting response: " + result.getResponse().getContentAsString());
        return loginResponse.get("token");
    }

    private static String getToken(MockMvc mvc, String userName) throws Exception {
        String password = userName.split("@")[0];
        return getToken(mvc, userName, password);
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
