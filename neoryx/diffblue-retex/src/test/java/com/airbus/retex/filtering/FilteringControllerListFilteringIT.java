package com.airbus.retex.filtering;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.childrequest.PhysicalPart;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.filtering.Filtering;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.model.routing.Routing;
import com.airbus.retex.persistence.filtering.FilteringRepository;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class FilteringControllerListFilteringIT extends BaseControllerTest {

    private static final String EQUIPMENT_NUMBER = "123456";
    @Autowired
    private FilteringRepository filteringRepository;

    private final static String API_FILTERINGS = "/api/filterings";

    private Filtering filtering1;
    private Filtering filtering2;
    private PhysicalPart physicalPart1;
    private PhysicalPart physicalPart2;
    private ChildRequest childRequest;
    private Part part;

    @BeforeEach
    public void before() {
        runInTransaction(() -> {
            Request parentRequest = dataSetInitializer.createRequest("request 1");

            part = dataSetInitializer.createPart(part1 -> {
                        part1.setPartNumber("332A32302101");
                    }
                    ,null);

            Routing routing = dataSetInitializer.createRouting(part);
            childRequest = dataSetInitializer.createChildRequest(cr -> {
                cr.setParentRequest(parentRequest);
                cr.setRouting(routing);
                cr.setRoutingNaturalId(routing.getNaturalId());
                cr.setStatus(EnumStatus.CREATED);
            });

            physicalPart1 = dataSetInitializer.createPhysicalPart(physicalPart1 -> {
                physicalPart1.setPart(part);
                physicalPart1.setChildRequest(childRequest);
                physicalPart1.setEquipmentNumber(EQUIPMENT_NUMBER);
                physicalPart1.setSerialNumber("02101");
            });
            childRequest.addPhysicalPart(physicalPart1);

            filtering1 = dataSetInitializer.createFiltering(filtering1 -> {
                filtering1.setPhysicalPart(physicalPart1);
                filtering1.getPhysicalPart().setChildRequest(childRequest);
            });

            physicalPart2 = dataSetInitializer.createPhysicalPart(physicalPart -> {
                physicalPart.setPart(part);
                physicalPart.setChildRequest(childRequest);
                physicalPart.setEquipmentNumber(EQUIPMENT_NUMBER);
                physicalPart.setSerialNumber("02102");
            });

            filtering2 = dataSetInitializer.createFiltering(filtering -> {
                filtering.setPhysicalPart(physicalPart2);
                filtering.getPhysicalPart().setChildRequest(childRequest);
            });
        });
    }

    @Test
    public void getAllFilterings_Ok() throws Exception {
        asUser = dataset.user_superAdmin;
        expectedStatus = HttpStatus.OK;

        checkGetAllFilterings(hasSize((int) filteringRepository.count()));

    }

    @Test
    public void getAllFilterings_Forbidden() throws Exception {
        asUser = dataset.user_simpleUser;
        expectedStatus = HttpStatus.FORBIDDEN;
        withRequest = get(API_FILTERINGS);
        abstractCheck();

    }

    @Test
    public void getAllFilteringsPaginated_Ok() throws Exception {
        asUser = dataset.user_superAdmin;
        withParams.add("page", "1");
        withParams.add("size", "2");
        expectedStatus = HttpStatus.OK;

        checkGetAllFilterings(hasSize(2));
    }

    @Test
    public void getAllFilteringsWithPNFilter_Ok() throws Exception {
        asUser = dataset.user_superAdmin;
        withParams.add("partNumber", "332A32302101");
        expectedStatus = HttpStatus.OK;

        checkGetAllFilterings(hasSize(2));
    }

    @Test
    public void getAllFilteringsWithSNFilter_Ok() throws Exception {
        asUser = dataset.user_superAdmin;
        withParams.add("serialNumber", "02101");
        expectedStatus = HttpStatus.OK;

        checkGetAllFilterings(hasSize(1));
        checkGetAllFilterings(List.of(filtering1),List.of(filtering2));
    }

    @Test
    public void getAllFilteringsWithPNAndSNFilters_Ok() throws Exception {
        asUser = dataset.user_superAdmin;
        withParams.add("partNumber", "332A32302101");
        withParams.add("serialNumber", "02102");
        expectedStatus = HttpStatus.OK;

        checkGetAllFilterings(hasSize(1));
        checkGetAllFilterings(List.of(filtering2),List.of(filtering1));
    }

    private void checkGetAllFilterings(List<Filtering> contains, List<Filtering> notContains) throws Exception {
        withRequest = get(API_FILTERINGS);

        Integer[] containsIds = contains.stream().map(filtering -> Integer.valueOf(filtering.getId().intValue())).collect(Collectors.toList()).toArray(new Integer[0]);
        Integer[] notContainsId = notContains.stream().map(filtering -> Integer.valueOf(filtering.getId().intValue())).collect(Collectors.toList()).toArray(new Integer[0]);

        abstractCheck()
                .andExpect(jsonPath("$.results[*].id", hasItems(containsIds)))
                .andExpect(jsonPath("$.results[*].id", not(hasItems(notContainsId))));
    }

    private void checkGetAllFilterings(Matcher<? extends Collection> resultsMatcher) throws Exception {
        withRequest = get(API_FILTERINGS);

        abstractCheck()
                .andExpect(jsonPath("$.results", notNullValue()))
                .andExpect(jsonPath("$.results", resultsMatcher));
    }
}
