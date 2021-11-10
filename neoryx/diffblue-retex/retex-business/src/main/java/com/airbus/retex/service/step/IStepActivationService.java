package com.airbus.retex.service.step;

import com.airbus.retex.model.operation.OperationFunctionalArea;

public interface IStepActivationService {

    /**
     * Creates all StepActivation for all steps found.
     *
     * @param operationFunctionalArea
     * @param operationTypeId
     */
    void createAllStepActivations(OperationFunctionalArea operationFunctionalArea, Long operationTypeId);

    /**
     * Creates StepActivation only or steps not yet related to a stepActivation.
     *
     * @param operationFunctionalArea
     * @param operationTypeId
     */
    void createMissingStepActivations(OperationFunctionalArea operationFunctionalArea, Long operationTypeId);

}
