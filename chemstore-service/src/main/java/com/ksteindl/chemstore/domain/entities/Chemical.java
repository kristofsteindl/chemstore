package com.ksteindl.chemstore.domain.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Chemical {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String shortName;

    private String exactName;

    @ManyToOne(fetch = FetchType.LAZY)
    private Lab lab;

    @ManyToOne(fetch = FetchType.LAZY)
    private ChemicalCategory category;

    private Boolean deleted = false;
}
