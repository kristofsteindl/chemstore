package com.ksteindl.chemstore.domain.repositories;

import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.domain.entities.Project;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends CrudRepository<Project, Long> {

    Optional<Project> findByNameAndLab(String name, Lab lab);

    List<Project> findAllByLab(Lab lab, Sort sort);

    @Query("SELECT p FROM Project p WHERE p.lab = ?1 and p.deleted = false")
    List<Project> findAllActive(Lab lab, Sort sort);



}
