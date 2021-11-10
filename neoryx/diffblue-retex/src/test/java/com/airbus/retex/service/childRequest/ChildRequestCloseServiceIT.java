package com.airbus.retex.service.childRequest;

import com.airbus.retex.AbstractServiceIT;
import com.airbus.retex.business.exception.NotFoundException;
import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.persistence.childRequest.ChildRequestRepository;
import com.airbus.retex.persistence.request.RequestRepository;
import com.airbus.retex.service.request.IRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class ChildRequestCloseServiceIT extends AbstractServiceIT {





    private ChildRequest childRequest11;


    private ChildRequest orphanChildRequest;

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

        Request parentRequest1 = dataSetInitializer.createRequest("parentRequest1");


        childRequest11 = dataSetInitializer.createChildRequest(cr->{cr.setParentRequest(parentRequest1);cr.setStatus(EnumStatus.CREATED);});


        orphanChildRequest = dataSetInitializer.createChildRequest(cr->{cr.setParentRequest(null);cr.setStatus(EnumStatus.CREATED);});


        requestRepository.refresh(parentRequest1);


    }



    @Test
    public void close_ko_badChildRequestID ()  {
        checkNotFoundException(1234L, "retex.error.childrequest.not.found");
    }

    @Test
    public void close_ko_ChildRequestIsOrphan ()  {
        checkNotFoundException(orphanChildRequest.getId(), "retex.error.childrequest.is.orphan");
    }

    @Test
    public void close_ok ()  {
        assertDoesNotThrow(() -> {
                    childRequestService.updateChildRequestStatus(childRequest11.getId(), EnumStatus.CLOSED);
                });
        ChildRequest crClosed = childRequestRepository.getOne(childRequest11.getId());
        assertThat(crClosed.getStatus(), equalTo(EnumStatus.CLOSED));
    }



    private void checkNotFoundException(final Long creqID,  final String expectedMessage){
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            childRequestService.updateChildRequestStatus(creqID, EnumStatus.CLOSED);
        });
        assertTrue(thrown.getMessage().contains(expectedMessage));
    }
}
