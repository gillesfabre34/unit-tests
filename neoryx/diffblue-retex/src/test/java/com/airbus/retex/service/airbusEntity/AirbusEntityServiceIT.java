package com.airbus.retex.service.airbusEntity;

import com.airbus.retex.AbstractServiceIT;
import com.airbus.retex.business.dto.PageDto;
import com.airbus.retex.business.dto.airbusentity.AirbusEntityLightDto;
import com.airbus.retex.service.impl.airbusEntity.AirbusEntityServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class AirbusEntityServiceIT extends AbstractServiceIT {
    @Autowired
    private AirbusEntityServiceImpl service;

    @Test
    public void search () throws Exception {
        PageDto<AirbusEntityLightDto> result = service.search();
        assertThat(result.getResults().size(), equalTo(3));
    }
}
