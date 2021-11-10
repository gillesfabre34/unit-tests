package com.airbus.retex.model.admin.specifications;

import org.springframework.data.jpa.domain.Specification;

import com.airbus.retex.model.admin.role.Role;
import com.airbus.retex.model.common.Language;

// FIXME : SHOULD BE DELETED
public class RoleSpecification {

    private RoleSpecification() {
    }

    public static Specification<Role> filterByAirbusEntity(Long airbusEntityId) {
        return (root, query, cb) ->
                cb.equal(root.get("airbusEntity").get("id"), airbusEntityId);
    }

    // FIXME : filtrer sur la langue (renommer en langue)
    public static Specification<Role> filterByIsoCode(Language language) {

        return (root, query, cb) ->
            // FIXME : remonter les labels du role (translate)
            /*final Join<Role, RoleLabel> roleRoleLabelJoin = root.join("roleLabels");
            final Join<Role, RoleFeature> featureRoleSetJoin = root.join("featureRoles");
            final Join<RoleFeature, Feature> featureListJoin = featureRoleSetJoin.join("feature");
            final Join<Feature, FeatureLabel> featureLabelListJoin = featureListJoin.join("featureLabels");

            roleRoleLabelJoin.on(cb.equal(roleRoleLabelJoin.get(RoleSpecification.isoCode), isoCode));
            featureLabelListJoin.on(cb.equal(featureLabelListJoin.get(RoleSpecification.isoCode), isoCode));

            return cb.or(cb.equal(featureLabelListJoin.get(RoleSpecification.isoCode), isoCode));*/
        cb.or(); // FIXME cleanup, useless code

    }
}
