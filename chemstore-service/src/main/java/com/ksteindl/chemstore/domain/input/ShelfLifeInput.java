package com.ksteindl.chemstore.domain.input;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@Builder
public class ShelfLifeInput implements Input{

    @NotNull(message = "amount is required")
    @Min(value = 1L, message = "amount of shelf life duration must be a positive integer (amount)")
    private Integer amount;

    @Pattern(regexp = "d|w|m|y")
    private String unit;

    @NotNull(message = "Lab (labKey) is required")
    private String labKey;

    @NotNull(message = "Type of chemical (chemTypeId) is required")
    private Long chemTypeId;

}
