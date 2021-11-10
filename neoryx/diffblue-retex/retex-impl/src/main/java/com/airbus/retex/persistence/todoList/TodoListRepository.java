package com.airbus.retex.persistence.todoList;

import com.airbus.retex.model.todoList.TodoList;
import com.airbus.retex.persistence.CustomJpaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoListRepository extends JpaRepository<TodoList, Long>, CustomJpaRepository<TodoList> {

    Boolean existsByNaturalIdAndOperationTypeId(Long id, Long operationTypeId);

    Boolean existsByOperationTypeIdAndInspectionIdAndTodoListNameIdAndNaturalIdNot(Long operationTypeId, Long inspectionId, Long todoListNameId, Long id);

    @Query(
        value = "SELECT COUNT(DISTINCT otl.operation_id) FROM operation_todo_list otl WHERE otl.todo_list_id = :technicalId",
        nativeQuery = true
    )
    Integer countRelatedOperations(@Param("technicalId") Long technicalId);
}
