package com.airbus.retex.routingComponent;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.business.dto.post.PostCreationDto;
import com.airbus.retex.business.dto.routingComponent.RoutingComponentCreateUpdateDto;
import com.airbus.retex.business.dto.step.StepCreationDto;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.common.StepType;
import com.airbus.retex.model.operation.OperationType;
import com.airbus.retex.model.post.PostFieldsEnum;
import com.airbus.retex.model.routingComponent.RoutingComponentIndex;
import com.airbus.retex.model.step.StepFieldsEnum;
import com.airbus.retex.persistence.routingComponent.RoutingComponentIndexRepository;
import com.airbus.retex.utils.ConstantUrl;
import com.fasterxml.jackson.databind.JsonNode;

public class VersioningRoutingComponentControllerIT extends BaseControllerTest {

    @Autowired
    private RoutingComponentIndexRepository repository;

    private RoutingComponentCreateUpdateDto componentCreationDto;


    @Test
    public void givenAValidatedRoutingComponent_whenValidateId_thenANewVersionIsCreated() throws Exception {

        asUser = dataset.user_superAdmin;
        expectedStatus = HttpStatus.OK;
        componentCreationDto = createRoutingComponentDto(dataset.operationType_dimensional, 1);

        AtomicReference<RoutingComponentIndex> routingComponentIndexFirst = new AtomicReference<>();
        // Create (-> new version)
        ResultActions result = check(null , routingComponentIndex -> {
            routingComponentIndexFirst.set(routingComponentIndex);
            assertThat(routingComponentIndex.getStatus(), equalTo(EnumStatus.CREATED));
            Assertions.assertEquals(1,
                    repository.findAllVersionsByNaturalId(routingComponentIndex.getNaturalId()).size());

            //SET NATURAL ID
            componentCreationDto.getSteps().get(0).setId(routingComponentIndex.getRoutingComponent().getSteps().get(0).getNaturalId());
            componentCreationDto.getSteps().get(0).getPosts().get(0).setId(routingComponentIndex.getRoutingComponent().getSteps().get(0).getPosts().iterator().next().getNaturalId());
        });

        JsonNode jsonNode = objectMapper.readTree(result.andReturn().getResponse().getContentAsString());
        Long routingComponentIndexId = jsonNode.get("routingComponentIndexId").asLong();

        // Update to validated (-> same version)
        check(routingComponentIndexId , routingComponentIndexValidated -> {
            Assertions.assertEquals(routingComponentIndexFirst.get().getTechnicalId(), routingComponentIndexValidated.getTechnicalId());
            Assertions.assertEquals(routingComponentIndexFirst.get().getNaturalId(), routingComponentIndexValidated.getNaturalId());
            Assertions.assertEquals(routingComponentIndexFirst.get().getRoutingComponent().getNaturalId(), routingComponentIndexValidated.getRoutingComponent().getNaturalId());
            Assertions.assertEquals(routingComponentIndexFirst.get().getRoutingComponent().getSteps().get(0).getNaturalId(), routingComponentIndexValidated.getRoutingComponent().getSteps().get(0).getNaturalId());
            Assertions.assertEquals(routingComponentIndexFirst.get().getRoutingComponent().getSteps().get(0).getPosts().iterator().next().getNaturalId(), routingComponentIndexValidated.getRoutingComponent().getSteps().get(0).getPosts().iterator().next().getNaturalId());

            Assertions.assertEquals(1, repository.findAllVersionsByNaturalId(routingComponentIndexId).size());
            assertThat(routingComponentIndexValidated.getStatus(), equalTo(EnumStatus.VALIDATED));
        });

        // Update to validated (-> new version)
        check(routingComponentIndexId , routingComponentIndexLast -> {
            Assertions.assertNotEquals(routingComponentIndexFirst.get().getTechnicalId(), routingComponentIndexLast.getTechnicalId());
            Assertions.assertEquals(routingComponentIndexFirst.get().getNaturalId(), routingComponentIndexLast.getNaturalId());
            Assertions.assertEquals(routingComponentIndexFirst.get().getRoutingComponent().getNaturalId(), routingComponentIndexLast.getRoutingComponent().getNaturalId());
            Assertions.assertEquals(routingComponentIndexFirst.get().getRoutingComponent().getSteps().get(0).getNaturalId(), routingComponentIndexLast.getRoutingComponent().getSteps().get(0).getNaturalId());
            Assertions.assertEquals(routingComponentIndexFirst.get().getRoutingComponent().getSteps().get(0).getPosts().iterator().next().getNaturalId(), routingComponentIndexLast.getRoutingComponent().getSteps().get(0).getPosts().iterator().next().getNaturalId());
            Assertions.assertEquals(2, repository.findAllVersionsByNaturalId(routingComponentIndexId).size());
            assertThat(routingComponentIndexLast.getStatus(), equalTo(EnumStatus.VALIDATED));
            assertThat(routingComponentIndexLast.getRoutingComponent().getSteps().size() > 0, equalTo(true));
            assertThat(routingComponentIndexLast.getRoutingComponent().getSteps().get(0).getPosts().size() > 0, equalTo(true));
        });
    }



