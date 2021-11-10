package com.airbus.retex.routing;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.business.converter.DtoConverter;
import com.airbus.retex.business.dto.operationPost.RoutingFunctionalAreaPostDto;
import com.airbus.retex.business.dto.step.StepUpdateDto;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.common.StepType;
import com.airbus.retex.model.functional.FunctionalArea;
import com.airbus.retex.model.functionality.Functionality;
import com.airbus.retex.model.media.Media;
import com.airbus.retex.model.operation.Operation;
import com.airbus.retex.model.operation.OperationFunctionalArea;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.model.post.Post;
import com.airbus.retex.model.post.RoutingFunctionalAreaPost;
import com.airbus.retex.model.routing.Routing;
import com.airbus.retex.model.routingComponent.RoutingComponent;
import com.airbus.retex.model.routingComponent.RoutingComponentIndex;
import com.airbus.retex.model.step.Step;
import com.airbus.retex.model.step.StepActivation;
import com.airbus.retex.model.todoList.TodoList;
import com.airbus.retex.utils.ConstantUrl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Transactional
public class RoutingControllerInspectionDetailIT extends BaseControllerTest {

    private Operation operation;
    private FunctionalArea functionalArea;
    private RoutingComponent routingComponent;
    private RoutingFunctionalAreaPost routingFunctionalAreaPost;
    private Post post;
    private Step stepOne;
    private Part part;
    private Routing routing;

    private OperationFunctionalArea operationFunctionalAreaWithoutPost;

    private Operation operationTodoList;
    private TodoList todoList;


    private RoutingComponentIndex newRoutingComponentIndex;

    @Autowired
    private DtoConverter dtoConverter;

