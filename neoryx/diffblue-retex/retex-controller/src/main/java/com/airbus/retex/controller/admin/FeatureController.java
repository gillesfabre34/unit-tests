package com.airbus.retex.controller.admin;

import com.airbus.retex.business.dto.feature.FeatureDto;
import com.airbus.retex.service.admin.IFeatureService;
import com.airbus.retex.utils.ConstantUrl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "Feature", tags = {"Feature endpoints"})
@RestController
public class FeatureController {

    @Autowired
    private IFeatureService featureService;

    @ApiOperation("Get list of feature")
    @GetMapping(ConstantUrl.API_FEATURE)
    @Secured("ROLE_ADMIN:READ")
    public List<FeatureDto> getAllRoleLabels(Authentication authentication) {
        return featureService.getAllFeatures();
    }
}
