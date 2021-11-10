package com.airbus.retex.service.routingComponent;

import com.airbus.retex.business.dto.routingComponent.RoutingComponentFilterDto;
import com.airbus.retex.business.dto.routingComponent.RoutingComponentFilterRequestDto;

public interface IRoutingComponentFiltersService {

    RoutingComponentFilterDto getRoutingComponentFilters(RoutingComponentFilterRequestDto filters);
}
