package com.ksteindl.chemstore.domain.repositories.appuser;

import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.domain.input.AppUserQuery;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class AppUserRepositoryCustomImpl implements AppUserRepositoryCustom {
    
    @Autowired
    private EntityManager entityManager;

    @Override
    public List<AppUser> findAppUsers(AppUserQuery appUserQuery) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<AppUser> selectQuery = criteriaBuilder.createQuery(AppUser.class);
        Root<AppUser> root = selectQuery.from(AppUser.class);
        selectQuery.select(root);

        selectQuery.where(assemblePredicate(appUserQuery, criteriaBuilder, root));
        
        selectQuery.orderBy(criteriaBuilder.desc(root.get("username")));
        selectQuery.distinct(true);
        
        TypedQuery<AppUser> appUserTypedQuery = entityManager.createQuery(selectQuery);
        return appUserTypedQuery.getResultList();
    }

    private Predicate[] assemblePredicate(AppUserQuery appUserQuery, CriteriaBuilder criteriaBuilder, Root<AppUser> root) {
        List<Predicate> predicates = new ArrayList<>();
        
        String labKey = appUserQuery.getLabKey();
        if (labKey != null) {
            Join<AppUser, Lab> userInLabJoin = root.join("labsAsUser", JoinType.LEFT);
            Predicate userInLabPredicate = criteriaBuilder.equal(userInLabJoin.get("key"), labKey);

            Join<AppUser, Lab> adminInLabJoin = root.join("labsAsAdmin", JoinType.LEFT);
            Predicate adminInLabPredicate = criteriaBuilder.equal(adminInLabJoin.get("key"), labKey);

            Join<AppUser, Lab> managedLabJoin = root.join("managedLabs", JoinType.LEFT);
            Predicate managedLabPredicate = criteriaBuilder.equal(managedLabJoin.get("key"), labKey);
            
            
            predicates.add(criteriaBuilder.or(userInLabPredicate, adminInLabPredicate, managedLabPredicate));
        }
        
        Boolean onlyActive = appUserQuery.getOnlyActive();
        if (onlyActive != null && onlyActive) {
            predicates.add(criteriaBuilder.isFalse(root.get("deleted")));
        }

        String username = appUserQuery.getUsername();
        if (username != null) {
            predicates.add(criteriaBuilder.equal(root.get("username"), username));
        }
        
        return predicates.toArray(new Predicate[predicates.size()]);
    }

}
