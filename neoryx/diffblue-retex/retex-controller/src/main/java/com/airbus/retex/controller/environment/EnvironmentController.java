package com.airbus.retex.controller.environment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.airbus.retex.business.dto.environment.EnvironmentLightDto;
import com.airbus.retex.service.environment.IEnvironmentService;
import com.airbus.retex.utils.ConstantUrl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "Environment", tags = {"Environment"})
@RestController
public class EnvironmentController {

    @Autowired
    private IEnvironmentService environmentService;


    @ApiOperation("Get list of Environment")
    @GetMapping(ConstantUrl.API_ENVIRONMENTS)
    @Secured({"ROLE_REQUEST:READ", "ROLE_FILTERING:READ"})
    public ResponseEntity<List<EnvironmentLightDto>> getAllEnvironment() {
        return ResponseEntity.ok().body(environmentService.getAllEnvironments());
    }
}
