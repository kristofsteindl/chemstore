package com.ksteindl.chemstore.audittrail;

import com.ksteindl.chemstore.domain.entities.AuditTracable;
import lombok.AllArgsConstructor;

import java.util.function.Function;

@AllArgsConstructor
public class AttributeProducer<T extends AuditTracable> {
    
    public final String attributeName;
    public final String attributeLabel;
    public final Function<T, String> valueProducer;
    public final Function<T, String> valueLabelProducer;
    
}
