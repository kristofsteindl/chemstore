package com.ksteindl.chemstore.domain.repositories.appuser;

import com.ksteindl.chemstore.domain.entities.AppUser;
import com.ksteindl.chemstore.domain.entities.Lab;
import com.ksteindl.chemstore.domain.input.AppUserQuery;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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
