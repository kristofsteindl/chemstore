package com.ksteindl.chemstore.domain.repositories;

import com.ksteindl.chemstore.domain.entities.Project;
import com.ksteindl.chemstore.domain.entities.Recipe;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends CrudRepository<Recipe, Long> {

    Optional<Recipe> findByNameAndProject(String name, Project project);

    @Query("SELECT r FROM Recipe r WHERE r.project = ?1 ORDER BY r.project.name")
    List<Project> findAllByProject(Project project);

    @Query("SELECT r FROM Recipe r WHERE r.project = ?1 and r.deleted = false ORDER BY r.project.name")
    List<Project> findAllActive(Project project);
}
