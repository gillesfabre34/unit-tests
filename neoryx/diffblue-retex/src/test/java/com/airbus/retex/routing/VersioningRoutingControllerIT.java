package com.airbus.retex.routing;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.business.dto.routing.RoutingCreationDto;
import com.airbus.retex.model.classification.EnumClassification;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.model.routing.Routing;
import com.airbus.retex.model.routing.RoutingFieldsEnum;
import com.airbus.retex.persistence.routing.RoutingRepository;
import com.airbus.retex.utils.ConstantUrl;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Transactional
public class VersioningRoutingControllerIT extends BaseControllerTest {

    @Autowired
    private RoutingRepository repository;

    private Part part;

    @BeforeEach
    public void before() {
        part = dataSetInitializer.createPart(null);

        dataSetInitializer.createFunctionalArea((fa) -> {
            fa.setPart(null);
            fa.setAreaNumber("1");
            fa.setClassification(EnumClassification.ZC);
            fa.setFunctionality(dataset.functionality_teeth);
            fa.setFunctionalAreaName(dataset.functionalAreaName_OuterRingBottom);
            fa.setMaterial(dataset.material_15CN6.getId());
            fa.setTreatment(dataset.treatment_cadmiumPlating);
            fa.setDisabled(false);
            part.addFunctionalAreas(fa);
        });
    }

    @Test
    public void createRouting_ThenValidateIt() throws Exception {
        asUser = dataset.user_superAdmin;
        expectedStatus = HttpStatus.CREATED;
        RoutingCreationDto createDto = createRoutingDto();

        // create
        withRequest = post(ConstantUrl.API_ROUTINGS).content(objectMapper.writeValueAsString(createDto));

        AtomicReference<Long> routingIdRef = new AtomicReference<>();
        checkCreation(
            createDto,
            (routing -> {
                assertThat(routing.getStatus(), equalTo(EnumStatus.CREATED));
                assertThat(routing.getVersionNumber(), equalTo(1L));
                assertThat(routing.getIsLatestVersion(), equalTo(true));
                assertThat(routing.getPart().getTechnicalId(), equalTo(part.getTechnicalId()));
                assertThat(routing.getOperations().size(), equalTo(4));
                routingIdRef.set(routing.getNaturalId());
            }))
            .andExpect(jsonPath("routingIds", hasSize(1)))
            .andExpect(jsonPath("errorMappingNotFound").isEmpty())
            .andExpect(jsonPath("errorRoutingExists").isEmpty());

        // validate
        expectedStatus = HttpStatus.NO_CONTENT;
        withRequest = put(ConstantUrl.API_PUBLISH_ROUTING.replace("{id}", routingIdRef.get().toString()))
                .content(objectMapper.writeValueAsString(createDto));

        abstractCheck();

        // check routing is validated
        expectedStatus = HttpStatus.OK;
        withRequest = get(ConstantUrl.API_ROUTING.replace("{id}", routingIdRef.get().toString()));

        abstractCheck()
                .andExpect(jsonPath("id", equalTo(Integer.valueOf(routingIdRef.get().toString()))))
                .andExpect(jsonPath("status", equalTo(EnumStatus.VALIDATED.name())))
                .andExpect(jsonPath("versionNumber", equalTo(1)))
                .andExpect(jsonPath("isLatestVersion", equalTo(true)))
                .andExpect(jsonPath("translatedFields.EN.name", equalTo("Routing A")))
                .andExpect(jsonPath("translatedFields.FR.name", equalTo("Gamme A")))
                .andExpect(jsonPath("creator.firstName").value(asUser.getFirstName()))
                .andExpect(jsonPath("creator.lastName").value(asUser.getLastName()))
                .andExpect(jsonPath("part.id", equalTo(Integer.valueOf(part.getNaturalId().toString()))))
                .andExpect(jsonPath("part.partNumberRoot").value(part.getPartNumberRoot()))
                .andExpect(jsonPath("part.partDesignation.translatedFields.FR.designation", notNullValue()))
                .andExpect(jsonPath("part.partDesignation.translatedFields.EN.designation", notNullValue()));
    }

    private RoutingCreationDto createRoutingDto() {
        RoutingCreationDto createdDto = new RoutingCreationDto();
        createdDto.setPartId(part.getNaturalId());

        Map<RoutingFieldsEnum, String> mapFR = new HashMap<>();
        mapFR.put(RoutingFieldsEnum.name, "Gamme A");
        Map<RoutingFieldsEnum, String> mapEN = new HashMap<>();
        mapEN.put(RoutingFieldsEnum.name, "Routing A");
        Map<Language, Map<RoutingFieldsEnum, String>> translatedFields = new HashMap<>();
        translatedFields.put(Language.FR, mapFR);
        translatedFields.put(Language.EN, mapEN);
        createdDto.setTranslatedFields(translatedFields);

        return createdDto;
    }

    private ResultActions checkCreation(RoutingCreationDto creationDto, Consumer<Routing> routingAssertion) throws Exception {
        withRequest = post(ConstantUrl.API_ROUTINGS).content(objectMapper.writeValueAsString(creationDto));

        ResultActions result = abstractCheck();
        JsonNode jsonNode = objectMapper.readTree(result.andReturn().getResponse().getContentAsString());

        List<Long> createdRoutingIds = new ArrayList<>();
        for (JsonNode node : jsonNode.get("routingIds")) {
            createdRoutingIds.add(node.asLong());
        }

        Long routingId = createdRoutingIds.get(0);

        if (routingAssertion != null) {
            runInTransaction(() -> {
                Routing routing = repository.findLastVersionByNaturalId(routingId).orElse(null);
                routingAssertion.accept(routing);
            });
        }

        return result;
    }
}
