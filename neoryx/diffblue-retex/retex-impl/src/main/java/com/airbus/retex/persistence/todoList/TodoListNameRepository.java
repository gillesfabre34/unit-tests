package com.airbus.retex.persistence.todoList;

import com.airbus.retex.model.task.TodoListName;
import com.airbus.retex.persistence.CustomJpaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoListNameRepository extends JpaRepository<TodoListName, Long>, CustomJpaRepository<TodoListName>, JpaSpecificationExecutor<TodoListName> {

    /**
     * find toDoListName by operation type id
     * @param operationTypeId
     * @return
     */
    @Query(value = "SELECT tln FROM TodoListName tln " +
            "JOIN TodoList tl ON tl.operationType = tln.id WHERE tln.id = :operationTypeId")
    List<TodoListName> findByOperationTypeId(@Param("operationTypeId")Long operationTypeId);
}
