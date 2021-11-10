package com.airbus.retex.service.impl.routingComponent.handler;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.airbus.retex.business.dto.routingComponent.RoutingComponentCreateUpdateDto;
import com.airbus.retex.business.dto.routingComponent.RoutingComponentFullDto;
import com.airbus.retex.business.dto.step.StepCreationDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.operation.OperationType;
import com.airbus.retex.model.operation.OperationTypeBehaviorEnum;
import com.airbus.retex.model.routingComponent.RoutingComponentIndex;
import com.airbus.retex.service.routingComponent.IRoutingComponentHandlerService;
import com.airbus.retex.service.routingComponent.IRoutingComponentService;

/**
 * In this case, we are going to create a routing component without operation_id, inspection_id, task_id, subtask_id
 * We will save only routingcomponent's informations/name.
 * We are going to create an empty step with empty posts
 * if update, set only informations and files of the step
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class GenericComponentHandler extends AbstractHandler implements IRoutingComponentHandlerService {

    @Autowired
    private IRoutingComponentService routingComponentService;

    /**
     * @inheritDoc
     */
    @Override
    public RoutingComponentFullDto handle(RoutingComponentCreateUpdateDto routingComponentCreationDto,
                                          Boolean validated, @Nullable RoutingComponentIndex existingRoutingComponentIndex) throws FunctionalException {

        routingComponentCreationDto.setStatus(validated ? EnumStatus.VALIDATED : EnumStatus.CREATED);

        RoutingComponentIndex routingComponentIndex = createOrUpdateRoutingComponentIndexedEntry(routingComponentCreationDto, existingRoutingComponentIndex);

        return routingComponentService.buildRoutingComponentDto(
                routingComponentIndex,
                routingComponentIndex.getRoutingComponent(),
                null,
                null,
                null,
                routingComponentIndex.getNaturalId());
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean supports(OperationType operationType) {
        return operationType.isBehavior(OperationTypeBehaviorEnum.GENERIC);
    }

    @Override
    protected void mapDtoToVersion(RoutingComponentCreateUpdateDto updateDto, RoutingComponentIndex version) throws FunctionalException {
        version.setStatus(updateDto.getStatus());
        addOperationType(updateDto, version.getRoutingComponent());

        // by default create a an empty step, without posts
        // we will use just to associated different additional information of the component
        List<StepCreationDto> steps = updateDto.getSteps();
        if (null == steps || CollectionUtils.isEmpty(steps)) {
            throw new FunctionalException("retex.routing.component.steps.list.empty");
        }
        associateEmptyStepAndAdditionalInformations(steps.get(0), version);

    }

}
