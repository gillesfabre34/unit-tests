package com.airbus.retex.dataset;

import com.airbus.retex.model.post.Post;
import com.airbus.retex.model.routingComponent.RoutingComponent;
import com.airbus.retex.model.routingComponent.RoutingComponentIndex;
import com.airbus.retex.model.step.Step;
import com.airbus.retex.model.todoList.TodoList;

public class RoutingComponentDataset {

    public DamageDataset damageDataset;

    /**Dimensional**/
    public RoutingComponentIndex routingComponentIndexDimensional;
    public RoutingComponent routingComponentDimensional;
    public Step stepRoutingComponentDimensional;
    public Post postRoutingComponentDimensional;


    /**Laboratory**/
    public RoutingComponentIndex routingComponentIndexLaboratory;
    public RoutingComponent routingComponentLaboratory;
    public Step stepRoutingComponentLaboratory;
    public Post postRoutingComponentLaboratory;


    /**Tridimensional**/
    public RoutingComponentIndex routingComponentIndexTridimensional;
    public RoutingComponent routingComponentTridimensional;
    public Step stepRoutingComponentTridimensional;
    public Post postRoutingComponentTridimensional;


    /**Visual**/
    public RoutingComponentIndex routingComponentIndexVisual;
    public RoutingComponent routingComponentVisual;
    public Step stepRoutingComponentVisual;
    public Post postRoutingComponentVisual;


    /**Preliminary**/
    public RoutingComponentIndex routingComponentIndexPreliminary;
    public TodoList todoListPreliminary;
    public Step stepTodoListPreliminary;


    /**Closure**/
    public RoutingComponentIndex routingComponentIndexClosure;
    public TodoList todoListClosure;
    public Step stepTodoListClosure;

}
