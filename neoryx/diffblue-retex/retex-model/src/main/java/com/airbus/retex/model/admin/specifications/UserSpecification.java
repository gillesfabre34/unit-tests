package com.airbus.retex.model.admin.specifications;

import com.airbus.retex.model.admin.role.Role;
import com.airbus.retex.model.admin.role.RoleCode;
import com.airbus.retex.model.common.EnumActiveState;
import com.airbus.retex.model.user.User;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    private UserSpecification() {
    }

    public static Specification<User> filterByAirbusEntity(Long airbusEntityId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("airbusEntity").get("id"), airbusEntityId);
    }


    public static Specification<User> filterByActive() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("state"), EnumActiveState.ACTIVE);
    }

    public static Specification<User> filterbySearch(String searchText) {
        String[] searchSplitted = searchText.split("\\s+");
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            for(String keyWord : searchSplitted){
                List<Predicate> predicatesKeyWords = new ArrayList<>();
                predicatesKeyWords.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + keyWord.toLowerCase() + "%"));
                predicatesKeyWords.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + keyWord.toLowerCase() + "%"));
                predicatesKeyWords.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("staffNumber")), "%" + keyWord.toLowerCase() + "%"));
                predicates.add(criteriaBuilder.or(predicatesKeyWords.toArray(new Predicate[]{})));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
        };
    }
    public static Specification<User> filterByRole(RoleCode roleCode) {
        return (root, query, cb) -> {
            Join<User, Role> joinUserRole = root.join("roles", JoinType.LEFT);
            return cb.and(
                    cb.equal(joinUserRole.get("roleCode").as(RoleCode.class), roleCode)
            );
        };
    }

    public static Specification<User> filterByTechnicalResponsible(Boolean isTechnicalResponsible) {
        if (!isTechnicalResponsible) {
            throw new UnsupportedOperationException("False value is not supported yet");
        }
        return (root, query, cb) -> {
            Join<User, Role> joinUserRole = root.join("roles", JoinType.LEFT);
            return cb.and(
                    cb.equal(joinUserRole.get("isTechnicalResponsible").as(Boolean.class), isTechnicalResponsible)
            );
        };
    }

}