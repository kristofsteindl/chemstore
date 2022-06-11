package com.ksteindl.chemstore.audittrail;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.service.wrapper.AppUserCard;
import com.ksteindl.chemstore.service.wrapper.LabCard;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.Transient;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Immutable
@Setter
@Getter
public class AuditTrailEntry implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long entityId;
    
    private String actionType;

    private String entityTypeName;

    private String entityTypeLabel;
    
    @ManyToOne
    @JsonIgnore
    private AppUser performer;

    @ManyToOne
    @JsonIgnore
    private Lab lab;

    @JsonProperty(value = "performer")
    private AppUserCard getUserCard() {
        return performer == null? null : new AppUserCard(performer);
    }

    @JsonProperty(value = "lab")
    private LabCard getLabCard() {
        return lab == null ? null : new LabCard(lab);
    }
    
    private OffsetDateTime dateTime;
    
    private String comment;
    
    @Lob
    @JsonIgnore
    private String serializedUpdatedAttributes;
    
    @Transient
    private List<EntityLogAttribute> updatedAttributes = new ArrayList<>();

    @PrePersist
    private void serializeUpdatedAttributes() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        serializedUpdatedAttributes = objectMapper.writeValueAsString(updatedAttributes);
    }
    
    @PostLoad    
    private void deserializeUpdatedAttributes() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        updatedAttributes = objectMapper.readValue(serializedUpdatedAttributes,  List.class);
    }
    
    
    
    
    
}
