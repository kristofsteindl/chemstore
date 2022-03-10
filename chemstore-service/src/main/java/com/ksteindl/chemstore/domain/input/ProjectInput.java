package com.ksteindl.chemstore.domain.input;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ProjectInput implements Input{

    @NotBlank(message = "Project name cannot be blank")
    private String name;

    @NotBlank(message = "Lab (labKey) cannot be blank")
    private String labKey;

}
