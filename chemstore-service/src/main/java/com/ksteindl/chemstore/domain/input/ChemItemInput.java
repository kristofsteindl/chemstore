package com.ksteindl.chemstore.domain.input;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ChemItemInput implements Input{

    public static ChemItemInputBuilder builder() {
        return new ChemItemInputBuilder();
    }

    public ChemItemInput(String labKey, LocalDate arrivalDate, String chemicalShortName, Long manufacturerId, String batchNumber, Double quantity, String unit, Integer pieces, LocalDate expirationDateBeforeOpened) {
        this.labKey = labKey;
        this.arrivalDate = arrivalDate;
        this.chemicalShortName = chemicalShortName;
        this.manufacturerId = manufacturerId;
        this.batchNumber = batchNumber;
        this.quantity = quantity;
        this.unit = unit;
        this.pieces = pieces;
        this.expirationDateBeforeOpened = expirationDateBeforeOpened;
    }

    @NotBlank(message = "Lab key is required (labKey)")
    protected String labKey;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    protected LocalDate arrivalDate;

    @NotBlank(message = "Chemical name is required (chemicalShortName)")
    protected String chemicalShortName;

    @NotNull(message = "Manufacturer id is required (manufacturerId)")
    protected Long manufacturerId;

    @NotBlank(message = "Batch number of chemical cannot be blank")
    protected String batchNumber;

    @NotNull(message = "Quantity of chemical cannot be blank")
    @DecimalMin(value = "0.0", inclusive = false)
    protected Double quantity;

    @NotBlank(message = "unit is required")
    protected String unit;

    @Min(1)
    protected Integer pieces;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Expiration date (before opened) is required")
    private LocalDate expirationDateBeforeOpened;

    public static class ChemItemInputBuilder {
        
        protected String labKey;
        protected LocalDate arrivalDate;
        protected String chemicalName;
        protected Long manufacturerId;
        protected String batchNumber;
        protected Double quantity;
        protected String unit;
        protected Integer pieces;
        protected LocalDate expirationDateBeforeOpened;

        public ChemItemInputBuilder setLabKey(String labKey) {
            this.labKey = labKey;
            return this;
        }

        public ChemItemInputBuilder setArrivalDate(LocalDate arrivalDate) {
            this.arrivalDate = arrivalDate;
            return this;
        }

        public ChemItemInputBuilder setChemicalName(String chemicalName) {
            this.chemicalName = chemicalName;
            return this;
        }

        public ChemItemInputBuilder setManufacturerId(Long manufacturerId) {
            this.manufacturerId = manufacturerId;
            return this;
        }

        public ChemItemInputBuilder setBatchNumber(String batchNumber) {
            this.batchNumber = batchNumber;
            return this;
        }

        public ChemItemInputBuilder setQuantity(Double quantity) {
            this.quantity = quantity;
            return this;
        }

        public ChemItemInputBuilder setUnit(String unit) {
            this.unit = unit;
            return this;
        }

        public ChemItemInputBuilder setAmount(Integer pieces) {
            this.pieces = pieces;
            return this;
        }

        public ChemItemInputBuilder setExpirationDateBeforeOpened(LocalDate expirationDateBeforeOpened) {
            this.expirationDateBeforeOpened = expirationDateBeforeOpened;
            return this;
        }

        public ChemItemInput build() {
            return new ChemItemInput(labKey, arrivalDate, chemicalName, manufacturerId, batchNumber, quantity, unit, pieces, expirationDateBeforeOpened);
        }
    }

}
