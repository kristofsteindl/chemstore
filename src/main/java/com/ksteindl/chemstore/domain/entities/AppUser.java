package com.ksteindl.chemstore.domain.entities;

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
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) // for backup, duplicate supposed to be checked in service
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

    @JsonIgnore
    private OffsetDateTime createdAt;
    @JsonIgnore
    private OffsetDateTime updatedAt;

//    @JsonProperty("labKeys")
//    public List<String> getLabKeysAsUser() {
//        return null == labsAsUser ?
//                new ArrayList<String>() :
//                labsAsUser.stream().map(lab -> lab.getKey()).collect(Collectors.toList());
//    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    /*
    * https://javabydeveloper.com/many-many-unidirectional-association/
    * https://www.baeldung.com/spring-custom-validation-message-source
    * */


}
