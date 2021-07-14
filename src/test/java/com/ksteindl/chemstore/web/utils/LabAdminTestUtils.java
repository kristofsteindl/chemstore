package com.ksteindl.chemstore.web.utils;

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

    public static final String DELTA_MANUFACTURER_NAME = "Delta Manufacturer";
    public static ManufacturerInput getDeltaManufacturerInput() {
        ManufacturerInput deltaManufacturerInput = new ManufacturerInput();
        deltaManufacturerInput.setName(DELTA_MANUFACTURER_NAME);
        return deltaManufacturerInput;
    }
}
