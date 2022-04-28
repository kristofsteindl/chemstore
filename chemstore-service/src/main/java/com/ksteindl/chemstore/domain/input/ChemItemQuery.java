package com.ksteindl.chemstore.domain.input;

import lombok.Builder;
import lombok.Data;

import java.security.Principal;

@Builder
@Data
public class ChemItemQuery {
    
    private Principal principal;
    private String labKey;
    private Long chemicalId;
    private Boolean opened;
    private Boolean expired;
    private Boolean consumed;
    private Integer page;
    private Integer size;
    
}
