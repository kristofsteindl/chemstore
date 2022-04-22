package com.ksteindl.chemstore.service.wrapper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksteindl.chemstore.domain.entities.Project;

public class ProjectCard {
    
    @JsonIgnore
    private final Project project;

    public ProjectCard(Project project) {
        this.project = project;
    }

    @JsonProperty
    public Long getId() {
        return project.getId();
    }

    @JsonProperty
    public String getName() {
        return project.getName();
    }
}
