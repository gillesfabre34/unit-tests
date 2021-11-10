package com.airbus.retex.model.routing.specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import org.springframework.data.jpa.domain.Specification;

import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.model.routing.Routing;
import com.airbus.retex.model.routing.RoutingFieldsEnum;
import com.airbus.retex.model.routing.RoutingTranslation;

public class RoutingSpecification {

	private static final String POURCENTAGE = "%";
	private static final String PART_NUMBER = "partNumber";
	/**
	 * private constructeur
	 */
	private RoutingSpecification() {

	}

    /**
     *
     * @param name
     * @return
     */
    public static Specification<Routing> filterByRoutingName(String name) {
        return (root, query, cb) -> {
            Join<Routing, RoutingTranslation> joinRoutingPart = root.join("translations", JoinType.LEFT);
            query.groupBy(joinRoutingPart.get("entity"));
            return cb.and(
                    cb.equal(joinRoutingPart.get("field"), RoutingFieldsEnum.name),
					cb.like(joinRoutingPart.get("value"), POURCENTAGE + name + POURCENTAGE)
            );
        };
    }


    /**
     *
     * @param pnOrPnRoot
     * @return
     */
    public static Specification<Routing> filterByPnOrPnRoot(String pnOrPnRoot) {
        return (root, query, cb) -> {
            Join<Routing, Part> joinRoutingPart = root.join("part", JoinType.LEFT);

            return cb.or(
                    cb.like(joinRoutingPart.get(PART_NUMBER), pnOrPnRoot+POURCENTAGE),
                    cb.like(joinRoutingPart.get("partNumberRoot"), pnOrPnRoot+POURCENTAGE)
            );
        };
    }

    /**
     *
     * @param Id
     * @return
     */
	public static Specification<Routing> filterById(Long id) {
        return (root, query, cb) ->
		cb.equal(root.get("naturalId"), id);

    }



    /**
     *
     * @param creationDate
     * @return
     */
    public static Specification<Routing> filterByCreationDate(LocalDate creationDate) {
        return (root, query, cb) ->
             cb.and(cb.greaterThanOrEqualTo(root.get("creationDate"), LocalDateTime.of(creationDate, LocalTime.MIDNIGHT)),
                     cb.lessThan(root.get("creationDate"), LocalDateTime.of(creationDate.plusDays(1), LocalTime.MIDNIGHT)));
    }

    /**
     *
     * @param status
     * @return
     */
    public static Specification<Routing> filterByStatus(EnumStatus status) {
        return (root, query, cb) ->
             cb.equal(root.get("status"), status);

    }


    /**
     *
     * @param partNumber
     * @return
     */
    public static Specification<Routing> filterByPartNumber(String partNumber) {
        return (root, query, cb) -> {
            Join<Routing, Part> joinRoutingPart = root.join("part", JoinType.LEFT);

            return cb.and(cb.like(joinRoutingPart.get(PART_NUMBER), partNumber+POURCENTAGE),
                    cb.notEqual(joinRoutingPart.get(PART_NUMBER), ""));
        };
    }
    /**
     *
     * @param partNumberRoot
     * @return
     */
    public static Specification<Routing> filterByPartNumberRoot(String partNumberRoot) {
        return (root, query, cb) -> {
            Join<Routing, Part> joinRoutingPart = root.join("part", JoinType.LEFT);

            return cb.and(cb.like(joinRoutingPart.get("partNumberRoot"), partNumberRoot+POURCENTAGE),
                    cb.equal(joinRoutingPart.get(PART_NUMBER), ""));
        };
    }



}
