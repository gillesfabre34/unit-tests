package com.airbus.retex.model.damage.specification;

import org.springframework.data.jpa.domain.Specification;

import com.airbus.retex.model.common.EnumActiveState;
import com.airbus.retex.model.damage.Damage;


public class DamageSpecification {


    private DamageSpecification() {
    }

    public static Specification<Damage> filterByIsoCodeAndActive() {
		return (root, query, cb) -> cb.or(cb.equal(root.get("state"), EnumActiveState.ACTIVE));
    }

    public static Specification<Damage> filterByIdIsoCodeAndActive(Long id) {
		return (root, query, cb) -> cb.and(cb.equal(root.get("id"), id),
				cb.equal(root.get("state"), EnumActiveState.ACTIVE));
    }
}
