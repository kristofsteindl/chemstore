package com.ksteindl.chemstore.domain.repositories;

import com.ksteindl.chemstore.domain.entities.Lab;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabRepository extends CrudRepository<Lab, Long> {

    List<Lab> findByKeyOrName(String key, String name);

    Iterable<Lab> findAllByOrderByNameAsc();
}
