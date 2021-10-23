package com.ksteindl.chemstore.domain.repositories;

import com.ksteindl.chemstore.domain.entities.ChemItem;
import com.ksteindl.chemstore.domain.entities.Chemical;
import com.ksteindl.chemstore.domain.entities.Lab;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ChemItemRepository extends CrudRepository<ChemItem, Long> {

    @Query("SELECT c FROM ChemItem c WHERE c.lab = ?1 and c.chemical = ?2 and c.batchNumber = ?3 ")
    List<ChemItem> findEqualChemItems(Lab lab, Chemical chemical, String batchNumber, Sort sort);

    Page<ChemItem> findByLab(Lab lab, Pageable pageable);

    @Query("SELECT c FROM ChemItem c WHERE c.lab = ?1 and c.consumptionDate is null and (c.expirationDate >= CURRENT_DATE or c.expirationDate is null)")
    Page<ChemItem> findAvailableByLab(Lab lab, Pageable pageable);

}
