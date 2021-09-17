package com.ksteindl.chemstore.utils;

import com.ksteindl.chemstore.domain.input.ChemItemInput;

import java.time.LocalDate;

public class ChemItemTestUtils {

    public static String TEST_CHEM_ITEM_BATCH_NUMBER = "1234";
    public static Double TEST_CHEM_ITEM_QUANTITY = 2500.0;
    public static String TEST_CHEM_ITEM_UNIT = "ml";
    public static ChemItemInput getTestChemItemInput() {
        return ChemItemInput.builder()
                .setLabKey(AccountManagerTestUtils.ALPHA_LAB_KEY)
                .setChemicalName(LabAdminTestUtils.ETHANOL_SHORT_NAME)
                .setManufacturerName(LabAdminTestUtils.ALPHA_MANUFACTURER_NAME)
                .setArrivalDate(LocalDate.now())
                .setBatchNumber(TEST_CHEM_ITEM_BATCH_NUMBER)
                .setExpirationDateBeforeOpened(LocalDate.now().plusYears(1))
                .setQuantity(TEST_CHEM_ITEM_QUANTITY)
                .setUnit(TEST_CHEM_ITEM_UNIT)
                .build();
    }

}
