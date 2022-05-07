package com.ksteindl.chemstore.domain.repositories;

import com.ksteindl.chemstore.domain.entities.ChemItem;
import com.ksteindl.chemstore.domain.input.ChemItemQuery;
import com.ksteindl.chemstore.service.wrapper.PagedList;
import org.springframework.data.domain.Pageable;

public interface ChemItemRepositoryCustom {
    
    PagedList<ChemItem> findChemItems(ChemItemQuery chemItemQuery, Pageable pageable);
    
}
