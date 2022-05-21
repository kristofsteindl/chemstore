package com.ksteindl.chemstore.domain.input;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AppUserQuery {
    
    private String labKey;
    private String username;
    private Boolean onlyActive;
    
}
