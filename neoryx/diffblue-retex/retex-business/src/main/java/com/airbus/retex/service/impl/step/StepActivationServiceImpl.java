package com.airbus.retex.service.impl.step;

import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.operation.OperationFunctionalArea;
import com.airbus.retex.model.operation.OperationTypeBehaviorEnum;
import com.airbus.retex.model.post.RoutingFunctionalAreaPost;
import com.airbus.retex.model.step.Step;
import com.airbus.retex.model.step.StepActivation;
import com.airbus.retex.service.step.IStepActivationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Transactional(rollbackFor = Exception.class)
public class StepActivationServiceImpl implements IStepActivationService {

    public void createAllStepActivations(OperationFunctionalArea operationFunctionalArea, Long operationTypeId) {
        List<Step> steps = getSteps(operationFunctionalArea, operationTypeId);

        for (Step step : steps) {
            StepActivation stepActivation = createStepActivation(step);
            operationFunctionalArea.addStepActivation(stepActivation);


            //Ajout d'un RoutingFunctionalAreaPost Vide pour les visual (pour la gestion du control Value)
            if(step.getRoutingComponent().getOperationType().isBehavior(OperationTypeBehaviorEnum.VISUAL_COMPONENT)) {
                RoutingFunctionalAreaPost routingFunctionalAreaPost = new RoutingFunctionalAreaPost();
                routingFunctionalAreaPost.setPost(step.getPosts().iterator().next());
                routingFunctionalAreaPost.setStepActivation(stepActivation);
                routingFunctionalAreaPost.setThreshold(0F);
                stepActivation.addRoutingFunctionalAreaPost(routingFunctionalAreaPost);
            }
        }
    }

    public void createMissingStepActivations(OperationFunctionalArea operationFunctionalArea, Long operationTypeId) {
        List<Step> steps = getSteps(operationFunctionalArea, operationTypeId);

        for (Step step : steps) {
            AtomicReference<StepActivation> stepActivationRef = new AtomicReference<>();

            operationFunctionalArea.getStepActivations().forEach(sa -> {
                if (sa.getStep().getNaturalId().equals(step.getNaturalId())) {
                    stepActivationRef.set(sa);
                }
            });

            if (null == stepActivationRef.get()) {
                StepActivation stepActivation = createStepActivation(step);
                operationFunctionalArea.addStepActivation(stepActivation);
            }
        }
    }

    /**
     * @param step
     * @return
     */
    private StepActivation createStepActivation(Step step) {
        StepActivation stepActivation = new StepActivation();
        stepActivation.setStep(step);
        stepActivation.setActivated(Boolean.TRUE);

        return stepActivation;
    }

    /**
     * @param operationFunctionalArea
     * @param operationTypeId
     * @return
     */
    private List<Step> getSteps(OperationFunctionalArea operationFunctionalArea, Long operationTypeId) {
        List<Step> steps = new ArrayList<>();

        operationFunctionalArea
                .getFunctionalArea()
                .getFunctionality()
                .getRoutingComponents()
                .forEach(rc -> {
                    if (rc.getRoutingComponentIndex().getStatus().equals(EnumStatus.VALIDATED)
                            && rc.getOperationType().getId().equals(operationTypeId)) {
                        steps.addAll(rc.getSteps());
                    }
                });

        return steps;
    }

}
