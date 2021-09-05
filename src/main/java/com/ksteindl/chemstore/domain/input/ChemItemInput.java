package com.ksteindl.chemstore.domain.input;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ChemItemInput implements Input{

    public static ChemItemInputBuilder builder() {
        return new ChemItemInputBuilder();
    }

    @NotNull(message = "The labKey is required")
    private String labKey;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate arrivalDate;

    @NotNull(message = "The chemicalId is required")
    private String chemicalShortName;

    @NotNull(message = "The manifacturerId is required")
    private String manufacturerName;

    @NotBlank(message = "Batch number of chemical cannot be blank")
    private String batchNumber;

    @NotBlank(message = "Quantity of chemical cannot be blank")
    @DecimalMin(value = "0.0", inclusive = false)
    private Double quantity;

    @NotBlank(message = "unit is required")
    private String unit;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotBlank(message = "Expiration date (before opened) is required")
    private LocalDate expirationDateBeforeOpened;

    public static class ChemItemInputBuilder {

        private String labKey;
        private LocalDate arrivalDate;
        private String chemicalName;
        private String manufacturerName;
        private String batchNumber;
        private Double quantity;
        private String unit;
        private LocalDate expirationDateBeforeOpened;

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

        public ChemItemInputBuilder setManufacturerName(String manufacturerName) {
            this.manufacturerName = manufacturerName;
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

        public ChemItemInputBuilder setExpirationDateBeforeOpened(LocalDate expirationDateBeforeOpened) {
            this.expirationDateBeforeOpened = expirationDateBeforeOpened;
            return this;
        }

        public ChemItemInput build() {
            return new ChemItemInput(labKey, arrivalDate, chemicalName, manufacturerName, batchNumber, quantity, unit, expirationDateBeforeOpened);
        }
    }

}
