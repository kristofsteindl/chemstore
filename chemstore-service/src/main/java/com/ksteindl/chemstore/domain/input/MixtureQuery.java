package com.ksteindl.chemstore.domain.input;

import lombok.Builder;
import lombok.Data;

import java.security.Principal;

@Builder
@Data
public class MixtureQuery {
    
    private Principal principal;
    private String labKey;
    private Long recipeId;
    private Boolean available;
    private Integer page;
    private Integer size;
    
}
