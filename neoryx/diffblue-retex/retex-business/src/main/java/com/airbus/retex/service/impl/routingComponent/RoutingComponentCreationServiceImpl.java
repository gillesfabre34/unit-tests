package com.airbus.retex.service.impl.routingComponent;

import com.airbus.retex.business.dto.routingComponent.RoutingComponentCreateUpdateDto;
import com.airbus.retex.business.dto.routingComponent.RoutingComponentFullDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.exception.TechnicalError;
import com.airbus.retex.model.operation.OperationType;
import com.airbus.retex.model.routingComponent.RoutingComponentIndex;
import com.airbus.retex.service.routingComponent.IRoutingComponentCreationService;
import com.airbus.retex.service.routingComponent.IRoutingComponentHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class RoutingComponentCreationServiceImpl implements IRoutingComponentCreationService {

    @Autowired
    private List<IRoutingComponentHandlerService> routingComponentHandlers = new ArrayList<>();

    /**
     * Manage creation of RoutingComponent according to the given operationType
     *
     * @param operationType
     * @param routingComponentDto
     * @param published
     * @param routingComponentIndex
     * @return
     * @throws FunctionalException
     */
    @Override
    public RoutingComponentFullDto delegateRoutingComponentCreateOrUpdate(
            OperationType operationType,
            RoutingComponentCreateUpdateDto routingComponentDto,
            Boolean published,
            @Nullable RoutingComponentIndex routingComponentIndex
    ) throws FunctionalException {

        RoutingComponentFullDto result = null;
        int i = 0;
        int end = routingComponentHandlers.size();

        while (i < end && null == result) {
            if (routingComponentHandlers.get(i).supports(operationType)) {
                result = routingComponentHandlers.get(i).handle(routingComponentDto, published, routingComponentIndex);
            }
            i++;
        }

        if (null == result) {
            throw new TechnicalError("retex.error.no.routing.component.handler.found");
        }

        return result;
    }
}
