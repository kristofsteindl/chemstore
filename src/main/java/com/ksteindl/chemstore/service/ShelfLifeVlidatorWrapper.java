package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.domain.entities.ShelfLife;
import com.ksteindl.chemstore.domain.input.ShelfLifeInput;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.BindingResult;

import java.security.Principal;

@Data
@Builder
public class ShelfLifeVlidatorWrapper {

    ShelfLifeInput shelfLifeInput;
    ShelfLife shelfLife;
    Long id;
    BindingResult result;
    Principal principal;
}
