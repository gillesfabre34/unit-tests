package com.airbus.retex.service.routingComponent;

import java.util.Locale;

import com.airbus.retex.business.dto.PageDto;
import com.airbus.retex.business.dto.routingComponent.RoutingComponentFilteringDto;
import com.airbus.retex.business.dto.routingComponent.RoutingComponentIndexDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.exception.NotFoundException;

public interface IRoutingComponentIndexService {
    /**
     * get routing components matching filters
     * @param filtering
     * @return
     */
    PageDto<RoutingComponentIndexDto> findRoutingComponentsIndex(RoutingComponentFilteringDto filtering, Locale locale);


    /**
     * deleteVersion a routing
     * @param id
     */
    void deleteRoutingComponent(Long id) throws NotFoundException, FunctionalException;
}
