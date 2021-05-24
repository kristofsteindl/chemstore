package com.ksteindl.chemstore.domain.input;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class ChemItemInput implements Input{

    @NotNull(message = "The labId is required")
    private Long labId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate arrivalDate;

    @NotNull(message = "The chemicalId is required")
    private Long chemicalId;

    @NotNull(message = "The manifacturerId is required")
    private Long manifacturerId;

    @NotBlank(message = "Batch number of chemical cannot be blank")
    private String batchNumber;

    @NotBlank(message = "Quantity of chemical cannot be blank")
    private Double quantity;

    @NotBlank(message = "unit is required")
    private String unit;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotBlank(message = "Expiration date (before opened) is required")
    private LocalDate expirationDateBeforeOpened;

}
