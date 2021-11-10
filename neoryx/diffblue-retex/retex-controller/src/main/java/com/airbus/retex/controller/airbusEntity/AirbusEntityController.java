package com.airbus.retex.controller.airbusEntity;

import com.airbus.retex.business.dto.PageDto;
import com.airbus.retex.business.dto.airbusentity.AirbusEntityLightDto;
import com.airbus.retex.controller.BaseController;
import com.airbus.retex.service.impl.airbusEntity.AirbusEntityServiceImpl;
import com.airbus.retex.utils.ConstantUrl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AirbusEntityController extends BaseController<AirbusEntityServiceImpl, AirbusEntityLightDto> {

    @GetMapping(ConstantUrl.API_AIRBUS_ENTITIES)
    @Override
    public ResponseEntity<List<AirbusEntityLightDto>> all() {
        return super.all();
    }

    @GetMapping(ConstantUrl.API_AIRBUS_ENTITIES_SEARCH)
    @Override
    public ResponseEntity<PageDto<AirbusEntityLightDto>> search() {
        return super.search();
    }
}
