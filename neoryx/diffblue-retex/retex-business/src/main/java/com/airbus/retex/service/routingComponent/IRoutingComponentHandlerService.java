package com.airbus.retex.service.routingComponent;

import com.airbus.retex.business.dto.routingComponent.RoutingComponentCreateUpdateDto;
import com.airbus.retex.business.dto.routingComponent.RoutingComponentFullDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.model.operation.OperationType;
import com.airbus.retex.model.routingComponent.RoutingComponentIndex;
import org.springframework.lang.Nullable;

public interface IRoutingComponentHandlerService {
    /**
     * Returns true if the handler can handle the given operationType
     *
     * @param operationType
     * @return
     */
    boolean supports(OperationType operationType);

    /**
     * Handle creation and update
     *
     * @param routingComponentCreation
     * @param validated
     * @param existingRoutingComponentIndex
     * @return
     * @throws FunctionalException
     */
    RoutingComponentFullDto handle(RoutingComponentCreateUpdateDto routingComponentCreation,
                                   Boolean validated, @Nullable RoutingComponentIndex existingRoutingComponentIndex) throws FunctionalException;

}
