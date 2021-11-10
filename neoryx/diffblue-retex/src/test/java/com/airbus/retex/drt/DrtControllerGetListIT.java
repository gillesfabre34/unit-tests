package com.airbus.retex.drt;
import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.admin.role.RoleCode;
import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.childrequest.PhysicalPart;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.drt.Drt;
import com.airbus.retex.model.filtering.Filtering;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.model.part.PartDesignation;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.model.routing.Routing;

import com.airbus.retex.persistence.childRequest.ChildRequestRepository;
import com.airbus.retex.persistence.drt.DrtRepository;
import com.airbus.retex.persistence.filtering.FilteringRepository;
import com.airbus.retex.persistence.part.PartDesignationRepository;

import com.airbus.retex.utils.ConstantUrl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class DrtControllerGetListIT extends BaseControllerTest {
    private static final String API_DRTS = "/api/drts";

    @Autowired
    private DrtRepository drtRepository;

    @Autowired
    private FilteringRepository filteringRepository;

    @Autowired
    private ChildRequestRepository childRequestRepository;
    @Autowired
    private PartDesignationRepository partDesignationRepository;

    private Filtering filtering1;
    private Filtering filtering2;

    private Drt drt1;
    private Drt drt2;
    private PhysicalPart physicalPart1;
    private PhysicalPart physicalPart2;
    private ChildRequest childRequest;
    private Part part1;
    private Part part2;
    private Request parentRequest;
    private PartDesignation partDesignation1;
    private PartDesignation partDesignation2;
    private static String PART_DESIGNATION1_EN = "partDesignation1_EN";
    private static String PART_DESIGNATION2_EN = "partDesignation2_EN";
    private static String PART_NUMBER1 = "110000000011";
    private static String PART_NUMBER2 = "220000000022";

    @BeforeEach
    private void before() {
        parentRequest = dataSetInitializer.createRequest("request 1");
        partDesignation1 = dataSetInitializer.createPartDesignation(PART_DESIGNATION1_EN, "partDesignation1_FR");
        partDesignation2 = dataSetInitializer.createPartDesignation(PART_DESIGNATION2_EN, "partDesignation2_FR");
        part1 = dataSetInitializer.createPart(part1 -> {
                    part1.setPartNumber(PART_NUMBER1);
                    part1.setPartDesignation(partDesignation1);
                }
                ,null);
        part2 = dataSetInitializer.createPart(part1 -> {
                    part1.setPartNumber(PART_NUMBER2);
                    part1.setPartDesignation(partDesignation1);
                }
                ,null);

        Routing routing = dataSetInitializer.createRouting(part1);
        childRequest = dataSetInitializer.createChildRequest(cr -> {
            cr.setParentRequest(parentRequest);
            cr.setRouting(routing);
            cr.setRoutingNaturalId(routing.getNaturalId());
            cr.setStatus(EnumStatus.CREATED);
        });



        physicalPart1 = dataSetInitializer.createPhysicalPart(physicalPart1 -> {
            physicalPart1.setPart(part1);
            physicalPart1.setChildRequest(childRequest);
            physicalPart1.setSerialNumber("02101");

        });
        childRequest.addPhysicalPart(physicalPart1);

        filtering1 = dataSetInitializer.createFiltering(filtering1 -> {
            filtering1.setPhysicalPart(physicalPart1);
            filtering1.getPhysicalPart().setChildRequest(childRequest);
        });

        physicalPart2 = dataSetInitializer.createPhysicalPart(physicalPart -> {
            physicalPart.setPart(part2);
            physicalPart.setChildRequest(childRequest);
            physicalPart.setSerialNumber("02102");
        });

        filtering2 = dataSetInitializer.createFiltering(filtering -> {
            filtering.setPhysicalPart(physicalPart2);
            filtering.getPhysicalPart().setChildRequest(childRequest);
        });

        drt1  = dataSetInitializer.createDRT(drt-> {
            drt.setFiltering(filtering1);
            drt.setStatus(EnumStatus.VALIDATED);
            drt.setIntegrationDate(LocalDate.now());
        });

        drt2  = dataSetInitializer.createDRT(drt-> {
            drt.setFiltering(filtering2);
            drt.setStatus(EnumStatus.IN_PROGRESS);
            drt.setIntegrationDate(LocalDate.now().plusDays(2));
        });

        filtering1.setDrt(drt1);
        filtering2.setDrt(drt2);
        // removing out of control DRT (we want to have only two created DRT for these tests)
        runInTransaction(() -> {
            drtRepository.delete(dataset.drt_example);
            filteringRepository.save(filtering1);
            filteringRepository.save(filtering2);
            childRequest.addDrt(drt1);
            childRequest.addDrt(drt2);
            childRequestRepository.save(childRequest);
        });
    }

    @Test
    public void searchDrtWithFilters_OK() throws Exception {
        asUser = dataSetInitializer.createUserWithRole("manager", "manager", RoleCode.MANAGER);
        dataSetInitializer.createUserFeature(FeatureCode.DRT_INTERNAL_INSPECTION, asUser, EnumRightLevel.READ);

        expectedStatus = HttpStatus.OK;


        withParams.add("page", "0");
        withParams.add("size", "10");


        ResultActions result = check(API_DRTS);
        result.andExpect(jsonPath("$.results", notNullValue()))
                .andExpect(jsonPath("$.results", hasSize(2)))
                .andExpect(jsonPath("$.totalResults").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.results[*].id", hasItems(drt1.getId().intValue(), drt2.getId().intValue())))

                .andExpect(jsonPath("$.results[*].partNumber", containsInAnyOrder(PART_NUMBER1, PART_NUMBER2)))
                .andExpect(jsonPath("$.results[*].serialNumber", containsInAnyOrder(drt1.getFiltering().getPhysicalPart().getSerialNumber().toString(), drt2.getFiltering().getPhysicalPart().getSerialNumber().toString())))
                .andExpect(jsonPath("$.results[*].status", containsInAnyOrder(drt1.getStatus().toString(), drt2.getStatus().toString())))
                .andExpect(jsonPath("$.results[*].origin.id", hasItems(parentRequest.getOrigin().getId().intValue())))
                .andExpect(jsonPath("$.results[*].origin.name", hasItems(parentRequest.getOrigin().getName())))
                .andExpect(jsonPath("$.results[*].assignedOperator", hasSize(2)))
                .andExpect(jsonPath("$.results[*].designation", hasSize(2)))
                .andExpect(jsonPath("$.results[0].designation.designation", is(oneOf(PART_DESIGNATION1_EN, PART_DESIGNATION2_EN))))
                .andExpect(jsonPath("$.results[1].designation.designation", is(oneOf(PART_DESIGNATION1_EN, PART_DESIGNATION2_EN))))
                .andExpect(jsonPath("$.results[*].integrationDate", containsInAnyOrder(drt1.getIntegrationDate().toString(), drt2.getIntegrationDate().toString())));


    }




    @Test
    public void getDrt_KO_StatusIsForbidden() throws Exception {
        asUser = dataset.user_simpleUser;
        expectedStatus = HttpStatus.FORBIDDEN;
        withRequest = get(ConstantUrl.API_DRTS );

        abstractCheck();

    }

    private ResultActions check(String endpoint) throws Exception {
        withRequest = get(endpoint);

        return abstractCheck();
    }

}
