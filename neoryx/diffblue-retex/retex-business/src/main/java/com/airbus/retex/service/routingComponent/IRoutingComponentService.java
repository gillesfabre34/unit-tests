package com.airbus.retex.service.routingComponent;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import com.airbus.retex.business.dto.routingComponent.RoutingComponentCreateUpdateDto;
import com.airbus.retex.business.dto.routingComponent.RoutingComponentFullDto;
import com.airbus.retex.business.dto.step.StepFullDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.model.IRoutingComponentModel;
import com.airbus.retex.model.routingComponent.RoutingComponentIndex;

@Transactional
public interface IRoutingComponentService {
    /**
     * Returns Routing Component by id
     *
     * @param routingComponentIndexId
     * @return
     */
    Optional<RoutingComponentFullDto> getRoutingComponent(Long routingComponentIndexId, Long version);

    /**
     * Get Routing Component Dto and set attributes
     *
     * @param component
     * @param inspectionValue
     * @param taskId
     * @param subTaskId
     * @param routingComponentIndexId
     * @return
     */
    RoutingComponentFullDto buildRoutingComponentDto(RoutingComponentIndex routingComponentIndex, IRoutingComponentModel component, String inspectionValue, Long taskId, Long subTaskId, Long routingComponentIndexId);

    /**
     * Get routing component's steps
     *
     * @param routingComponentIndexId
     * @return
     */
    List<StepFullDto> getRoutingComponentSteps(Long routingComponentIndexId) throws FunctionalException;


    /**
     * Create a routing component
     *
     * @param routingComponentCreationDto
     * @return
     */
    RoutingComponentFullDto createRoutingComponent(RoutingComponentCreateUpdateDto routingComponentCreationDto, Boolean published) throws FunctionalException;

    /**
     * Update a routing component
     *
     * @param routingComponentUpdateDto
     * @return
     */
    RoutingComponentFullDto updateRoutingComponent(RoutingComponentCreateUpdateDto routingComponentUpdateDto, Long id, Boolean published) throws FunctionalException;

    /**
     * Update a routing component
     *
     * @param damageTechnicalId
     * @return boolean
     */
    boolean isDamageLinkedToRoutingComponent(Long damageTechnicalId);

}
