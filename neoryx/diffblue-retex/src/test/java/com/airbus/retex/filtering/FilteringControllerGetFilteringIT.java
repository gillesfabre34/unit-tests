package com.airbus.retex.filtering;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.dataset.DatasetInitializer;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.childrequest.PhysicalPart;
import com.airbus.retex.model.common.EnumFilteringPosition;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.drt.Drt;
import com.airbus.retex.model.filtering.Filtering;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.model.routing.Routing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class FilteringControllerGetFilteringIT extends BaseControllerTest {

    private static final Integer EQUIPMENT_NUMBER = 123456;
    private static final String API_FILTERINGS = "/api/filterings";
    private static final String API_FILTERING = API_FILTERINGS + "/{id}";
    private static final Long WRONG_FILTERING_ID = 1000L;
    private static final String PARENT_REQUEST_NAME = "Parent request ";
    private static final String NOTIFICATION_1 = "Notification 1";
    private Filtering filtering;
    private ChildRequest childRequest;
    private PhysicalPart physicalPart;
    private Part part;
    private Request parentRequest;
    private Drt drt;

    @BeforeEach
    public void before() {
        runInTransaction(
                () -> {
                    part = dataSetInitializer.createPart(part1 -> part1.setMedia(dataset.media_1), Set.of(dataset.mpn_1));
                    parentRequest = dataSetInitializer.createRequest(PARENT_REQUEST_NAME);

                    Routing routing = dataSetInitializer.createRouting(part);
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

                    drt = dataSetInitializer.createDRT(drt1 -> drt1.setChildRequest(childRequest));

                    filtering = dataSetInitializer.createFiltering(filtering1 -> {
                        filtering1.setDrt(drt);
                        filtering1.setNotification(NOTIFICATION_1);
                        filtering1.setPosition(EnumFilteringPosition.FRONT);
                        filtering1.setPhysicalPart(physicalPart);
                        filtering1.addMedia(dataset.media_1);
                    });
                });
        dataSetInitializer.createUserFeature(FeatureCode.FILTERING, dataset.user_simpleUser, EnumRightLevel.WRITE);

    }

    @Test
    public void getFiltering_ok() throws Exception {
        asUser = dataset.user_simpleUser;
        expectedStatus = HttpStatus.OK;

        withRequest = get(API_FILTERING, filtering.getId());

        abstractCheck()
                .andExpect(jsonPath("$.id").value(filtering.getId()))
                .andExpect(jsonPath("$.drtId").value(drt.getId()))
                .andExpect(jsonPath("$.partNumber").value(filtering.getPhysicalPart().getPart().getPartNumber()))
                .andExpect(jsonPath("$.serialNumber").value(filtering.getPhysicalPart().getSerialNumber()))
                .andExpect(jsonPath("$.equipmentNumber", notNullValue()))
                .andExpect(jsonPath("$.designation.id").value(childRequest.getRouting().getPart().getPartDesignation().getId()))
                .andExpect(jsonPath("$.status").value(filtering.getStatus().toString()))
                .andExpect(jsonPath("$.origin.name").value(parentRequest.getOrigin().getName()))
                .andExpect(jsonPath("$.aircraftFamily.id").value(filtering.getAircraftFamily().getId()))
                .andExpect(jsonPath("$.aircraftType.id").value(filtering.getAircraftType().getId()))
                .andExpect(jsonPath("$.aircraftVersion.id").value(filtering.getAircraftVersion().getId()))
                .andExpect(jsonPath("$.aircraftSerialNumber").value(dataSetInitializer.SERIAL_NUMBER_AIRCRAFT))
                .andExpect(jsonPath("$.associatedRequest", containsString(PARENT_REQUEST_NAME)))
                .andExpect(jsonPath("$.filteringDate", notNullValue()))
                .andExpect(jsonPath("$.lastModified", nullValue()))
                .andExpect(jsonPath("$.position").value(EnumFilteringPosition.FRONT.toString()))
                .andExpect(jsonPath("$.notification").value(NOTIFICATION_1))
                .andExpect(jsonPath("$.canCreateDrt").value(false))
                .andExpect(jsonPath("$.mpn", notNullValue()))
                .andExpect(jsonPath("$.mpn.code").value(dataset.mpn_1.getCode()))
                .andExpect(jsonPath("$.medias[*]",notNullValue()))
                .andExpect(jsonPath("$.partMedia", notNullValue()))
                .andExpect(jsonPath("$.partMedia.uuid").value(dataset.media_1.getUuid().toString()));
    }

    @Test
    public void getFiltering_ok_withoutChildRequest() throws Exception {
        childRequest.setPhysicalParts(new HashSet<>());
        physicalPart = dataSetInitializer.createPhysicalPart(pp -> {
            pp.setEquipmentNumber(dataSetInitializer.getNextCode(4));
            pp.setSerialNumber(DatasetInitializer.SERIAL_NUMBER_PHYSICAL_PART);
            pp.setPart(part);
        });
        childRequest.addPhysicalPart(physicalPart);
        filtering = dataSetInitializer.createFiltering(filtering1 -> {
            filtering1.setNotification(NOTIFICATION_1);
            filtering1.setPosition(EnumFilteringPosition.FRONT);
            filtering1.setPhysicalPart(physicalPart);
        });

        asUser = dataset.user_simpleUser;
        expectedStatus = HttpStatus.OK;

        withRequest = get(API_FILTERING, filtering.getId());

        abstractCheck()
                .andExpect(jsonPath("$.id").value(filtering.getId()))
                .andExpect(jsonPath("$.drtId", nullValue()))
                .andExpect(jsonPath("$.partNumber").value(filtering.getPhysicalPart().getPart().getPartNumber()))
                .andExpect(jsonPath("$.serialNumber").value(filtering.getPhysicalPart().getSerialNumber()))
                .andExpect(jsonPath("$.equipmentNumber", notNullValue()))
                .andExpect(jsonPath("$.designation.id").value(childRequest.getRouting().getPart().getPartDesignation().getId()))
                .andExpect(jsonPath("$.status").value(filtering.getStatus().toString()))
                .andExpect(jsonPath("$.origin", nullValue()))
                .andExpect(jsonPath("$.aircraftFamily.id").value(filtering.getAircraftFamily().getId()))
                .andExpect(jsonPath("$.aircraftType.id").value(filtering.getAircraftType().getId()))
                .andExpect(jsonPath("$.aircraftVersion.id").value(filtering.getAircraftVersion().getId()))
                .andExpect(jsonPath("$.aircraftSerialNumber").value(dataSetInitializer.SERIAL_NUMBER_AIRCRAFT))
                .andExpect(jsonPath("$.associatedRequest", nullValue()))
                .andExpect(jsonPath("$.filteringDate", notNullValue()))
                .andExpect(jsonPath("$.lastModified", nullValue()))
                .andExpect(jsonPath("$.position").value(EnumFilteringPosition.FRONT.toString()))
                .andExpect(jsonPath("$.notification").value(NOTIFICATION_1))
                .andExpect(jsonPath("$.canCreateDrt").value(false))
                .andExpect(jsonPath("$.mpn", notNullValue()))
                .andExpect(jsonPath("$.mpn.code").value(dataset.mpn_1.getCode()))
                .andExpect(jsonPath("$.medias[*]",notNullValue()))
                .andExpect(jsonPath("$.partMedia", notNullValue()))
                .andExpect(jsonPath("$.partMedia.uuid").value(dataset.media_1.getUuid().toString()));
    }

    @Test
    public void getFiltering_ko_forbidden() throws Exception {
        asUser = dataset.user_simpleUser2;
        expectedStatus = HttpStatus.FORBIDDEN;

        withRequest = get(API_FILTERING, filtering.getId());

        abstractCheck();
    }


    @Test
    public void getFiltering_ko_wrongFilteringId() throws Exception {
        asUser = dataset.user_simpleUser;
        withRequest = get(API_FILTERING, 0);

        expectedStatus = HttpStatus.NOT_FOUND;
        abstractCheck()
        .andExpect(jsonPath("$.messages").value("Filtering is not found"));
    }

}
