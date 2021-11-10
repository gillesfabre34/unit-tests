package com.airbus.retex.control;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.model.control.*;
import com.airbus.retex.persistence.control.ControlRepository;
import com.airbus.retex.persistence.control.ControlRoutingComponentRepository;
import com.airbus.retex.persistence.control.ControlTodoListRepository;
import com.airbus.retex.persistence.control.ControlVisualRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ControlIT extends BaseControllerTest {

    @Autowired
    ControlRepository controlRepository;

    @Autowired
    ControlTodoListRepository controlTodoListRepository;

    @Autowired
    ControlRoutingComponentRepository controlRoutingComponentRepository;

    @Autowired
    ControlVisualRepository controlVisualRepository;

    @Test
    public void saveControlTodoList() {
        runInTransaction(() -> {
            controlTodoListRepository.save(createControlTodoList());
            List<ControlTodoList> controls = controlTodoListRepository.findAll();
            assertThat(controls.size(), equalTo(1));
        });

    }

    @Test
    public void saveControlVisual() {
        runInTransaction(() -> {
            controlVisualRepository.save(createControlVisual());
            List<ControlVisual> control = controlVisualRepository.findAll();
            assertThat(control.size(), equalTo(1));
        });
    }

    @Test
    public void saveControlRoutingComponent() {
        runInTransaction(() -> {
            controlRoutingComponentRepository.save(createControlRoutingComponent());
        List<ControlRoutingComponent> controls = controlRoutingComponentRepository.findAll();
        assertThat(controls.size(), equalTo(1));
        });
    }

    @Test
    public void saveMultiControl() {
        runInTransaction(() -> {
            controlTodoListRepository.save(createControlTodoList());
            controlVisualRepository.save(createControlVisual());
            controlRoutingComponentRepository.save(createControlRoutingComponent());
            List<AbstractControl> controls = controlRepository.findAll();
            assertThat(controls.size(), equalTo(3));
        });
    }
    private ControlTodoList createControlTodoList() {
        ControlTodoList controlTodoList = new ControlTodoList();
        controlTodoList.setOperation(dataset.operation_3_todo_list);
        controlTodoList.setTodoList(dataset.todoList_1);
        controlTodoList.setValue(EnumTodoListValue.OK);
        controlTodoList.setDrt(dataset.drt_example);
        return controlTodoList;
    }

    private ControlVisual createControlVisual() {
        ControlVisual controlVisual = new ControlVisual();
        controlVisual.setRoutingFunctionalAreaPost(dataSetInitializer.createRoutingFunctionalAreaPost());
        controlVisual.setValue(true);
        controlVisual.setDrt(dataset.drt_example);
        return controlVisual;
    }

    private ControlRoutingComponent createControlRoutingComponent() {
        ControlRoutingComponent controlRoutingComponent = new ControlRoutingComponent();
        controlRoutingComponent.setRoutingFunctionalAreaPost(dataSetInitializer.createRoutingFunctionalAreaPost());
        controlRoutingComponent.setValue(1F);
        controlRoutingComponent.setDrt(dataset.drt_example);
        return controlRoutingComponent;
    }



}
