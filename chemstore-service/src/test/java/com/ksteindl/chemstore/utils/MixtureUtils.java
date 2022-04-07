package com.ksteindl.chemstore.utils;

import com.ksteindl.chemstore.domain.input.MixtureInput;

import java.time.LocalDate;

public class MixtureUtils {
    
    public static final Double LISO_CONT_ELU_B_MIX_ALPHA_AMOUNT = 1500.0;
    
    public static MixtureInput getLisoContEluBMixInputForAlpha() {
        MixtureInput input = new MixtureInput();
        input.setAmount(LISO_CONT_ELU_B_MIX_ALPHA_AMOUNT);
        input.setCreationDate(LocalDate.now());
        return input;
    }
}
