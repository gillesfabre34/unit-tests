package com.airbus.retex.controller.measureUnit;

import com.airbus.retex.business.dto.measureUnit.MeasureUnitDto;
import com.airbus.retex.service.measureUnit.IMeasureUnitService;
import com.airbus.retex.utils.ConstantUrl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
@Api(value = "Measure Unit Endpoint", tags = {"Measure Unit api"})
public class MeasureUnitController {

    @Autowired
    private IMeasureUnitService measureUnitService;

    @ApiOperation("Get list of measure unit")
    @GetMapping(ConstantUrl.API_MEASURE_UNITS)
    @Secured({"ROLE_ROUTING_COMPONENT:READ", "ROLE_ROUTING:READ"})
    public ResponseEntity<List<MeasureUnitDto>> getAllUnits() {
        return ResponseEntity.ok().body(measureUnitService.getAllUnits());
    }
}
