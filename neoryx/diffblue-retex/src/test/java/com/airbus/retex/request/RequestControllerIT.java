package com.airbus.retex.request;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.business.dto.request.RequestSortingValues;
import com.airbus.retex.business.dto.request.RequestUpdateDto;
import com.airbus.retex.business.dto.user.ReferenceDto;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.model.user.User;
import com.airbus.retex.persistence.request.RequestRepository;
import com.airbus.retex.utils.ConstantUrl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

public class RequestControllerIT extends BaseControllerTest {
    public static final String API_REQUEST_TECHNICAL_MANAGERS = "/api/requests/{id}/managers";
    public static final String API_REQUEST_UPDATE = "/api/requests/{id}/save";
    public static final String API_REQUESTS = "/api/requests/{id}";

    private static final String ORIGIN_URL_FOR_TEST = "http://origin.url.com";
    @Autowired
    private RequestRepository requestRepository;

    private Request requestOne;
    private Request requestTwo;

    @BeforeEach
    public void before() {
        requestOne = dataSetInitializer.createRequest("requestOne",
                request -> {
                    request.getAta().setCode(dataset.ata_1.getId());
                    request.setRequester(dataset.request_1.getRequester());
                    request.setAirbusEntity(dataset.airbusEntity_france);
                    request.setCreationDate(LocalDateTime.now());
                    request.setStatus(EnumStatus.TO_DO);
                    request.setRequester(dataset.user_superAdmin);
                    request.setOriginUrl(ORIGIN_URL_FOR_TEST);
                    request.addOriginMedia(dataset.media_1);
                    request.addSpecMedia(dataset.media_1);
                }
        );

        requestTwo = dataSetInitializer.createRequest("request_2",
                request -> {
                    request.getAta().setCode(dataset.ata_2.getId());
                    request.setRequester(dataset.request_2.getRequester());
                    request.setAirbusEntity(dataset.airbusEntity_france);
                    request.setCreationDate(LocalDateTime.now());
                    request.setStatus(EnumStatus.CREATED);
                    request.setRequester(dataset.user_superAdmin);
                }
        );

        dataSetInitializer.createATA(
                ata -> ata.setCode(dataset.ATA_CODE_64)
        );
    }

    @Test
    public void searchRequestsWithFilters_OK() throws Exception {
        asUser = dataset.user_simpleUser;
        this.dataSetInitializer.createUserFeature(FeatureCode.REQUEST, asUser, EnumRightLevel.READ);
        withParams.addAll("airbusEntityIds", List.of(this.dataset.airbusEntity_france.getId().toString()));
        withParams.addAll("statuses", List.of(EnumStatus.CREATED.toString(), EnumStatus.DONE.toString(), EnumStatus.IN_PROGRESS.toString()));
        withParams.add("sortBy", RequestSortingValues.name.name());

        expectedStatus = HttpStatus.OK;

        ResultActions result = check(ConstantUrl.API_REQUESTS);
        result.andExpect(jsonPath("$.results", notNullValue()))
                .andExpect(jsonPath("$.results", hasSize(5)))

                .andExpect(jsonPath("$.results[0].airbusEntity.countryName", equalTo(dataset.airbusEntity_france.getCountryName())))
                .andExpect(jsonPath("$.results[0].status", equalTo(dataset.request_save.getStatus().name())))
                .andExpect(jsonPath("$.results[0].name", notNullValue()))
                .andExpect(jsonPath("$.results[0].versionNumber", notNullValue()))
                .andExpect(jsonPath("$.results[0].status", notNullValue()))
                .andExpect(jsonPath("$.results[0].reference", notNullValue()))
                .andExpect(jsonPath("$.results[0].creationDate", notNullValue()))
                .andExpect(jsonPath("$.results[0].status", notNullValue()))
                .andExpect(jsonPath("$.results[0].origin.id", notNullValue()))
                .andExpect(jsonPath("$.results[0].origin.name", notNullValue()))
                .andExpect(jsonPath("$.results[0].origin.color", notNullValue()))
                .andExpect(jsonPath("$.results[0].requester.id", notNullValue()))
                .andExpect(jsonPath("$.results[0].requester.firstName", notNullValue()))
                .andExpect(jsonPath("$.results[0].requester.lastName", notNullValue()))
                .andExpect(jsonPath("$.results[0].airbusEntity.id", notNullValue()))
                .andExpect(jsonPath("$.results[0].airbusEntity.code", notNullValue()))
                .andExpect(jsonPath("$.results[0].isDeletable", notNullValue()))
                .andExpect(jsonPath("$.results[0].airbusEntity.countryName", notNullValue()));

        // Verify translation
        result.andExpect(jsonPath("$.results[0].name", equalTo("Request save" + dataset.request_save.getId() + " (EN)")));
    }

