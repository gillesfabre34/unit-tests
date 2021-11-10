package com.airbus.retex.model.todoList.specification;

import org.springframework.data.jpa.domain.Specification;

import com.airbus.retex.model.task.TodoListName;

public class TodoListSpecification {

    /**
     * private constructor
     */
    private TodoListSpecification() {

    }

    public static Specification<TodoListName> filterByOperationId(Long operationTypeId) {
        return (root, query, cb) -> cb.equal(root.get("operationTypeId"), operationTypeId);

    }

}
