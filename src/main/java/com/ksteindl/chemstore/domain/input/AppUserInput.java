package com.ksteindl.chemstore.domain.input;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Data
public class AppUserInput implements Input {

    @Email(message = "Username must be an email")
    @NotBlank(message = "Username is required")
    protected String username;

    @NotBlank(message = "full name cannot be blank")
    protected String fullName;

    private List<Long> labIdsAsUser = new ArrayList<>();

    private List<Long> labIdsAsAdmin = new ArrayList<>();

    private List<String> roles = new ArrayList<>();

    @NotBlank(message = "password cannot be blank")
    private String password;

    @NotBlank(message = "password2 cannot be blank")
    private String password2;
}
