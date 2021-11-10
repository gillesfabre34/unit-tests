package com.airbus.retex.service.request;

import com.airbus.retex.AbstractServiceIT;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.persistence.childRequest.ChildRequestRepository;
import com.airbus.retex.persistence.request.RequestRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;



import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RequestDeleteServiceIT extends AbstractServiceIT {

    public static final String REF_CREATED = "REF_CREATED";
    public static final String REF_VALIDATED = "REF_VALIDATED";
    @Autowired
    private IRequestService requestService;

    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private ChildRequestRepository childRequestRepository;

    private Request request;
    private Request request1;
    private Request request2;
    private ChildRequest childRequest;

    @BeforeEach
    public void before() {
        runInTransaction(() -> {
            request = dataSetInitializer.createRequest("request test",
                    request -> request.setStatus(EnumStatus.CREATED));

            childRequest = dataSetInitializer.createChildRequest();

            childRequest.addClient(dataset.client_1);
            childRequest.addPhysicalPart(dataSetInitializer.createPhysicalPart(childRequest, "sn10"));
            childRequest.setParentRequest(request);
            childRequest.setStatus(EnumStatus.IN_PROGRESS);

            childRequest = childRequestRepository.save(childRequest);
            request.addChildRequest(childRequest);
            request1 = dataSetInitializer.createRequest("request test 1",
                    request -> request.setStatus(EnumStatus.CREATED));

            request2 = dataSetInitializer.createRequest("request test 2",
                    request -> {
                        request.setStatus(EnumStatus.CREATED);
                        request.setReference(REF_CREATED);
                    });
        });
        runInTransaction(() -> {
            request2.setStatus(EnumStatus.VALIDATED);
            request2.setReference(REF_VALIDATED);
            request2 = requestRepository.save(request2);
        });
    }

    @Test
    public void deleteRequestWithStatusCreated_ok() throws FunctionalException {
        requestService.deleteRequest(request1.getId());
        assertEquals(Optional.empty(), requestRepository.findById(request1.getId()));
    }

    @Test
    public void deleteRequestNotExisting_ko() {
        check(9999L, "retex.error.request.not.found");
    }


    @Test
    // Historically ...the request2 has in hibernate...CREATED ...then VALIDATED (instances).
    // Requesting a RETEX DELETE on request2 means...modifying the status of the latest instance of request2 to DELETED
    // then Copying previous version of this request (CREATED) to the current .....
    public void deleteRequestWithValidated_ok() throws FunctionalException {
        requestService.deleteRequest(request2.getId());
        runInTransaction(() -> {
            Request theRequestAfterDelete = requestRepository.getOne(request2.getId());
            assertThat(theRequestAfterDelete.getStatus() ,equalTo(EnumStatus.CREATED));
            assertThat(theRequestAfterDelete.getReference() ,equalTo(REF_CREATED));
        });
    }


    @Test
    public void deleteRequest_withChildsRequests_ko() {
        //runInTransaction(() -> {
        //    request.addChildRequest(childRequest);
        //    requestRepository.save(request);
        //    //requestRepository.refresh(request);
        //});
        check(request.getId(), "retex.error.childrequest.invalid.status.delete.impossible");

    }

    @Test
    /**
     * Testing that the cascade.REMOVE works correctly on Request Entity.
     * This test does not validate the functional rules on when and how a request should be deleted.
     * It tests only the CascadeType.REMOVE.
     */
    public void deleteFromRepositoryRequest_withChildsRequests_no_service_usage_ok() {

            Long childRequestId = childRequest.getId();
            Long requestId = request.getId();
            assertTrue(requestRepository.existsById(requestId));
            assertTrue(childRequestRepository.existsById(childRequestId));

            requestRepository.deleteById(requestId);

            assertFalse(requestRepository.existsById(requestId));
            assertFalse(childRequestRepository.existsById(childRequestId));

    }
    private void check(Long requestId, final String expectedMessage) {
        FunctionalException functionalException = assertThrows(FunctionalException.class, () ->
                requestService.deleteRequest(requestId)
        );

        assertTrue(functionalException.getMessage().equals(expectedMessage), "Received "+ functionalException.getMessage() + "instead of expected " + expectedMessage);

    }

}
