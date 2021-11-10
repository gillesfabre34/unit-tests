package com.airbus.retex.service.impl.drt.mapper;

import com.airbus.retex.business.dto.inspection.InspectionQCheckValueDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.mapper.AbstractMapper;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.drt.Drt;
import com.airbus.retex.model.drt.DrtOperationStatusFunctionalArea;
import com.airbus.retex.model.operation.Operation;
import com.airbus.retex.model.qcheck.QcheckRoutingComponent;
import com.airbus.retex.model.routingComponent.RoutingComponentIndex;
import com.airbus.retex.model.step.StepActivation;
import com.airbus.retex.service.impl.util.RetexStreamUtil;
import com.airbus.retex.service.translate.ITranslateService;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class DrtQCheckMapper extends AbstractMapper {

    @Autowired
    private ITranslateService translateService;
    @Autowired
    private DrtDtoMapper drtDtoMapper;

    public void updateQcheckDrt(@MappingTarget Drt drt, Long operationId, Long taskId, List<InspectionQCheckValueDto> inspectionQCheckValueDtoList, boolean validate) throws FunctionalException {
        //CURRENT DrtOperationStatusFunctionalArea IN QCHECK STATUS
        DrtOperationStatusFunctionalArea drtOperationStatusFunctionalArea = getStatusOperationQcheck(drt, operationId, taskId);

        //LIST OF Q_CHECK ALREADY DONE
        Set<QcheckRoutingComponent> qcheckRoutingComponentSet = getQcheckRoutingComponent(drt, operationId, taskId);

        //LIST OF RC TO BE QCHECK
        Set<RoutingComponentIndex> routingComponentIndexSet = getAllRoutingComponentIndex(drt, operationId, taskId);

        //MAP of Q_CHECK TO DO
        Map<Long, Boolean> mapInspectionQcheck = inspectionQCheckValueDtoList.stream()
                .filter(inspectionQCheckValueDto -> inspectionQCheckValueDto.getIdRoutingComponentIndex() != null)
                .collect(Collectors.toMap(InspectionQCheckValueDto::getIdRoutingComponentIndex, InspectionQCheckValueDto::getQCheckValue));

        for(RoutingComponentIndex routingComponentIndex : routingComponentIndexSet) {
            if(mapInspectionQcheck.containsKey(routingComponentIndex.getNaturalId())) {
                //Search for an existing Qcheck
                Optional<QcheckRoutingComponent> qcheckRoutingComponentOptional = qcheckRoutingComponentSet.stream()
                        .filter(qcheckRoutingComponent1 -> qcheckRoutingComponent1.getRoutingComponentIndex().getNaturalId().equals(routingComponentIndex.getNaturalId()))
                        .collect(RetexStreamUtil.findOneOrEmpty());

                // IF Q_CHECK IS DONE
                if(qcheckRoutingComponentOptional.isPresent()) {
                    qcheckRoutingComponentOptional.get().setValue(mapInspectionQcheck.get(routingComponentIndex.getNaturalId()));
                } else if(mapInspectionQcheck.get(routingComponentIndex.getNaturalId()) != null) { //NEW Q_CHECK
                    drt.addQcheckRoutingComponent(new QcheckRoutingComponent(routingComponentIndex, drt, drtOperationStatusFunctionalArea.getOperationFunctionalArea(), mapInspectionQcheck.get(routingComponentIndex.getNaturalId())));
                }
            }
        }

        if(validate) {
            drtOperationStatusFunctionalArea.setStatus(isQcheckDone(drt, routingComponentIndexSet, operationId, taskId) ? EnumStatus.VALIDATED : EnumStatus.IN_PROGRESS);
        } else {
            drtOperationStatusFunctionalArea.setStatus(EnumStatus.Q_CHECK);
        }
    }

    private boolean isQcheckDone(Drt drt, Set<RoutingComponentIndex> routingComponentIndexSet, Long operationId, Long taskId) throws FunctionalException {
        Set<QcheckRoutingComponent> qcheckRoutingComponentSet = getQcheckRoutingComponent(drt, operationId, taskId);

        if(qcheckRoutingComponentSet.size() == routingComponentIndexSet.size()){
            return qcheckRoutingComponentSet.stream().allMatch(qcheckRoutingComponent -> Boolean.TRUE.equals(qcheckRoutingComponent.getValue()));
        }
        throw new FunctionalException("retex.error.drt.qcheck.missing");
    }

    private DrtOperationStatusFunctionalArea getStatusOperationQcheck(Drt drt, Long operationId, Long taskId) throws FunctionalException {
        Optional<DrtOperationStatusFunctionalArea> drtOperationStatusFunctionalAreaOptional = drt.getOperationStatus().stream()
                .filter(abstractDrtOperationStatus -> abstractDrtOperationStatus instanceof DrtOperationStatusFunctionalArea)
                .map(abstractDrtOperationStatus ->  (DrtOperationStatusFunctionalArea) abstractDrtOperationStatus)
                .filter(drtOperationStatusFunctionalArea -> drtOperationStatusFunctionalArea.getOperationFunctionalArea().getOperation().getNaturalId().equals(operationId)
                        && drtOperationStatusFunctionalArea.getOperationFunctionalArea().getFunctionalArea().getNaturalId().equals(taskId))
                .collect(RetexStreamUtil.findOneOrEmpty());

        if(drtOperationStatusFunctionalAreaOptional.isPresent()) {
            if(drtOperationStatusFunctionalAreaOptional.get().getStatus().equals(EnumStatus.Q_CHECK)) {
                return drtOperationStatusFunctionalAreaOptional.get();
            } else {
                throw new FunctionalException("retex.error.drt.qcheck.wrong.status");
            }
        } else {
            throw new IllegalStateException();
        }
    }

    private Set<QcheckRoutingComponent> getQcheckRoutingComponent(Drt drt, Long operationId, Long taskId) {
        return drt.getQCheckRoutingComponents().stream()
                .filter(qcheckRoutingComponent -> qcheckRoutingComponent.getOperationFunctionalArea().getOperation().getNaturalId().equals(operationId)
                        && qcheckRoutingComponent.getOperationFunctionalArea().getFunctionalArea().getNaturalId().equals(taskId)).collect(Collectors.toSet());
    }


    private Set<RoutingComponentIndex> getAllRoutingComponentIndex(Drt drt, Long operationId, Long taskId) throws FunctionalException {
        Operation operation = drtDtoMapper.getOperation(drt, operationId);
        Set<StepActivation> stepActivation = drtDtoMapper.getStepActivation(operation, taskId);

        return stepActivation.stream()
                .map(sa -> sa.getStep().getRoutingComponent().getRoutingComponentIndex())
                .filter(RetexStreamUtil.distinctBy(RoutingComponentIndex::getNaturalId))
                .collect(Collectors.toSet());
    }


}