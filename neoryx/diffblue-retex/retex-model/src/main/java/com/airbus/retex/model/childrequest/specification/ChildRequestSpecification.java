package com.airbus.retex.model.childrequest.specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.data.jpa.domain.Specification;

import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.childrequest.PhysicalPart;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.drt.Drt;

public class ChildRequestSpecification {

	/**
	 * private contructeur
	 */
	private ChildRequestSpecification() {

	}

    public static Specification<ChildRequest> filterByStatus(EnumStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<ChildRequest> filterByISIROrigin() {
        return (root, query, cb) -> {
            query.distinct(true);
            Subquery<Long> subQuery = query.subquery(Long.class);
            Root<Drt> subRoot = subQuery.from(Drt.class);
            subQuery.select(cb.count(subRoot.get("id")))
                    .where(cb.equal(subRoot.get("childRequest").get("id"), root.get("id")));

            return cb.and(
                    cb.equal(root.get("parentRequest").get("origin").get("name"), "ISIR"),
                    cb.equal(root.get("drtToInspect"), 1),
                    cb.equal(subQuery, 0)
            );
        };
    }

    public static Specification<ChildRequest> filterByCIVPorRETEXOrigin(String originName) {
        return (root, query, cb) -> {
            query.distinct(true);
            Subquery<Long> subQuery = query.subquery(Long.class);
            Root<Drt> subRoot = subQuery.from(Drt.class);
            subQuery.select(cb.count(subRoot.get("id")))
                    .where(cb.equal(subRoot.get("childRequest").get("id"), root.get("id")));
            return cb.and(cb.equal(root.get("parentRequest").get("origin").get("name"), originName),
                    cb.greaterThan(root.get("drtToInspect"), subQuery)
            );
        };
    }

    public static Specification<ChildRequest> filterBySerialNumber(String serialNumber) {
        return (root, query, cb) -> {
            Join<ChildRequest, PhysicalPart> joinPhysicalParts = root.join("physicalParts");
            return cb.equal(joinPhysicalParts.get("serialNumber"), serialNumber);
        };
    }

    public static Specification<ChildRequest> filterByPart(String pnType, String partNumberValue) {
        return (root, query, cb) -> {

            Join<ChildRequest, PhysicalPart> joinPhysicalParts = root.join("physicalParts");
            return cb.equal(joinPhysicalParts.get("part").get(pnType), partNumberValue);
        };
    }
}