    @Test
    public void getAllRequesters_Ok() throws Exception {
        asUser = dataset.user_simpleUser;
        expectedStatus = HttpStatus.OK;
        check(ConstantUrl.API_REQUEST_REQUESTERS).andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void findARequest_KO_NotFound() throws Exception {
        expectedStatus = HttpStatus.NOT_FOUND;
        asUser = dataset.user_simpleUser;
        dataSetInitializer.createUserFeature(FeatureCode.REQUEST, asUser, EnumRightLevel.READ);

        ResultActions result = check(ConstantUrl.API_REQUESTS + "/" + "1234");
        result.andExpect(jsonPath("$.messages[0]").value("Request not found"));
    }

    @Test
    public void findARequest_KO_StatusIsForbidden() throws Exception {
        asUser = dataset.user_simpleUser;
        expectedStatus = HttpStatus.FORBIDDEN;
        withRequest = get(ConstantUrl.API_REQUESTS + "/" + "1");

        abstractCheck();

    }

    @Test
    public void findARequest_KO_StatusIsForbidden_2() throws Exception {

        asUser = dataset.user_simpleUser;
        dataSetInitializer.createUserFeature(FeatureCode.DRT_INTERNAL_INSPECTION, asUser, EnumRightLevel.READ);
        expectedStatus = HttpStatus.FORBIDDEN;
        withRequest = get(ConstantUrl.API_REQUESTS + "/" + "1");

        abstractCheck();
    }

    @Test
    public void findARequest_Ok() throws Exception {
        expectedStatus = HttpStatus.OK;
        asUser = dataset.user_simpleUser;
        User validator = dataset.user_simpleUser2;
        dataSetInitializer.createUserFeature(FeatureCode.REQUEST, asUser, EnumRightLevel.READ);

        expectedStatus = HttpStatus.OK;

        ResultActions result = check(ConstantUrl.API_REQUESTS + "/" + requestOne.getId());


        result.andExpect(jsonPath("$.id").value(requestOne.getId()))
                .andExpect(jsonPath("$.reference", notNullValue()))
                .andExpect(jsonPath("$.versionNumber", notNullValue()))
                .andExpect(jsonPath("$.creationDate", notNullValue()))
                .andExpect(jsonPath("$.dueDate", notNullValue()))
                .andExpect(jsonPath("$.status").value(EnumStatus.TO_DO.toString()))
                //  BeforeEach (see up) does set the requester as superAdmin .....this is why we have these three lines below....
                .andExpect(jsonPath("$.requester.id").value(dataset.user_superAdmin.getId()))
                .andExpect(jsonPath("$.requester.firstName").value(dataset.user_superAdmin.getFirstName()))
                .andExpect(jsonPath("$.requester.lastName").value(dataset.user_superAdmin.getLastName()))
                .andExpect(jsonPath("$.origin.id").value(dataset.request_1.getOrigin().getId()))
                .andExpect(jsonPath("$.origin.name").value(dataset.request_1.getOrigin().getName()))
                .andExpect(jsonPath("$.origin.color").value(dataset.request_1.getOrigin().getColor()))
                .andExpect(jsonPath("$.airbusEntity.id").value(dataset.airbusEntity_france.getId()))
                .andExpect(jsonPath("$.airbusEntity.code").value(dataset.airbusEntity_france.getCode()))
                .andExpect(jsonPath("$.airbusEntity.countryName").value(dataset.airbusEntity_france.getCountryName()))
                .andExpect(jsonPath("$.validator.id").value(validator.getId()))
                .andExpect(jsonPath("$.validator.firstName").value(validator.getFirstName()))
                .andExpect(jsonPath("$.validator.lastName").value(validator.getLastName()))
                .andExpect(jsonPath("$.versionNumber", notNullValue()))
                .andExpect(jsonPath("$.specMedias[*]", notNullValue()))
                .andExpect(jsonPath("$.specMedias[*].uuid", notNullValue()))
                .andExpect(jsonPath("$.specMedias[*].filename", notNullValue()))
                .andExpect(jsonPath("$.originMedias[*]", notNullValue()))
                .andExpect(jsonPath("$.originMedias[*].uuid", notNullValue()))
                .andExpect(jsonPath("$.originMedias[*].filename", notNullValue()))
                .andExpect(jsonPath("$.originURL").value(ORIGIN_URL_FOR_TEST))
                .andExpect(jsonPath("$.isDeletable").value(false))
                .andExpect(jsonPath("$.aircraftFamily.id").value(dataset.aircraft_family_1.getId()))
                .andExpect(jsonPath("$.aircraftTypes[*]",hasSize(1)))
                .andExpect(jsonPath("$.aircraftTypes[0].id").value(dataset.aircraft_version_1.getId()))
                .andExpect(jsonPath("$.aircraftVersions[*]",hasSize(1)))
                .andExpect(jsonPath("$.aircraftVersions[0].id").value(dataset.aircraft_version_1.getId()))
                // Verify translation
                .andExpect(jsonPath("$.name").value("requestOne" + requestOne.getId() + " (EN)"))
                .andExpect(jsonPath("$.description").value("Description in english for requestOne" + requestOne.getId() + " (EN)"));
    }

    @Test
    public void findARequest_Ok_deletable_true() throws Exception {
        expectedStatus = HttpStatus.OK;
        asUser = dataset.user_simpleUser;
        User validator = dataset.user_simpleUser2;
        dataSetInitializer.createUserFeature(FeatureCode.REQUEST, asUser, EnumRightLevel.READ);

        expectedStatus = HttpStatus.OK;

        ResultActions result = check(ConstantUrl.API_REQUESTS + "/" + requestTwo.getId());


        result.andExpect(jsonPath("$.id").value(requestTwo.getId()))
                .andExpect(jsonPath("$.isDeletable").value(true));
    }

    @Test
    public void getTechnicalRequesters_OK() throws Exception {
        asUser = dataset.user_simpleUser;
        this.dataSetInitializer.createUserFeature(FeatureCode.REQUEST, asUser, EnumRightLevel.READ);
        expectedStatus = HttpStatus.OK;
        withRequest = get(API_REQUEST_TECHNICAL_MANAGERS, dataset.request_1.getId());

        abstractCheck();
    }

    @Test
    public void getRequestVersions_OK() throws Exception {
        asUser = dataset.user_simpleUser;
        this.dataSetInitializer.createUserFeature(FeatureCode.REQUEST, asUser, EnumRightLevel.READ);
        expectedStatus = HttpStatus.OK;

        ResultActions result = check(ConstantUrl.API_REQUESTS + "/" + /*requestOne.getId()*/ 1234L + "/versions");
        //result.andExpect(jsonPath("$.[0].isLatestVersion").value(true));
        result.andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void updateParentRequestDetailsSave_Ok() throws Exception {
        asUser = dataset.user_superAdmin;
        expectedStatus = HttpStatus.NO_CONTENT;

        RequestUpdateDto updateDto = constructUpdateRequest();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String request = ow.writeValueAsString(updateDto);

        withRequest = put(API_REQUEST_UPDATE, dataset.request_1.getId()).content(request);
        abstractCheck();
        // check is updated
        Request requestUpdated = requestRepository.findById(dataset.request_1.getId()).get();
        assertEquals(requestUpdated.getAta().getCode(), updateDto.getAtaCode());
        assertEquals(requestUpdated.getReference(), updateDto.getReference());
    }

    @Test
    public void deleteRequest_ok() throws Exception {
        asUser = dataset.user_superAdmin;
        expectedStatus = HttpStatus.NO_CONTENT;
        Request requestToDelete = dataSetInitializer.createRequest("requestToDelete", request -> {
            request.setStatus(EnumStatus.CREATED);
        });
        withRequest = delete(API_REQUESTS, requestToDelete.getId());
        abstractCheck();
        // check is updated
        Optional<Request> requestUpdated = requestRepository.findById(requestToDelete.getId());
        assertTrue(requestUpdated.isEmpty());
    }

    @Test
    public void deleteRequest_delete_impossible() throws Exception {
        asUser = dataset.user_superAdmin;
        expectedStatus = HttpStatus.BAD_REQUEST;
        withRequest = delete(API_REQUESTS, dataset.request_update.getId());
        Exception exception = abstractCheck().andReturn().getResolvedException();
        assertEquals("retex.error.request.invalid.status.delete.impossible", exception.getMessage());
    }

    @Test
    public void deleteRequest_delete_not_found_request() throws Exception {
        asUser = dataset.user_superAdmin;
        expectedStatus = HttpStatus.NOT_FOUND;
        withRequest = delete(API_REQUESTS, 100000L);
        Exception exception = abstractCheck().andReturn().getResolvedException();
        assertEquals("retex.error.request.not.found", exception.getMessage());
    }

    private RequestUpdateDto constructUpdateRequest() throws JsonProcessingException {
        RequestUpdateDto updateDto = new RequestUpdateDto();
        updateDto.setAtaCode(dataset.ata_2.getCode());
        updateDto.setClientIds(List.of(1L, 2L));
        updateDto.setOriginId(1L);
        updateDto.setAircraftFamily(new ReferenceDto<>(1L));
        updateDto.setEnvironment(new ReferenceDto<>(1L));
        updateDto.setMissionType(new ReferenceDto<>(3L));
        updateDto.setAircraftTypes(List.of(1L));
        updateDto.setAircraftVersions(List.of(1L));
        updateDto.setReference("ref01");
        updateDto.setOperatorIds(List.of(4L, 5L));
        updateDto.setTechnicalManagerIds(List.of(6L, 7L));

        return updateDto;
    }

    private ResultActions check(String endpoint) throws Exception {
        withRequest = get(endpoint);
        return abstractCheck();
    }
}
