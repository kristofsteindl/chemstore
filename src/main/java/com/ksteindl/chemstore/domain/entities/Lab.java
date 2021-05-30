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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "MANAGER_OF_LAB_TABLE", joinColumns = @JoinColumn(name = "LAB_ID"), inverseJoinColumns = @JoinColumn(name = "APP_USER_ID"))
    @JsonIgnore
    private List<AppUser> labManagers;

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

    @JsonProperty("labManagerUsernames")
    public List<String> getLabManagerUsername() {
        return labManagers.stream().map(manager -> manager.getUsername()).collect(Collectors.toList());
    }

}
