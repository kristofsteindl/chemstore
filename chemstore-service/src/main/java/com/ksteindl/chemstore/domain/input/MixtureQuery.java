package com.ksteindl.chemstore.domain.input;

import lombok.Builder;
import lombok.Data;

import java.security.Principal;
import java.time.LocalDate;

@Builder
@Data
public class MixtureQuery {
    
    private Principal principal;
    private String labKey;
    private Long projectId;
    private Long recipeId;
    private LocalDate availableOn;
    private Boolean available;
    private Integer page;
    private Integer size;
    
}
