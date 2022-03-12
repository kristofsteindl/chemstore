package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.domain.entities.Project;
import com.ksteindl.chemstore.domain.input.ProjectInput;
import com.ksteindl.chemstore.domain.repositories.ProjectRepository;
import com.ksteindl.chemstore.exceptions.ResourceNotFoundException;
import com.ksteindl.chemstore.exceptions.ValidationException;
import com.ksteindl.chemstore.util.Lang;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    private static final Logger logger = LogManager.getLogger(ProjectService.class);
    private final static Sort SORT_BY_NAME = Sort.by(Sort.Direction.ASC, "name");

    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    LabService labService;

    public Project createProject(ProjectInput projectInput, Principal managerPrincipal) {
        throwExceptionIfNotUnique(projectInput);
        Lab lab = labService.findLabForManager(projectInput.getLabKey(), managerPrincipal);
        Project project = new Project();
        project.setName(projectInput.getName());
        project.setLab(lab);
        return projectRepository.save(project);
    }

    public Project updateProject(ProjectInput projectInput, Long id, Principal managerPrincipal) {
        Project project = findById(id);
        labService.validateLabForManager(project.getLab(), managerPrincipal);
        throwExceptionIfNotUnique(projectInput, project);
        project.setName(projectInput.getName());
        return projectRepository.save(project);
    }

    public List<Project> getProjects(String labKey, Principal user) {
        return getProjects(labKey, user, true);
    }

    public List<Project> getProjects(String labKey, Principal user, boolean onlyActive) {
        Lab lab = labService.findLabForUser(labKey, user);
        return onlyActive ?
                projectRepository.findAllActive(lab, SORT_BY_NAME) :
                projectRepository.findAllByLab(lab, SORT_BY_NAME);
    }

    public void deleteProject(Long id, Principal managerPrincipal) {
        Project project = findById(id);
        labService.validateLabForManager(project.getLab(), managerPrincipal);
        project.setDeleted(true);
        projectRepository.save(project);
    }

    public Project findById(Long id) {
        return findById(id, true);
    }

    public Project findById(Long id, Boolean onlyActive) {
        Project project = projectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Lang.PROJECT_ENTITY_NAME, id));
        if (onlyActive && project.getDeleted()) {
            throw new ResourceNotFoundException(String.format(Lang.PROJECT_ALREADY_DELETED, project.getName()));
        }
        return project;
    }

    public void throwExceptionIfNotUnique(ProjectInput input) {
        String name = input.getName();
        Lab lab = labService.findLabByKey(input.getLabKey());
        Optional<Project> optional = projectRepository.findByNameAndLab(name, lab);
        optional.ifPresent(project -> {
            throw new ValidationException(String.format(Lang.PROJECT_WITH_SAME_NAME_FOUND_TEMPLATE, input.getName(), lab.getKey()));
        });
    }
    
    public void throwExceptionIfNotUnique(ProjectInput newInput, Project oldProject) {
        Lab lab = oldProject.getLab();
        String name = newInput.getName();
        projectRepository
                .findByNameAndLab(name, lab)
                .filter(foundProject -> !foundProject.getId().equals(oldProject.getId()))
                .ifPresent(sameNameProject -> {
                    throw new ValidationException(
                        String.format(Lang.PROJECT_WITH_SAME_NAME_FOUND_TEMPLATE, name, lab.getKey()));
                });
    }
}
