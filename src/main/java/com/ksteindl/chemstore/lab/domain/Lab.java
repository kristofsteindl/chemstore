package com.ksteindl.chemstore.lab.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ksteindl.chemstore.user.domain.AppUser;
import lombok.Data;
import lombok.Getter;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Getter
public class Lab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String key;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private AppUser labManager;

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
