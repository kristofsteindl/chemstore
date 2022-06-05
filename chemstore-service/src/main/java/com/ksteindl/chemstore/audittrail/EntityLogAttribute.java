package com.ksteindl.chemstore.audittrail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
class EntityLogAttribute {
    
    private String attributeName;
    private String attributeLabel;
    private String oldValue;
    private String newValue;
    private String oldLabel;
    private String newLabel;
    
}
