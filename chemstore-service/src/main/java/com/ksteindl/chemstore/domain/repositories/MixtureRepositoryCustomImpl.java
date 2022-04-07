package com.ksteindl.chemstore.domain.repositories;

import com.ksteindl.chemstore.domain.entities.Mixture;
import com.ksteindl.chemstore.domain.input.MixtureQuery;
import com.ksteindl.chemstore.service.wrapper.PagedList;
import com.ksteindl.chemstore.service.wrapper.PagedListBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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
    
    private Predicate[] assemblePredicate(MixtureQuery mixtureQuery, CriteriaBuilder criteriaBuilder, Root<Mixture> root) {
        List<Predicate> predicates = new ArrayList<>();

        Predicate labKeyPredicate = criteriaBuilder.equal(
                root.get("recipe").get("project").get("lab").get("key"), mixtureQuery.getLabKey());
        predicates.add(labKeyPredicate);
        if (mixtureQuery.getAvailable() != null) {
            Predicate availablePredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("expirationDate"), LocalDate.now());
            predicates.add(availablePredicate);
        }
        if (mixtureQuery.getRecipeId() != null) {
            Predicate recipePredicate = criteriaBuilder.equal(root.get("recipe").get("id"), mixtureQuery.getRecipeId());
            predicates.add(recipePredicate);
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
