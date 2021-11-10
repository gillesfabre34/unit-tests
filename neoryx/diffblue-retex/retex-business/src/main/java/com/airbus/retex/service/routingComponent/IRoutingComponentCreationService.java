package com.airbus.retex.service.routingComponent;

import com.airbus.retex.business.dto.routingComponent.RoutingComponentCreateUpdateDto;
import com.airbus.retex.business.dto.routingComponent.RoutingComponentFullDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.model.operation.OperationType;
import com.airbus.retex.model.routingComponent.RoutingComponentIndex;
import org.springframework.lang.Nullable;

public interface IRoutingComponentCreationService {
    /**
     * Manage creation of RoutingComponent according to the given operationType
     * Possible values: Preliminary, Closure, Visual, Dimensional, Tridimensional, Laboratory
     *
     * @param operationType
     * @param routingComponentCreationDto
     */
    RoutingComponentFullDto delegateRoutingComponentCreateOrUpdate(
            OperationType operationType,
            RoutingComponentCreateUpdateDto routingComponentCreationDto,
            Boolean published,
            @Nullable RoutingComponentIndex routingComponentIndex
    ) throws FunctionalException;

}
