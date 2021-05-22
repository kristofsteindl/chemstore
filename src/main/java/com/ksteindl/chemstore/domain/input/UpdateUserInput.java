package com.ksteindl.chemstore.domain.input;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UpdateUserInput implements Input {

    @Email(message = "Username must be an email")
    @NotBlank(message = "Username is required")
    protected String username;

    @NotBlank(message = "full name cannot be blank")
    protected String fullName;
}
