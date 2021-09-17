package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.domain.input.*;
import com.ksteindl.chemstore.BaseControllerTest;
import com.ksteindl.chemstore.utils.AccountManagerTestUtils;
import com.ksteindl.chemstore.utils.LabAdminTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ShelfLifeServiceTest extends BaseControllerTest{

    @Autowired
    private ShelfLifeService shelfLifeService;
    @Autowired
    private ChemTypeService chemTypeService;

    @Test
    @Rollback
    @Transactional
    public void testCreateShelfLife_whenAllValid_gotNoException() {
        ShelfLifeInput input = LabAdminTestUtils.getSolidForBetaInput();
        Long chemTypeId = chemTypeService.getChemTypes().stream()
                .filter(chemType -> chemType.getName().equals(LabAdminTestUtils.SOLID_COMPOUND_NAME))
                .findAny()
                .get()
                .getId();
        input.setChemTypeId(chemTypeId);
        shelfLifeService.createShelfLife(input, AccountManagerTestUtils.BETA_LAB_MANAGER_PRINCIPAL);
    }



}
