package com.ksteindl.chemstore.domain.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Data
public class Lab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) // for backup, duplicate supposed to be checked in service
    private String key;

    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    private AppUser labManager;

    private Boolean deleted = false;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(updatable = false)
    private OffsetDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    @JsonProperty("labManagerUsername")
    public String getLabManagerUsername() {
        return labManager == null? "" : labManager.getUsername();
    }

}
