package com.airbus.retex.service.filtering;

import com.airbus.retex.AbstractServiceIT;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.childrequest.PhysicalPart;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.drt.Drt;
import com.airbus.retex.model.origin.Origin;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.model.routing.Routing;
import com.airbus.retex.persistence.childRequest.ChildRequestRepository;
import com.airbus.retex.persistence.drt.DrtRepository;
import com.airbus.retex.persistence.part.PartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class RetrieveChildRequestServiceIT extends AbstractServiceIT {

    @Autowired
    private PartRepository partRepository;

    @Autowired
    private ChildRequestRepository childRequestRepository;

    @Autowired
    private IRetrieveChildRequestService retrieveChildRequestService;

    @Autowired
    private DrtRepository drtRepository;

    private static final String REQ_NAME_1 = "requestname1";
    private static final String SERIAL_NUMBER = "18254986";
    private static final String SERIAL_NUMBER_2 = "00462550";
    private static final String WRONG_SERIAL_NUMBER = "000000";

    ChildRequest childRequest;

    @BeforeEach
    public void before() {
        runInTransaction(() -> initializeChildRequest(dataset.ORIGIN_RETEX, dataSetInitializer.createPart(null),
                5L, SERIAL_NUMBER, EnumStatus.VALIDATED, new ArrayList<>())
        );
    }

    private ChildRequest initializeChildRequest(Origin origin, Part part, Long drtToInspect, String serialNumber,
                                                EnumStatus status, List<Drt> drts) {
        Request parentRequest = dataSetInitializer.createRequest(REQ_NAME_1, request -> {
            request.setOrigin(origin);
        });
        Routing routing = dataSetInitializer.createRouting(r -> r.setPart(part), part);
        childRequest = dataSetInitializer.createChildRequest(cr -> {
            cr.setRouting(routing);
            cr.setRoutingNaturalId(routing.getNaturalId());
            cr.setParentRequest(parentRequest);
            cr.setDrtToInspect(drtToInspect);
            cr.setStatus(status);
            for (Drt drt : drts) {
                cr.addDrt(drt);
            }
        });
        PhysicalPart physicalPart = dataSetInitializer.createPhysicalPart(pp -> {
            pp.setChildRequest(childRequest);
            pp.setSerialNumber(serialNumber);
            pp.setPart(part);
        });
        childRequest.addPhysicalPart(physicalPart);
        childRequestRepository.saveAndFlush(childRequest);

        for (Drt drt : drts) {
            drt.setChildRequest(childRequest);
            drtRepository.saveAndFlush(drt);
        }
        return childRequest;
    }


    private void checkFunctionalException(String pn, String sn) throws FunctionalException {
        assertEquals(retrieveChildRequestService.getChildRequest(pn, sn), Optional.empty());
    }

    private void createChildRequestPriority2() {
        createChildRequestPriority2_1();
        createChildRequestPriority2_2();
        createChildRequestPriority2_3();
    }

    private ChildRequest createChildRequestPriority2_1() {
        return initializeChildRequest(dataset.ORIGIN_CIVP, dataSetInitializer.createPart(null),
                5L, dataSetInitializer.SERIAL_NUMBER_PHYSICAL_PART, EnumStatus.VALIDATED, new ArrayList<>());
    }

    private ChildRequest createChildRequestPriority2_2() {
        return initializeChildRequest(dataset.ORIGIN_CIVP, dataSetInitializer.createPart(null),
                1L, SERIAL_NUMBER, EnumStatus.VALIDATED, new ArrayList<>());
    }

    private ChildRequest createChildRequestPriority2_3() {
        dataset.part_example_2.setPartNumber("");
        dataset.part_example_2.setPartNumberRoot(dataset.part_example.getPartNumber());
        partRepository.save(dataset.part_example_2);
        return initializeChildRequest(dataset.ORIGIN_CIVP, dataset.part_example_2,
                1L, SERIAL_NUMBER_2, EnumStatus.VALIDATED, new ArrayList<>());
    }

    private void createChildRequestPriority3() {
        createChildRequestPriority3_1();
        createChildRequestPriority3_2();
        createChildRequestPriority3_3();
    }

    private ChildRequest createChildRequestPriority3_1() {
        return initializeChildRequest(dataset.ORIGIN_RETEX, dataSetInitializer.createPart(null),
                1L, dataSetInitializer.SERIAL_NUMBER_PHYSICAL_PART, EnumStatus.VALIDATED, new ArrayList<>());
    }

    private ChildRequest createChildRequestPriority3_2() {
        return initializeChildRequest(dataset.ORIGIN_RETEX, dataSetInitializer.createPart(null),
                1L, SERIAL_NUMBER, EnumStatus.VALIDATED, new ArrayList<>());
    }

    private ChildRequest createChildRequestPriority3_3() {
        dataset.part_example_2.setPartNumber("");
        dataset.part_example_2.setPartNumberRoot(dataset.part_example.getPartNumber());
        partRepository.save(dataset.part_example_2);
        return initializeChildRequest(dataset.ORIGIN_RETEX, dataset.part_example_2,
                1L, SERIAL_NUMBER_2, EnumStatus.VALIDATED, new ArrayList<>());
    }

    @Test
    public void getChildRequestWithOriginISIR_succeed() throws FunctionalException {
        createChildRequestPriority2();
        createChildRequestPriority3();

        childRequest = initializeChildRequest(dataset.ORIGIN_ISIR, dataset.part_example,
                1L, dataSetInitializer.SERIAL_NUMBER_PHYSICAL_PART, EnumStatus.VALIDATED, new ArrayList<>());
        ChildRequest childRequestResult = retrieveChildRequestService
                .getChildRequest(dataset.part_example.getPartNumber(), dataSetInitializer.SERIAL_NUMBER_PHYSICAL_PART).get();
        assertEquals(childRequest.getId(), childRequestResult.getId());
    }

    @Test
    public void getChildRequestWithOriginCIVP_PN_SN_succeed() throws FunctionalException {
            createChildRequestPriority2_2();
            createChildRequestPriority2_3();
            createChildRequestPriority3();

            childRequest = createChildRequestPriority2_1();

        ChildRequest childRequestResult = retrieveChildRequestService
                .getChildRequest(childRequest.getRouting().getPart().getPartNumber(), dataSetInitializer.SERIAL_NUMBER_PHYSICAL_PART).get();
        assertEquals(childRequest.getId(), childRequestResult.getId());
    }


    @Test
    public void getChildRequestWithOriginCIVP_PN_succeed() throws FunctionalException {
        createChildRequestPriority2_3();
        createChildRequestPriority3();

        childRequest = createChildRequestPriority2_2();
        ChildRequest childRequestResult = retrieveChildRequestService
                .getChildRequest(childRequest.getRouting().getPart().getPartNumber(), WRONG_SERIAL_NUMBER).get();
        assertEquals(childRequest.getId(), childRequestResult.getId());
    }


    @Test
    public void getChildRequestWithOriginCIVP_PNRoot_succeed() throws FunctionalException {
        createChildRequestPriority3();
        childRequest = createChildRequestPriority2_3();

        ChildRequest childRequestResult = retrieveChildRequestService
                .getChildRequest(childRequest.getRouting().getPart().getPartNumberRoot(), WRONG_SERIAL_NUMBER).get();
        assertEquals(childRequest.getId(), childRequestResult.getId());
    }


    @Test
    public void getChildRequestWithOriginRETEX_PN_SN_succeed() throws FunctionalException {
        createChildRequestPriority3_2();
        createChildRequestPriority3_3();
        childRequest = createChildRequestPriority3_1();
        ChildRequest childRequestResult = retrieveChildRequestService
                .getChildRequest(childRequest.getRouting().getPart().getPartNumber(), dataSetInitializer.SERIAL_NUMBER_PHYSICAL_PART).get();
        assertEquals(childRequest.getId(), childRequestResult.getId());
    }


    @Test
    public void getChildRequestWithOriginRETEX_PN_succeed() throws FunctionalException {
        childRequest = createChildRequestPriority3_2();
        ChildRequest childRequestResult = retrieveChildRequestService
                .getChildRequest(childRequest.getRouting().getPart().getPartNumber(), SERIAL_NUMBER).get();
        assertEquals(childRequest.getId(), childRequestResult.getId());
    }


    @Test
    public void getChildRequestWithOriginRETEX_PNRoot_succeed() throws FunctionalException {
        childRequest = createChildRequestPriority3_3();

        ChildRequest childRequestResult = retrieveChildRequestService
                .getChildRequest(childRequest.getRouting().getPart().getPartNumberRoot(), SERIAL_NUMBER_2).get();
        assertEquals(childRequest.getId(), childRequestResult.getId());
    }


    @Test
    public void getChildRequestWithOrigin_failed_statusInvalid() throws FunctionalException {
        Part part = dataSetInitializer.createPart(null);
        initializeChildRequest(dataset.ORIGIN_ISIR, part,
                1L, dataSetInitializer.SERIAL_NUMBER_PHYSICAL_PART, EnumStatus.CREATED, new ArrayList<>());
        checkFunctionalException(
                part.getPartNumber(), dataSetInitializer.SERIAL_NUMBER_PHYSICAL_PART);
    }

    @Test
    public void getChildRequestWithOriginNull_failed() throws FunctionalException {
        runInTransaction(() -> initializeChildRequest(null, dataSetInitializer.createPart(null),
                1L, dataSetInitializer.SERIAL_NUMBER_PHYSICAL_PART, EnumStatus.VALIDATED, new ArrayList<>()));
        checkFunctionalException(childRequest.getRouting().getPart().getPartNumber(), dataSetInitializer.SERIAL_NUMBER_PHYSICAL_PART);
    }


    @Test
    public void getChildRequestWithOrigin_failed_multipleChildRequest() throws FunctionalException {
        initializeChildRequest(dataset.ORIGIN_CIVP, dataset.part_example,
                5L, dataSetInitializer.SERIAL_NUMBER_PHYSICAL_PART, EnumStatus.VALIDATED, new ArrayList<>());
        initializeChildRequest(dataset.ORIGIN_CIVP, dataset.part_example,
                5L, dataSetInitializer.SERIAL_NUMBER_PHYSICAL_PART, EnumStatus.VALIDATED, new ArrayList<>());
        FunctionalException thrown = assertThrows(FunctionalException.class, () -> {
            retrieveChildRequestService.getChildRequest(dataset.part_example.getPartNumber(), dataSetInitializer.SERIAL_NUMBER_PHYSICAL_PART);
        });
        assertTrue(thrown.getMessage().contains("retex.error.drt.creation.multiple.childrequest.found"));
    }

    @Test
    public void getChildRequestWithOrigin_failed_NoChildRequest() throws FunctionalException {
        checkFunctionalException(dataset.part_example_2.getPartNumber(), dataSetInitializer.SERIAL_NUMBER_PHYSICAL_PART);
    }

    @Test
    public void getChildRequestWithOriginISIR_failed_WrongPN() throws FunctionalException {
        initializeChildRequest(dataset.ORIGIN_ISIR, dataset.part_example,
                1L, dataSetInitializer.SERIAL_NUMBER_PHYSICAL_PART, EnumStatus.VALIDATED, new ArrayList<>());
        checkFunctionalException(dataset.part_example_2.getPartNumber(), dataSetInitializer.SERIAL_NUMBER_PHYSICAL_PART);
    }

    @Test
    public void getChildRequestWithOriginISIR_failed_WrongSN() throws FunctionalException {
        Part part = dataSetInitializer.createPart(null);
        initializeChildRequest(dataset.ORIGIN_ISIR, part,
                1L, dataSetInitializer.SERIAL_NUMBER_PHYSICAL_PART, EnumStatus.VALIDATED, new ArrayList<>());
        checkFunctionalException(part.getPartNumber(), WRONG_SERIAL_NUMBER);
    }


    @Test
    public void getChildRequestWithOriginCIVP_failed_DrtTreatedSuperiorOrEqualAtDrtToInspect() throws FunctionalException {
        Part part = dataSetInitializer.createPart(null);
        initializeChildRequest(dataset.ORIGIN_CIVP, part,
                1L, dataSetInitializer.SERIAL_NUMBER_PHYSICAL_PART, EnumStatus.VALIDATED, List.of(dataSetInitializer.createDRT()));
        checkFunctionalException(part.getPartNumber(), dataSetInitializer.SERIAL_NUMBER_PHYSICAL_PART);
    }

    @Test
    public void getChildRequestWithOriginCIVP_failed_DrtTreatedSuperiorOrEqualAtDrtToInspect_AndWrongSerialNumber() throws FunctionalException {
        Part part = dataSetInitializer.createPart(null);
        initializeChildRequest(dataset.ORIGIN_CIVP, part,
                1L, dataSetInitializer.SERIAL_NUMBER_PHYSICAL_PART, EnumStatus.VALIDATED, List.of(dataSetInitializer.createDRT()));
        checkFunctionalException(part.getPartNumber(), WRONG_SERIAL_NUMBER);
    }

    @Test
    public void getChildRequestWithOriginCIVP_failed_DrtTreatedSuperiorOrEqualAtDrtToInspect_WrongSerialNumber_AndPNRootEqualPN() throws FunctionalException {
        Part part = dataSetInitializer.createPart(p -> {
            p.setPartNumber("");
        }, null);
        initializeChildRequest(dataset.ORIGIN_CIVP, part,
                1L, dataSetInitializer.SERIAL_NUMBER_PHYSICAL_PART, EnumStatus.VALIDATED, List.of(dataSetInitializer.createDRT()));
        checkFunctionalException(part.getPartNumberRoot(), SERIAL_NUMBER);
    }

    @Test
    public void getChildRequestWithOriginRETEX_failed_DrtTreatedSuperiorOrEqualAtDrtToInspect() throws FunctionalException {
        Part part = dataSetInitializer.createPart(null);
        initializeChildRequest(dataset.ORIGIN_RETEX, part, 1L, dataSetInitializer.SERIAL_NUMBER_PHYSICAL_PART,
                EnumStatus.VALIDATED, List.of(dataSetInitializer.createDRT()));
        checkFunctionalException(part.getPartNumber(), dataSetInitializer.SERIAL_NUMBER_PHYSICAL_PART);
    }

    @Test
    public void getChildRequestWithOriginRETEX_failed_DrtTreatedSuperiorOrEqualAtDrtToInspect_AndWrongSerialNumber() throws FunctionalException {
        Part part = dataSetInitializer.createPart(null);
        initializeChildRequest(dataset.ORIGIN_RETEX, part,
                1L, dataSetInitializer.SERIAL_NUMBER_PHYSICAL_PART, EnumStatus.VALIDATED, List.of(dataSetInitializer.createDRT()));
        checkFunctionalException(part.getPartNumber(), WRONG_SERIAL_NUMBER);
    }

    @Test
    public void getChildRequestWithOriginRETEX_failed_DrtTreatedSuperiorOrEqualAtDrtToInspect_WrongSerialNumber_AndPNRootEqualPN() throws FunctionalException {
        Part part = dataSetInitializer.createPart(p -> {
            p.setPartNumber("");
        }, null);
        childRequest = initializeChildRequest(dataset.ORIGIN_RETEX, part,
                1L, dataSetInitializer.SERIAL_NUMBER_PHYSICAL_PART, EnumStatus.VALIDATED, List.of(dataSetInitializer.createDRT()));
        checkFunctionalException(part.getPartNumberRoot(), WRONG_SERIAL_NUMBER);
    }
}
