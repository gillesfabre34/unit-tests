package com.airbus.retex.dataset;

import com.airbus.retex.model.common.EnumActiveState;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.common.StepType;
import com.airbus.retex.model.todoList.TodoList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.TreeSet;

@Component
public class DatasetFactory {

    @Autowired
    DatasetInitializer datasetInitializer;

    public DamageDataset createDamageDataset(){
        DamageDataset damageDataset = new DamageDataset();
        damageDataset.functionality =  datasetInitializer.createFunctionality();
        damageDataset.damage = datasetInitializer.createDamage(EnumActiveState.ACTIVE, "test");
        damageDataset.functionalityDamage = datasetInitializer.createFunctionalityDamage(damageDataset.functionality, damageDataset.damage);
        return damageDataset;
    }

    public RoutingComponentDataset createRoutingComponentDataset(){
        RoutingComponentDataset routingComponentDataset = new RoutingComponentDataset();
        routingComponentDataset.damageDataset = createDamageDataset();

        /***ROUTING COMPONENT**/
        routingComponentDataset.routingComponentDimensional = datasetInitializer.createRoutingComponent(routingComponent1 -> {
            routingComponent1.setDamageId(routingComponentDataset.damageDataset.damage.getNaturalId());
            routingComponent1.setFunctionality(routingComponentDataset.damageDataset.functionality);
            routingComponent1.setOperationType(datasetInitializer.getDataset().operationType_dimensional);
        });
        routingComponentDataset.routingComponentIndexDimensional = datasetInitializer.createRoutingComponentIndex(routingComponentDataset.routingComponentDimensional.getTechnicalId(), null);
        routingComponentDataset.stepRoutingComponentDimensional = datasetInitializer.createStep(step -> {
            step.setRoutingComponent(routingComponentDataset.routingComponentDimensional);
            step.setFiles(Set.of(datasetInitializer.createMedia()));
            step.setType(StepType.AUTO);
        });
        routingComponentDataset.postRoutingComponentDimensional = datasetInitializer.createPost(post -> post.setStep(routingComponentDataset.stepRoutingComponentDimensional), "postFR","postEN");

        routingComponentDataset.routingComponentLaboratory = datasetInitializer.createRoutingComponent(routingComponent1 -> {
            routingComponent1.setDamageId(routingComponentDataset.damageDataset.damage.getNaturalId());
            routingComponent1.setFunctionality(routingComponentDataset.damageDataset.functionality);
            routingComponent1.setOperationType(datasetInitializer.getDataset().operationType_laboratory);
        });
        routingComponentDataset.routingComponentIndexLaboratory = datasetInitializer.createRoutingComponentIndex(routingComponentDataset.routingComponentLaboratory.getTechnicalId(), null);
        routingComponentDataset.stepRoutingComponentLaboratory = datasetInitializer.createStep(step -> {
            step.setRoutingComponent(routingComponentDataset.routingComponentLaboratory);
            step.setFiles(Set.of(datasetInitializer.createMedia()));
        });
        routingComponentDataset.postRoutingComponentLaboratory = datasetInitializer.createPost(post -> post.setStep(routingComponentDataset.stepRoutingComponentLaboratory), "postFR","postEN");


        routingComponentDataset.routingComponentTridimensional = datasetInitializer.createRoutingComponent(routingComponent1 -> {
            routingComponent1.setDamageId(routingComponentDataset.damageDataset.damage.getNaturalId());
            routingComponent1.setFunctionality(routingComponentDataset.damageDataset.functionality);
            routingComponent1.setOperationType(datasetInitializer.getDataset().operationType_tridimensional);
        });
        routingComponentDataset.routingComponentIndexTridimensional = datasetInitializer.createRoutingComponentIndex(routingComponentDataset.routingComponentTridimensional.getTechnicalId(), null);
        routingComponentDataset.stepRoutingComponentTridimensional = datasetInitializer.createStep(step -> {
            step.setRoutingComponent(routingComponentDataset.routingComponentTridimensional);
            step.setFiles(Set.of(datasetInitializer.createMedia()));
        });
        routingComponentDataset.postRoutingComponentTridimensional = datasetInitializer.createPost(post -> post.setStep(routingComponentDataset.stepRoutingComponentTridimensional), "postFR","postEN");


        /**VISUAL**/
        routingComponentDataset.routingComponentVisual = datasetInitializer.createRoutingComponent(routingComponent1 -> {
            routingComponent1.setDamageId(routingComponentDataset.damageDataset.damage.getNaturalId());
            routingComponent1.setFunctionality(routingComponentDataset.damageDataset.functionality);
            routingComponent1.setOperationType(datasetInitializer.getDataset().operationType_visual);
        });
        routingComponentDataset.routingComponentIndexVisual = datasetInitializer.createRoutingComponentIndex(routingComponentDataset.routingComponentVisual.getTechnicalId(), null);
        routingComponentDataset.stepRoutingComponentVisual = datasetInitializer.createStep(step -> {
            step.setRoutingComponent(routingComponentDataset.routingComponentVisual);
            step.setFiles(Set.of(datasetInitializer.createMedia()));
        });
        routingComponentDataset.postRoutingComponentVisual = datasetInitializer.createPost(post -> post.setStep(routingComponentDataset.stepRoutingComponentVisual), "postFR","postEN");


        /**TODOLIST**/
        routingComponentDataset.todoListPreliminary = datasetInitializer.createTodoList(todoList -> {
            todoList.setOperationType(datasetInitializer.getDataset().operationType_preliminary);
        });
        routingComponentDataset.routingComponentIndexPreliminary = datasetInitializer.createRoutingComponentIndex(null, routingComponentDataset.todoListPreliminary.getTechnicalId());
        routingComponentDataset.stepTodoListPreliminary = datasetInitializer.createStep(step -> {
            step.setTodoList(routingComponentDataset.todoListPreliminary);
            step.setFiles(Set.of(datasetInitializer.createMedia()));
        });


        routingComponentDataset.todoListClosure = datasetInitializer.createTodoList(todoList -> {
            todoList.setOperationType(datasetInitializer.getDataset().operationType_closure);
        });
        routingComponentDataset.routingComponentIndexClosure = datasetInitializer.createRoutingComponentIndex(null, routingComponentDataset.todoListClosure.getTechnicalId());
        routingComponentDataset.stepTodoListClosure = datasetInitializer.createStep(step -> {
            step.setTodoList(routingComponentDataset.todoListClosure);
            step.setFiles(Set.of(datasetInitializer.createMedia()));
        });
        return routingComponentDataset;
    }



