package com.ksteindl.chemstore.utils;

import com.ksteindl.chemstore.domain.input.ChemicalCategoryInput;
import com.ksteindl.chemstore.domain.input.ChemicalInput;
import com.ksteindl.chemstore.domain.input.ManufacturerInput;

public class LabAdminTestUtils {


    public static final String ALPHA_MANUFACTURER_NAME = "Alpha Manufacturer";
    public static ManufacturerInput getAlphaManufacturerInput() {
        ManufacturerInput alphaManufacturerInput = new ManufacturerInput();
        alphaManufacturerInput.setName(ALPHA_MANUFACTURER_NAME);
        return alphaManufacturerInput;
    }

    public static final String OMEGA_MANUFACTURER_NAME = "Omega Manufacturer";
    public static ManufacturerInput getOmegaManufacturerInput() {
        ManufacturerInput alphaManufacturerInput = new ManufacturerInput();
        alphaManufacturerInput.setName(OMEGA_MANUFACTURER_NAME);
        return alphaManufacturerInput;
    }

    public static final String GAMMA_MANUFACTURER_NAME = "Gamma Manufacturer";
    public static ManufacturerInput getGammaManufacturerInput() {
        ManufacturerInput alphaManufacturerInput = new ManufacturerInput();
        alphaManufacturerInput.setName(GAMMA_MANUFACTURER_NAME);
        return alphaManufacturerInput;
    }

    public static final String DELTA_MANUFACTURER_NAME = "Delta Manufacturer";
    public static ManufacturerInput getDeltaManufacturerInput() {
        ManufacturerInput deltaManufacturerInput = new ManufacturerInput();
        deltaManufacturerInput.setName(DELTA_MANUFACTURER_NAME);
        return deltaManufacturerInput;
    }

    public static final String ORGANIC_CATEGORY = "Organic Solvent";
    public static final String BUFFER_CATEGORY = "Buffer Solution";
    public static final String PHOSPHATE_CATEGORY = "Phosphate Solution";
    public static final String WATER_CATEGORY = "Water";
    public static final String SOLID_CATEGORY = "Solid Compound";
    public static final String DELETED_CATEGORY = "Deleted for Alpha";

    public static final Integer ORGANIC_FOR_ALPHA_YEARS = 2;
    public static final String ORGANIC_FOR_ALPHA_UNIT = "y";

    public static final Integer BUFFER_FOR_ALPHA_DAYS = 60;
    public static final String BUFFER_FOR_ALPHA_UNIT = "d";

    public static final Integer ORGANIC_FOR_BETA_YEARS = 3;
    public static final String ORGANIC_FOR_BETA_UNIT = "y";


    //For ALPHA
    public static ChemicalCategoryInput getOrganicForAlphaInput() {
        return ChemicalCategoryInput.builder()
                .name(ORGANIC_CATEGORY)
                .amount(LabAdminTestUtils.ORGANIC_FOR_ALPHA_YEARS)
                .unit(LabAdminTestUtils.ORGANIC_FOR_ALPHA_UNIT)
                .labKey(AccountManagerTestUtils.ALPHA_LAB_KEY)
                .build();
    }

    public static ChemicalCategoryInput getBufferForAlphaInput() {
        return ChemicalCategoryInput.builder()
                .name(BUFFER_CATEGORY)
                .amount(LabAdminTestUtils.BUFFER_FOR_ALPHA_DAYS)
                .unit(LabAdminTestUtils.BUFFER_FOR_ALPHA_UNIT)
                .labKey(AccountManagerTestUtils.ALPHA_LAB_KEY)
                .build();
    }

    public static ChemicalCategoryInput getDeletedForAlphaInput() {
        return ChemicalCategoryInput.builder()
                .name(DELETED_CATEGORY)
                .amount(LabAdminTestUtils.BUFFER_FOR_ALPHA_DAYS)
                .unit(LabAdminTestUtils.BUFFER_FOR_ALPHA_UNIT)
                .labKey(AccountManagerTestUtils.ALPHA_LAB_KEY)
                .build();
    }

    //For BETA
    public static ChemicalCategoryInput getOrganicForBetaInput() {
        return ChemicalCategoryInput.builder()
                .name(ORGANIC_CATEGORY)
                .amount(LabAdminTestUtils.ORGANIC_FOR_BETA_YEARS)
                .unit(LabAdminTestUtils.ORGANIC_FOR_BETA_UNIT)
                .labKey(AccountManagerTestUtils.BETA_LAB_KEY)
                .build();
    }

    public static final String ACETONITRIL_EXACT_NAME = "Acetonitril";
    public static final String ACETONITRIL_SHORT_NAME = "ACN";

    public static final String ETHANOL_EXACT_NAME = "Ethanol";
    public static final String ETHANOL_SHORT_NAME = "EtOH";

    public static final String METHANOL_EXACT_NAME = "Methyl alcohol, pure";
    public static final String METHANOL_SHORT_NAME = "MeOH";

    public static final String NH4_ACETATE_EXACT_NAME = "Ammonium acetate, 1M";
    public static final String NH4_ACETATE_SHORT_NAME = "NH4-acetate";

    public static final String IPA_EXACT_NAME = "Iso-propyl alcohol";
    public static final String IPA_SHORT_NAME = "IPA";

    //For Alpha: ACN(O), EtOH(O)
    public static ChemicalInput getAcnForAlphaInput() {
        ChemicalInput chemicalInput = new ChemicalInput();
        chemicalInput.setExactName(ACETONITRIL_EXACT_NAME);
        chemicalInput.setShortName(ACETONITRIL_SHORT_NAME);
        chemicalInput.setLabKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        return chemicalInput;
    }

    public static ChemicalInput getEtOhForAlphaInput() {
        ChemicalInput chemicalInput = new ChemicalInput();
        chemicalInput.setExactName(ETHANOL_EXACT_NAME);
        chemicalInput.setShortName(ETHANOL_SHORT_NAME);
        chemicalInput.setLabKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        return chemicalInput;
    }

    public static ChemicalInput getMeOhForAlphaInput() {
        ChemicalInput chemicalInput = new ChemicalInput();
        chemicalInput.setExactName(METHANOL_EXACT_NAME);
        chemicalInput.setShortName(METHANOL_SHORT_NAME);
        chemicalInput.setLabKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        return chemicalInput;
    }

    public static ChemicalInput getNH4AcForAlphaInput() {
        ChemicalInput chemicalInput = new ChemicalInput();
        chemicalInput.setExactName(NH4_ACETATE_EXACT_NAME);
        chemicalInput.setShortName(NH4_ACETATE_SHORT_NAME);
        chemicalInput.setLabKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        return chemicalInput;
    }

    public static ChemicalInput getIpaForAlphaInput() {
        ChemicalInput chemicalInput = new ChemicalInput();
        chemicalInput.setExactName(IPA_EXACT_NAME);
        chemicalInput.setShortName(IPA_SHORT_NAME);
        chemicalInput.setLabKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        return chemicalInput;
    }

    public static ChemicalInput getAcnForBetaInput() {
        ChemicalInput chemicalInput = new ChemicalInput();
        chemicalInput.setExactName(ACETONITRIL_EXACT_NAME);
        chemicalInput.setShortName(ACETONITRIL_SHORT_NAME);
        chemicalInput.setLabKey(AccountManagerTestUtils.BETA_LAB_KEY);
        return chemicalInput;
    }


}
