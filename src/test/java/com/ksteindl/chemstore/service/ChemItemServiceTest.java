package com.ksteindl.chemstore.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class ChemItemServiceTest {

    @Autowired
    public ChemItemService chemItemService;

    @Test
    public void testCreateChemItem_whenAllGood_gotNoError() {

    }
}
