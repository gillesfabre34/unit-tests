package com.airbus.retex.model.part.specification;

import org.springframework.data.jpa.domain.Specification;

import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.part.Part;

public class PartSpecification {

	/**
	 * private contructeur
	 */
	private PartSpecification() {

	}
    /**
     *
     * @param ataCode
     * @return
     */
    public static Specification<Part> filterByAtaCode(String ataCode) {
		return (root, query, cb) -> cb.equal(root.get("ata").get("code"), ataCode);
    }

    /**
     *
     * @param partNumber
     * @return
     */
    public static Specification<Part> filterByPartNumber(String partNumber) {
		return (root, query, cb) -> cb.or(
                    cb.like(root.get("partNumber"), partNumber+"%"),
                    cb.like(root.get("partNumberRoot"), partNumber+"%")
            );

    }

    /**
     *
     * @param designationId
     * @return
     */
    public static Specification<Part> filterByDesignation(Long designationId) {
		return (root, query, cb) -> cb.equal(root.get("partDesignation"), designationId);

    }

    /**
     *
     * @param status
     * @return
     */
    public static Specification<Part> filterByStatus(EnumStatus status) {
		return (root, query, cb) -> cb.equal(root.get("status"), status);

    }

}
