package com.airbus.retex.drt;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.dataset.DatasetInitializer;
import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.childrequest.PhysicalPart;
import com.airbus.retex.model.common.EnumFilteringPosition;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.drt.Drt;
import com.airbus.retex.model.filtering.Filtering;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.model.routing.Routing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Set;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class DrtControllerGetHeaderIT extends BaseControllerTest {

    private static final String API_DRT_HEADER = "/api/drts/{id}/header";
    private static final String ASSOCIATED_NAME = "parentrequestname";

    private Filtering filtering;
    private ChildRequest childRequest;
    private PhysicalPart physicalPart;
    private Part part;
    private Request parentRequest;
    private Drt drt;
    private Routing routing;

    @BeforeEach
    public void setup() {
        runInTransaction( () -> {
            part = dataSetInitializer.createPart(part1 -> part1.setMedia(dataset.media_1), Set.of(dataset.mpn_1));
            parentRequest = dataSetInitializer.createRequest(ASSOCIATED_NAME);

            routing = dataSetInitializer.createRouting(part);
            childRequest = dataSetInitializer.createChildRequest(cr -> {
                cr.setParentRequest(parentRequest);
                cr.setRouting(routing);
                cr.setRoutingNaturalId(routing.getNaturalId());
                cr.setStatus(EnumStatus.CREATED);
            });

            physicalPart = dataSetInitializer.createPhysicalPart(pp -> {
                pp.setEquipmentNumber(dataSetInitializer.getNextCode(4));
                pp.setSerialNumber(DatasetInitializer.SERIAL_NUMBER_PHYSICAL_PART);
                pp.setPart(part);
            });
            childRequest.addPhysicalPart(physicalPart);

            drt = dataSetInitializer.createDRT(drt1 -> {
                drt1.setChildRequest(childRequest);
                drt1.setAssignedOperator(dataset.user_superAdmin);
                drt1.setStatus(EnumStatus.CREATED);
            });

            filtering = dataSetInitializer.createFiltering(filtering1 -> {
                filtering1.setDrt(drt);
                filtering1.setPosition(EnumFilteringPosition.FRONT);
                filtering1.setPhysicalPart(physicalPart);
            });
        });
    }

    @Test
    public void getHeader_OK() throws Exception {
        asUser = dataset.user_superAdmin;
        withRequest = get(API_DRT_HEADER, drt.getId());
        expectedStatus = HttpStatus.OK;
        ResultActions res = abstractCheck();
        res.andExpect(jsonPath("$.id").value(drt.getId()))
                .andExpect(jsonPath("$.routingId").value(routing.getNaturalId()))
                .andExpect(jsonPath("$.routingVersion").value(routing.getVersionNumber()))
                .andExpect(jsonPath("$.partNumber").value(part.getPartNumber()))
                .andExpect(jsonPath("$.serialNumber").value(physicalPart.getSerialNumber()))
                .andExpect(jsonPath("$.associatedRequest", notNullValue()))
                .andExpect(jsonPath("$.designation.id").value(part.getPartDesignation().getId()))
                .andExpect(jsonPath("$.integrationDate").value(drt.getIntegrationDate().toString()))
                .andExpect(jsonPath("$.status").value(drt.getStatus().toString()))
                .andExpect(jsonPath("$.origin.name").value(parentRequest.getOrigin().getName()))
                .andExpect(jsonPath("$.assignedOperator.id").value(drt.getAssignedOperator().getId()))
        ;
    }

    @Test
    public void getHeader_KO_forbidden() throws Exception {
        asUser = dataset.user_simpleUser;
        withRequest = get(API_DRT_HEADER, 0);
        expectedStatus = HttpStatus.FORBIDDEN;
        ResultActions res = abstractCheck();
    }
}