    @BeforeEach
    public void before() {

        runInTransaction(() -> {
            // CREATION ROUTING
            part = dataSetInitializer.createPart(null);
            operation = dataSetInitializer.createOperation(1);
            routing = dataSetInitializer.createRouting(r -> r.addOperation(operation), part);
            Functionality functionality = dataSetInitializer.createFunctionality();
            functionalArea = dataSetInitializer.createFunctionalArea(functionalArea -> {
                functionalArea.setPart(part);
                functionalArea.setFunctionality(functionality);
            });
            part.addFunctionalAreas(functionalArea);
            routingComponent = dataSetInitializer.createRoutingComponent(rc -> {
                rc.setFunctionality(functionality);
                rc.setOperationType(operation.getOperationType());
                rc.setDamageId(dataset.damage_corrosion.getNaturalId());
                rc.setDamage(dataset.damage_corrosion);
                rc.setInspection(dataset.inspection_internal);
            });
            functionality.addRoutingComponent(routingComponent);

            dataSetInitializer.createRoutingComponentIndex(routingComponent.getTechnicalId(), null);

            RoutingComponent routingComponentNew = dataSetInitializer.createRoutingComponent(rc -> {
                rc.setFunctionality(functionality);
                rc.setOperationType(operation.getOperationType());
                rc.setDamageId(dataset.damage_crack.getNaturalId());
                rc.setDamage(dataset.damage_crack);
                rc.setInspection(dataset.inspection_internal);
            });
            functionality.addRoutingComponent(routingComponentNew);
            newRoutingComponentIndex = dataSetInitializer.createRoutingComponentIndex(routingComponentNew.getTechnicalId(), null);

            stepOne = dataSetInitializer.createStep(step -> {
                step.setRoutingComponent(routingComponent);
                step.setFiles(new HashSet<>(Arrays.asList(dataSetInitializer.createMedia())));
            });

            post = dataSetInitializer.createPost(p -> {
                p.setStep(stepOne);
            }, "designationFR", "designationEN");

            OperationFunctionalArea operationFunctionalArea = dataSetInitializer.createOperationFunctionalArea(opfa -> {
                opfa.setFunctionalArea(functionalArea);
                opfa.setOperation(operation);
            });

            StepActivation stepActivation = dataSetInitializer.createStepActivation(true, stepOne, operationFunctionalArea);

            routingFunctionalAreaPost = dataSetInitializer.createRoutingFunctionalAreaPost(routingfaPost -> {
                routingfaPost.setPost(post);
                routingfaPost.setStepActivation(stepActivation);
            });

            todoList = dataSetInitializer.createTodoList(todoList1 -> {
                todoList1.setOperationType(dataset.operationType_preliminary);
            });

            //CREATION TODOLIST
            operationTodoList = dataSetInitializer.createOperation(2, operation -> {
                operation.setRouting(routing);
                operation.setOperationType(dataset.operationType_preliminary);
                operation.addTodoList(todoList);
            });

            dataSetInitializer.createRoutingComponentIndex(null, todoList.getTechnicalId());

            dataSetInitializer.createStep(step -> {
                step.setTodoList(todoList);
                step.setRoutingComponent(null);
                step.setFiles(new HashSet<>(Arrays.asList(dataSetInitializer.createMedia())));
            });

            //ENRICH DATA Other post for the Same Routing Component
            Post post1 = dataSetInitializer.createPost(p -> {
                p.setStep(stepOne);
            }, "designationFR", "designationEN");
            routingFunctionalAreaPost = dataSetInitializer.createRoutingFunctionalAreaPost(routingFunctionalAreaPost -> {
                routingFunctionalAreaPost.setPost(post1);
                routingFunctionalAreaPost.setStepActivation(stepActivation);
            });

            // OTHER Routing Component for the functional Area
            RoutingComponent routingComponent2 = dataSetInitializer.createRoutingComponent(rc -> {
                rc.setFunctionality(dataset.functionality_teeth);
                rc.setOperationType(operation.getOperationType());
                rc.setDamageId(dataset.damage_corrosion.getNaturalId());
                rc.setDamage(dataset.damage_corrosion);
            });

            dataSetInitializer.createRoutingComponentIndex(routingComponent2.getTechnicalId(), null);

            Step stepTwo = dataSetInitializer.createStep(step -> {
                step.setRoutingComponent(routingComponent2);
                step.setStepNumber(2);
                step.setFiles(new HashSet<>(Arrays.asList(dataSetInitializer.createMedia())));
            });

            Post post2 = dataSetInitializer.createPost(p -> {
                p.setStep(stepTwo);
            }, "designationFR", "designationEN");

            StepActivation stepActivationTwo = dataSetInitializer.createStepActivation(true, stepTwo, operationFunctionalArea);

            routingFunctionalAreaPost = dataSetInitializer.createRoutingFunctionalAreaPost(routingFunctionalAreaPost -> {
                routingFunctionalAreaPost.setPost(post2);
                routingFunctionalAreaPost.setStepActivation(stepActivationTwo);
            });

            Post post3 = dataSetInitializer.createPost(p -> {
                p.setStep(stepTwo);
            }, "designationFR", "designationEN");

            routingFunctionalAreaPost = dataSetInitializer.createRoutingFunctionalAreaPost(routingFunctionalAreaPost -> {
                routingFunctionalAreaPost.setPost(post3);
                routingFunctionalAreaPost.setStepActivation(stepActivationTwo);
            });
        });
    }

