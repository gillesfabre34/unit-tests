package com.airbus.retex.model.routingComponent.specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import org.springframework.data.jpa.domain.Specification;

import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.routingComponent.RoutingComponent;
import com.airbus.retex.model.routingComponent.RoutingComponentIndex;
import com.airbus.retex.model.todoList.TodoList;

public class RoutingComponentSpecification {

    private static final String TODO_LIST = "todoList";
    private static final String ROUTING_COMPONENT = "routingComponent";

    /**
     * private contructor
     */
    private RoutingComponentSpecification() {

    }

    /**
     * @param operationTypeId
     * @return
     */
    public static Specification<RoutingComponentIndex> filterByOperation(Long operationTypeId) {
        return (root, query, cb) -> {
            Join<RoutingComponentIndex,RoutingComponent> joinRoutingComponentOperation = root.join(ROUTING_COMPONENT, JoinType.LEFT);
            Join<RoutingComponentIndex,TodoList> joinTodoListOperation = root.join(TODO_LIST, JoinType.LEFT);

            return cb.or(
                cb.equal(joinRoutingComponentOperation.get("operationType"), operationTypeId),
                cb.equal(joinTodoListOperation.get("operationType"), operationTypeId)
            );
        };
    }

    /**
     * @param functionalityId
     * @return
     */
    public static Specification<RoutingComponentIndex> filterByFunctionality(Long functionalityId) {
        return (root, query, cb) -> {
            Join<RoutingComponentIndex,RoutingComponent> joinRoutingComponentFunctionality = root.join(ROUTING_COMPONENT);
            return cb.equal(joinRoutingComponentFunctionality.get("functionality").get("id"), functionalityId);
        };
    }

    /**
     * @param todoListNameId
     * @return
     */
    public static Specification<RoutingComponentIndex> filterByTodoListName(Long todoListNameId) {
        return (root, query, cb) -> {
            Join<RoutingComponentIndex, TodoList> joinTodoListTodoListName = root.join(TODO_LIST);
            return cb.equal(joinTodoListTodoListName.get("todoListNameId"), todoListNameId);
        };
    }

    /**
     * @param damageId
     * @return
     */
    public static Specification<RoutingComponentIndex> filterByDamage(Long damageId) {
        return (root, query, cb) -> {
            Join<RoutingComponentIndex,RoutingComponent> joinRoutingComponentDamage = root.join(ROUTING_COMPONENT);
            return cb.equal(joinRoutingComponentDamage.get("damageId"), damageId);
        };
    }

    /**
     * @param inspectionId
     * @return
     */
    public static Specification<RoutingComponentIndex> filterByInspection(Long inspectionId) {
        return (root, query, cb) -> {
            Join<RoutingComponentIndex,RoutingComponent> joinRoutingComponentInspection = root.join(ROUTING_COMPONENT, JoinType.LEFT);
            Join<RoutingComponentIndex,TodoList> joinTodoListInspection = root.join(TODO_LIST, JoinType.LEFT);

            return cb.or(
                cb.equal(joinRoutingComponentInspection.get("inspection"), inspectionId),
                cb.equal(joinTodoListInspection.get("inspection"), inspectionId)
            );
        };
    }

    /**
     * @param status
     * @return
     */
    public static Specification<RoutingComponentIndex> filterByStatus(String status) {
        return (root, query, cb) -> cb.equal(root.get("status"), cb.literal(EnumStatus.valueOf(status)));
    }
}
