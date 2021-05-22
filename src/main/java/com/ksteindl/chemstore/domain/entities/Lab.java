package com.ksteindl.chemstore.domain.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Data
public class Lab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String key;

    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    private AppUser labManager;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "LAB_ADMIN_TABLE", joinColumns = @JoinColumn(name = "LAB_ID"), inverseJoinColumns = @JoinColumn(name = "LAB_ADMIN_ID"))
    private List<AppUser> labAdmins;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(updatable = false)
    private OffsetDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

}
