package com.ksteindl.chemstore.domain.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class LabInput implements Input{

    @NotBlank(message = "key of lab is required")
    @Pattern(regexp = "[a-z\\-]+$", message = "key can consist only lower case letters and '-' character")
    private String key;

    @NotBlank(message = "name of lab is required")
    private String name;

    @NotEmpty(message = "The labManagerId is required")
    private List<String> labManagerUsernames;



}
