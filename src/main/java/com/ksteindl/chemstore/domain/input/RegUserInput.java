package com.ksteindl.chemstore.domain.input;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RegUserInput extends UpdateUserInput {

    @NotBlank(message = "password cannot be blank")
    private String password;

    @NotBlank(message = "password2 cannot be blank")
    private String password2;

}
