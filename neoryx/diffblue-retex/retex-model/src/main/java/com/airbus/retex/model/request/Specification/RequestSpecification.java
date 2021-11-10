package com.airbus.retex.model.request.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import org.springframework.data.jpa.domain.Specification;

import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.model.request.RequestFieldsEnum;
import com.airbus.retex.model.translation.Translate;

public class RequestSpecification {

    private RequestSpecification(){}

    // Here is an example to use like method
    // %A : To find all values ending by "A" like "requestA , TotoA"
    // Request% : To fnd all values starting with "Request" term like "RequestToto, RequestTata ,...."
    public static Specification<Request> filterByName(String name, Language lang) {
        return (root, query, cb) -> {
            Join<Request, Translate> join = root.join("translates", JoinType.LEFT);

            return cb.and(
                    cb.like(join.get("value"), "%"+name+"%"),
                    cb.equal(join.get("field"), RequestFieldsEnum.name.name()),
                    cb.equal(join.get("language"), lang)
            );
        };
    }

    public static Specification<Request> filterByTranslationOnName(Language lang) {
        return (root, query, cb) -> {
            Join<Request, Translate> join = root.join("translates", JoinType.LEFT);

            return cb.and(
                    cb.equal(join.get("field"), RequestFieldsEnum.name.name()),
                    cb.equal(join.get("language"), lang)
            );
        };
    }

    public static Specification<Request> filterByEntity(Long airbusEntityId) {
        return (root, query, cb) -> cb.equal(root.get("airbusEntity"), airbusEntityId);
    }

    public static Specification<Request> filterByRequester(Long requesterId) {
        return (root, query, cb) -> cb.equal(root.get("requester"), requesterId);
    }

    public static Specification<Request> filterByOrigin(Long origin) {
        return (root, query, cb) -> cb.equal(root.get("origin").get("id"), origin);
    }

    public static Specification<Request> filterByStatus(EnumStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }


}
