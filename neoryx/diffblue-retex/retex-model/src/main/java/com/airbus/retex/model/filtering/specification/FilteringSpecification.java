package com.airbus.retex.model.filtering.specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import org.springframework.data.jpa.domain.Specification;

import com.airbus.retex.model.childrequest.PhysicalPart;
import com.airbus.retex.model.filtering.Filtering;
import com.airbus.retex.model.part.Part;

public class FilteringSpecification {

	private static final String PHYSICAL_PART = "physicalPart";

	/**
	 * private contructeur
	 */
	private FilteringSpecification() {

	}

    /**
     *
     * @param serialNumber
     * @return
     */
    public static Specification<Filtering> filterBySerialNumber(String serialNumber) {
        return (root, query, cb) -> {
            Join<Filtering, PhysicalPart> joinFilteringPhysical = root.join(PHYSICAL_PART, JoinType.LEFT);

            return  cb.like(joinFilteringPhysical.get("serialNumber"), serialNumber+"%");
        };
    }

    /**
     *
     * @param partNumber
     * @return
     */
    public static Specification<Filtering> filterByPartNumber(String partNumber) {
        return (root, query, cb) -> {
            Join<Filtering, PhysicalPart> joinFilteringPhysicalPart = root.join(PHYSICAL_PART, JoinType.LEFT);
            Join<PhysicalPart, Part> joinFilteringPart = joinFilteringPhysicalPart.join("part", JoinType.LEFT);

            return cb.like(joinFilteringPart.get("partNumber"), partNumber+"%");
        };
    }

    /**
     *
     * @param partNumber
     * @param serialNumber
     * @return
     */
    public static Specification<Filtering> filterByPartNumberAndSerialNumber(String partNumber,String serialNumber) {
        return (root, query, cb) -> {
            Join<Filtering, PhysicalPart> joinFilteringPhysicalPart = root.join(PHYSICAL_PART, JoinType.LEFT);
            Join<PhysicalPart, Part> joinFilteringPart = joinFilteringPhysicalPart.join("part", JoinType.LEFT);

            return cb.and(
                    cb.like(joinFilteringPart.get("partNumber"), partNumber+"%"),
                    cb.like(joinFilteringPhysicalPart.get("serialNumber"), serialNumber+"%"));
        };
    }
}
