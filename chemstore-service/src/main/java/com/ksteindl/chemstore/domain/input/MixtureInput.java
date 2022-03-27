package com.ksteindl.chemstore.domain.input;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
public class MixtureInput {
    
    private Long recipeId;
    
    private String username;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate creationDate;
    
    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.001", message = "amount of the mixture (amount) must be a greater or equal then 0.001")
    private Double amount;
    
    private List<Long> chemItemIds;
    
    private List<Long> mixtureItemIds;
}
