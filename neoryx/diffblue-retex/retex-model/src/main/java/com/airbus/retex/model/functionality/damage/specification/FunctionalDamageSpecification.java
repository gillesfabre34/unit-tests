package com.airbus.retex.model.functionality.damage.specification;

import org.springframework.data.jpa.domain.Specification;

import com.airbus.retex.model.functionality.damage.FunctionalityDamage;

public class FunctionalDamageSpecification {


    private FunctionalDamageSpecification() {
    }

    public static Specification<FunctionalityDamage> filterByIsoCodeAndId() {
        return (root, query, cb) ->
// FIXME
//            final Join<FunctionalityDamage, Functionality> functionalityDamageFunction = root.join("functionality");
//
//            final Join<Functionality, FunctionalityValue> functionalityValueJoin =
//                    functionalityDamageFunction.join("functionalityValues");
//            functionalityValueJoin.on(cb.equal(functionalityValueJoin.get(ISO_CODE), isoCode));
//
//            final Join<FunctionalityDamage, FunctionalityDamageInfo> functionalDamageInfo =
//                    root.join("functionalityDamageInfo", JoinType.LEFT);
//            functionalDamageInfo.on(cb.equal(root.get("id"), id));
//
//            final Join<FunctionalityDamageInfo, FunctionalityDamageValue> functionalDamageValue =
//                    functionalDamageInfo.join("functionalityDamageValues", JoinType.LEFT);
//            functionalDamageValue.on(cb.equal(functionalDamageValue.get(ISO_CODE), isoCode));
//
//            return cb.or(cb.equal(root.get("id"), id));
        null;
    }
}
