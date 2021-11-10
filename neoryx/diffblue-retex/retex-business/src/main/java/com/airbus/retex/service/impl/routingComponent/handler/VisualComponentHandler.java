package com.airbus.retex.service.impl.routingComponent.handler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.airbus.retex.business.dto.routingComponent.RoutingComponentCreateUpdateDto;
import com.airbus.retex.business.dto.routingComponent.RoutingComponentFullDto;
import com.airbus.retex.business.dto.step.StepCreationDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.mesureUnit.MeasureUnit;
import com.airbus.retex.model.operation.OperationType;
import com.airbus.retex.model.operation.OperationTypeBehaviorEnum;
import com.airbus.retex.model.post.Post;
import com.airbus.retex.model.routingComponent.RoutingComponentIndex;
import com.airbus.retex.persistence.measureUnit.MeasureUnitRepository;
import com.airbus.retex.persistence.routingComponent.RoutingComponentRepository;
import com.airbus.retex.service.routingComponent.IRoutingComponentService;

@Service
public class VisualComponentHandler extends RoutingComponentHandler {

    @Autowired
    private RoutingComponentRepository routingComponentRepository;
    @Autowired
    private IRoutingComponentService routingComponentService;
    @Autowired
    private MeasureUnitRepository measureUnitRepository;

    @Override
    public RoutingComponentFullDto handle(RoutingComponentCreateUpdateDto routingComponentCreation, Boolean validated, RoutingComponentIndex existingRoutingComponentIndex) throws FunctionalException {


        routingComponentCreation.setStatus(validated ? EnumStatus.VALIDATED : EnumStatus.CREATED);

        RoutingComponentIndex routingComponentIndex = createOrUpdateRoutingComponentIndexedEntry(routingComponentCreation, existingRoutingComponentIndex);

        return routingComponentService.buildRoutingComponentDto(routingComponentIndex,
                routingComponentIndex.getRoutingComponent(),
                routingComponentIndex.getRoutingComponent().getInspection().getValue(),
                routingComponentIndex.getRoutingComponent().getFunctionality().getId(),
                routingComponentIndex.getRoutingComponent().getDamageId(),
                routingComponentIndex.getNaturalId());

    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean supports(OperationType operationType) {
        return operationType.isBehavior(OperationTypeBehaviorEnum.VISUAL_COMPONENT);
    }

    @Override
    protected void mapDtoToVersion(RoutingComponentCreateUpdateDto updateDto, RoutingComponentIndex version) throws FunctionalException {
        version.setStatus(updateDto.getStatus());
        prepareRoutingComponentForUpdateOrCreate(version.getRoutingComponent(), updateDto);
        // save different given steps
        List<StepCreationDto> steps = updateDto.getSteps();
        if (steps != null && steps.size() != 1) {
            throw new FunctionalException("retex.rc.generic.nb.steps.not.accepted");
        }
        if (null == steps || CollectionUtils.isEmpty(steps)) {
            throw new FunctionalException("retex.routing.component.steps.list.empty");
        }
        associateEmptyStepAndAdditionalInformations(steps.get(0), version);

        //associate a Post for add Control Value
        if(null == version.getRoutingComponent().getSteps().get(0).getPosts() || version.getRoutingComponent().getSteps().get(0).getPosts().isEmpty()) {
            Post post = new Post();
            List<MeasureUnit> measureUnitsList = measureUnitRepository.findAll();
            post.setMeasureUnit(measureUnitsList.get(0));
            post.setMeasureUnitId(measureUnitsList.get(0).getId());
            Set<Post> postlist = new HashSet<>();
            postlist.add(post);
            version.getRoutingComponent().getSteps().get(0).setPosts(postlist);
        }
    }

}
