package com.airbus.retex.model.functionality.specification;

import com.airbus.retex.model.functionality.Functionality;
import com.airbus.retex.model.routingComponent.RoutingComponent;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;

public class FunctionalitySpecification {

    private FunctionalitySpecification() {
    }

    public static Specification<Functionality> filterByDamageId(Long subTaskId) {
        return (root, query, cb) -> {
            Join<Functionality, RoutingComponent> joinRoutingComponentFunctionality = root.join("routingComponents");
            query.distinct(true);
            return cb.equal(joinRoutingComponentFunctionality.get("damageId"), subTaskId);
        };
    }

}
