package com.ksteindl.chemstore.audittrail;

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
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AuditTrailCustomRepositoryImpl implements AuditTrailCustomRepository{

    @Autowired
    private EntityManager entityManager;
    
    @Override
    public PagedList<AuditTrailEntry> findAuditTrailEntries(AuditTrailEntryQuery auditTrailEntryQuery) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        Long count = countHits(criteriaBuilder, auditTrailEntryQuery);
        
        CriteriaQuery<AuditTrailEntry> selectQuery = criteriaBuilder.createQuery(AuditTrailEntry.class);
        Root<AuditTrailEntry> root = selectQuery.from(AuditTrailEntry.class);
        selectQuery.select(root);

        selectQuery.where(assemblePredicate(auditTrailEntryQuery, criteriaBuilder, root));
        selectQuery.orderBy(criteriaBuilder.desc(root.get("id")));
        
        Pageable pageable = Pageable.ofSize(auditTrailEntryQuery.getSize()).withPage(auditTrailEntryQuery.getPage());
        TypedQuery<AuditTrailEntry> pagedChemItemQuery = entityManager.createQuery(selectQuery);
        pagedChemItemQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        pagedChemItemQuery.setMaxResults(pageable.getPageSize());
        List<AuditTrailEntry> content = pagedChemItemQuery.getResultList();

        PagedListBuilder pageBuilder = PagedList.builder(content)
                .setCurrentPage(auditTrailEntryQuery.getPage())
                .setTotalItems(count)
                .setTotalPages((int)(count / (long) auditTrailEntryQuery.getSize()) + 1);
        return pageBuilder.build();
    }

    private Predicate[] assemblePredicate(AuditTrailEntryQuery auditTrailEntryQuery, CriteriaBuilder criteriaBuilder, Root<AuditTrailEntry> root) {
        List<Predicate> predicates = new ArrayList<>();

        OffsetDateTime from = auditTrailEntryQuery.getFrom();
        if (from != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dateTime"), from));
        }

        OffsetDateTime to = auditTrailEntryQuery.getTo();
        if (to != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dateTime"), to));
        }

        Set<Long> performerIds = auditTrailEntryQuery.getPerformerIds();
        if (performerIds != null && !performerIds.isEmpty()) {
            predicates.add(root.get("performer").get("id").in(performerIds));
        }

        Boolean global = auditTrailEntryQuery.getGlobal();
        Set<Long> labIds = auditTrailEntryQuery.getLabIds();
        if (labIds != null && !labIds.isEmpty()) {
            Predicate inLabPredicate = root.get("lab").get("id").in(labIds);
            if (global == null || global == false) {
                predicates.add(inLabPredicate);
            } else {
                predicates.add(criteriaBuilder.or(inLabPredicate, criteriaBuilder.isNull(root.get("lab"))));
            }
        }

        Set<Long> entityIds = auditTrailEntryQuery.getEntityIds();
        if (entityIds != null && !entityIds.isEmpty()) {
            predicates.add(root.get("entityId").in(entityIds));
        }

        Set<String> actionTypes = auditTrailEntryQuery.getActionTypes();
        if (actionTypes != null && !actionTypes.isEmpty()) {
            predicates.add(root.get("actionType").in(actionTypes.stream().map(str -> ActionType.valueOf(str)).collect(Collectors.toList())));
        }

        Set<String> entityTypes = auditTrailEntryQuery.getEntityTypes();
        if (entityTypes != null && !entityTypes.isEmpty()) {
            predicates.add(root.get("entityTypeName").in(entityTypes));
        }

        return predicates.toArray(new Predicate[predicates.size()]);
    }

    private Long countHits(CriteriaBuilder criteriaBuilder, AuditTrailEntryQuery auditTrailEntryQuery) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<AuditTrailEntry> root = countQuery.from(AuditTrailEntry.class);
        countQuery.select(criteriaBuilder.count(root));
        countQuery.where(assemblePredicate(auditTrailEntryQuery, criteriaBuilder, root));
        return entityManager.createQuery(countQuery).getSingleResult();
    }
}
