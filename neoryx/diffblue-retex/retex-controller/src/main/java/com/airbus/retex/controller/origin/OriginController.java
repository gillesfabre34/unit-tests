package com.airbus.retex.controller.origin;

import com.airbus.retex.business.dto.PageDto;
import com.airbus.retex.business.dto.origin.OriginDto;
import com.airbus.retex.controller.BaseController;
import com.airbus.retex.service.impl.origin.OriginServiceImpl;
import com.airbus.retex.utils.ConstantUrl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OriginController extends BaseController<OriginServiceImpl, OriginDto> {

    @GetMapping(ConstantUrl.API_ORIGINS)
    @Override
    public ResponseEntity<List<OriginDto>> all() {
        return ResponseEntity.ok().body(service.search().getResults());
    }
    @GetMapping(ConstantUrl.API_ORIGINS_SEARCH)
    @Override
    public ResponseEntity<PageDto<OriginDto>> search() {
        return super.search();
    }
}
