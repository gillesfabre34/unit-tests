package com.airbus.retex.service.request;

import com.airbus.retex.AbstractServiceIT;
import com.airbus.retex.business.dto.user.UserLightDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class GetAllRequestersIT extends AbstractServiceIT {

    @Autowired
    private IRequestService requestService;

    @Test
    public void findAll_ok () throws Exception {
        List<UserLightDto> result = requestService.getAllRequesters();
        assertThat(result.size(), equalTo(1));
    }
}