    @Test
    public void getInspectionDetailOK() throws Exception {
        asUser = dataset.user_simpleUser;
        dataSetInitializer.createUserFeature(FeatureCode.ROUTING, dataset.user_simpleUser, EnumRightLevel.READ);
        expectedStatus = HttpStatus.OK;

        Post post1 = dataSetInitializer.createPost("AAA1", "BBB1");
        Post post2 = dataSetInitializer.createPost("AAA2", "BBB2");
        Media file = dataSetInitializer.createMedia();

        Step step = dataSetInitializer.createStep(s -> {
            s.setRoutingComponent(routingComponent);
            s.addPost(post1);
            s.addPost(post2);
            s.addFile(file);
        }, null, 1, StepType.AUTO, new HashSet<Post>());

        OperationFunctionalArea operationFunctionalArea = dataSetInitializer.createOperationFunctionalArea(ofa -> {
            ofa.setOperation(operation);
            ofa.setFunctionalArea(functionalArea);
        });

        StepActivation stepActivation = dataSetInitializer.createStepActivation(true, step, operationFunctionalArea);
        stepActivation.addRoutingFunctionalAreaPost(dataSetInitializer.createRoutingFunctionalAreaPost(rfap -> {
            rfap.setStepActivation(stepActivation);
            rfap.setPost(post1);
            rfap.setThreshold(5.0f);
        }));
        stepActivation.addRoutingFunctionalAreaPost(dataSetInitializer.createRoutingFunctionalAreaPost(rfap -> {
            rfap.setStepActivation(stepActivation);
            rfap.setPost(post2);
            rfap.setThreshold(2.0f);
        }));

        operationFunctionalArea.addStepActivation(stepActivation);
        operation.addOperationFunctionalArea(operationFunctionalArea);
        todoList.addStep(step);
        routing.addOperation(operation);

        withRequest = get(ConstantUrl.API_ROUTING_INSPECTION, routing.getNaturalId(), operation.getNaturalId(), functionalArea.getNaturalId());

        abstractCheck()
                .andExpect(jsonPath("$.inspectionDetail[0].idRoutingComponentIndex", equalTo(routingComponent.getRoutingComponentIndex().getNaturalId().intValue())))
                .andExpect(jsonPath("$.inspectionDetail[0].damage.name", notNullValue()))
                .andExpect(jsonPath("$.inspectionDetail[0].steps", notNullValue()))
                .andExpect(jsonPath("$.inspectionDetail[0].steps[0].id", notNullValue()))
                .andExpect(jsonPath("$.inspectionDetail[0].steps[0].information", notNullValue()))
                .andExpect(jsonPath("$.inspectionDetail[0].steps[0].stepActivation.activated", notNullValue()))
                .andExpect(jsonPath("$.inspectionDetail[0].steps[0].name", notNullValue()))
                .andExpect(jsonPath("$.inspectionDetail[0].steps[0].stepNumber", notNullValue()))
                .andExpect(jsonPath("$.inspectionDetail[0].steps[0].type", notNullValue()))
                .andExpect(jsonPath("$.inspectionDetail[0].steps[0].files", notNullValue()))
                .andExpect(jsonPath("$.inspectionDetail[0].steps[0].files[0].filename", notNullValue()))
                .andExpect(jsonPath("$.inspectionDetail[0].steps[0].posts", notNullValue()))
                .andExpect(jsonPath("$.inspectionDetail[0].steps[0].posts", hasSize(2)))
                .andExpect(jsonPath("$.inspectionDetail[0].steps[0].posts[0].id", notNullValue()))
                .andExpect(jsonPath("$.inspectionDetail[0].steps[0].posts[0].designation", notNullValue()))
                .andExpect(jsonPath("$.inspectionDetail[0].steps[0].posts[0].measureUnit", notNullValue()))
                .andExpect(jsonPath("$.inspectionDetail[0].steps[0].posts[0].measureUnit.id", notNullValue()))
                .andExpect(jsonPath("$.inspectionDetail[0].steps[0].posts[0].measureUnit.name", notNullValue()))
                .andExpect(jsonPath("$.inspectionDetail[0].steps[0].posts[0].postThreshold.threshold", notNullValue()))
                .andExpect(jsonPath("$.newRoutingComponentIndex", notNullValue()))
                .andExpect(jsonPath("$.newRoutingComponentIndex[0].idRoutingComponentIndex").value(newRoutingComponentIndex.getNaturalId()))
                .andExpect(jsonPath("$.newRoutingComponentIndex[0].damage.name", notNullValue()));
    }

