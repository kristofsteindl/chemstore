package com.ksteindl.chemstore.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksteindl.chemstore.domain.entities.Chemical;
import com.ksteindl.chemstore.domain.input.ChemItemInput;
import com.ksteindl.chemstore.domain.input.ChemicalInput;
import com.ksteindl.chemstore.service.ChemItemService;
import com.ksteindl.chemstore.service.ChemicalService;
import com.ksteindl.chemstore.web.utils.ChemItemTestUtils;
import com.ksteindl.chemstore.web.utils.LabAdminTestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.AssertionErrors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ChemItemControllerTest extends BaseControllerTest {

    private static final Logger logger = LogManager.getLogger(ChemItemControllerTest.class);

    @Autowired
    private MockMvc mvc;

    private final String BASE_URL = "/api/chem-controller";

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withAlphaLabUser_got201() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput();
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().isCreated())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withAlphaLabAdmin_got201() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput();
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().isCreated())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withAlphaLabManager_got201() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput();
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().isCreated())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withAccountManager_got403() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput();
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ACCOUNT_MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().is(403))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withBetaLabUser_got400() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput();
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withBetaLabAdmin_got400() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput();
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withBetaLabManager_got400() throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput();
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateChemItem_withAlphaLabUser__fetchedFromDbIsExpected(@Autowired ChemItemService chemItemService) throws Exception {
        ChemItemInput testChemItemInput = ChemItemTestUtils.getTestChemItemInput();
        MvcResult result = mvc.perform(post(BASE_URL)
                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_USER).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(testChemItemInput)))
                .andExpect(status().isCreated())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info(result.getResponse().getContentAsString());
    }


    //
//    @Test
//    @Rollback
//    @Transactional
//    void testCreateChemical_withLabAdmin_fetchedFromDbIsExpected(@Autowired ChemicalService chemicalService) throws Exception {
//        ChemicalInput input = LabAdminTestUtils.getAcnInput();
//        MvcResult result = mvc.perform(post(CHEMICAL_URL)
//                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
//                        .content(asJsonString(input)))
//                .andExpect(status().isCreated())
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//        Optional<Chemical> optional = chemicalService.getChemicals().stream().filter(chemical -> chemical.getShortName().equals(input.getShortName())).findAny();
//        if (optional.isEmpty()) {
//            AssertionErrors.fail("Chemical was not found after the calling succesfully POST " + MANUFACTURER_URL);
//        }
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testCreateChemical_withEmptyInput1_got400() throws Exception {
//        MvcResult result = mvc.perform(post(CHEMICAL_URL)
//                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
//                        .content(""))
//                .andExpect(status().is(400))
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testCreateChemical_withEmptyInput2_got400() throws Exception {
//        MvcResult result = mvc.perform(post(CHEMICAL_URL)
//                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
//                        .content("{}"))
//                .andExpect(status().is(400))
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testCreateChemical_withEmptyShortName1_got400() throws Exception {
//        ChemicalInput input = LabAdminTestUtils.getAcnInput();
//        input.setShortName(null);
//        MvcResult result = mvc.perform(post(CHEMICAL_URL)
//                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
//                        .content(asJsonString(input)))
//                .andExpect(status().is(400))
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testCreateChemical_withEmptyShortName2_got400() throws Exception {
//        ChemicalInput input = LabAdminTestUtils.getAcnInput();
//        input.setShortName("");
//        MvcResult result = mvc.perform(post(CHEMICAL_URL)
//                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
//                        .content(asJsonString(input)))
//                .andExpect(status().is(400))
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testCreateChemical_withEmptyExactName1_got400() throws Exception {
//        ChemicalInput input = LabAdminTestUtils.getAcnInput();
//        input.setExactName(null);
//        MvcResult result = mvc.perform(post(CHEMICAL_URL)
//                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
//                        .content(asJsonString(input)))
//                .andExpect(status().is(400))
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testCreateChemical_withEmptyExactName2_got400() throws Exception {
//        ChemicalInput input = LabAdminTestUtils.getAcnInput();
//        input.setExactName("");
//        MvcResult result = mvc.perform(post(CHEMICAL_URL)
//                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
//                        .content(asJsonString(input)))
//                .andExpect(status().is(400))
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testCreateChemical_whenShortNameAlreadyExists_got400() throws Exception {
//        ChemicalInput input = LabAdminTestUtils.getAcnInput();
//        input.setShortName(LabAdminTestUtils.ETHANOL_SHORT_NAME);
//        MvcResult result = mvc.perform(post(CHEMICAL_URL)
//                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
//                        .content(asJsonString(input)))
//                .andExpect(status().is(400))
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
//
//    @Test
//    @Rollback
//    @Transactional
//    void testCreateChemical_whenExactNameAlreadyExists_got400() throws Exception {
//        ChemicalInput input = LabAdminTestUtils.getAcnInput();
//        input.setExactName(LabAdminTestUtils.ETHANOL_EXACT_NAME);
//        MvcResult result = mvc.perform(post(CHEMICAL_URL)
//                        .header("Authorization", TOKEN_FOR_ALPHA_LAB_ADMIN).contentType(MediaType.APPLICATION_JSON)
//                        .content(asJsonString(input)))
//                .andExpect(status().is(400))
//                .andReturn();
//        logger.info("status code: " + result.getResponse().getStatus());
//        logger.info(result.getResponse().getContentAsString());
//    }
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
