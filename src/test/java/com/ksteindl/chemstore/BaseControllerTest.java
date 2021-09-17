package com.ksteindl.chemstore;

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

import java.util.List;

public class BaseControllerTest {

    private static final Logger logger = LogManager.getLogger(BaseControllerTest.class);

    protected static String TOKEN_FOR_ACCOUNT_MANAGER;
    protected static String TOKEN_FOR_ALPHA_LAB_ADMIN;
    protected static String TOKEN_FOR_ALPHA_LAB_MANAGER;
    protected static String TOKEN_FOR_ALPHA_LAB_USER;


    @Autowired
    protected ChemTypeService chemTypeService;
    @Autowired
    protected ShelfLifeService shelfLifeService;
    @Autowired
    protected JwtProvider jwtProvider;

    @BeforeEach
    public void createShelfLifes() {
        //SHELF LIFE
        Long bufferSolutionId = chemTypeService.getChemTypes().stream().filter(chemType -> chemType.getName().equals(LabAdminTestUtils.BUFFER_SOLUTION_NAME)).findAny().get().getId();
        Long solidCompoundId = chemTypeService.getChemTypes().stream().filter(chemType -> chemType.getName().equals(LabAdminTestUtils.SOLID_COMPOUND_NAME)).findAny().get().getId();
        Long waterChemTypeId = chemTypeService.getChemTypes().stream().filter(chemType -> chemType.getName().equals(LabAdminTestUtils.WATER_CHEM_TYPE_NAME)).findAny().get().getId();
        ShelfLifeInput bufferForAlphaInput = ShelfLifeInput.builder()
                .amount(LabAdminTestUtils.BUFFER_FOR_ALPHA_DAYS)
                .unit(LabAdminTestUtils.BUFFER_FOR_ALPHA_UNIT)
                .labKey(AccountManagerTestUtils.ALPHA_LAB_KEY)
                .chemTypeId(bufferSolutionId).build();
        ShelfLifeInput bufferForBetaInput = ShelfLifeInput.builder()
                .amount(LabAdminTestUtils.BUFFER_FOR_BETA_DAYS)
                .unit(LabAdminTestUtils.BUFFER_FOR_BETA_UNIT)
                .labKey(AccountManagerTestUtils.BETA_LAB_KEY)
                .chemTypeId(bufferSolutionId).build();
        ShelfLifeInput solidForAlphaInput = LabAdminTestUtils.getSolidForAlphaInput();
        ShelfLifeInput waterForAlphaInput = LabAdminTestUtils.getSolidForAlphaInput();
        solidForAlphaInput.setChemTypeId(solidCompoundId);
        waterForAlphaInput.setChemTypeId(waterChemTypeId);

        shelfLifeService.createShelfLife(bufferForAlphaInput, AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
        shelfLifeService.createShelfLife(bufferForBetaInput,  AccountManagerTestUtils.BETA_LAB_MANAGER_PRINCIPAL);
        shelfLifeService.createShelfLife(solidForAlphaInput,  AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
        ShelfLife waterForAlpha = shelfLifeService.createShelfLife(waterForAlphaInput,  AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);
        shelfLifeService.deleteShelfLife(waterForAlpha.getId(), AccountManagerTestUtils.ALPHA_LAB_MANAGER_PRINCIPAL);

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
