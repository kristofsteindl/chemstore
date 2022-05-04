package com.ksteindl.chemstore.domain.repositories;

import com.ksteindl.chemstore.domain.entities.ChemItem;
import com.ksteindl.chemstore.domain.input.ChemItemQuery;
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

public class ChemItemRepositoryCustomImpl implements ChemItemRepositoryCustom{
    
    @Autowired
    private EntityManager entityManager;
    
    @Override
    public PagedList<ChemItem> findChemItems(ChemItemQuery chemItemQuery, Pageable pageable) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        Long count = countHits(criteriaBuilder, chemItemQuery);
        
        CriteriaQuery<ChemItem> selectQuery = criteriaBuilder.createQuery(ChemItem.class);
        Root<ChemItem> root = selectQuery.from(ChemItem.class);
        selectQuery.select(root);
        
        selectQuery.where(assemblePredicate(chemItemQuery, criteriaBuilder, root));
        selectQuery.orderBy(criteriaBuilder.desc(root.get("id")));
        
        TypedQuery<ChemItem> pagedChemItemQuery = entityManager.createQuery(selectQuery);
        pagedChemItemQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        pagedChemItemQuery.setMaxResults(pageable.getPageSize());
        List<ChemItem> content = pagedChemItemQuery.getResultList();
        
        PagedListBuilder<ChemItem> pageBuilder = PagedList.builder(content)
                .setCurrentPage(chemItemQuery.getPage())
                .setTotalItems(count)
                .setTotalPages((int)(count / (long) chemItemQuery.getSize()) + 1);
        return pageBuilder.build();
    }

    @Override
    public List<ChemItem> findUsedChemItems(Long usedChemItemId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ChemItem> selectQuery = criteriaBuilder.createQuery(ChemItem.class);
        Root<ChemItem> root = selectQuery.from(ChemItem.class);
        root.join("");
        return null;
    }

    private Predicate[] assemblePredicate(ChemItemQuery chemItemQuery, CriteriaBuilder criteriaBuilder, Root<ChemItem> root) {
        List<Predicate> predicates = new ArrayList<>();

        Predicate labKeyPredicate = criteriaBuilder.equal(
                root.get("lab").get("key"), chemItemQuery.getLabKey());
        predicates.add(labKeyPredicate);
        
        Boolean opened = chemItemQuery.getOpened();
        if (opened != null) {
            if (opened) {
                predicates.add(criteriaBuilder.isNotNull(root.get("openingDate")));
            } else {
                predicates.add(criteriaBuilder.isNull(root.get("openingDate")));
            }
        }

        Boolean expired = chemItemQuery.getExpired();
        if (expired != null) {
            if (expired) {
                predicates.add(criteriaBuilder.lessThan(root.get("expirationDate"), LocalDate.now()));
            } else {
                Predicate expDateOk = criteriaBuilder.or(
                        criteriaBuilder.greaterThanOrEqualTo(root.get("expirationDate"), LocalDate.now()),
                        criteriaBuilder.isNull(root.get("expirationDate")));
                Predicate expDateBeforeOpenOk = criteriaBuilder.greaterThanOrEqualTo(root.get("expirationDateBeforeOpened"), LocalDate.now());
                predicates.add(criteriaBuilder.and(expDateOk, expDateBeforeOpenOk));
            }
        }

        Boolean consumed = chemItemQuery.getConsumed();
        if (consumed != null) {
            if (consumed) {
                predicates.add(criteriaBuilder.isNotNull(root.get("consumptionDate")));
            } else {
                predicates.add(criteriaBuilder.isNull(root.get("consumptionDate")));
            }
        }
        
        Long chemicalId = chemItemQuery.getChemicalId();
        if (chemicalId != null) {
            Predicate recipePredicate = criteriaBuilder.equal(root.get("chemical").get("id"), chemicalId);
            predicates.add(recipePredicate);
        }
        return predicates.toArray(new Predicate[predicates.size()]);
    }
    
    private Long countHits(CriteriaBuilder criteriaBuilder, ChemItemQuery chemItemQuery) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<ChemItem> root = countQuery.from(ChemItem.class);
        countQuery.select(criteriaBuilder.count(root));
        countQuery.where(assemblePredicate(chemItemQuery, criteriaBuilder, root));
        return entityManager.createQuery(countQuery).getSingleResult();
    }
}
