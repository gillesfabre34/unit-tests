package com.airbus.retex.routing;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.operation.Operation;
import com.airbus.retex.model.operation.OperationFunctionalArea;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.model.routing.Routing;
import com.airbus.retex.model.routing.RoutingFieldsEnum;
import com.airbus.retex.model.routing.RoutingTranslation;
import com.airbus.retex.persistence.routing.RoutingRepository;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class RoutingControllerIT extends BaseControllerTest {

    private final static String API_ROUTINGS = "/api/routings";
    private final static String API_ROUTINGS_PUBLISH = API_ROUTINGS + "/{id}/publish";

    @Autowired
    private RoutingRepository routingRepository;

    private Routing routingOne;
    private Routing routingTwo;
    private Routing routingThree;
    private Routing routingFour;
    private Routing routingCreated;

    private Routing routingFilterOne;
    private Routing routingFilterTwo;
    private Routing routingFilterThree;

    private Part part1;
    private Part part2;
    private Part part3;
    private Part part4;
    private Part part5;
    private Part part6;

    private final static String PART_NUMBER_ROOT = "99999999";


    @BeforeEach
    public void before() {
        runInTransaction(() -> {
            part1 = dataSetInitializer.createPart(null);
            part2 = dataSetInitializer.createPart(null);
            part3 = dataSetInitializer.createPart(null);
            part4 = dataSetInitializer.createPart(part -> part.setPartNumberRoot(PART_NUMBER_ROOT), null);
            part5 = dataSetInitializer.createPart(null);
            part6 = dataSetInitializer.createPart(null);

            LocalDateTime date = LocalDateTime.now();

            part1.setMedia(dataSetInitializer.createMedia());
            routingOne = dataSetInitializer.createRouting(part1);
            routingTwo = dataSetInitializer.createRouting(part2);
            routingThree = dataSetInitializer.createRouting(part3);
            routingCreated = dataSetInitializer.createRouting(r -> r.setStatus(EnumStatus.CREATED), part1);

            routingFilterOne = dataSetInitializer.createRouting(
                    routing -> {
                        routing.setTechnicalId(101L);
                        routing.setCreationDate(date);
                        routing.setStatus(EnumStatus.VALIDATED);
                        routing.setCreator(dataset.user_superAdmin);

                    }, part4
            );

            routingFilterTwo = dataSetInitializer.createRouting(
                    routing -> {
                        routing.setTechnicalId(102L);
                        routing.setCreationDate(date);
                        routing.setStatus(EnumStatus.CREATED);
                        routing.setCreator(dataset.user_superAdmin);

                    }, part5
            );

            routingFilterThree = dataSetInitializer.createRouting(
                    routing -> {
                        routing.setTechnicalId(103L);
                        routing.setCreationDate(date);
                        routing.setStatus(EnumStatus.CREATED);
                        routing.setCreator(dataset.user_superAdmin);
                        routing.getTranslations().clear();
                        routing.addTranslation(dataSetInitializer.createTranslation(RoutingTranslation::new, RoutingFieldsEnum.name, Language.EN, "TEST ENGLISH !!"));
                        routing.addTranslation(dataSetInitializer.createTranslation(RoutingTranslation::new, RoutingFieldsEnum.name, Language.FR, "TEST FRANCAIS !!"));
                    }, part6
            );
        });
    }

    @Test
    public void getAllRoutingsOK() throws Exception {
        asUser = dataset.user_simpleUser;
        dataSetInitializer.createUserFeature(FeatureCode.ROUTING, dataset.user_simpleUser, EnumRightLevel.READ);
        expectedStatus = HttpStatus.OK;

        checkGetAllRoutings(hasSize((int) routingRepository.count()));
    }


    @Test
    public void getAllRoutingsForbidden() throws Exception {
        expectedStatus = HttpStatus.FORBIDDEN;
        asUser = dataset.user_superAdmin;
        dataSetInitializer.createUserFeature(FeatureCode.ROUTING, dataset.user_superAdmin, EnumRightLevel.NONE);

        withRequest = get(API_ROUTINGS);

        abstractCheck();
    }


    @Test
    public void getAllRoutingsPaginatedOK() throws Exception {
        asUser = dataset.user_superAdmin;
        withParams.add("page", "1");
        withParams.add("size", "2");
        expectedStatus = HttpStatus.OK;

        checkGetAllRoutings(hasSize(2));
    }


    @Test
    public void getOneRoutingOk() throws Exception {
        expectedStatus = HttpStatus.OK;
        asUser = dataset.user_simpleUser;
        dataSetInitializer.createUserFeature(FeatureCode.ROUTING, dataset.user_simpleUser, EnumRightLevel.READ);

        withRequest = get(API_ROUTINGS + "/" + routingOne.getNaturalId());
        abstractCheck()
                .andExpect(jsonPath("$.id").value(routingOne.getNaturalId()))
                .andExpect(jsonPath("$.translatedFields.FR.name", notNullValue()))
                .andExpect(jsonPath("$.translatedFields.EN.name", notNullValue()))
                .andExpect(jsonPath("$.part.media", notNullValue()))
                .andExpect(jsonPath("$.part.partNumber").value(part1.getPartNumber()))
                .andExpect(jsonPath("$.part.partNumberRoot").value(part1.getPartNumberRoot()))
                .andExpect(jsonPath("$.part.partDesignation.translatedFields.FR.designation", notNullValue()))
                .andExpect(jsonPath("$.part.partDesignation.translatedFields.EN.designation", notNullValue()))
                .andExpect(jsonPath("$.status").value(routingOne.getStatus().toString()))
                .andExpect(jsonPath("$.creator.firstName").value(dataset.user_superAdmin.getFirstName()))
                .andExpect(jsonPath("$.creator.lastName").value(dataset.user_superAdmin.getLastName()));
    }


    @Test
    public void getOneRoutingForbidden() throws Exception {
        expectedStatus = HttpStatus.FORBIDDEN;
        asUser = dataset.user_superAdmin;
        dataSetInitializer.createUserFeature(FeatureCode.ROUTING, dataset.user_superAdmin, EnumRightLevel.NONE);

        withRequest = get(API_ROUTINGS + "/" + routingOne.getNaturalId());
        abstractCheck();
    }

    @Test
    public void getOneRoutingWithBadId() throws Exception {
        expectedStatus = HttpStatus.NOT_FOUND;
        asUser = dataset.user_simpleUser;
        dataSetInitializer.createUserFeature(FeatureCode.ROUTING, dataset.user_simpleUser, EnumRightLevel.READ);

        withRequest = get(API_ROUTINGS + "/0");

        abstractCheck();
    }

    private void checkGetAllRoutings(Matcher<? extends Collection> resultsMatcher) throws Exception {
        withRequest = get(API_ROUTINGS);

        abstractCheck()
                .andExpect(jsonPath("$.results", notNullValue()))
                .andExpect(jsonPath("$.results", resultsMatcher))
                .andExpect(jsonPath("$.totalPages").isNumber())
                .andExpect(jsonPath("$.totalResults").isNumber());
    }


    private void checkGetAllRoutings(List<Routing> contains, List<Routing> notContains) throws Exception {
        asUser = dataset.user_superAdmin;
        withRequest = get(API_ROUTINGS);
        expectedStatus = HttpStatus.OK;


        Integer[] containsIds = contains.stream().map(routing -> Integer.valueOf(routing.getNaturalId().intValue())).collect(Collectors.toList()).toArray(new Integer[0]);
        Integer[] notContainsId = notContains.stream().map(routing -> Integer.valueOf(routing.getNaturalId().intValue())).collect(Collectors.toList()).toArray(new Integer[0]);

        abstractCheck()
                .andExpect(jsonPath("$.results[*].id", hasItems(containsIds)))
                .andExpect(jsonPath("$.results[*].id", not(hasItems(notContainsId))));
    }

    @Test
    public void deleteRoutingOk() throws Exception {
        asUser = dataset.user_simpleUser;
        dataSetInitializer.createUserFeature(FeatureCode.ROUTING, dataset.user_simpleUser, EnumRightLevel.WRITE);
        Part part = dataSetInitializer.createPart(null);
        Routing routingToDelete = dataSetInitializer.createRouting(routing -> {
            routing.setStatus(EnumStatus.CREATED);
                }, part
        );

        withRequest = delete(API_ROUTINGS + "/" + routingToDelete.getNaturalId());
        expectedStatus = HttpStatus.NO_CONTENT;
        abstractCheck();
    }

    @Test
    public void deleteRoutingWithOperationOk() throws Exception {
        asUser = dataset.user_simpleUser;
        dataSetInitializer.createUserFeature(FeatureCode.ROUTING, dataset.user_simpleUser, EnumRightLevel.WRITE);
        Part part = dataSetInitializer.createPart(null);
        Routing routingToDelete = dataSetInitializer.createRouting(routing -> {
            routing.setStatus(EnumStatus.CREATED);
                }, part
        );
        Operation operation = dataSetInitializer.createOperation(1,
                operation1 -> operation1.setRouting(routingToDelete));
        OperationFunctionalArea operationFunctionalArea = dataSetInitializer
                .createOperationFunctionalArea(opeFunctionalArea -> opeFunctionalArea.setOperation(operation));

//        dataSetInitializer.createOperationTodoList(operationTodoList -> operationTodoList.setOperation(operation));
        withRequest = delete(API_ROUTINGS + "/" + routingToDelete.getNaturalId());
        expectedStatus = HttpStatus.NO_CONTENT;
        abstractCheck();
    }

    @Test
    public void deleteRoutingForbidden() throws Exception {
        expectedStatus = HttpStatus.FORBIDDEN;
        asUser = dataset.user_superAdmin;
        dataSetInitializer.createUserFeature(FeatureCode.ROUTING, dataset.user_superAdmin, EnumRightLevel.NONE);

        withRequest = delete(API_ROUTINGS + "/" + routingOne.getNaturalId());

        abstractCheck();
    }

    @Test
    public void deleteRoutingWithoutWriteForbidden() throws Exception {
        expectedStatus = HttpStatus.FORBIDDEN;
        asUser = dataset.user_superAdmin;
        dataSetInitializer.createUserFeature(FeatureCode.ROUTING, dataset.user_superAdmin, EnumRightLevel.READ);

        withRequest = delete(API_ROUTINGS + "/" + routingThree.getNaturalId());

        abstractCheck();
    }

    @Test
    public void deleteRoutingIdNotExist() throws Exception {
        asUser = dataset.user_simpleUser;
        dataSetInitializer.createUserFeature(FeatureCode.ROUTING, dataset.user_simpleUser, EnumRightLevel.WRITE);
        withRequest = delete(API_ROUTINGS + "/" + 0);
        expectedStatus = HttpStatus.NOT_FOUND;

        abstractCheck();
    }

    @Test
    public void getAllRoutingsNoFilters_OK() throws Exception {
        asUser = dataset.user_superAdmin;
        withRequest = get(API_ROUTINGS);
        expectedStatus = HttpStatus.OK;
        checkGetAllRoutings(hasSize((int) routingRepository.count()));
    }

    @Test
    public void getRoutingsWithPnOrPnRootPartNumberFilter() throws Exception {
        withParams.add("pnOrPnRoot", part4.getPartNumber());

        checkGetAllRoutings(List.of(routingFilterOne), List.of(routingFilterTwo, routingFilterThree));
    }

    @Test
    public void getRoutingsWithPnOrPnRootPartNumberRootFilter() throws Exception {
        withParams.add("pnOrPnRoot", part4.getPartNumberRoot());

        checkGetAllRoutings(List.of(routingFilterOne), List.of(routingFilterTwo, routingFilterThree));
    }
    @Test
    public void getRoutingsWithStatusFilter() throws Exception {
        withParams.add("status", EnumStatus.CREATED.name());

        checkGetAllRoutings(List.of(routingFilterTwo), List.of(routingFilterOne, routingFilterThree));
    }

    @Test
    public void getRoutingsWithMultiFilters() throws Exception {
        withParams.add("partNumber", part1.getPartNumber());
        withParams.add("status", EnumStatus.CREATED.name());

        checkGetAllRoutings(List.of(), List.of(routingFilterOne, routingFilterTwo, routingFilterThree));
    }


    @Test
    public void getRoutingsWithRoutingNameFilter_English() throws Exception {
        asUser = dataset.user_superAdmin;
        expectedStatus = HttpStatus.OK;
        withParams.add("name", "ENGL");

        checkGetAllRoutings(hasSize(1));
    }

    @Test
    public void getRoutingsWithRoutingNameFilter_Francais() throws Exception {
        asUser = dataset.user_superAdmin;
        expectedStatus = HttpStatus.OK;
        withParams.add("name", "ANCA");

        checkGetAllRoutings(hasSize(1));
    }

    @Test
    public void getRoutingsWithIncorrectRoutingNameFilter() throws Exception {
        asUser = dataset.user_superAdmin;
        expectedStatus = HttpStatus.OK;
        withParams.add("name", "ANCAZZZ");

        checkGetAllRoutings(hasSize(0));
    }

    @Test
    public void getRoutingsWithRoutingIdFilter() throws Exception {
        asUser = dataset.user_superAdmin;
        expectedStatus = HttpStatus.OK;
        withParams.add("id", routingOne.getNaturalId().toString());

        checkGetAllRoutings(hasSize(1));
    }


    @Test
    public void getRoutingsWithCreationDateFilter() throws Exception {
        asUser = dataset.user_superAdmin;
        expectedStatus = HttpStatus.OK;
        withParams.add("creationDate", LocalDate.now().toString());

        checkGetAllRoutings(hasSize((int) routingRepository.count()));
    }

    @Test
    public void getRoutingsWithOtherMultiFilters() throws Exception {
        asUser = dataset.user_superAdmin;
        withParams.add("name", "ENGL");
        withParams.add("creationDate", LocalDate.now().toString());
        withRequest = get(API_ROUTINGS);
        expectedStatus = HttpStatus.OK;

        checkGetAllRoutings(hasSize(1));
    }


    @Test
    public void validateRoutings_ok() throws Exception {
        asUser = dataset.user_superAdmin;
        withRequest = put(API_ROUTINGS_PUBLISH, routingCreated.getNaturalId());
        expectedStatus = HttpStatus.NO_CONTENT;

        abstractCheck();
    }

    @Test
    public void validateRoutings_ko_statusNotCreated() throws Exception {
        asUser = dataset.user_superAdmin;
        routingOne.setStatus(EnumStatus.VALIDATED);
        routingRepository.save(routingOne);
        withRequest = put(API_ROUTINGS_PUBLISH, routingOne.getNaturalId());
        expectedStatus = HttpStatus.BAD_REQUEST;

        abstractCheck().andExpect(jsonPath("$.messages").value("Routing isn't in the right status and can't be published"));
    }

    @Test
    public void validateRoutings_ko_routingNotFound() throws Exception {
        asUser = dataset.user_superAdmin;
        withRequest = put(API_ROUTINGS_PUBLISH,  -1);
        expectedStatus = HttpStatus.NOT_FOUND;

        abstractCheck().andExpect(jsonPath("$.messages").value("Routing not found"));
    }


    @Test
    public void validateRoutings_ko_forbidden() throws Exception {
        asUser = dataset.user_simpleUser2;
        withRequest = put(API_ROUTINGS_PUBLISH,  routingOne.getNaturalId());
        expectedStatus = HttpStatus.FORBIDDEN;

        abstractCheck();
    }

    @Test
    public void getRoutingsWithPartNumberFilter() throws Exception {
        withParams.add("partNumber", part4.getPartNumber());

        checkGetAllRoutings(List.of(routingFilterOne), List.of(routingFilterTwo, routingFilterThree));
    }


    @Test
    public void getRoutingsWithPartNumberRootFilter() throws Exception {
        withParams.add("partNumberRoot", part4.getPartNumberRoot());
        part4 = dataSetInitializer.createPart(part -> {
            part.setPartNumberRoot(part4.getPartNumberRoot());
            part.setPartNumber("");
            part.setTechnicalId(part4.getTechnicalId());
            part.setNaturalId(part4.getNaturalId());
        }, null);

        checkGetAllRoutings(List.of(routingFilterOne), List.of(routingFilterTwo, routingFilterThree));
    }

    @Test
    public void getRoutingsWithPartNumberFilter_notExist() throws Exception{
        withParams.add("partNumber", "aaaaa");

        checkGetAllRoutings(List.of(),List.of(routingFilterOne,routingFilterTwo,routingFilterThree));
    }


    @Test
    public void getRoutingsWithPartNumberRootFilter_notExist() throws Exception{
        withParams.add("partNumberRoot", "aaaaa");

        checkGetAllRoutings(List.of(),List.of(routingFilterOne,routingFilterTwo,routingFilterThree));
    }

    @Test
    public void getRoutingsWithSamePartNumberAndPartNumberRootFilter() throws Exception{
        withParams.add("partNumber", part6.getPartNumber());
        withParams.add("partNumberRoot", part6.getPartNumberRoot());

        checkGetAllRoutings(List.of(routingFilterThree), List.of(routingFilterTwo, routingFilterOne));
    }


    @Test
    public void getRoutingsWithDifferentPartNumberAndPartNumberRootFilter() throws Exception{
        withParams.add("partNumber", part5.getPartNumber());
        withParams.add("partNumberRoot", part4.getPartNumberRoot());

        checkGetAllRoutings(List.of(),List.of(routingFilterOne,routingFilterTwo,routingFilterThree));
    }

    @Test
    public void getRoutingsWithSamePnOrPnRootAndPartNumberFilter() throws Exception{
        withParams.add("partNumber", part6.getPartNumber());
        withParams.add("pnOrPnRoot", part6.getPartNumberRoot());

        checkGetAllRoutings(List.of(routingFilterThree), List.of(routingFilterTwo, routingFilterOne));
    }


    @Test
    public void getRoutingsWithDifferentPnOrPnRootAndPartNumberFilter() throws Exception{
        withParams.add("partNumber", part5.getPartNumber());
        withParams.add("pnOrPnRoot", part4.getPartNumberRoot());

        checkGetAllRoutings(List.of(),List.of(routingFilterOne,routingFilterTwo,routingFilterThree));
    }

    @Test
    public void getRoutingsWithSamePnOrPnRootAndPartNumberRootFilter() throws Exception{
        withParams.add("partNumberRoot", part4.getPartNumberRoot());
        part4 = dataSetInitializer.createPart(part -> {
            part.setPartNumberRoot(part4.getPartNumberRoot());
            part.setPartNumber("");
            part.setTechnicalId(part4.getTechnicalId());
            part.setNaturalId(part4.getNaturalId());
        }, null);

        checkGetAllRoutings(List.of(routingFilterOne), List.of(routingFilterTwo, routingFilterThree));
    }


    @Test
    public void getRoutingsWithDifferentPnOrPnRootAndPartNumberRootFilter() throws Exception{
        withParams.add("partNumberRoot", part5.getPartNumberRoot());
        withParams.add("pnOrPnRoot", part4.getPartNumber());

        checkGetAllRoutings(List.of(),List.of(routingFilterOne,routingFilterTwo,routingFilterThree));
    }
}
