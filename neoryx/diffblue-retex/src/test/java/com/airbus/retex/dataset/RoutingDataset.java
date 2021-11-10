package com.airbus.retex.dataset;

import com.airbus.retex.model.operation.Operation;
import com.airbus.retex.model.operation.OperationFunctionalArea;
import com.airbus.retex.model.post.RoutingFunctionalAreaPost;
import com.airbus.retex.model.routing.Routing;
import com.airbus.retex.model.step.StepActivation;


public class RoutingDataset {

    public PartDataset partDataset;

    public Operation operationDimensional;
    public OperationFunctionalArea operationFunctionalAreaDimensional;
    public StepActivation stepActivationDimensional;
    public RoutingFunctionalAreaPost routingFunctionalAreaPostDimensional;

    public Operation operationLaboratory;
    public OperationFunctionalArea operationFunctionalAreaLaboratory;
    public StepActivation stepActivationLaboratory;
    public RoutingFunctionalAreaPost routingFunctionalAreaPostLaboratory;

    public Operation operationTridimensional;
    public OperationFunctionalArea operationFunctionalAreaTridimensional;
    public StepActivation stepActivationTridimensional;
    public RoutingFunctionalAreaPost routingFunctionalAreaPostTridimensional;

    public Operation operationVisual;
    public OperationFunctionalArea operationFunctionalAreaVisual;
    public StepActivation stepActivationVisual;
    public RoutingFunctionalAreaPost routingFunctionalAreaPostVisual;


    public Operation operationPreliminary;
    public Operation operationClosure;

    public Routing routing;

}
