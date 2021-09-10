package com.ksteindl.chemstore.web.utils;

import com.ksteindl.chemstore.domain.input.ChemTypeInput;
import com.ksteindl.chemstore.domain.input.ChemicalInput;
import com.ksteindl.chemstore.domain.input.ManufacturerInput;

public class LabAdminTestUtils {

    public static final String ACETONITRIL_EXACT_NAME = "Acetonitril";
    public static final String ACETONITRIL_SHORT_NAME = "ACN";
    public static ChemicalInput getAcnInput() {
        ChemicalInput chemicalInput = new ChemicalInput();
        chemicalInput.setExactName(ACETONITRIL_EXACT_NAME);
        chemicalInput.setShortName(ACETONITRIL_SHORT_NAME);
        return chemicalInput;
    }

    public static final String ETHANOL_EXACT_NAME = "Ethanol";
    public static final String ETHANOL_SHORT_NAME = "EtOH";
    public static ChemicalInput getEtOHInput() {
        ChemicalInput chemicalInput = new ChemicalInput();
        chemicalInput.setExactName(ETHANOL_EXACT_NAME);
        chemicalInput.setShortName(ETHANOL_SHORT_NAME);
        return chemicalInput;
    }

    public static final String ISOPROPYL_ALCHOL_EXACT_NAME = "Isopropyl alcohol";
    public static final String ISOPROPYL_ALCHOL_SHORT_NAME = "IPA";
    public static ChemicalInput getIpaInput() {
        ChemicalInput chemicalInput = new ChemicalInput();
        chemicalInput.setExactName(ISOPROPYL_ALCHOL_EXACT_NAME);
        chemicalInput.setShortName(ISOPROPYL_ALCHOL_SHORT_NAME);
        return chemicalInput;
    }

    public static final String METHANOL_EXACT_NAME = "Methyl alcohol, pure";
    public static final String METHANOL_SHORT_NAME = "MeOH";
    public static ChemicalInput getMeOHInput() {
        ChemicalInput chemicalInput = new ChemicalInput();
        chemicalInput.setExactName(METHANOL_EXACT_NAME);
        chemicalInput.setShortName(METHANOL_SHORT_NAME);
        return chemicalInput;
    }

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

    public static final String ORGANIC_SOLVANT_NAME = "Organic Solvant";
    public static ChemTypeInput getOrganicSolvantInput() {
        ChemTypeInput organicSolvant = new ChemTypeInput();
        organicSolvant.setName(ORGANIC_SOLVANT_NAME);
        return organicSolvant;
    }

    public static final String SOLID_COMPOUND_NAME = "Solid Compound";
    public static ChemTypeInput getSolidCompoundInput() {
        ChemTypeInput solidCompound = new ChemTypeInput();
        solidCompound.setName(SOLID_COMPOUND_NAME);
        return solidCompound;
    }

    public static final String PHOSPHATE_SOLUTION_NAME = "Poshate Solution";
    public static ChemTypeInput getPhosphateSolutionInput() {
        ChemTypeInput poshateInput = new ChemTypeInput();
        poshateInput.setName(PHOSPHATE_SOLUTION_NAME);
        return poshateInput;
    }


}
