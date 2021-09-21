package com.ksteindl.chemstore.utils;

import com.ksteindl.chemstore.domain.input.ChemItemInput;
import com.ksteindl.chemstore.service.ChemItemService;
import com.ksteindl.chemstore.service.ManufacturerService;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;

public class ChemItemTestUtils {

    public static String TEST_CHEM_ITEM_BATCH_NUMBER = "1234";
    public static Double TEST_CHEM_ITEM_QUANTITY = 2500.0;
    public static Integer TEST_CHEM_ITEM_AMOUNTT = 3;
    public static ChemItemInput getTestChemItemInput(ManufacturerService manufacturerService) {
        // smelly unit reading here
        String unit;
        if (new File(ChemItemService.UNIT_FILE_NAME).exists()) {
            try {
                unit = Files.readAllLines(Paths.get(ChemItemService.UNIT_FILE_NAME), StandardCharsets.UTF_8).get(0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            unit = ChemItemService.DEFAULT_UNITS.get(0);
        }
        return ChemItemInput.builder()
                .setLabKey(AccountManagerTestUtils.ALPHA_LAB_KEY)
                .setArrivalDate(LocalDate.now().minusDays(1))
                .setChemicalName(LabAdminTestUtils.ETHANOL_SHORT_NAME)
                .setManufacturerId(manufacturerService.getManufacturers().stream().filter(m -> m.getName().equals(LabAdminTestUtils.OMEGA_MANUFACTURER_NAME)).findAny().get().getId())
                .setUnit(unit)
                .setAmount(TEST_CHEM_ITEM_AMOUNTT)
                .setBatchNumber(TEST_CHEM_ITEM_BATCH_NUMBER)
                .setQuantity(TEST_CHEM_ITEM_QUANTITY)
                .setExpirationDateBeforeOpened(LocalDate.now().plusYears(1).plusMonths(1).plusDays(1))
                .build();
    }

}
