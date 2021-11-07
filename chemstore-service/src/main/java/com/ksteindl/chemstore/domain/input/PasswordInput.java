package com.ksteindl.chemstore.domain.input;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PasswordInput {

    @NotBlank(message = "old password is required")
    private String oldPassword;

    @NotBlank(message = "password cannot be blank")
    private String newPassword;

    @NotBlank(message = "password2 cannot be blank")
    private String newPassword2;
}
