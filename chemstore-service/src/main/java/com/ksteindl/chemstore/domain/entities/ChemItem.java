package com.ksteindl.chemstore.domain.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksteindl.chemstore.service.wrapper.AppUserCard;
import com.ksteindl.chemstore.service.wrapper.LabCard;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class ChemItem implements Serializable, Cloneable {

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
        return this.openedBy == null ? null : new AppUserCard(this.openedBy);
    }


    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expirationDate;


    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate consumptionDate;


    @ManyToOne(fetch = FetchType.LAZY)
    private AppUser consumedBy;

    @JsonProperty("consumedBy")
    public AppUserCard getConsumedByCard() {
        return this.consumedBy == null ? null : new AppUserCard(this.consumedBy);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return "ChemItem{" +
                "id=" + id +
                ", lab=" + lab.getKey() +
                ", arrivalDate=" + arrivalDate +
                ", arrivedBy=" + arrivedBy.getUsername() +
                ", chemical=" + chemical.getShortName() +
                ", manufacturer=" + manufacturer.getName() +
                ", batchNumber='" + batchNumber + '\'' +
                ", quantity=" + quantity +
                ", unit='" + unit + '\'' +
                ", seqNumber=" + seqNumber +
                ", expirationDateBeforeOpened=" + expirationDateBeforeOpened +
                ", openingDate=" + openingDate +
                ", openedBy=" + (openedBy == null ? "null" : openedBy.getUsername()) +
                ", expirationDate=" + expirationDate +
                ", consumptionDate=" + consumptionDate +
                ", consumedBy=" +  (consumedBy == null ? "null" : consumedBy.getUsername()) +
                '}';
    }
}
