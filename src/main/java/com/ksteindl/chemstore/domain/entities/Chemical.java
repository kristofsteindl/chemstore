package com.ksteindl.chemstore.domain.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Chemical {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) // for backup, duplicate supposed to be checked in service
    private String shortName;

    @Column(unique = true) // for backup, duplicate supposed to be checked in service
    private String exactName;

    private Boolean deleted = false;
}
