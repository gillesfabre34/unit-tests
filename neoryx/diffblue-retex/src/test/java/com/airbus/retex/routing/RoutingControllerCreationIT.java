package com.airbus.retex.routing;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.business.dto.routing.RoutingCreationDto;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.functional.FunctionalArea;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.model.routing.Routing;
import com.airbus.retex.model.routing.RoutingFieldsEnum;
import com.airbus.retex.persistence.routing.RoutingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Transactional
public class RoutingControllerCreationIT extends BaseControllerTest {

    @Autowired
    private RoutingRepository routingRepository;

    @Autowired
    private MessageSource messageSource;

    final static String API_ROUTINGS = "/api/routings";
    private Part part;

    private Routing routingOne;

    @BeforeEach
    public void before() {
        part = dataSetInitializer.createPart(part1 -> part1.setPartNumber("9999999"), null);
        routingOne = dataSetInitializer.createRouting(part);

        dataSetInitializer.createUserFeature(FeatureCode.ROUTING, dataset.user_simpleUser, EnumRightLevel.WRITE);
    }

    @Test
    public void create_routing_ok() throws Exception {

        asUser = dataset.user_simpleUser;
        expectedStatus = HttpStatus.CREATED;
        withRequest = post(API_ROUTINGS).content(objectMapper.writeValueAsBytes(initRoutingCreationDto(dataset.part_example.getNaturalId(), "")));

        assertNotNull(abstractCheck().andReturn().getResponse().getContentAsString());
    }


    @Test
    public void create_routing_ok_with_PNRoot() throws Exception {

        asUser = dataset.user_simpleUser;
        expectedStatus = HttpStatus.CREATED;
        withRequest = post(API_ROUTINGS).content(objectMapper.writeValueAsBytes(initRoutingCreationDto(null, dataset.part_example_2.getPartNumberRoot())));

        abstractCheck();
    }


    @Test
    public void create_routing_ko_forbidden() throws Exception {

        expectedStatus = HttpStatus.FORBIDDEN;
        asUser = dataset.user_simpleUser2;
        withRequest = post(API_ROUTINGS).content(objectMapper.writeValueAsBytes(initRoutingCreationDto(part.getNaturalId(), null)));

        abstractCheck();
    }

    @Test
    public void create_routing_ko_translated_fields_empty() throws Exception {
        RoutingCreationDto routingCreationDto = initRoutingCreationDto(null, dataset.part_example.getPartNumberRoot());
        routingCreationDto.setTranslatedFields(null);

        expectedStatus = HttpStatus.BAD_REQUEST;
        asUser = dataset.user_simpleUser;
        withRequest = post(API_ROUTINGS).content(objectMapper.writeValueAsBytes(routingCreationDto));

        abstractCheck();
    }

    @Test
    public void create_routing_ko_both_present() throws Exception {

        expectedStatus = HttpStatus.BAD_REQUEST;
        asUser = dataset.user_simpleUser;
        withRequest = post(API_ROUTINGS).content(objectMapper.writeValueAsBytes(initRoutingCreationDto(1L, dataset.part_example.getPartNumberRoot())));

        abstractCheck()
                .andExpect(jsonPath("messages").value("The part number and the root part number are both present"));
    }

    @Test
    public void create_routing_ko_part_not_exist() throws Exception {

        expectedStatus = HttpStatus.BAD_REQUEST;
        asUser = dataset.user_simpleUser;
        withRequest = post(API_ROUTINGS).content(objectMapper.writeValueAsBytes(initRoutingCreationDto(-1L, null)));

        abstractCheck();
    }

    @Test
    public void create_routing_ko_part_number_not_exist() throws Exception {

        expectedStatus = HttpStatus.BAD_REQUEST;
        asUser = dataset.user_simpleUser;
        withRequest = post(API_ROUTINGS).content(objectMapper.writeValueAsBytes(initRoutingCreationDto(null, "5485125")));

        abstractCheck();
    }

    @Test
    public void createRoutingWithPNRootWithRoutingAlreadyCreated() throws Exception {
        FunctionalArea functionalArea1 = dataSetInitializer.createFunctionalArea();
        FunctionalArea functionalArea2 = dataSetInitializer.createFunctionalArea();

        Part partlink1 = dataSetInitializer.createPart(part -> {
            part.setPartNumber("1546346");
            part.setPartNumberRoot("88888888");
            part.addFunctionalAreas(functionalArea1);
        }, null);
        Part partlink2 = dataSetInitializer.createPart(part -> {
            part.setPartNumber("414563541");
            part.setPartNumberRoot("88888888");
            part.addFunctionalAreas(functionalArea2);
        }, null);

        Part partWithRouting = dataSetInitializer.createPart(part -> {
            part.setPartNumber("68591663");
            part.setPartNumberRoot("88888888");
        }, null);
        dataSetInitializer.createRouting(partWithRouting);


        expectedStatus = HttpStatus.CREATED;
        asUser = dataset.user_simpleUser;
        withRequest = post(API_ROUTINGS).content(objectMapper.writeValueAsBytes(initRoutingCreationDto(null, "88888888")));

        abstractCheck()
                .andExpect(jsonPath("routingIds", hasSize(2)))
                .andExpect(jsonPath("errorMappingNotFound").isNotEmpty());
    }


    @Test
    public void createRoutingWithPnAlreadyCreated() throws Exception {
        FunctionalArea functionalArea = dataSetInitializer.createFunctionalArea();
        Part part = dataSetInitializer.createPart(part1 -> {
            part1.setPartNumber("88888888");
            part1.setPartNumberRoot(null);
            part1.addFunctionalAreas(functionalArea);
        }, null);

        dataSetInitializer.createRouting(part);

        expectedStatus = HttpStatus.CREATED;
        asUser = dataset.user_simpleUser;

        withRequest = post(API_ROUTINGS).content(objectMapper.writeValueAsBytes(initRoutingCreationDto(part.getNaturalId(), null)));
        abstractCheck()
                .andExpect(jsonPath("errorRoutingExists").isNotEmpty());
    }


    private RoutingCreationDto initRoutingCreationDto(Long partId, String partNumberRoot) {
        RoutingCreationDto routingCreationDto = new RoutingCreationDto();
        routingCreationDto.setPartId(partId);
        routingCreationDto.setPartNumberRoot(partNumberRoot);

        Map<RoutingFieldsEnum, String> nameFr = new HashMap<>();
        nameFr.put(RoutingFieldsEnum.name, "gamme 1");
        Map<RoutingFieldsEnum, String> nameEN = new HashMap<>();
        nameEN.put(RoutingFieldsEnum.name, "routing 1");
        Map<Language, Map<RoutingFieldsEnum, String>> translatedFields = new HashMap<>();
        translatedFields.put(Language.FR, nameFr);
        translatedFields.put(Language.EN, nameEN);
        routingCreationDto.setTranslatedFields(translatedFields);
        return routingCreationDto;
    }
}