    @Test
    public void getFirstInspectionDetailOK() throws Exception {
        AtomicReference<OperationFunctionalArea> operationFunctionalArea = new AtomicReference<>();
        AtomicReference<Operation> operation = new AtomicReference<>();

        runInTransaction(() -> {
            operationFunctionalArea.set(createDatasetTestWithoutPostAssociation());
            operation.set(operationFunctionalArea.get().getOperation());
        });

        asUser = dataset.user_simpleUser;
        dataSetInitializer.createUserFeature(FeatureCode.ROUTING, dataset.user_simpleUser, EnumRightLevel.READ);
        expectedStatus = HttpStatus.OK;

        withRequest = get(ConstantUrl.API_ROUTING_INSPECTION, operation.get().getRouting().getNaturalId(), operation.get().getNaturalId(), operationFunctionalArea.get().getFunctionalArea().getNaturalId());

        abstractCheck()
                .andExpect(jsonPath("$.inspectionDetail[0].idRoutingComponentIndex").value(routingComponent.getRoutingComponentIndex().getNaturalId()))
                .andExpect(jsonPath("$.inspectionDetail[0].damage.name").value("Field name for " + routingComponent.getDamage().getClass().getSimpleName() + ": corrosion"))
                .andExpect(jsonPath("$.inspectionDetail[0].steps", notNullValue()))
                .andExpect(jsonPath("$.inspectionDetail[0].steps[0].id").value(stepOne.getNaturalId()))
                .andExpect(jsonPath("$.inspectionDetail[0].steps[0].stepActivation.activated", notNullValue()))
                .andExpect(jsonPath("$.inspectionDetail[0].steps[0].information").value("information Step"))
                .andExpect(jsonPath("$.inspectionDetail[0].steps[0].name", notNullValue()))
                .andExpect(jsonPath("$.inspectionDetail[0].steps[0].stepNumber").value(stepOne.getStepNumber()))
                .andExpect(jsonPath("$.inspectionDetail[0].steps[0].type", notNullValue()))
                .andExpect(jsonPath("$.inspectionDetail[0].steps[0].files", notNullValue()))
                .andExpect(jsonPath("$.inspectionDetail[0].steps[0].files[0].filename").value("image.jpg"))
                .andExpect(jsonPath("$.inspectionDetail[0].steps[0].posts", notNullValue()))
                .andExpect(jsonPath("$.inspectionDetail[0].steps[0].posts", hasSize(1)))
                .andExpect(jsonPath("$.inspectionDetail[0].steps[0].posts[0].id").value(post.getNaturalId()))
                .andExpect(jsonPath("$.inspectionDetail[0].steps[0].posts[0].designation",notNullValue()))
                .andExpect(jsonPath("$.inspectionDetail[0].steps[0].posts[0].measureUnit", notNullValue()))
                .andExpect(jsonPath("$.inspectionDetail[0].steps[0].posts[0].measureUnit.id").value(dataset.measureUnit_mm.getId()))
                .andExpect(jsonPath("$.inspectionDetail[0].steps[0].posts[0].measureUnit.name").value("mm"))
                .andExpect(jsonPath("$.inspectionDetail[0].steps[0].posts[0].postThreshold.threshold", notNullValue()));

        //TODO JOHANNES ADD TEST
    }

    @Test
    public void getInspectionDetailForbidden() throws Exception {
        asUser = dataset.user_simpleUser;
        dataSetInitializer.createUserFeature(FeatureCode.ROUTING, dataset.user_simpleUser, EnumRightLevel.NONE);
        expectedStatus = HttpStatus.FORBIDDEN;

        withRequest = get(ConstantUrl.API_ROUTING_INSPECTION, routing.getNaturalId(), operation.getNaturalId(), todoList.getNaturalId());

        abstractCheck();
    }

    @Test
    public void getInspectionDetailTodoListOK() throws Exception {
        asUser = dataset.user_simpleUser;
        dataSetInitializer.createUserFeature(FeatureCode.ROUTING, dataset.user_simpleUser, EnumRightLevel.READ);
        expectedStatus = HttpStatus.OK;

        Post post = dataSetInitializer.createPost("AAA", "BBB");
        Step step = dataSetInitializer.createStep(s -> {
            s.setRoutingComponent(null);
            s.addPost(post);
        }, null, 1, StepType.AUTO, new HashSet<Post>());

        todoList.addStep(step);
        operationTodoList.addTodoList(todoList);
        routing.addOperation(operationTodoList);

        withRequest = get(ConstantUrl.API_ROUTING_INSPECTION, routing.getNaturalId(), operationTodoList.getNaturalId(), todoList.getNaturalId());

        abstractCheck()
                .andExpect(jsonPath("$.inspectionDetail[0].idRoutingComponentIndex", equalTo(todoList.getRoutingComponentIndex().getNaturalId().intValue())))
                .andExpect(jsonPath("$.inspectionDetail[0].steps[0].information", notNullValue()))
                .andExpect(jsonPath("$.inspectionDetail[0].steps[0].files", notNullValue()));
    }

    @Test
    public void putInspectionDetailForbidden() throws Exception {
        asUser = dataset.user_simpleUser;
        dataSetInitializer.createUserFeature(FeatureCode.ROUTING, dataset.user_simpleUser, EnumRightLevel.NONE);
        expectedStatus = HttpStatus.FORBIDDEN;
        StepUpdateDto stepUpdateDto = new StepUpdateDto();
        stepUpdateDto.setId(stepOne.getNaturalId());
        List<RoutingFunctionalAreaPostDto> routingFunctionalAreaPostDtoList = Collections.singletonList(dtoConverter.toDto(routingFunctionalAreaPost, RoutingFunctionalAreaPostDto.class));
        stepUpdateDto.setPosts(routingFunctionalAreaPostDtoList);

        List<StepUpdateDto> stepUpdateDtos = new ArrayList<>();
        stepUpdateDtos.add(stepUpdateDto);

        withRequest = put(ConstantUrl.API_ROUTING_INSPECTION, routing.getNaturalId(), operation.getNaturalId(), functionalArea.getNaturalId()).content(objectMapper.writeValueAsString(stepUpdateDtos));

        abstractCheck();
    }

