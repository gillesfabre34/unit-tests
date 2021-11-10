package com.airbus.retex.service.filtering;

import com.airbus.retex.AbstractServiceIT;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.dataset.DatasetInitializer;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.childrequest.PhysicalPart;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.drt.Drt;
import com.airbus.retex.model.filtering.Filtering;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.model.routing.Routing;
import com.airbus.retex.persistence.childRequest.ChildRequestRepository;
import com.airbus.retex.persistence.drt.DrtRepository;
import com.airbus.retex.persistence.filtering.FilteringRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;


public class FilteringCreateDrtServiceIT extends AbstractServiceIT {
    @Autowired
    IFilteringService filteringService;

    @Autowired
    DrtRepository drtRepository;

    @Autowired
    ChildRequestRepository childRequestRepository;

    @Autowired
    FilteringRepository filteringRepository;

    private static final Long WRONG_FILTERING_ID = 0L;
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
    public void createDrt_ok() throws FunctionalException {
        Long drtId = filteringService.createDrt(filtering.getId());
        Drt drt = drtRepository.findById(drtId).get();
        childRequest = childRequestRepository.findById(childRequest.getId()).get();
        filtering = filteringRepository.findById(filtering.getId()).get();

        assertEquals(drtId, drt.getId());
        assertEquals(childRequest.getId(), drt.getChildRequest().getId());
        assertEquals(filtering.getId(), drt.getFiltering().getId());
        assertNotNull(drt.getIntegrationDate());
        assertEquals(EnumStatus.CREATED, drt.getStatus());

        assertTrue(childRequest.getDrts().size() == 1);

        assertEquals(drtId, filtering.getDrt().getId());
        assertNotNull(filtering.getLastModified());
        assertEquals(EnumStatus.IN_PROGRESS, filtering.getStatus());
    }

    @Test
    public void createDrt_ko_WrongId() {
        FunctionalException thrown = assertThrows(FunctionalException.class, () -> {
            filteringService.createDrt(WRONG_FILTERING_ID);
        });
        assertEquals("retex.error.filtering.not.found", thrown.getMessage());
    }

    @Test
    public void createDrt_ko_WrongPNSN() {
        PhysicalPart physicalPart = dataSetInitializer
                .createPhysicalPart(pp -> pp.setPart(dataSetInitializer.createPart(null)));
        filtering.setPhysicalPart(physicalPart);
        filteringRepository.save(filtering);
        FunctionalException thrown = assertThrows(FunctionalException.class, () -> {
            filteringService.createDrt(filtering.getId());
        });

        assertEquals("retex.error.drt.creation.childrequest.not.found", thrown.getMessage());
        Object[] pnSnCouple = thrown.getCodeArgs().values().iterator().next();
        assertEquals(pnSnCouple[0], physicalPart.getPart().getPartNumber());
        assertEquals(pnSnCouple[1], physicalPart.getSerialNumber());
    }
}

