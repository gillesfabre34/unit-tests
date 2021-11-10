package com.airbus.retex.model;

import com.airbus.retex.model.basic.IModel;
import com.airbus.retex.model.inspection.Inspection;
import com.airbus.retex.model.operation.OperationType;
import com.airbus.retex.model.step.Step;

import java.util.List;

public interface IRoutingComponentModel extends IModel {
    /**
     * @return
     */
    Long getNaturalId();

    /**
     * @param operationType
     */
    void setOperationType(OperationType operationType);

    /**
     * @param inspection
     */
    void setInspection(Inspection inspection);

    List<Step> getSteps();
}