    @Test
    public void putInspectionDetailOk() throws Exception {
        asUser = dataset.user_simpleUser;
        dataSetInitializer.createUserFeature(FeatureCode.ROUTING, dataset.user_simpleUser, EnumRightLevel.WRITE);
        expectedStatus = HttpStatus.NO_CONTENT;

        OperationFunctionalArea operationFunctionalArea = dataSetInitializer.createOperationFunctionalArea(opfa -> {
            opfa.setFunctionalArea(functionalArea);
            opfa.setOperation(operation);
        });

        operation.addOperationFunctionalArea(operationFunctionalArea);

        StepActivation stepActivation = dataSetInitializer.createStepActivation(true, stepOne, operationFunctionalArea);
        RoutingFunctionalAreaPost routingFunctionalAreaPost = dataSetInitializer.createRoutingFunctionalAreaPost(routingfaPost -> {
            routingfaPost.setPost(post);
        });
        operationFunctionalArea.addStepActivation(stepActivation);
        stepActivation.addRoutingFunctionalAreaPost(routingFunctionalAreaPost);

        stepOne.addStepActivation(stepActivation);
        stepOne.addPost(post);

        RoutingFunctionalAreaPostDto routingFunctionalAreaPostDto = new RoutingFunctionalAreaPostDto();
        routingFunctionalAreaPostDto.setPostId(post.getNaturalId());
        routingFunctionalAreaPostDto.setThreshold(10.0f);
        List<RoutingFunctionalAreaPostDto> routingFunctionalAreaPostDtoList = Collections.singletonList(routingFunctionalAreaPostDto);

        StepUpdateDto stepUpdateDto = new StepUpdateDto();
        stepUpdateDto.setId(stepOne.getNaturalId());
        stepUpdateDto.setPosts(routingFunctionalAreaPostDtoList);

        List<StepUpdateDto> stepUpdateDtos = new ArrayList<>();
        stepUpdateDtos.add(stepUpdateDto);
        withRequest = put(ConstantUrl.API_ROUTING_INSPECTION, operation.getRouting().getNaturalId(), operation.getNaturalId(), functionalArea.getNaturalId()).content(objectMapper.writeValueAsString(stepUpdateDtos));

        abstractCheck();
    }

    @Test
    public void putInspectionDetailPostNotFound() throws Exception {
        asUser = dataset.user_simpleUser;
        dataSetInitializer.createUserFeature(FeatureCode.ROUTING, dataset.user_simpleUser, EnumRightLevel.WRITE);
        expectedStatus = HttpStatus.NOT_FOUND;

        RoutingFunctionalAreaPostDto routingFunctionalAreaPostDto = dtoConverter.toDto(routingFunctionalAreaPost, RoutingFunctionalAreaPostDto.class);
        routingFunctionalAreaPostDto.setPostId(99L);

        StepUpdateDto stepUpdateDto = new StepUpdateDto();
        stepUpdateDto.setId(stepOne.getTechnicalId());
        List<RoutingFunctionalAreaPostDto> routingFunctionalAreaPostDtoList = Collections.singletonList(routingFunctionalAreaPostDto);
        stepUpdateDto.setPosts(routingFunctionalAreaPostDtoList);

        List<StepUpdateDto> stepUpdateDtos = new ArrayList<>();
        stepUpdateDtos.add(stepUpdateDto);

        withRequest = put(ConstantUrl.API_ROUTING_INSPECTION, routing.getNaturalId(), dataset.operationType_preliminary.getId(), functionalArea.getTechnicalId()).content(objectMapper.writeValueAsString(stepUpdateDtos));

        abstractCheck();
    }

