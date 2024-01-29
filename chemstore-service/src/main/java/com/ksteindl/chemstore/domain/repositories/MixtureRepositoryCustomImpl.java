package com.ksteindl.chemstore.domain.repositories;

import com.ksteindl.chemstore.domain.entities.ChemItem;
import com.ksteindl.chemstore.domain.entities.Mixture;
import com.ksteindl.chemstore.domain.input.MixtureQuery;
import com.ksteindl.chemstore.service.wrapper.PagedList;
import com.ksteindl.chemstore.service.wrapper.PagedListBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MixtureRepositoryCustomImpl implements MixtureRepositoryCustom{
    
    @Autowired
    private EntityManager entityManager;
    
    @Override
    public PagedList<Mixture> findMixtures(MixtureQuery mixtureQuery, Pageable pageable) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        Long count = countHits(criteriaBuilder, mixtureQuery);
        
        CriteriaQuery<Mixture> selectQuery = criteriaBuilder.createQuery(Mixture.class);
        Root<Mixture> root = selectQuery.from(Mixture.class);
        selectQuery.select(root);
        
        selectQuery.where(assemblePredicate(mixtureQuery, criteriaBuilder, root));
        selectQuery.orderBy(criteriaBuilder.desc(root.get("id")));
        
        TypedQuery<Mixture> pagedMixtureQuery = entityManager.createQuery(selectQuery);
        pagedMixtureQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        pagedMixtureQuery.setMaxResults(pageable.getPageSize());
        List<Mixture> content = pagedMixtureQuery.getResultList();
        
        PagedListBuilder<Mixture> pageBuilder = PagedList.builder(content)
                .setCurrentPage(mixtureQuery.getPage())
                .setTotalItems(count)
                .setTotalPages((int)(count / (long) mixtureQuery.getSize()) + 1);
        return pageBuilder.build();
    }

    @Override
    public List<Mixture> findProductMixtureItems(ChemItem ingredientChemItem) {
        return findProductMixtureItems("chemItems", ingredientChemItem.getId());
    }
    
    @Override
    public List<Mixture> findProductMixtureItems(Mixture ingredientMixture) {
        return findProductMixtureItems("mixtureItems", ingredientMixture.getId());
    }
    
    private List<Mixture> findProductMixtureItems(String itemAttribute, Long productItemId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Mixture> selectQuery = criteriaBuilder.createQuery(Mixture.class);
        Root<Mixture> root = selectQuery.from(Mixture.class);

        Join<Mixture, ChemItem> join = root.join(itemAttribute, JoinType.LEFT);
        selectQuery.where(criteriaBuilder.equal(join.get("id"), productItemId));
        selectQuery.select(root).distinct(true);

        return entityManager.createQuery(selectQuery).getResultList();
    }
    
    private Predicate[] assemblePredicate(MixtureQuery mixtureQuery, CriteriaBuilder criteriaBuilder, Root<Mixture> root) {
        List<Predicate> predicates = new ArrayList<>();

        Predicate labKeyPredicate = criteriaBuilder.equal(
                root.get("recipe").get("project").get("lab").get("key"), mixtureQuery.getLabKey());
        predicates.add(labKeyPredicate);
        if (mixtureQuery.getAvailable() != null) {
            Predicate availablePredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("expirationDate"), LocalDate.now());
            predicates.add(availablePredicate);
        }
        if (mixtureQuery.getProjectId() != null) {
            Predicate recipePredicate = criteriaBuilder.equal(root.get("recipe").get("project").get("id"), mixtureQuery.getProjectId());
            predicates.add(recipePredicate);
        }
        if (mixtureQuery.getRecipeId() != null) {
            Predicate recipePredicate = criteriaBuilder.equal(root.get("recipe").get("id"), mixtureQuery.getRecipeId());
            predicates.add(recipePredicate);
        }
        LocalDate availableOn = mixtureQuery.getAvailableOn();
        if (availableOn != null) {
            Predicate notExpired = criteriaBuilder.greaterThanOrEqualTo(root.get("expirationDate"), availableOn);
            Predicate aleadyCreated = criteriaBuilder.lessThanOrEqualTo(root.get("creationDate"), availableOn);
            predicates.add(notExpired);
            predicates.add(aleadyCreated);
        }
        return predicates.toArray(new Predicate[predicates.size()]);
    }
    
    private Long countHits(CriteriaBuilder criteriaBuilder, MixtureQuery mixtureQuery) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Mixture> root = countQuery.from(Mixture.class);
        countQuery.select(criteriaBuilder.count(root));
        countQuery.where(assemblePredicate(mixtureQuery, criteriaBuilder, root));
        return entityManager.createQuery(countQuery).getSingleResult();
    }
}
