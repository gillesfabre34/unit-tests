package com.airbus.retex.service.request;

import com.airbus.retex.AbstractServiceIT;
import com.airbus.retex.business.dto.request.RequestFullDto;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.origin.Origin;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class GetRequestByIDServiceIT extends AbstractServiceIT {
    private User requester;
    private static final String BASE_NAME_REQUEST = "requestname";
    private static final String REQ_NAME_1 = BASE_NAME_REQUEST + 1;
    private static final String REQ_NAME_2 = BASE_NAME_REQUEST + 2;
    private Request request1;
    private Request request2;


    @Autowired
    private IRequestService requestService;
    @BeforeEach
    public void before() {

        requester = dataSetInitializer.createUser();

        Origin origin1 = dataset.ORIGIN_CIVP;

        request1 = dataSetInitializer.createRequest(REQ_NAME_1, request -> {
            request.setAirbusEntity(dataset.airbusEntity_canada);
            request.setStatus(EnumStatus.DONE);
            request.setOrigin(origin1);
            request.setValidator(dataset.user_simpleUser2);
            request.setCreationDate(LocalDateTime.now());
            request.setDueDate(LocalDate.now().plusDays(3));
            request.setReference("REFREQUEST_1");
            request.setRequester(requester);
        });

        request2 = dataSetInitializer.createRequest(REQ_NAME_2, request -> {
            request.setStatus(EnumStatus.IN_PROGRESS);
            request.setOrigin(origin1);
        });

    }
    @Test
    public void getRequestByID_ok () throws Exception {
        RequestFullDto result = requestService.findRequestById(request1.getId(), null);

        assertThat(result.getId(), equalTo(request1.getId()));
        assertThat(result.getDueDate(), notNullValue());
        assertThat(result.getCreationDate(), notNullValue());
        assertThat(result.getOrigin().getId(), equalTo(dataset.ORIGIN_CIVP.getId()));
        assertThat(result.getOrigin().getColor(), equalTo(dataset.ORIGIN_CIVP.getColor()));
        assertThat(result.getOrigin().getName(), equalTo(dataset.ORIGIN_CIVP.getName()));
        assertThat(result.getAirbusEntity().getId(), equalTo(dataset.airbusEntity_canada.getId()));
        assertThat(result.getAirbusEntity().getCode(), equalTo(dataset.airbusEntity_canada.getCode()));
        assertThat(result.getAirbusEntity().getCountryName(), equalTo(dataset.airbusEntity_canada.getCountryName()));
        assertThat(result.getValidator().getId(), equalTo(dataset.user_simpleUser2.getId()));
        assertThat(result.getValidator().getLastName(), equalTo(dataset.user_simpleUser2.getLastName()));
        assertThat(result.getValidator().getFirstName(), equalTo(dataset.user_simpleUser2.getFirstName()));
        assertThat(result.getValidator().getFirstName(), equalTo(dataset.user_simpleUser2.getFirstName()));
        // These can't be tested here ....the translation is done doing at serialization
        // ------------------------------------------------------------------------------
        assertThat(result.getStatus(), equalTo(EnumStatus.DONE));
        assertThat(result.getRequester().getId(), equalTo(requester.getId()));
        assertThat(result.getRequester().getFirstName(), equalTo(requester.getFirstName()));
        assertThat(result.getRequester().getLastName(), equalTo(requester.getLastName()));
    }

}
