package com.airbus.retex.model.inspection;

import com.airbus.retex.model.basic.AbstractBaseModel;
import com.airbus.retex.model.todoList.TodoList;
import com.airbus.retex.model.translation.Translate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
public class Inspection extends AbstractBaseModel {
    public static final String FIELD_NAME = "name";

    @Column(name = "value")
    private String value;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "inspection")
    private List<TodoList> todoList;

    @OneToMany(mappedBy = "entityId", fetch = FetchType.LAZY)
    @Where(clause = "class_name = 'Inspection'")
    private List<Translate> translates  = new ArrayList<>();
}
