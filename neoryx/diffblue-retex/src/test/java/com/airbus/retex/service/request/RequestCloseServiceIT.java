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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RequestCloseServiceIT extends AbstractServiceIT {

    @Autowired
    private IRequestService requestService;

    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private ChildRequestRepository childRequestRepository;

    private Request request;
    private ChildRequest childRequest1;
    private ChildRequest childRequest2;
    private ChildRequest childRequest3;


    @BeforeEach
    public void before() {
        runInTransaction(() -> {
            request = dataSetInitializer.createRequest("request test",
                    request -> request.setStatus(EnumStatus.COMPLETED));

            childRequest1 = dataSetInitializer.createChildRequest();
            childRequest1.setStatus(EnumStatus.COMPLETED);
            //dataSetInitializer.createChildRequest() adds the client1 already ...so we add below the client 2
            childRequest1.addClient(dataset.client_2);
            childRequest1.addPhysicalPart(dataSetInitializer.createPhysicalPart(childRequest1,"sn10"));

            childRequest1 = childRequestRepository.save(childRequest1);

            childRequest2 = dataSetInitializer.createChildRequest();
            
            childRequest2.setStatus(EnumStatus.DELETED);
            childRequest2.addClient(dataset.client_2);
            childRequest2.addPhysicalPart(dataSetInitializer.createPhysicalPart(childRequest2,"sn10"));

            childRequest2 = childRequestRepository.save(childRequest2);

            childRequest3 = dataSetInitializer.createChildRequest();
            childRequest3.setStatus(EnumStatus.CREATED);
            childRequest3.addClient(dataset.client_2);
            childRequest3.addPhysicalPart(dataSetInitializer.createPhysicalPart(childRequest3,"sn10"));

            childRequest3 = childRequestRepository.save(childRequest3);


        });
    }

    @Test
    public void closeRequest_withChildsRequestWithStatusCompleted_ok() throws FunctionalException {
        request.addChildRequest(childRequest1);
        requestService.updateRequestStatus(request.getId(), EnumStatus.CLOSED );
        request = requestRepository.findById(request.getId()).get();
        assertThat(request.getStatus(),equalTo(EnumStatus.CLOSED));
    }

    @Test
    public void closeRequestNoExist_ko() {
        check(9999L, "retex.error.request.not.found");
    }

    @Test
    public void closeRequest_withChildsRequestWithStatusDeleted_ok() throws FunctionalException {
        request.addChildRequest(childRequest2);
        requestService.updateRequestStatus(request.getId(), EnumStatus.CLOSED);
        request = requestRepository.findById(request.getId()).get();
        assertThat(request.getStatus(),equalTo(EnumStatus.CLOSED));
    }

    @Test
    public void closeRequest_withChildsRequestBadStatus_ko() {
        childRequest3.setParentRequest(request);
        childRequest3 = childRequestRepository.save(childRequest3);
        request.addChildRequest(childRequest3);
        requestRepository.save(request);
        check(request.getId(), "retex.error.close.request");

    }

    @Test
    public void closeRequest_withTwoChildsRequestBadStatus_ko() {

        childRequest2.setParentRequest(request);
        childRequest2 = childRequestRepository.save(childRequest2);
        request.addChildRequest(childRequest2);

        childRequest3.setParentRequest(request);
        childRequest3 = childRequestRepository.save(childRequest3);
        request.addChildRequest(childRequest3);

        requestRepository.save(request);
        check(request.getId(), "retex.error.close.request");

    }


    private void check(Long requestId, final String expectedMessage) {
        FunctionalException functionalException = assertThrows(FunctionalException.class, () ->
                requestService.updateRequestStatus(requestId, EnumStatus.CLOSED)
        );

        assertThat(functionalException.getMessage(), equalTo(expectedMessage));
    }
}