    @Test
    public void putInspectionDetailOperationNotFound() throws Exception {
        asUser = dataset.user_simpleUser;
        dataSetInitializer.createUserFeature(FeatureCode.ROUTING, dataset.user_simpleUser, EnumRightLevel.WRITE);
        expectedStatus = HttpStatus.NOT_FOUND;

        StepUpdateDto stepUpdateDto = new StepUpdateDto();
        stepUpdateDto.setId(stepOne.getNaturalId());

        List<RoutingFunctionalAreaPostDto> routingFunctionalAreaPostDtoList = Collections.singletonList(dtoConverter.toDto(routingFunctionalAreaPost, RoutingFunctionalAreaPostDto.class));
        stepUpdateDto.setPosts(routingFunctionalAreaPostDtoList);

        List<StepUpdateDto> stepUpdateDtos = new ArrayList<>();
        stepUpdateDtos.add(stepUpdateDto);


        withRequest = put(ConstantUrl.API_ROUTING_INSPECTION, routing.getNaturalId(), 99L, functionalArea.getTechnicalId()).content(objectMapper.writeValueAsString(stepUpdateDtos));

        abstractCheck();
    }

    @Test
    public void putInspectionDetailFunctionalAreaNotFound() throws Exception {
        asUser = dataset.user_simpleUser;
        dataSetInitializer.createUserFeature(FeatureCode.ROUTING, dataset.user_simpleUser, EnumRightLevel.WRITE);
        expectedStatus = HttpStatus.NOT_FOUND;
        StepUpdateDto stepUpdateDto = new StepUpdateDto();
        stepUpdateDto.setId(stepOne.getNaturalId());
        List<RoutingFunctionalAreaPostDto> routingFunctionalAreaPostDtoList = Collections.singletonList(dtoConverter.toDto(routingFunctionalAreaPost, RoutingFunctionalAreaPostDto.class));
        stepUpdateDto.setPosts(routingFunctionalAreaPostDtoList);

        List<StepUpdateDto> stepUpdateDtos = new ArrayList<>();
        stepUpdateDtos.add(stepUpdateDto);

        withRequest = put(ConstantUrl.API_ROUTING_INSPECTION, routing.getNaturalId(), operation.getNaturalId(), 99L).content(objectMapper.writeValueAsString(stepUpdateDtos));

        abstractCheck();
    }

    private OperationFunctionalArea createDatasetTestWithoutPostAssociation() {
        // CREATION ROUTING
        runInTransaction(() -> {
            FunctionalArea functionalAreaWithoutPostAssociation = dataSetInitializer.createFunctionalArea(functionalArea -> {
                functionalArea.setFunctionality(dataset.functionality_splines);
            });
            operationFunctionalAreaWithoutPost = dataSetInitializer.createOperationFunctionalArea(opfa -> {
                opfa.setFunctionalArea(functionalAreaWithoutPostAssociation);
            });

            Part part = dataSetInitializer.createPart(p -> {
                p.addFunctionalAreas(functionalAreaWithoutPostAssociation);
            }, null);
            Operation operationWithoutPostAssociation = dataSetInitializer.createOperation(1, o -> {
                o.addOperationFunctionalArea(operationFunctionalAreaWithoutPost);
            });
            Routing routing = dataSetInitializer.createRouting(r -> r.addOperation(operationWithoutPostAssociation), part);

            routingComponent = dataSetInitializer.createRoutingComponent(rc -> {
                rc.setFunctionality(dataset.functionality_splines);
                rc.setOperationType(operationWithoutPostAssociation.getOperationType());
                rc.setDamageId(dataset.damage_corrosion.getNaturalId());
                rc.setDamage(dataset.damage_corrosion);
            });
            dataSetInitializer.createRoutingComponentIndex(routingComponent.getTechnicalId(), null);

            dataset.functionality_splines.addRoutingComponent(routingComponent);

        StepActivation stepActivation = dataSetInitializer.createStepActivation(true, stepOne, operationFunctionalAreaWithoutPost);
        stepOne = dataSetInitializer.createStep(step -> {
            step.setRoutingComponent(routingComponent);
            step.addStepActivation(stepActivation);
            step.setFiles(new HashSet<>(Arrays.asList(dataSetInitializer.createMedia())));
        });

            post = dataSetInitializer.createPost(p -> {
                p.setStep(stepOne);
            }, "designationFR", "designationEN");
            stepOne.addPost(post);

            stepActivation.addRoutingFunctionalAreaPost(dataSetInitializer.createRoutingFunctionalAreaPost(rfap -> {
                rfap.setStepActivation(stepActivation);
                rfap.setPost(post);
            }));
            operationFunctionalAreaWithoutPost.addStepActivation(stepActivation);

        });
        return operationFunctionalAreaWithoutPost;

    }
}
