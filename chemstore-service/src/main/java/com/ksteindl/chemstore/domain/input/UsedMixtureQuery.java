package com.ksteindl.chemstore.domain.input;

import lombok.Builder;
import lombok.Data;

import java.security.Principal;

@Builder
@Data
public class UsedMixtureQuery {
    
    private Principal principal;
    private String labKey;
    private Long chemItemId;
    private Long mixtureItemId;
    
}