    private ResultActions check(Long rCIndexId, Consumer<RoutingComponentIndex> updatedRoutingComponentAssertion) throws Exception {
        if(rCIndexId == null){
            withRequest = post(ConstantUrl.API_ROUTING_COMPONENTS).content(objectMapper.writeValueAsString(componentCreationDto));
        }else {
            withParams.add("validate", "true");
            withRequest = put(ConstantUrl.API_ROUTING_COMPONENTS + "/" + rCIndexId).content(objectMapper.writeValueAsString(componentCreationDto));

        }
        ResultActions result = abstractCheck();
        JsonNode jsonNode = objectMapper.readTree(result.andReturn().getResponse().getContentAsString());
        if(null == rCIndexId){
            rCIndexId = jsonNode.get("routingComponentIndexId").asLong();
        }


        if(updatedRoutingComponentAssertion != null) {
            Long finalRCIndexId = rCIndexId;
            runInTransaction(() -> {
                RoutingComponentIndex routingComponentIndex = repository.findLastVersionByNaturalId(finalRCIndexId).orElse(null);
                updatedRoutingComponentAssertion.accept(routingComponentIndex);
            });
        }

        return result;
    }

    private RoutingComponentCreateUpdateDto createRoutingComponentDto(OperationType operationType, int nbStep) {
        RoutingComponentCreateUpdateDto routingComponentCreation = new RoutingComponentCreateUpdateDto();
        routingComponentCreation.setInspectionValue(dataset.inspection_internal.getValue());
        routingComponentCreation.setOperationTypeId(operationType.getId());
        routingComponentCreation.setTaskId(dataset.functionality_teeth.getId());
        routingComponentCreation.setSubTaskId(dataset.damage_corrosion.getTechnicalId());

        if (nbStep != 0) {
            routingComponentCreation.setSteps(createSteps(nbStep));
        }
        return routingComponentCreation;
    }

    List<StepCreationDto> createSteps(int nbSteps) {
        List<StepCreationDto> steps = new ArrayList<>();
        IntStream.range(0, nbSteps).forEach(e ->
                steps.add(createStepCreationDto("Information step " + e, "name step " + e,
                        "Step's informations " + e, "Step's name" + e,
                        Arrays.asList(dataSetInitializer.createTemporaryMedia().getUuid().toString())))
        );
        return steps;
    }

    StepCreationDto createStepCreationDto(String informationsFR, String nameFR,
                                          String informationsEN, String nameEN, List<String> files) {
        StepCreationDto stepCreationDto = new StepCreationDto();
        stepCreationDto.setType(StepType.AUTO);
        stepCreationDto.setMediaUuids(files);
        // set translated Fields
        Map<Language, Map<StepFieldsEnum, String>> stepsFields = new HashMap<>();
        Map<StepFieldsEnum, String> stepsFieldsFR = new HashMap<>();
        stepsFieldsFR.put(StepFieldsEnum.information, informationsFR);
        stepsFieldsFR.put(StepFieldsEnum.name, nameFR);
        Map<StepFieldsEnum, String> stepsFieldsEN = new HashMap<>();
        stepsFieldsFR.put(StepFieldsEnum.information, informationsEN);
        stepsFieldsFR.put(StepFieldsEnum.name, nameEN);
        stepsFields.put(Language.FR, stepsFieldsFR);
        stepsFields.put(Language.EN, stepsFieldsEN);
        stepCreationDto.setTranslatedFields(stepsFields);
        // set step's posts
        stepCreationDto.setPosts(Arrays.asList(createPosts("Désignation en français", "Designation on english")));
        return stepCreationDto;
    }

    PostCreationDto createPosts(String designationFR, String designationEN) {
        PostCreationDto postCreationDto = new PostCreationDto();
        postCreationDto.setMeasureUnitId(dataset.measureUnit_mm.getId());
        Map<Language, Map<PostFieldsEnum, String>> postsFields = new HashMap<>();
        Map<PostFieldsEnum, String> postsFieldsFR = new HashMap<>();
        postsFieldsFR.put(PostFieldsEnum.designation, designationFR);
        Map<PostFieldsEnum, String> postsFieldsEN = new HashMap<>();
        postsFieldsEN.put(PostFieldsEnum.designation, designationEN);
        postsFields.put(Language.FR, postsFieldsFR);
        postsFields.put(Language.EN, postsFieldsEN);
        postCreationDto.setTranslatedFields(postsFields);
        return postCreationDto;
    }
}
