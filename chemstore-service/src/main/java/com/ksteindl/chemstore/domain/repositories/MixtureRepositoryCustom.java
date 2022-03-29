package com.ksteindl.chemstore.domain.repositories;

import com.ksteindl.chemstore.domain.entities.Mixture;
import com.ksteindl.chemstore.domain.input.MixtureQuery;
import com.ksteindl.chemstore.service.wrapper.PagedList;
import org.springframework.data.domain.Pageable;

public interface MixtureRepositoryCustom {
    
    PagedList<Mixture> findMixtures(MixtureQuery mixtureQuery, Pageable pageable);
    
}
