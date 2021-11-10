package com.airbus.retex.childRequest;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.business.dto.childRequest.ChildRequestSortingValues;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.client.Client;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.model.routing.Routing;
import com.airbus.retex.persistence.aircraft.AircraftFamilyRepository;
import com.airbus.retex.persistence.aircraft.AircraftTypeRepository;
import com.airbus.retex.persistence.aircraft.AircraftVersionRepository;
import com.airbus.retex.persistence.client.ClientRepository;
import com.airbus.retex.persistence.routing.RoutingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class ChildRequestControllerIT extends BaseControllerTest {
    private static final String API_CHILD_REQUEST = "/api/requests/{id}/child-requests";
    private static final String API_CHILD_REQUEST_ID = API_CHILD_REQUEST + "/{childRequestId}";

    @Autowired
    private AircraftFamilyRepository aircraftFamilyRepository;
    @Autowired
    private AircraftTypeRepository aircraftTypeRepository;
    @Autowired
    private AircraftVersionRepository aircraftVersionRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private RoutingRepository routingRepository;


    @Test
    public void getFilteredChildRequestsOfRequest_ok() throws Exception{
        asUser = dataset.user_simpleUser;
        dataset.routing_1.setStatus(EnumStatus.VALIDATED);
        routingRepository.save(dataset.routing_1);
        Request request = dataset.request_1;
        createChildRequests(request, 5);
        this.dataSetInitializer.createUserFeature(FeatureCode.REQUEST, asUser, EnumRightLevel.READ);
        expectedStatus = HttpStatus.OK;
        withParams.add("sortBy", ChildRequestSortingValues.id.name());
        withRequest = get(API_CHILD_REQUEST, request.getId());
        checkListChildRequests();
    }

    private void checkListChildRequests() throws Exception{
        abstractCheck()
                .andExpect(jsonPath("$.results[0].id", notNullValue()))
                .andExpect(jsonPath("$.results[0].partNumberRoot", notNullValue()))
                .andExpect(jsonPath("$.results[0].partNumber", notNullValue()))
                .andExpect(jsonPath("$.results[0].designation", notNullValue()))
                .andExpect(jsonPath("$.results[0].drtTotal", notNullValue()))
                .andExpect(jsonPath("$.results[0].drtTreated", notNullValue()))
                .andExpect(jsonPath("$.results[0].isDeletable", notNullValue()))
                .andExpect(jsonPath("$.results[0].status", notNullValue()))
                .andExpect(jsonPath("$.results[0].versionNumber", notNullValue()));
    }

    private void createChildRequests(Request request, int nbChilds){
        for (int i=0; i<nbChilds; i++) {
            ChildRequest childRequest = dataSetInitializer.createChildRequest((r) -> {
                r.setParentRequest(request);
            });
        }
    }

    @Test
    public void getOneChildRequest_OK() throws Exception{
        asUser = dataset.user_simpleUser;
        this.dataSetInitializer.createUserFeature(FeatureCode.REQUEST, asUser, EnumRightLevel.READ);
        expectedStatus = HttpStatus.OK;

        AtomicReference<ChildRequest> childRequest = new AtomicReference<>();

        runInTransaction(() -> {
            Routing routing = dataSetInitializer.createRouting(r -> {
                r.setStatus(EnumStatus.VALIDATED);
            }, dataset.part_link_routing);

            childRequest.set(dataSetInitializer.createChildRequest(cr -> {
                cr.setRoutingNaturalId(routing.getNaturalId());
                cr.setStatus(EnumStatus.CLOSED);
            }));

        });
        Request request = dataset.request_1;

        withRequest = get(API_CHILD_REQUEST_ID, request.getId(), childRequest.get().getId()).contentType(MediaType.APPLICATION_JSON);
        abstractCheck()
                .andExpect(jsonPath("$.serialNumbers").isArray())
                .andExpect(jsonPath("$.serialNumbers").isNotEmpty())
                .andExpect(jsonPath("$.medias").isArray())
                .andExpect(jsonPath("$.medias").isNotEmpty())
                .andExpect(jsonPath("$.aircraftFamily", notNullValue()))
                .andExpect(jsonPath("$.aircraftTypes", notNullValue()))
                .andExpect(jsonPath("$.aircraftVersions", notNullValue()))
                .andExpect(jsonPath("$.missionType", notNullValue()))
                .andExpect(jsonPath("$.ataCode", notNullValue()))
                .andExpect(jsonPath("$.environment", notNullValue()))
                .andExpect(jsonPath("$.childRequestClients").isArray())
                .andExpect(jsonPath("$.childRequestClients").isNotEmpty())
                .andExpect(jsonPath("$.parentRequestClients").isArray())
                .andExpect(jsonPath("$.parentRequestClients").isNotEmpty())
                .andExpect(jsonPath("$.routing", notNullValue()))
                .andExpect(jsonPath("$.isDeletable").value(false))
                .andExpect(jsonPath("$.modulation", notNullValue()))
                .andExpect(jsonPath("$.status", notNullValue()))
                .andExpect(jsonPath("$.versionNumber", notNullValue()));
    }

    @Test
    public void getOneChildRequest_ChildClientsExceedParentClients_Exception() throws Exception {
        asUser = dataset.user_simpleUser;
        this.dataSetInitializer.createUserFeature(FeatureCode.REQUEST, asUser, EnumRightLevel.READ);
        expectedStatus = HttpStatus.BAD_REQUEST;
        Client exceededClient = dataSetInitializer.createClient("Client numéro X");
        language= Language.FR;
        ChildRequest childRequest = dataSetInitializer.createChildRequest(cr -> {
            cr.addClient(clientRepository.findById(exceededClient.getId()).get());
        });
        Request request = dataset.request_1;

        withRequest = get(API_CHILD_REQUEST_ID, request.getId(), childRequest.getId()).contentType(MediaType.APPLICATION_JSON);
        checkFunctionalException("retex.error.childrequest.clients.are.not.subset.of.parent.request");
    }
}
