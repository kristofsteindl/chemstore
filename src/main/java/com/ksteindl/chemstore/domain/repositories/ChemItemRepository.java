package com.ksteindl.chemstore.domain.repositories;

import com.ksteindl.chemstore.domain.entities.ChemItem;
import com.ksteindl.chemstore.domain.entities.Chemical;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ChemItemRepository extends CrudRepository<ChemItem, Long> {

    List<ChemItem> findByChemicalAndBatchNumber(Chemical chemical, String batchNumber);

}
