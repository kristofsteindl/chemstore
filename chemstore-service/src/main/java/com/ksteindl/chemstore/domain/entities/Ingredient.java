package com.ksteindl.chemstore.domain.entities;

public interface Ingredient {
    
    Long getId();
    
    void setId(Long id);
    
    Recipe getContainerRecipe();
    
    void setContainerRecipe(Recipe containerRecipe);
    
    Double getAmount();
    
    void setAmount(Double amount);
    
    String getUnit();
    
    void setUnit(String unit);
}
