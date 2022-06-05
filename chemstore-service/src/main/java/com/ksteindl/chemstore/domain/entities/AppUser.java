package com.ksteindl.chemstore.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ksteindl.chemstore.security.role.Role;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Data
public class AppUser implements AuditTracable {

    // TODO maybe we should split AppUser and UserDetails implementation

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(updatable = false, unique = true) // for backup, duplicate supposed to be checked in service
    private String username;
    @JsonIgnore
    private String password;
    private String fullName;
    private Boolean deleted = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "USER_OF_LAB_TABLE", joinColumns = @JoinColumn(name = "APP_USER_ID"), inverseJoinColumns = @JoinColumn(name = "LAB_ID"))
    private List<Lab> labsAsUser;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ADMIN_OF_LAB_TABLE", joinColumns = @JoinColumn(name = "APP_USER_ID"), inverseJoinColumns = @JoinColumn(name = "LAB_ID"))
    private List<Lab> labsAsAdmin;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "ROLE_OF_USER", joinColumns = @JoinColumn(name = "APP_USER_ID"), inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
    private Set<Role> roles;

    @ManyToMany(mappedBy = "labManagers")
    private List<Lab> managedLabs;

    @JsonIgnore
    private OffsetDateTime createdAt;
    @JsonIgnore
    private OffsetDateTime updatedAt;
    
    public List<String> getLabKeysAsUser() {
        return null == labsAsUser ?
                new ArrayList<>() :
                labsAsUser.stream().map(lab -> lab.getKey()).collect(Collectors.toList());
    }

    public List<String> getLabKeysAsAdmin() {
        return null == labsAsUser ?
                new ArrayList<>() :
                labsAsAdmin.stream().map(lab -> lab.getKey()).collect(Collectors.toList());
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    @Override
    public String toString() {
        return "AppUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", deleted=" + deleted +
                ", labsAsUser=" + labsAsUser +
                ", labsAsAdmin=" + labsAsAdmin +
                ", roles=" + roles +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppUser appUser = (AppUser) o;
        return username.equals(appUser.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    /*
    * https://javabydeveloper.com/many-many-unidirectional-association/
    * https://www.baeldung.com/spring-custom-validation-message-source
    * */


}
