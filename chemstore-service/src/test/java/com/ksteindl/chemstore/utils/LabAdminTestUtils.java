package com.ksteindl.chemstore.utils;

import com.ksteindl.chemstore.domain.input.ChemicalCategoryInput;
import com.ksteindl.chemstore.domain.input.ChemicalInput;
import com.ksteindl.chemstore.domain.input.IngredientInput;
import com.ksteindl.chemstore.domain.input.ManufacturerInput;
import com.ksteindl.chemstore.domain.input.ProjectInput;
import com.ksteindl.chemstore.domain.input.RecipeInput;
import com.ksteindl.chemstore.service.IngredientService;

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

    public static final String ORGANIC_CATEGORY = "Organic solvent";
    public static final String BUFFER_CATEGORY = "Buffer solution";
    public static final String PHOSPHATE_CATEGORY = "Phosphate solution";
    public static final String WATER_CATEGORY = "Water";
    public static final String SOLID_CATEGORY = "Solid compound";
    public static final String DELETED_CATEGORY = "Deleted for alpha";

    public static final Integer ORGANIC_FOR_ALPHA_YEARS = 2;
    public static final String ORGANIC_FOR_ALPHA_UNIT = "y";

    public static final Integer BUFFER_FOR_ALPHA_DAYS = 60;
    public static final String BUFFER_FOR_ALPHA_UNIT = "d";

    public static final Integer ORGANIC_FOR_BETA_YEARS = 3;
    public static final String ORGANIC_FOR_BETA_UNIT = "y";

    public static final Integer SOLID_FOR_ALPHA_WEEKS = 60;
    public static final String SOLID_FOR_ALPHA_UNIT = "w";


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

    public static ChemicalCategoryInput getSolidForAlphaInput() {
        return ChemicalCategoryInput.builder()
                .name(SOLID_CATEGORY)
                .amount(LabAdminTestUtils.SOLID_FOR_ALPHA_WEEKS)
                .unit(LabAdminTestUtils.SOLID_FOR_ALPHA_UNIT)
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

    public static final String ETHANOL_EXACT_NAME = "Ethanol, 99%";
    public static final String ETHANOL_SHORT_NAME = "EtOH";

    public static final String METHANOL_EXACT_NAME = "Methyanol, pure";
    public static final String METHANOL_SHORT_NAME = "MeOH";

    public static final String NH4_ACETATE_EXACT_NAME = "Ammonium acetate, 1M";
    public static final String NH4_ACETATE_SHORT_NAME = "NH4Ac";

    public static final String IPA_EXACT_NAME = "Isopropyl alcohol";
    public static final String IPA_SHORT_NAME = "IPA";

    public static final String FA_EXACT_NAME = "Formic acid";
    public static final String FA_SHORT_NAME = "FA";

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

    public static ChemicalInput getFaForAlphaInput() {
        ChemicalInput chemicalInput = new ChemicalInput();
        chemicalInput.setExactName(FA_EXACT_NAME);
        chemicalInput.setShortName(FA_SHORT_NAME);
        chemicalInput.setLabKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        return chemicalInput;
    }

    public static final String ROSU_NAME = "Rosuvastatin";
    public static final String AMLO_NAME = "Amlodipine";
    public static final String LISI_NAME = "Lisinopril";
    
    public static ProjectInput getRosuForAlphaInput() {
        ProjectInput projectInput = new ProjectInput();
        projectInput.setName(ROSU_NAME);
        projectInput.setLabKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        return projectInput;
    }

    public static ProjectInput getAmloForAlphaInput() {
        ProjectInput projectInput = new ProjectInput();
        projectInput.setName(AMLO_NAME);
        projectInput.setLabKey(AccountManagerTestUtils.ALPHA_LAB_KEY);
        return projectInput;
    }

    public static final String BUFFER_NAME = "buffer";
    public static final String CONTENT_ELUENT_A_NAME = "content eluent A";
    public static final String CONTENT_ELUENT_B_NAME = "content eluent B";
    
    public static final String DEGR_A_NAME = "Degradation eluent B";
    public static final String ML = "ml";
    public static final String G = "g";
    public static final Double AMOUNT_1000 = 1000d;
    public static final Integer SHELF_LIFE_9 = 9;
    public static final Integer SHELF_LIFE_10 = 10;
    
    
    public static RecipeInput getDegrAForLisoInput() {
        RecipeInput input = new RecipeInput();
        input.setName(DEGR_A_NAME);
        input.setUnit(ML);
        input.setAmount(AMOUNT_1000);
        input.setShelfLifeInDays(SHELF_LIFE_9);
        
        IngredientInput acn = new IngredientInput();
        acn.setType(IngredientService.CHEMICAL);
        acn.setAmount(500d);
        acn.setUnit(ML);
        input.getIngredients().add(acn);
        
        IngredientInput meOH = new IngredientInput();
        meOH.setType(IngredientService.CHEMICAL);
        meOH.setAmount(100d);
        meOH.setUnit(G);
        input.getIngredients().add(meOH);

        IngredientInput buffer = new IngredientInput();
        buffer.setType(IngredientService.RECIPE);
        buffer.setAmount(400d);
        buffer.setUnit(ML);
        input.getIngredients().add(buffer);
        
        return input;
    }

    public static RecipeInput getContentEluentBLisoInput() {
        RecipeInput input = new RecipeInput();
        input.setName(CONTENT_ELUENT_B_NAME);
        input.setUnit(ML);
        input.setAmount(AMOUNT_1000);
        input.setShelfLifeInDays(SHELF_LIFE_10);

        IngredientInput ing1 = new IngredientInput();
        ing1.setType(IngredientService.CHEMICAL);
        ing1.setAmount(500d);
        ing1.setUnit(ML);
        input.getIngredients().add(ing1);

        IngredientInput ing2 = new IngredientInput();
        ing2.setType(IngredientService.CHEMICAL);
        ing2.setAmount(200d);
        ing2.setUnit(ML);
        input.getIngredients().add(ing2);

        IngredientInput ing3 = new IngredientInput();
        ing3.setType(IngredientService.RECIPE);
        ing3.setAmount(300d);
        ing3.setUnit(ML);
        input.getIngredients().add(ing3);

        return input;
    }
    
    public static final String UPDATED_CONTENT_ELUENT_B_NAME = "updated content eluent B";
    public static final String L = "l";
    public static final Double AMOUNT_1 = 1d;
    public static final Integer SHELF_LIFE_11 = 11;

    public static RecipeInput getUpdatedContentEluentBLisoInput() {
        RecipeInput input = new RecipeInput();
        input.setName(UPDATED_CONTENT_ELUENT_B_NAME);
        input.setUnit(L);
        input.setAmount(AMOUNT_1);
        input.setShelfLifeInDays(SHELF_LIFE_11);

        IngredientInput ing1 = new IngredientInput();
        ing1.setType(IngredientService.CHEMICAL);
        ing1.setAmount(0.333d);
        ing1.setUnit(L);
        input.getIngredients().add(ing1);

        IngredientInput ing2 = new IngredientInput();
        ing2.setType(IngredientService.CHEMICAL);
        ing2.setAmount(333d);
        ing2.setUnit(ML);
        input.getIngredients().add(ing2);

        IngredientInput ing3 = new IngredientInput();
        ing3.setType(IngredientService.RECIPE);
        ing3.setAmount(0.334d);
        ing3.setUnit(L);
        input.getIngredients().add(ing3);

        return input;
    }


}
