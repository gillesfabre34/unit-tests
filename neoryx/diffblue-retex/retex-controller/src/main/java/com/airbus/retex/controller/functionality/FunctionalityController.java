package com.airbus.retex.controller.functionality;

import com.airbus.retex.business.dto.functionality.FunctionalityLightDto;
import com.airbus.retex.service.functionality.IFunctionalityService;
import com.airbus.retex.utils.ConstantUrl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
@Api(value = "Functionality Endpoints", tags = {"Functionality api"})
public class FunctionalityController {

    @Autowired
    private IFunctionalityService functionalityService;

    @ApiOperation("Get list of functionalities")
    @GetMapping(ConstantUrl.API_FUNCTIONALITIES)
    @Secured("ROLE_PART_MAPPING:READ")
    public ResponseEntity<List<FunctionalityLightDto>> getFunctionalities(Authentication authentication) {
        return ResponseEntity.ok(functionalityService.getFunctionalities());
    }
}
