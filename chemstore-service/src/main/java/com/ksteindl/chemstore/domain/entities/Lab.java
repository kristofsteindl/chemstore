package com.ksteindl.chemstore.domain.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksteindl.chemstore.audittrail.AuditTracable;
import com.ksteindl.chemstore.service.wrapper.AppUserCard;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Data
public class Lab implements AuditTracable {

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

    @JsonProperty("labManagers")
    public List<AppUserCard> getLabManagerUserCards() {
        return labManagers.stream().map(manager -> new AppUserCard(manager)).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lab lab = (Lab) o;
        return Objects.equals(key, lab.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return "Lab{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", labManagers=" + getLabManagerUserCards() +
                ", deleted=" + deleted +
                '}';
    }
    
    public String toLabel() {
        return key + "(" + name +")";
    }
}
