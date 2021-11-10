package com.airbus.retex.childRequest;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.childrequest.PhysicalPart;
import com.airbus.retex.model.client.Client;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.persistence.childRequest.ChildRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class ChildRequestControllerGetVersionsIT extends BaseControllerTest {

    private static final String API_GET_VERSION = "/api/requests/child-requests/{id}/versions";

    private Request parentRequest1;

    private ChildRequest childRequest11;

    private Client        client1;
    private Client        client2;
    private PhysicalPart phPart1;
    private PhysicalPart phPart2;

    @Autowired
    private ChildRequestRepository childRequestRepository;

    private static final Long DRT_TO_INSPECT = 100L;
    @BeforeEach
    public void before() {
        runInTransaction(() -> {
            parentRequest1 = dataSetInitializer.createRequest("parentRequest1");
        });

        childRequest11 = dataSetInitializer.createChildRequest(cr -> {
            cr.setParentRequest(parentRequest1);
            cr.setStatus(EnumStatus.CREATED);
            cr.setVersion(1L);
        });

        runInTransaction(() -> {
            //childRequest11 = childRequestRepository.getOne(childRequest11.getId());
            client1 =dataSetInitializer.createClient("client1");
            client2 =dataSetInitializer.createClient("client2");
            childRequest11.addClient(client1);
            childRequest11.addClient(client2);
            phPart1 = dataSetInitializer.createPhysicalPart(childRequest11, "sn10");
            phPart2 = dataSetInitializer.createPhysicalPart(childRequest11, "sn20");
            childRequest11.addPhysicalPart(phPart1);
            childRequest11.addPhysicalPart(phPart2);
            childRequest11 = childRequestRepository.save(childRequest11);
            // Envers 2nd revision with RevisionType.MOD
            // Retex version 1.0

        });
        runInTransaction(() -> {
            //childRequest11 = childRequestRepository.getOne(childRequest11.getId());
            childRequest11.setAircraftFamily(dataset.aircraft_family_2);
            childRequest11 = childRequestRepository.save(childRequest11);
            // Envers 3rd revision with RevisionType.MOD
            // Retex version 1.0
        });
        runInTransaction(() -> {
            //childRequest11 = childRequestRepository.getOne(childRequest11.getId());
            childRequest11.setStatus(EnumStatus.VALIDATED);
            childRequest11.setVersion(childRequest11.getVersion() + 1);
            childRequest11.setDrtToInspect(DRT_TO_INSPECT);
            childRequest11 = childRequestRepository.save(childRequest11);

            // Envers 4th revision with RevisionType.MOD
            // Retex version 2.0

        });
        runInTransaction(() -> {
            //childRequest11 = childRequestRepository.getOne(childRequest11.getId());
            //childRequest11.setAircraftFamily(dataset.aircraft_family_1);
            //childRequest11.setAircraftFamilyId(dataset.aircraft_family_1.getId());
            childRequest11.setDrtToInspect(DRT_TO_INSPECT * 2);
            childRequest11.setVersion(childRequest11.getVersion() + 1);
            childRequest11 = childRequestRepository.save(childRequest11);
            // Envers 5th revision with RevisionType.MOD
            // Retex version 3.0
        });
    }

    @Test
    public void getChildRequestVersions_ID_Not_Exisiting_empty_list_returned() throws Exception {
        asUser = dataset.user_simpleUser;
        this.dataSetInitializer.createUserFeature(FeatureCode.REQUEST,asUser , EnumRightLevel.READ);
        expectedStatus = HttpStatus.OK;
        // A request with ID 1234 is not existing, empty result must be returned
        ResultActions result = check(API_GET_VERSION,1234L);

        result.andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getChildRequestVersions_OK() throws Exception {
        asUser = dataset.user_simpleUser;
        this.dataSetInitializer.createUserFeature(FeatureCode.REQUEST,asUser , EnumRightLevel.READ);
        expectedStatus = HttpStatus.OK;
        // We created childRequest11 (STATUS CREATED) and after several modifications (still STATUS CREATED)
        // we have validated it (V2)....then we have done some modifications on it (V3)
        // The result of getVersions will collapse the CREATED one to only one latest , so we should have only three
        // Versions as below:
        //[
        // {"dateUpdate":"2019-10-23T18:37:53.456","versionNumber":1,"status":"CREATED","isLatestVersion":false},
        // {"dateUpdate":"2019-10-23T18:37:53.47","versionNumber":2,"status":"VALIDATED","isLatestVersion":false},
        // {"dateUpdate":"2019-10-23T18:37:53.484","versionNumber":3,"status":"VALIDATED","isLatestVersion":true}]
        ResultActions result = check(API_GET_VERSION,childRequest11.getId());

        result.andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[0].isLatestVersion",is(false)))
                .andExpect(jsonPath("$.[0].versionNumber",is(1)))
                .andExpect(jsonPath("$.[0].status",is(EnumStatus.CREATED.toString())))
                .andExpect(jsonPath("$.[1].isLatestVersion",is(false)))
                .andExpect(jsonPath("$.[1].versionNumber",is(2)))
                .andExpect(jsonPath("$.[1].status",is(EnumStatus.OBSOLETED.toString())))
                .andExpect(jsonPath("$.[2].isLatestVersion",is(true)))
                .andExpect(jsonPath("$.[2].versionNumber",is(3)))
                .andExpect(jsonPath("$.[2].status", is(EnumStatus.VALIDATED.toString())));

    }

    private ResultActions check(String endpoint, Long childRequestID) throws Exception {
        withRequest = get(endpoint, childRequestID);

        return abstractCheck();
    }
}
