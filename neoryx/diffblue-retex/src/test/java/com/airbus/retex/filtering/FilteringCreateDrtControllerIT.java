package com.airbus.retex.filtering;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.dataset.DatasetInitializer;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.childrequest.PhysicalPart;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.filtering.Filtering;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.model.routing.Routing;
import com.airbus.retex.persistence.filtering.FilteringRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class FilteringCreateDrtControllerIT extends BaseControllerTest {

    @Autowired
    private FilteringRepository filteringRepository;

    private static final Long WRONG_FILTERING_ID = 0L;
    private static final String API_FILTERING_POST_DRT = "/api/filterings/{id}/drt";

    private static final String PARENT_REQUEST_NAME = "Parent request ";
    private Filtering filtering;
    private ChildRequest childRequest;

    @BeforeEach
    public void before() {
        Part part = dataSetInitializer.createPart(null);
        Request parentRequest = dataSetInitializer.createRequest(PARENT_REQUEST_NAME);
        Routing routing = dataSetInitializer.createRouting(part);
        childRequest = dataSetInitializer.createChildRequest(cr -> {
            cr.setParentRequest(parentRequest);
            cr.setRoutingNaturalId(routing.getNaturalId());
            cr.setRouting(routing);
            cr.setStatus(EnumStatus.VALIDATED);
        });
        PhysicalPart physicalPart = dataSetInitializer.createPhysicalPart(pp -> {
            pp.setChildRequest(childRequest);
            pp.setSerialNumber(DatasetInitializer.SERIAL_NUMBER_PHYSICAL_PART);
            pp.setPart(part);
        });
        childRequest.addPhysicalPart(physicalPart);

        filtering = dataSetInitializer.createFiltering(filtering1 -> filtering1.setPhysicalPart(physicalPart));
        dataSetInitializer.createUserFeature(FeatureCode.FILTERING, dataset.user_simpleUser, EnumRightLevel.WRITE);
    }

    @Test
    public void createDrt_OK() throws Exception {
        asUser = dataset.user_simpleUser;
        expectedStatus = HttpStatus.OK;

        withRequest = post(API_FILTERING_POST_DRT, filtering.getId());

        abstractCheck()
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$").isNumber());

    }


    @Test
    public void createDrt_ko_forbidden() throws Exception {
        asUser = dataset.user_simpleUser2;
        expectedStatus = HttpStatus.FORBIDDEN;

        withRequest = post(API_FILTERING_POST_DRT, filtering.getId());

        abstractCheck();
    }

    @Test
    public void createDrt_ko_WrongId() throws Exception {
        asUser = dataset.user_simpleUser;
        expectedStatus = HttpStatus.NOT_FOUND;

        withRequest = post(API_FILTERING_POST_DRT, WRONG_FILTERING_ID);

        abstractCheck()
                .andExpect(jsonPath("$.messages").value("Filtering is not found" ));

    }

    @Test
    public void createDrt_ko_WrongPNSN() throws Exception {
        PhysicalPart physicalPart = dataSetInitializer
                .createPhysicalPart(pp -> pp.setPart(dataSetInitializer.createPart(null)));
        filtering.setPhysicalPart(physicalPart);
        filteringRepository.save(filtering);
        asUser = dataset.user_simpleUser;
        expectedStatus = HttpStatus.BAD_REQUEST;

        withRequest = post(API_FILTERING_POST_DRT, filtering.getId());

        abstractCheck()
                .andExpect(jsonPath("$.messages").value("Child Request not found with following PN/SN couple: "
                        + physicalPart.getPart().getPartNumber() + "/" + physicalPart.getSerialNumber()));

    }
}
