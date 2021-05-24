package com.ksteindl.chemstore.domain.input;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Data
public class LabInput implements Input{

    @NotBlank(message = "key of lab is required")
    @Pattern(regexp = "[a-z\\-]+$", message = "key can consist only lower case letters and '-' character")
    private String key;

    @NotBlank(message = "name of lab is required")
    private String name;

    @NotNull(message = "The labManagerId is required")
    private Long labManagerId;



}
