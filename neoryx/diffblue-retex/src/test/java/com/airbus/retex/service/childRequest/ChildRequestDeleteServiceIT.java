package com.airbus.retex.service.childRequest;

import com.airbus.retex.AbstractServiceIT;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.exception.NotFoundException;
import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.drt.Drt;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.persistence.childRequest.ChildRequestRepository;
import com.airbus.retex.persistence.request.RequestRepository;
import com.airbus.retex.service.request.IRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.transaction.annotation.Transactional;



import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Transactional
public class ChildRequestDeleteServiceIT extends AbstractServiceIT {

    private Request parentRequest1;

    private ChildRequest childRequest11;

    @Autowired
    private IChildRequestService childRequestService;

    @Autowired
    private IRequestService requestService;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private ChildRequestRepository childRequestRepository;
    @BeforeEach
    public void before() {

        parentRequest1 = dataSetInitializer.createRequest("parentRequest1");
        childRequest11 = dataSetInitializer.createChildRequest(cr->{
            cr.setParentRequest(parentRequest1);
        });
        childRequest11.addClient(dataset.client_1);
        childRequest11.addClient(dataset.client_2);
        childRequest11.addPhysicalPart(dataSetInitializer.createPhysicalPart(childRequest11, "sn10"));
        childRequest11.addPhysicalPart(dataSetInitializer.createPhysicalPart(childRequest11, "sn20"));
        childRequest11 = childRequestRepository.save(childRequest11);
        requestRepository.refresh(parentRequest1);
    }

    @Test
    public void delete_ko_badChildRequestID ()  {
        checkNotFoundException(1234L, "retex.error.childrequest.not.found");
    }

    @Test
    public void delete_ko_ChildRequestIsOrphan ()  {
        ChildRequest orphanChildRequest = dataSetInitializer.createChildRequest(cr->{
            cr.setParentRequest(null);
            cr.setStatus(EnumStatus.CREATED);
        });
        checkNotFoundException(orphanChildRequest.getId(), "retex.error.childrequest.is.orphan");
    }

    @Test
    public void delete_ok () throws FunctionalException {

        ChildRequest childRequest = dataSetInitializer.createChildRequest(cr->{
            cr.setParentRequest(parentRequest1);
            cr.setStatus(EnumStatus.CREATED);
        });
        Long childRequestID =  childRequest.getId();
        assertTrue(childRequestRepository.existsById(childRequestID));
        childRequestService.deleteChildRequest(childRequest.getId());
        assertFalse(childRequestRepository.existsById(childRequestID));

    }

    @Test
    public void delete_ko_drt_not_zero() {
        ChildRequest childRequestDrtNotZero = dataSetInitializer.createChildRequest(cr->{
            cr.setParentRequest(parentRequest1);
            cr.setStatus(EnumStatus.CREATED);
        });
        Drt drt = dataSetInitializer.createDRT(d->{d.setChildRequest(childRequestDrtNotZero);});
        childRequestDrtNotZero.addDrt(drt);
        ChildRequest childRequestDrtNotZeroAfterSave = childRequestRepository.save(childRequestDrtNotZero);
        checkFunctionalException(childRequestDrtNotZeroAfterSave.getId(), "retex.error.childrequest.has.drt.delete.impossible");
    }

    @Test
    public void delete_ko_badStatus() {
        ChildRequest childRequestBadStatus = dataSetInitializer.createChildRequest(cr->{
            cr.setStatus(EnumStatus.DONE);
            cr.setParentRequest(parentRequest1);
            cr.setStatus(EnumStatus.CLOSED);
        });
        checkFunctionalException(childRequestBadStatus.getId(), "retex.error.childrequest.invalid.status.delete.impossible");
    }

    private void checkFunctionalException(final Long creqID, final String expectedMessage){
        FunctionalException thrown = assertThrows(FunctionalException.class, () -> {
            childRequestService.deleteChildRequest(creqID);
        });
        assertTrue(thrown.getMessage().contains(expectedMessage));
    }

    private void checkNotFoundException(final Long creqID,  final String expectedMessage){
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            childRequestService.deleteChildRequest(creqID);
        });
        assertTrue(thrown.getMessage().contains(expectedMessage));
    }
}