    public PartDataset createPartDataset(){
        PartDataset partDataset = new PartDataset();
        partDataset.routingComponentDataset = createRoutingComponentDataset();

        partDataset.mpn = datasetInitializer.createMpn();
        partDataset.part = datasetInitializer.createPart(Set.of(partDataset.mpn));
        partDataset.functionalArea = datasetInitializer.createFunctionalArea(functionalArea1 -> {
            functionalArea1.setPart(partDataset.part);
            functionalArea1.setFunctionality(partDataset.routingComponentDataset.damageDataset.functionality);
        });
        return partDataset;
    }


    public RoutingDataset createRoutingDataset(){
        return createRoutingDataset(true);
    }

    public RoutingDataset createRoutingDataset(boolean withOperation){
        RoutingDataset routingDataset = new RoutingDataset();
        routingDataset.partDataset = createPartDataset();

        int operationNumber = 1;
        routingDataset.routing = datasetInitializer.createRouting(routingDataset.partDataset.part);

        if(withOperation) {
            //Dimensional
            routingDataset.operationDimensional = datasetInitializer.createOperation(operationNumber++, operation -> {
                operation.setOperationType(datasetInitializer.getDataset().operationType_dimensional);
                operation.setRouting(routingDataset.routing);
            });
            routingDataset.operationFunctionalAreaDimensional = datasetInitializer.createOperationFunctionalArea(operationFunctionalArea -> {
                operationFunctionalArea.setOperation(routingDataset.operationDimensional);
                operationFunctionalArea.setFunctionalArea(routingDataset.partDataset.functionalArea);
            });
            routingDataset.operationDimensional.addOperationFunctionalArea(routingDataset.operationFunctionalAreaDimensional);
            routingDataset.stepActivationDimensional = datasetInitializer.createStepActivation(true, routingDataset.partDataset.routingComponentDataset.stepRoutingComponentDimensional, routingDataset.operationFunctionalAreaDimensional);
            routingDataset.routingFunctionalAreaPostDimensional = datasetInitializer.createRoutingFunctionalAreaPost(routingFunctionalAreaPost -> {
                routingFunctionalAreaPost.setStepActivation(routingDataset.stepActivationDimensional);
                routingFunctionalAreaPost.setPost(routingDataset.partDataset.routingComponentDataset.postRoutingComponentDimensional);
            });


            //LABORATORY
            routingDataset.operationLaboratory = datasetInitializer.createOperation(operationNumber++, operation -> {
                operation.setOperationType(datasetInitializer.getDataset().operationType_laboratory);
                operation.setRouting(routingDataset.routing);
            });
            routingDataset.operationFunctionalAreaLaboratory = datasetInitializer.createOperationFunctionalArea(operationFunctionalArea -> {
                operationFunctionalArea.setOperation(routingDataset.operationLaboratory);
                operationFunctionalArea.setFunctionalArea(routingDataset.partDataset.functionalArea);
            });
            routingDataset.operationLaboratory.addOperationFunctionalArea(routingDataset.operationFunctionalAreaLaboratory);
            routingDataset.stepActivationLaboratory = datasetInitializer.createStepActivation(true, routingDataset.partDataset.routingComponentDataset.stepRoutingComponentLaboratory, routingDataset.operationFunctionalAreaLaboratory);
            routingDataset.routingFunctionalAreaPostLaboratory = datasetInitializer.createRoutingFunctionalAreaPost(routingFunctionalAreaPost -> {
                routingFunctionalAreaPost.setStepActivation(routingDataset.stepActivationLaboratory);
                routingFunctionalAreaPost.setPost(routingDataset.partDataset.routingComponentDataset.postRoutingComponentLaboratory);
            });

            //Tridimensional
            routingDataset.operationTridimensional = datasetInitializer.createOperation(operationNumber++, operation -> {
                operation.setOperationType(datasetInitializer.getDataset().operationType_tridimensional);
                operation.setRouting(routingDataset.routing);
            });
            routingDataset.operationFunctionalAreaTridimensional = datasetInitializer.createOperationFunctionalArea(operationFunctionalArea -> {
                operationFunctionalArea.setOperation(routingDataset.operationTridimensional);
                operationFunctionalArea.setFunctionalArea(routingDataset.partDataset.functionalArea);
            });
            routingDataset.operationTridimensional.addOperationFunctionalArea(routingDataset.operationFunctionalAreaTridimensional);
            routingDataset.stepActivationTridimensional = datasetInitializer.createStepActivation(true, routingDataset.partDataset.routingComponentDataset.stepRoutingComponentTridimensional, routingDataset.operationFunctionalAreaTridimensional);
            routingDataset.routingFunctionalAreaPostTridimensional = datasetInitializer.createRoutingFunctionalAreaPost(routingFunctionalAreaPost -> {
                routingFunctionalAreaPost.setStepActivation(routingDataset.stepActivationTridimensional);
                routingFunctionalAreaPost.setPost(routingDataset.partDataset.routingComponentDataset.postRoutingComponentTridimensional);
            });

            //Visual
            routingDataset.operationVisual = datasetInitializer.createOperation(operationNumber++, operation -> {
                operation.setOperationType(datasetInitializer.getDataset().operationType_visual);
                operation.setRouting(routingDataset.routing);
            });
            routingDataset.operationFunctionalAreaVisual = datasetInitializer.createOperationFunctionalArea(operationFunctionalArea -> {
                operationFunctionalArea.setOperation(routingDataset.operationVisual);
                operationFunctionalArea.setFunctionalArea(routingDataset.partDataset.functionalArea);
            });
            routingDataset.operationVisual.addOperationFunctionalArea(routingDataset.operationFunctionalAreaVisual);
            routingDataset.stepActivationVisual = datasetInitializer.createStepActivation(true, routingDataset.partDataset.routingComponentDataset.stepRoutingComponentVisual, routingDataset.operationFunctionalAreaVisual);
            routingDataset.routingFunctionalAreaPostVisual = datasetInitializer.createRoutingFunctionalAreaPost(routingFunctionalAreaPost -> {
                routingFunctionalAreaPost.setStepActivation(routingDataset.stepActivationVisual);
                routingFunctionalAreaPost.setPost(routingDataset.partDataset.routingComponentDataset.postRoutingComponentVisual);
            });


            /**TODOLIST**/
            //Preliminary
            routingDataset.operationPreliminary = datasetInitializer.createOperation(operationNumber++, operation -> {
                operation.setOperationType(datasetInitializer.getDataset().operationType_preliminary);
                operation.setRouting(routingDataset.routing);
                operation.addTodoList(routingDataset.partDataset.routingComponentDataset.todoListPreliminary);
            });

            //Closure
            routingDataset.operationClosure = datasetInitializer.createOperation(operationNumber++, operation -> {
                operation.setOperationType(datasetInitializer.getDataset().operationType_closure);
                operation.setRouting(routingDataset.routing);
                operation.addTodoList(routingDataset.partDataset.routingComponentDataset.todoListClosure);
            });

        }

        return routingDataset;
    }

