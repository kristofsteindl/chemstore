package com.ksteindl.chemstore.domain.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksteindl.chemstore.service.wrapper.AppUserCard;
import com.ksteindl.chemstore.service.wrapper.LabCard;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
public class ChemItem {

    // Maybe @JoinColumn(name = ...) is missing from every @ManyToOne attribute?

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    private Lab lab;

    @JsonProperty("lab")
    public LabCard getLabCard() {
        return new LabCard(this.lab);
    }


    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate arrivalDate;


    @ManyToOne(fetch = FetchType.LAZY)
    private AppUser arrivedBy;

    @JsonProperty("arrivedBy")
    public AppUserCard getArrivedByCard() {
        return new AppUserCard(this.arrivedBy);
    }


    @ManyToOne(fetch = FetchType.EAGER)
    private Chemical chemical;


    @ManyToOne(fetch = FetchType.EAGER)
    private Manufacturer manufacturer;


    private String batchNumber;


    private Double quantity;


    private String unit;


    private Integer seqNumber;


    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expirationDateBeforeOpened;


    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate openingDate;


    @ManyToOne(fetch = FetchType.LAZY)
    private AppUser openedBy;

    @JsonProperty("openedBy")
    public AppUserCard getOpenedByCard() {
        return new AppUserCard(this.openedBy);
    }


    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expirationDate;


    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate consumptionDate;


    @ManyToOne(fetch = FetchType.LAZY)
    private AppUser consumedBy;

    @JsonProperty("consumedBy")
    public AppUserCard getConsumedByCard() {
        return new AppUserCard(this.consumedBy);
    }


}
