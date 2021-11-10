package com.airbus.retex.service.routingComponent;

import com.airbus.retex.model.routingComponent.RoutingComponentIndex;
import org.springframework.data.domain.Page;

import java.util.Locale;

public interface NameComputeService {

    void computeNames(Page<RoutingComponentIndex> pageItemRoutingComponentsIndex, Locale locale);
}