    public RequestDataset createRequestDataset(){
        RequestDataset requestDataset = new RequestDataset();
        requestDataset.routingDataset = createRoutingDataset();

        requestDataset.parentRequest = datasetInitializer.createRequest("TestRequest", request -> {
            request.getAta().setCode(requestDataset.routingDataset.partDataset.part.getAta().getCode());
        });
        requestDataset.childRequest = datasetInitializer.createChildRequest(childRequest -> {
            childRequest.setParentRequest(requestDataset.parentRequest);
            childRequest.setRoutingNaturalId(requestDataset.routingDataset.routing.getNaturalId());
            childRequest.setRouting(requestDataset.routingDataset.routing);

        });
        requestDataset.physicalPart = datasetInitializer.createPhysicalPart(physicalPart -> {
            physicalPart.setChildRequest(requestDataset.childRequest);
            physicalPart.setPart(requestDataset.routingDataset.partDataset.part);
        });

        return requestDataset;
    }


    public DrtDataset createDrtDataset(){
        DrtDataset drtDataset = new DrtDataset();
        drtDataset.requestDataset = createRequestDataset();

        drtDataset.drt = datasetInitializer.createDRT(drt -> {
            drt.setChildRequest(drtDataset.requestDataset.childRequest);
            drt.setRouting(drtDataset.requestDataset.routingDataset.routing);
            drt.setStatus(EnumStatus.IN_PROGRESS);
        });

        drtDataset.filtering = datasetInitializer.createFiltering(filtering -> {
            filtering.setPhysicalPart(drtDataset.requestDataset.physicalPart);
            filtering.setDrt(drtDataset.drt);
        });

        drtDataset.controlDimensional = datasetInitializer.createControlRoutingComponent(controlRoutingComponent -> {
            controlRoutingComponent.setDrt(drtDataset.drt);
            controlRoutingComponent.setRoutingFunctionalAreaPost(drtDataset.requestDataset.routingDataset.routingFunctionalAreaPostDimensional);
        });

        drtDataset.controlTriDimensional = datasetInitializer.createControlRoutingComponent(controlRoutingComponent -> {
            controlRoutingComponent.setDrt(drtDataset.drt);
            controlRoutingComponent.setRoutingFunctionalAreaPost(drtDataset.requestDataset.routingDataset.routingFunctionalAreaPostTridimensional);
        });

        drtDataset.controlLaboratory = datasetInitializer.createControlRoutingComponent(controlRoutingComponent -> {
            controlRoutingComponent.setDrt(drtDataset.drt);
            controlRoutingComponent.setRoutingFunctionalAreaPost(drtDataset.requestDataset.routingDataset.routingFunctionalAreaPostLaboratory);
        });

        drtDataset.controlVisual = datasetInitializer.createControlControlVisual(controlRoutingComponent -> {
            controlRoutingComponent.setDrt(drtDataset.drt);
            controlRoutingComponent.setRoutingFunctionalAreaPost(drtDataset.requestDataset.routingDataset.routingFunctionalAreaPostVisual);
        });

        drtDataset.controlPremiminary = datasetInitializer.createControlControlTodoList(controlRoutingComponent -> {
            controlRoutingComponent.setDrt(drtDataset.drt);
            controlRoutingComponent.setOperation(drtDataset.requestDataset.routingDataset.operationPreliminary);
            controlRoutingComponent.setTodoList(drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.todoListPreliminary);
        });

        drtDataset.controlClosure = datasetInitializer.createControlControlTodoList(controlRoutingComponent -> {
            controlRoutingComponent.setDrt(drtDataset.drt);
            controlRoutingComponent.setOperation(drtDataset.requestDataset.routingDataset.operationClosure);
            controlRoutingComponent.setTodoList(drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.todoListClosure);
        });

        drtDataset.drtPicturesDimensional = datasetInitializer.createDrtPictures(drtDataset.drt, drtDataset.requestDataset.routingDataset.stepActivationDimensional);

        drtDataset.drtPicturesTriDimensional = datasetInitializer.createDrtPictures(drtDataset.drt, drtDataset.requestDataset.routingDataset.stepActivationTridimensional);

        drtDataset.drtPicturesLaboratory = datasetInitializer.createDrtPictures(drtDataset.drt, drtDataset.requestDataset.routingDataset.stepActivationLaboratory);

        drtDataset.drtOperationStatusDimensional = datasetInitializer.createDrtOperationStatusFunctionalArea(drtDataset.drt, drtDataset.requestDataset.routingDataset.operationFunctionalAreaDimensional);
        drtDataset.drtOperationStatusTriDimensional = datasetInitializer.createDrtOperationStatusFunctionalArea(drtDataset.drt, drtDataset.requestDataset.routingDataset.operationFunctionalAreaTridimensional);
        drtDataset.drtOperationStatusLaboratory = datasetInitializer.createDrtOperationStatusFunctionalArea(drtDataset.drt, drtDataset.requestDataset.routingDataset.operationFunctionalAreaLaboratory );
        drtDataset.drtOperationStatusVisual = datasetInitializer.createDrtOperationStatusFunctionalArea(drtDataset.drt, drtDataset.requestDataset.routingDataset.operationFunctionalAreaVisual);

        drtDataset.drtOperationStatusPreliminary = datasetInitializer.createDrtOperationStatusTodoList(drtDataset.drt, drtDataset.requestDataset.routingDataset.operationPreliminary, drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.todoListPreliminary);
        drtDataset.drtOperationStatusClosure = datasetInitializer.createDrtOperationStatusTodoList(drtDataset.drt, drtDataset.requestDataset.routingDataset.operationClosure, drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.todoListClosure);


        return drtDataset;
    }
}
