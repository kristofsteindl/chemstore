package com.ksteindl.chemstore.utils;

import java.security.Principal;

public class MockPrincipal implements Principal {

    public MockPrincipal(String name) {
        this.name = name;
    }

    private String name;

    @Override
    public String getName() {
        return name;
    }
}
