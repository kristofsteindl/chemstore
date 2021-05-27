package com.ksteindl.chemstore.payload;

import lombok.Data;

@Data
public class JwtLoginResponse {

    public JwtLoginResponse(boolean success, String token) {
        this.success = success;
        this.token = token;
    }

    private boolean success;
    private String token;

}
