package com.ksteindl.chemstore.domain.repositories;

import com.ksteindl.chemstore.domain.entities.ChemItem;
import com.ksteindl.chemstore.domain.entities.Mixture;
import com.ksteindl.chemstore.domain.input.MixtureQuery;
import com.ksteindl.chemstore.service.wrapper.PagedList;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MixtureRepositoryCustom {
    
    PagedList<Mixture> findMixtures(MixtureQuery mixtureQuery, Pageable pageable);

    List<Mixture> findUsedMixtureItems(ChemItem productChemItem);

    List<Mixture> findUsedMixtureItems(Mixture productMixture);
    
}
