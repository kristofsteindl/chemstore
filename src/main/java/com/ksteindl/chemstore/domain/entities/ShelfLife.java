package com.ksteindl.chemstore.domain.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Duration;

@Entity
@Getter
@Setter
public class ShelfLife {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Duration duration;

    @ManyToOne(fetch = FetchType.EAGER)
    private Lab lab;

    @ManyToOne(fetch = FetchType.EAGER)
    private ChemType chemType;

    private Boolean deleted = false;


}
