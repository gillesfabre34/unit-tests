package com.airbus.retex.service.filtering;

import com.airbus.retex.AbstractServiceIT;
import com.airbus.retex.business.dto.filtering.FilteringFullDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.childrequest.PhysicalPart;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.filtering.Filtering;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.model.routing.Routing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilteringGetServiceIT extends AbstractServiceIT {

    private static final Integer EQUIPMENT_NUMBER = 123456;
    @Autowired
    private IFilteringService filteringService;
    @Autowired
    private IRetrieveChildRequestService retrieveChildRequestService;

    private Filtering filtering;
    private ChildRequest childRequest;
    private PhysicalPart physicalPart;
    private Part part;

    @BeforeEach
    public void before() {
        part = dataSetInitializer.createPart(Set.of(dataset.mpn_1));
        Request parentRequest = dataSetInitializer.createRequest("request 1");
        Routing routing = dataSetInitializer.createRouting(part);
        childRequest = dataSetInitializer.createChildRequest(cr -> {
            cr.setParentRequest(parentRequest);
            cr.setRoutingNaturalId(routing.getNaturalId());
            cr.setRouting(routing);
            cr.setStatus(EnumStatus.CREATED);
        });

        physicalPart = dataSetInitializer.createPhysicalPart(pp -> {
            pp.setChildRequest(childRequest);
            pp.setSerialNumber(dataSetInitializer.SERIAL_NUMBER_PHYSICAL_PART);
            pp.setPart(part);
        });
        childRequest.addPhysicalPart(physicalPart);

        filtering = dataSetInitializer.createFiltering(f -> {
            f.setPhysicalPart(physicalPart);
            f.addMedia(dataset.media_1);
    });
    }

    @Test
    public void getFiltering_OK_existing_physicalPart() throws FunctionalException {
        FilteringFullDto filteringFullDto = filteringService.findFilteringById(filtering.getId(), Language.FR);

        assertEquals(filteringFullDto.getId(), filtering.getId());
    }

    @Test
    public void getFiltering_OK_checkRetrievingChildRequest() throws FunctionalException {
        FilteringFullDto filteringFullDto = filteringService.findFilteringById(filtering.getId(), Language.FR);

        assertEquals(filteringFullDto.getCanCreateDrt(), retrieveChildRequestService.getChildRequest(part.getPartNumber(), dataSetInitializer.SERIAL_NUMBER_PHYSICAL_PART).isPresent());
    }

    @Test
    public void getFiltering_OK_check_medias() throws FunctionalException {
        FilteringFullDto filteringFullDto = filteringService.findFilteringById(filtering.getId(), Language.FR);

        assertEquals(filteringFullDto.getMedias().size(),filtering.getMedias().size());
    }
}

