package com.airbus.retex.model.drt.specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import org.springframework.data.jpa.domain.Specification;

import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.childrequest.PhysicalPart;
import com.airbus.retex.model.drt.Drt;
import com.airbus.retex.model.filtering.Filtering;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.model.user.User;


public class DrtSpecification {

	/**
	 * private contructeur
	 */
	private DrtSpecification() {

	}

    /**
     * Filter by serial number.
     * @param serialNumber
     * @return
     */
    public static Specification<Drt> filterBySerialNumber(final String serialNumber) {
        return (root, query, cb) -> {
            Join<Drt, Filtering> joinDrtFiltering = root.join("filtering", JoinType.LEFT);
            Join<Filtering, PhysicalPart> joinFilteringPhysical = joinDrtFiltering.join("physicalPart", JoinType.LEFT);

            return  cb.like(joinFilteringPhysical.get("serialNumber"), serialNumber+"%");
        };
    }

    /**
     * Filter by part number.
     * @param partNumber
     * @return
     */
    public static Specification<Drt> filterByPartNumber(final String partNumber) {
        return (root, query, cb) -> {
            Join<Drt, Filtering> joinDrtFiltering = root.join("filtering", JoinType.LEFT);
            Join<Filtering, PhysicalPart> joinFilteringPhysicalPart = joinDrtFiltering.join("physicalPart", JoinType.LEFT);
            Join<PhysicalPart, Part> joinFilteringPart = joinFilteringPhysicalPart.join("part", JoinType.LEFT);

            return cb.like(joinFilteringPart.get("partNumber"), partNumber+"%");
        };
    }

    /**
     *
     * A technical responsible sees DRTs that come from requests where he is assigned.
     * @param authUserAsTechnicalResponsible
     * @return
     */
    public static Specification<Drt> filterByTechnicalResponsible(final Long authUserAsTechnicalResponsible) {
        return (root, query, cb) -> {
            Join<Drt, ChildRequest> joinDrtChildRequest = root.join("childRequest", JoinType.LEFT);
            Join<ChildRequest, Request> joinChildRequestRequest = joinDrtChildRequest.join("parentRequest", JoinType.LEFT);
            Join<Request, User> joinRequestTechResponsible= joinChildRequestRequest.join("technicalResponsibles", JoinType.LEFT);
            return cb.equal(joinRequestTechResponsible.get("id"), authUserAsTechnicalResponsible);
        };

    }

    /**
     *An operator sees DRTs unassigned or assigned to him if those DRT come from requests where he is assigned.
     * @param authUserAsOperator
     * @return
     */
    public static Specification<Drt> filterByOperator(final Long authUserAsOperator) {

            return (root, query, cb) -> {
                Join<Drt, User> joinDrtAssignedOperator = root.join("assignedOperator", JoinType.LEFT);
                Join<Drt, ChildRequest> joinDrtChildRequest = root.join("childRequest", JoinType.LEFT);
                Join<ChildRequest, Request> joinChildRequestRequest = joinDrtChildRequest.join("parentRequest", JoinType.LEFT);
                Join<Request, User> joinRequestOperator= joinChildRequestRequest.join("operators", JoinType.LEFT);

                return cb.and(
                        cb.or(
                            // assigned to him
                            cb.equal(joinDrtAssignedOperator.get("id"), authUserAsOperator),
                            // or assigend to no one
                            cb.isNull(joinDrtAssignedOperator)
                        ) // the authentified user must belong as well to the group of the request operator
                        , cb.equal(joinRequestOperator.get("id"), authUserAsOperator));

            };

    }
}