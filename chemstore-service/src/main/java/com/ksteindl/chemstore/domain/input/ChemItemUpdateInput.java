package com.ksteindl.chemstore.domain.input;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
public class ChemItemUpdateInput extends ChemItemInput {

    public static ChemItemUpdateInputBuilder builder() {
        return new ChemItemUpdateInputBuilder();
    }

    public ChemItemUpdateInput(
            String labKey, 
            LocalDate arrivalDate, 
            String chemicalShortName, 
            Long manufacturerId, 
            String batchNumber, 
            Double quantity, 
            String unit, 
            Integer pieces, 
            LocalDate expirationDateBeforeOpened, 
            String arrivedByUsername, 
            LocalDate openingDate, 
            String openedByUsername, 
            LocalDate consumptionDate, 
            String consumedByUsername) {
        super(labKey, arrivalDate, chemicalShortName, manufacturerId, batchNumber, quantity, unit, pieces, expirationDateBeforeOpened);
        this.arrivedByUsername = arrivedByUsername;
        this.openingDate = openingDate;
        this.openedByUsername = openedByUsername;
        this.consumptionDate = consumptionDate;
        this.consumedByUsername = consumedByUsername;
    }

    @NotBlank(message = "username of the user, who arrived the chem item is required (arrivedByUsername)")
    private String arrivedByUsername;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate openingDate;

    private String openedByUsername;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate consumptionDate;

    private String consumedByUsername;

    public static class ChemItemUpdateInputBuilder extends ChemItemInputBuilder{

        private String arrivedByUsername;
        private LocalDate openingDate;
        private String openedByUsername;
        private LocalDate consumptionDate;
        private String consumedByUsername;

        public ChemItemUpdateInputBuilder setArrivedByUsername(String arrivedByUsername) {
            this.arrivedByUsername = arrivedByUsername;
            return this;
        }

        public ChemItemUpdateInputBuilder setOpeningDate(LocalDate openingDate) {
            this.openingDate = openingDate;
            return this;
        }

        public ChemItemUpdateInputBuilder setOpenedByUsername(String openedByUsername) {
            this.openedByUsername = openedByUsername;
            return this;
        }

        public ChemItemUpdateInputBuilder setConsumptionDate(LocalDate consumptionDate) {
            this.consumptionDate = consumptionDate;
            return this;
        }

        public ChemItemUpdateInputBuilder setConsumedByUsername(String consumedByUsername) {
            this.consumedByUsername = consumedByUsername;
            return this;
        }
        

        public ChemItemUpdateInput build() {
            return new ChemItemUpdateInput(
                    labKey, 
                    arrivalDate, 
                    chemicalName, 
                    manufacturerId, 
                    batchNumber, 
                    quantity, 
                    unit,
                    pieces, 
                    expirationDateBeforeOpened,
                    arrivedByUsername, 
                    openingDate, 
                    openedByUsername, 
                    consumptionDate, 
                    consumedByUsername);
        }
    }

}
