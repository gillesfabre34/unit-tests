package com.airbus.retex.service.drt;

import com.airbus.retex.AbstractServiceIT;
import com.airbus.retex.business.dto.drt.DrtHeaderDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.dataset.DatasetInitializer;
import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.childrequest.PhysicalPart;
import com.airbus.retex.model.common.EnumFilteringPosition;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.drt.Drt;
import com.airbus.retex.model.filtering.Filtering;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.model.routing.Routing;
import com.airbus.retex.persistence.drt.DrtRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.Charset;
import java.util.Random;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.*;


public class DrtGetHeaderServiceIT extends AbstractServiceIT {

    @Autowired
    IDrtService drtService;

    @Autowired
    DrtRepository drtRepository;

    private static final String ASSOCIATED_NAME = "parentrequestname";

    private Filtering filtering;
    private ChildRequest childRequest;
    private PhysicalPart physicalPart;
    private Part part;
    private Request parentRequest;
    private Drt drt;
    private Routing routing;

    @BeforeEach
    public void before() {
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
                byte[] equipmentNumber = new byte[8];
                new Random().nextBytes(equipmentNumber);
                pp.setEquipmentNumber(new String(equipmentNumber, Charset.forName("UTF-8")));
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
        DrtHeaderDto drtHeaderDto = drtService.getHeader(drt.getId(), Language.EN);
        basicAssert(drtHeaderDto);
    }

    @Test
    public void getHeader_OK_routing_set_in_drt() throws Exception {
        // set a routing in drt which is different than the one in childrequest
        assertNotEquals(routing.getNaturalId(), dataset.routing_3.getNaturalId());
        runInTransaction(()->{
            Drt d = drtRepository.getOne(drt.getId());
            d.setRouting(dataset.routing_3);
            drtRepository.saveAndFlush(d);
        });
        routing = dataset.routing_3; // basicAssert compares the result with routing

        DrtHeaderDto drtHeaderDto = drtService.getHeader(drt.getId(), Language.EN);
        basicAssert(drtHeaderDto);
    }

    @Test
    public void getHeader_KO_not_found() throws Exception {
        FunctionalException thrown = assertThrows(FunctionalException.class, () -> {
            drtService.getHeader(0L, Language.EN);
        });
        assertEquals("retex.error.drt.not.found", thrown.getMessage());
    }

    private void basicAssert(DrtHeaderDto dto) throws Exception {
        assertNotNull(dto);
        assertEquals(drt.getId(), dto.getId());
        assertThat(dto.getAssociatedRequest(), startsWith(ASSOCIATED_NAME));
        assertEquals(routing.getNaturalId(), dto.getRoutingId());
        assertEquals(part.getPartDesignation().getId(), dto.getDesignation().getId());
        assertEquals(routing.getVersionNumber(), dto.getRoutingVersion());
        assertEquals(dataset.user_superAdmin.getId(), dto.getAssignedOperator().getId());
        assertEquals(drt.getIntegrationDate(), dto.getIntegrationDate());
        assertEquals(EnumStatus.CREATED, dto.getStatus());
        assertEquals(part.getPartNumber(), dto.getPartNumber());
        assertEquals(physicalPart.getSerialNumber(), dto.getSerialNumber());
        assertEquals(parentRequest.getOrigin().getName(), dto.getOrigin().getName());
    }
}
