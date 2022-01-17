package com.ksteindl.chemstore.domain.input;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ChemItemInput implements Input{

    public static ChemItemInputBuilder builder() {
        return new ChemItemInputBuilder();
    }

    @NotBlank(message = "Lab key is required (labKey)")
    private String labKey;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate arrivalDate;

    @NotBlank(message = "Chemical name is required (chemicalShortName)")
    private String chemicalShortName;

    @NotNull(message = "Manufacturer id is required (manufacturerId)")
    private Long manufacturerId;

    @NotBlank(message = "Batch number of chemical cannot be blank")
    private String batchNumber;

    @NotNull(message = "Quantity of chemical cannot be blank")
    @DecimalMin(value = "0.0", inclusive = false)
    private Double quantity;

    @NotBlank(message = "unit is required")
    private String unit;

    @Min(1)
    private Integer amount;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Expiration date (before opened) is required")
    private LocalDate expirationDateBeforeOpened;

    public static class ChemItemInputBuilder {
        
        private String labKey;
        private LocalDate arrivalDate;
        private String chemicalName;
        private Long manufacturerId;
        private String batchNumber;
        private Double quantity;
        private String unit;
        private Integer amount;
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

        public ChemItemInputBuilder setAmount(Integer amount) {
            this.amount = amount;
            return this;
        }

        public ChemItemInputBuilder setExpirationDateBeforeOpened(LocalDate expirationDateBeforeOpened) {
            this.expirationDateBeforeOpened = expirationDateBeforeOpened;
            return this;
        }

        public ChemItemInput build() {
            return new ChemItemInput(labKey, arrivalDate, chemicalName, manufacturerId, batchNumber, quantity, unit, amount, expirationDateBeforeOpened);
        }
    }

}
