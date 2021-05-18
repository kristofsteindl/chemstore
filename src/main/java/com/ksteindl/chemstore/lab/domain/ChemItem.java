package com.ksteindl.chemstore.lab.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ksteindl.chemstore.user.domain.AppUser;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Getter
public class ChemItem {

    // Maybe @JoinColumn(name = ...) is missing from every @ManyToOne attribute?

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Lab lab;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate arrivalDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private AppUser arrivedBy;

    @ManyToOne(fetch = FetchType.EAGER)
    private Chemical chemical;

    @ManyToOne(fetch = FetchType.EAGER)
    private Manufacturer manufacturer;

    private String batchNumber;

    private Double quantity;

    @ManyToOne(fetch = FetchType.EAGER)
    private Unit unit;

    private Integer seqNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expirationDateBeforeOpened;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate openingDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private AppUser openedBy;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expirationDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate consumptionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private AppUser consumedBy;

}
